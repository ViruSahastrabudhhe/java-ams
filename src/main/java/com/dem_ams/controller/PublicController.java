package com.dem_ams.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    @GetMapping
    public ResponseEntity<String> publicGet() {
        return  ResponseEntity.ok("Hello World");
    }
}
