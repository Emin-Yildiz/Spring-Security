package com.example.jwt.controller;

import com.example.jwt.model.Product;
import com.example.jwt.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping
    public Product create(@RequestBody Product product){
        return productService.create(product);
    }
//    @PostMapping()
//    public ResponseEntity<Product> create(@RequestBody Product product){
//        return new ResponseEntity<>(productService.create(product), HttpStatus.CREATED);
//    }

    @GetMapping()
    public ResponseEntity<List<Product>> findAll(){
        return ResponseEntity.ok(productService.findAll());
    }

}
