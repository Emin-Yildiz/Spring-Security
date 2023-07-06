package com.example.jwt.controller;

import com.example.jwt.dto.AuthRequest;
import com.example.jwt.dto.AuthResponse;
import com.example.jwt.jwt.JwtUtil;
import com.example.jwt.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class AuthLogin {

    private final AuthenticationManager authenticationManager; // Kullanıcının kimliğini doğrulamak için authManager kullanılır.
    private final JwtUtil jwtUtil;

    public AuthLogin(AuthenticationManager authenticationManager, JwtUtil jwtUtil){
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    //TODO genel mantık kullanıcı giriş yapar ve jwt token oluşturulur, uygulamanın devamında bu token ile istek atılır.

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword())
            );

            User user = (User) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);
            AuthResponse response = new AuthResponse(user.getEmail(), accessToken);

            return ResponseEntity.ok().body(response);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
