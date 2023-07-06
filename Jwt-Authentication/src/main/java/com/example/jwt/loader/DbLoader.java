package com.example.jwt.loader;

import com.example.jwt.model.User;
import com.example.jwt.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DbLoader implements ApplicationRunner {

    /*
    Bu class'ın amacı uygulama ayağı kalkarken bir kullanıcı oluşturup o kullanıcı üzerinden işlemleri gerçekleştirmektir.
    Eğer bu kullanıcıyı oluşturmaz isek 401 hatası ile karşılaşırız.
     */
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
