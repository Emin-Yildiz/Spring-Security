# Spring Security

- Spring security uygulamalarda authentication(kimlik doğrulama), Authorization(yetkilendirme), şifreleme, CSRF gibi güvenlik önlemlerini eklememizi sağlayan bir kütüphanedir.

- Spring security, uygulamalara kullanıcıların kimlik doğrulama sürecini eklemek için kimlik doğrulama yöntemleri sunar. Bunlar arasında kullanıcı adı-parola tabanlı kimlik doğrulama, token tabanlı kimlik doğrulama (JWT gibi) yöntemleri bulunur.

- Spring Security, kullanıcılara belirli işlevlerin (rollerin) erişimini kontrol etmelerine olanak tanır. Bu, kullanıcıların belirli sayfaları görüntülemek, kaynaklara erişmek veya işlemleri gerçekleştirmek için yetki verme imkanı tanır.

- Spring Security, aynı zamanda saldırılara karşı korumak içinde çeşitli araçlar sağlar.

## Projeye Eklenmesi

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

## Basic Authentication Kullanımı

- Basic Authentication kullanabilmek için öncelikle birkaç kullanıcı ve bu kullanıcıların rolleri olması gerekir.

- Aşağıda oluşturduğumuz kullanıcıların şifrelerini belirtirken, şifrelerin başına "{noop}" ifadesini koyammuz lazım eğer onu belirtmeyecek isek passwordEncoder oluşturmamız gerekecek.

- Aynı zamanda kullanıcı isimlerinin farklı olması gerekmekte. Yoksa hata alırız ve proje ayağa kalkmaz.

```java
@Bean
public AuthenticationManager authManager(HttpSecurity http) throws Exception {

    AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

    authenticationManagerBuilder.inMemoryAuthentication()
        .withUser("emin")
        .password("{noop}1234")
        .roles("ADMIN")
        .and()
        .withUser("ersin")
        .password("{noop}123456")
        .roles("USER")
        .and()
        .withUser("zeynep")
        .password("{noop}654321")
        .roles("CUSTOMER");

    return authenticationManagerBuilder.build();
}
```

- Kullanıcı login ve yetkilendirme işlemi için aşağıdaki şekilde config ayarlarımızı yapmamız lazım.

```java
@Bean
 SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception {

    http.authorizeHttpRequests()
        .requestMatchers("/auth/admin")
        .hasAnyRole("ADMIN")
        .and()
        .authorizeHttpRequests()
        .requestMatchers("/auth/user")
        .hasAnyRole("USER","ADMIN")
        .and()
        .authorizeHttpRequests()
        .anyRequest()
        //.permitAll() // yukarıda belirtilen sayfalar dışındaki sayfalar herhangi bir login işlmei olmadan erişilebilir.
        .authenticated()
        .and()
        .formLogin(Customizer.withDefaults());
    return http.build();
}
```

- **.requestMatchers("/auth/admin").hasAnyRole("ADMIN")**: /auth/admin yoluna gelen isteklerin "ADMIN" rolüne sahip kullanıcılara yetkilendirme gerektireceğini belirtir. Yani, bu yol sadece "ADMIN" rolüne sahip kullanıcılar tarafından erişilebilir olacaktır.

- **.requestMatchers("/auth/user").hasAnyRole("USER","ADMIN")**: /auth/user yoluna gelen isteklerin "USER" veya "ADMIN" rolüne sahip kullanıcılara yetkilendirme gerektireceğini belirtir. Yani, bu yol sadece "USER" veya "ADMIN" rolüne sahip kullanıcılar tarafından erişilebilir olacaktır.

- **.anyRequest().authenticated()**: Diğer tüm isteklerin kimlik doğrulama gerektireceğini belirtir. Yani, /auth/admin ve /auth/user dışındaki tüm istekler sadece kimlik doğrulaması yapılmış kullanıcılar tarafından erişilebilir olacaktır.
