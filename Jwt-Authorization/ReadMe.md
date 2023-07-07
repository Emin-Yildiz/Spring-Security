# Jwt Authorization

JWT(JSON Web Token), web uygulamalarında kullanılan bir kimlik doğrulama ve yetkilendirme mekanizmasıdır. JWT'ler, kullanıcıların kimlik bilgilerini güvenli bir şekilde taşımak ve doğrulamak için kullanılır.

JWT 3 ana bölümden oluşur.

- 1.Header(Başlık): Bu kısımda token oluşturulurken kullanılan algoritma bilgisi bulunur.
- 2.Payload: Bu kısımda JWT'nin içeriğini taşır. Kullanıcıya özel verilerin yanı sıra, bu bölümde tokenin süresi, geçerlilik tarihi gibi bilgiler de bulunabilir. Bu bölümdeki veriler JSON formatında kodlanır.
- 3.Signature: JWT'nin doğruluğunu sağlamak için kullanılır. Bu bölüm, başlık ve taşıyıcı bölümlerinin birleştirilmiş halini, belirli bir anahtar veya gizli bir metin kullanarak şifreleyerek oluşturulur. Bu şekilde, sunucu tarafında JWT'nin doğruluğu doğrulanabilir ve güvenlik sağlanır.

## JWT Authorization Kullanımı

- Kullanıcı login yaptığı zaman eğer kullanıcı veritabanında kayıtlı ise cevap olarak bir token döndürülür.
- Geriye dönen bu token kullanılarak uygulamaya istek atılır. Bu token sayesinde kimlik doğrulama, yetkilendirme işlemleri yapılır.
- Token içerisinde yer alan role bilgisi ile kullanıcı bazı uçlara erişirken bazı uçlara erişememektedir.

## JWT Kurulumu

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
```

- Öncelikle role class'ı tanımlamalıyız ve user class'ı içerisinde role bilgisi tanımlamalıyız. Bu role bilgisi many to many bir ilişkidir çünkü bir rolün birden fazla kişide olabilir bir kişide birden fazla rol olabilir.

```java
@ManyToMany
@JoinTable(
         ame = "users_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
)
private Set<Role> roles = new HashSet<>();
```

- Authentication uygulamasında yaptığımız gibi uygulama ayağa kalkarken oluşturulacak kullanıcılar lazım bir öncekindne farklı olarak birden fazla kullanıcı ve her bir kullanıcının rolü/rolleri bulunmaktadır. Aynı zamanda veritabanına role ekleme işlemide bu sırada yapılmaktadır.

```java
@Component
public class DbLoader implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public DbLoader(UserRepository userRepository,RoleRepository roleRepository){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (roleRepository.count() == 0){
            Role admin = new Role("ROLE_ADMIN");
            Role editor = new Role("ROLE_EDITOR");
            roleRepository.saveAll(List.of(admin,editor));
        }

        if (userRepository.count() == 0) {
            BCryptPasswordEncoder bCryptPasswordEncoder =  new BCryptPasswordEncoder();
            Optional<Role> optionalAdminRole = roleRepository.findById(1L);
            Optional<Role> optionalEditorRole = roleRepository.findById(2L);
            Role adminRole = optionalAdminRole.orElse(new Role());
            Role editorRole = optionalEditorRole.orElse(new Role());
            Set<Role> admin = Set.of(adminRole);
            Set<Role> editor = Set.of(editorRole);
            Set<Role> adminEditor = Set.of(adminRole,editorRole);
            userRepository.save(new User(4L,"root.com",bCryptPasswordEncoder.encode("root"),adminEditor));
            userRepository.save(new User(2L,"admin.com",bCryptPasswordEncoder.encode("admin"), admin));
            userRepository.save(new User(3L,"editor.com",bCryptPasswordEncoder.encode("editor"), editor));
        }

    }
}
```

- Bu uygulamada yetkilendirme işlemi yaptığımız için token içerisine claim alanını kullanarak kullanıcının rol bilgilerini tanımlarız.

```java
public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(String.format("%s,%s", user.getId(), user.getEmail())) // tokenin bu alanında kullanıcı kimlik bilgileri bulunur.
                .setIssuer("EminYildiz") // token'ın kim tarafından oluşturulur onu gösterir.
                .claim("roles",user.getRoles().toString()) // token'a kullanıcı yetkilerini verdik.
                .setIssuedAt(new Date()) // token'ın oluşturulma tarihi
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION)) // token'ın kullanımının biteceği tarih
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY) // token oluştururken kullanılacak olan algoritma ve, secret key burada belirtilir.
                .compact();
    }
```

- Oluşturduğumuz api'de ürün kayıt işlemi sadece admin üzerinden yürütülmektedir. Veritabanında bulunan ürünleri listeleme işlemi ise bütün kullanıcılar tarafından yapılmaktadır.

```java
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping
    @RolesAllowed("ROLE_ADMIN") // sadece admine yetki verdik.
    public Product create(@RequestBody Product product){
        return productService.create(product);
    }

    @GetMapping()
    @RolesAllowed({"ROLE_EDITOR","ROLE_ADMIN"}) // Hem admin hemde ediyor rolüne sahip kullanıcılar erişebilir.
    public ResponseEntity<List<Product>> findAll(){
        return ResponseEntity.ok(productService.findAll());
    }
}
```

## Security Config

Security yapılandırma yetkilendirme işlemi için "@EnableGlobalMethodSecurity()" anotasyonunu kullanabiliriz. Bu antotasyon sayesinde metodlara hangi role sahip olan kullanıcıların erişebileceğini belirleyebiliriz.

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = false,
        securedEnabled = false,
        jsr250Enabled = true // metod düzeyinde yetkilendirme işlemi yapabilmek için bu özelliği true yapıyoruz.
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    ...
}
```

