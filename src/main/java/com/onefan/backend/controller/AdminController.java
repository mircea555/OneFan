package com.onefan.backend.controller;

import com.onefan.backend.model.User;
import com.onefan.backend.repository.UserRepository;
import com.onefan.backend.model.Collectible;
import com.onefan.backend.repository.CollectibleRepository;
import com.onefan.backend.service.CollectibleService;
import com.onefan.backend.service.SolanaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollectibleRepository collectibleRepository;

    @Autowired
    private CollectibleService collectibleService;

    @Autowired
    private SolanaService solanaService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/collectibles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Collectible>> getAllCollectibles() {
        List<Collectible> all = collectibleRepository.findAll();
        return ResponseEntity.ok(all);
    }

    @PutMapping("/collectibles/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveCollectible(@PathVariable Long id) {
        Optional<Collectible> maybe = collectibleService.findById(id);
        if (maybe.isEmpty()) return ResponseEntity.notFound().build();

        Collectible collectible = maybe.get();

        if (collectible.getApproved() != null) {
            return ResponseEntity.badRequest().body("Already reviewed!");
        }

        try {
            Collectible updated = collectibleService.approveAndAuthenticate(collectible);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Blockchain mint error: " + e.getMessage());
        }
    }

    @PutMapping("/collectibles/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectCollectible(@PathVariable Long id) {
        Optional<Collectible> maybe = collectibleService.findById(id);
        if (maybe.isEmpty()) return ResponseEntity.notFound().build();

        Collectible collectible = maybe.get();

        if (collectible.getApproved() != null) {
            return ResponseEntity.badRequest().body("Already reviewed!");
        }

        collectible.setApproved(false);
        collectible.setAuthenticated(false);
        collectible.setOnChainTxSignature(null);
        collectibleService.update(id, collectible);
        return ResponseEntity.ok(collectible);
    }



    @GetMapping("/blockchain/log")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getBlockchainLog() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("src/main/resources/static/logs/solana.log"));
            return ResponseEntity.ok(lines);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Could not read blockchain log: " + e.getMessage());
        }
    }
}
