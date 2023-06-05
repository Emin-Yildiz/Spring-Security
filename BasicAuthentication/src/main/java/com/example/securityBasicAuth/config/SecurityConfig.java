package com.example.securityBasicAuth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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

    /*
    Aşağıda oluşturduğumuz kullanıcıların şifrelerini belirtirken, şifrelerin başına {noop} ifadesini koyammuz lazım
    eğer onu belirtmeyecek isek passwordEncoder oluşturmamız gerekecek.

    Aynı zamanda kullanıcı isimlerinin farklı olması gerekmekte. Yoksa hata alırız ve proje ayağa kalkmaz.
     */
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
}
