# Jwt Authentication

JWT(JSON Web Token), web uygulamalarında kullanılan bir kimlik doğrulama ve yetkilendirme mekanizmasıdır. JWT'ler, kullanıcıların kimlik bilgilerini güvenli bir şekilde taşımak ve doğrulamak için kullanılır.

JWT 3 ana bölümden oluşur.

- 1.Header(Başlık): Bu kısımda token oluşturulurken kullanılan algoritma bilgisi bulunur.
- 2.Payload: Bu kısımda JWT'nin içeriğini taşır. Kullanıcıya özel verilerin yanı sıra, bu bölümde tokenin süresi, geçerlilik tarihi gibi bilgiler de bulunabilir. Bu bölümdeki veriler JSON formatında kodlanır.
- 3.Signature: JWT'nin doğruluğunu sağlamak için kullanılır. Bu bölüm, başlık ve taşıyıcı bölümlerinin birleştirilmiş halini, belirli bir anahtar veya gizli bir metin kullanarak şifreleyerek oluşturulur. Bu şekilde, sunucu tarafında JWT'nin doğruluğu doğrulanabilir ve güvenlik sağlanır.

## JWT Kullanımı

- Kullanıcı login yaptığı zaman eğer kullanıcı veritabanında kayıtlı ise cevap olarak bir token döndürülür.
- Geriye dönen bu token kullanılarak uygulamaya istek atılır. Bu token sayesinde kimlik doğrulama, yetkilendirme işlemleri yapılır.

## JWT Kurulumu

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
```

- Öncelikle veri tabanına bir kullanıcı kaydetmeliyiz ve bu oluşturduğumuz kullanıcı üzerinden isteklerimizi göndermeliyiz. Aksi halde 401 hatası ile karşılasırız.

```java
@Component
public class DbLoader implements ApplicationRunner {

    private final UserRepository userRepository;

    public DbLoader(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (userRepository.count() == 0) {
            BCryptPasswordEncoder bCryptPasswordEncoder =  new BCryptPasswordEncoder();
            userRepository.save(new User(1L,"root.com",bCryptPasswordEncoder.encode("root")));
        }
    }
}
```

- Uygulamayı çalıştırdıktan sonra aşağıdaki komutu çalıştırakan "localhost:8080/auth/login"'e login isteiği atıyoruz. 

```code
curl -v -H "Content-Type: application/json" -d "{\"email\":\"root.com\", \"password\":\"root\"}" localhost:8080/auth/login
```

- İstekten sonra bize uygulama bize token dönderir bu token sayesinde diğer adreslere istek atmaya başlıyabiliriz.

```json
{
    "email": "admin.com",
    "accesToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyLGFkbWluLmNvbSIsImlzcyI6IkVtaW..."
}
```

- Aşağıdaki komuta elde ettiğimiz token ile istek atar isek veritabanında bulunan tüm kayıtları getirir. Tokenda herhangi bir hata olması durumunda ise 401 hatası ile karşı karşıya kalırız.

```code
curl -v -H "Authorization: Bearer <token>" localhost:8080/product
```

## Security Config

Security yapılandırma dosyasını aşağıdaki gibi yapılandırmamız lazım. Bu yapılandırma login sayfasını herkese açık yaparken gerş kalan bütün uçları token ise istek atmamıza olanak sağlar.

```java
 @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .antMatchers("/auth/login").permitAll()
                .anyRequest().authenticated();

        http.exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> {
                            response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    ex.getMessage()
                            );
                        }
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); 
    }
```
