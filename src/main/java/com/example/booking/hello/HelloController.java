package com.example.booking.hello;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/")
public class HelloController {

    @GetMapping
    public ResponseEntity getHello(){
        return ResponseEntity.ok().body("Hello, welcome to my movie booking system.");
    }
}
