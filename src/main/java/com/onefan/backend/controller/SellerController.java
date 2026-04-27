package com.onefan.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SellerController {

    @GetMapping("/api/seller/test")
    public String sellerTest() {
        return "Seller endpoint: you have ROLE_SELLER.";
    }
}
