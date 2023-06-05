package com.example.securityBasicAuth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class Controller {

    @GetMapping("/user")
    String user(){
        return "Selam User";
    }

    @GetMapping("/admin")
    String admin(){
        return "Selam Admin";
    }
}
