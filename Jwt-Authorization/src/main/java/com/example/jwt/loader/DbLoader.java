package com.example.jwt.loader;

import com.example.jwt.model.Role;
import com.example.jwt.model.User;
import com.example.jwt.repository.RoleRepository;
import com.example.jwt.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class DbLoader implements ApplicationRunner {

    /*
    Bu class'ın amacı uygulama ayağı kalkarken bir kullanıcı oluşturup o kullanıcı üzerinden işlemleri gerçekleştirmektir.
    Eğer bu kullanıcıyı oluşturmaz isek 401 hatası ile karşılaşırız.
     */
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
