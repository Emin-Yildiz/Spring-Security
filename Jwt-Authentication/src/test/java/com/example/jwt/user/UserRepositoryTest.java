//package com.example.jwt.user;
//
//import com.example.jwt.model.User;
//import com.example.jwt.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.test.annotation.Rollback;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Rollback(false)
//public class UserRepositoryTest {
//    private UserRepository repo;
//
//    public UserRepositoryTest(UserRepository repo){
//        this.repo = repo;
//    }
//
//    @Test
//    public void testCreateUser() {
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        String password = passwordEncoder.encode("1234");
//
//        User newUser = new User("emin@gmail.com", password);
//        User savedUser = repo.save(newUser);
//
////        assertThat(savedUser).isNotNull();
////        assertThat(savedUser.getId()).isGreaterThan(0);
//    }
//}
