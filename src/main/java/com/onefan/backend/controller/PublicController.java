package com.onefan.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PublicController {

    @GetMapping("/api/collectibles/public/test")
    public String publicTest() {
        return "Public endpoint: no authentication needed.";
    }
}
