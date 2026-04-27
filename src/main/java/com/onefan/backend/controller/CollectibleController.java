package com.onefan.backend.controller;

import com.onefan.backend.model.Collectible;
import com.onefan.backend.payload.request.CollectibleRequest;
import com.onefan.backend.security.UserDetailsImpl;
import com.onefan.backend.service.CollectibleService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CollectibleController {

    private final Path UPLOAD_DIR = Paths.get("uploads");

    @Autowired
    private CollectibleService collectibleService;

    public CollectibleController() throws IOException {
        if (!Files.exists(UPLOAD_DIR)) {
            Files.createDirectories(UPLOAD_DIR);
        }
    }


    @GetMapping("/collectibles/public")
    public ResponseEntity<List<Collectible>> getAllPublicCollectibles() {
        List<Collectible> all = collectibleService.findAllPublic();
        return ResponseEntity.ok(all);
    }
    @GetMapping("/collectibles/all")
    public ResponseEntity<List<Collectible>> getAllCollectiblesForBuyers() {
        List<Collectible> all = collectibleService.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/collectibles/public/{id}")
    public ResponseEntity<Collectible> getPublicCollectibleById(@PathVariable Long id) {
        Optional<Collectible> maybe = collectibleService.findById(id);
        if (maybe.isEmpty() || !Boolean.TRUE.equals(maybe.get().getApproved())) {
            return ResponseEntity.<Collectible>notFound().build();
        }
        return ResponseEntity.ok(maybe.get());
    }


    @PostMapping("/buyer/collectibles/{id}/buy")
    public ResponseEntity<?> buyCollectible(
            @PathVariable Long id,
            Authentication authentication) {

        Long buyerId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        Optional<Collectible> maybe = collectibleService.findById(id);

        if (maybe.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Collectible collectible = maybe.get();

        if (collectible.isSold()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Already sold.");
        }

        collectible.setOwnerId(buyerId);
        collectible.setSold(true);
        collectibleService.update(id, collectible);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/buyer/collectibles")
    public ResponseEntity<List<Collectible>> getOwnedCollectibles(Authentication authentication) {
        Long buyerId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        List<Collectible> owned = collectibleService.findAllByOwnerId(buyerId);
        return ResponseEntity.ok(owned);
    }

    @PostMapping("/seller/images")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No image file provided");
        }

        try {
            String original = file.getOriginalFilename();
            if (original == null) {
                original = "unknown";
            }
            String extension = "";
            int dot = original.lastIndexOf('.');
            if (dot >= 0) {
                extension = original.substring(dot);
            }

            String filename = System.currentTimeMillis() + "_" + original;

            Path destination = UPLOAD_DIR.resolve(filename);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            String publicPath = "/uploads/" + filename;
            return ResponseEntity.ok(publicPath);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(500)
                    .body("Could not save image: " + e.getMessage());
        }
    }


    @PostMapping("/seller/certificates")
    public ResponseEntity<String> uploadCertificate(@RequestParam("file") MultipartFile file) throws IOException {
        // build a safe filename
        String original = Path.of(file.getOriginalFilename()).getFileName().toString();
        String filename = System.currentTimeMillis() + "_" + original;

        // ensure `uploads/certificates` exists
        Path certDir = UPLOAD_DIR.resolve("certificates");
        Files.createDirectories(certDir);

        // write it
        Path target = certDir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // return exactly the URL you’ll store in the DB
        return ResponseEntity.ok("/uploads/certificates/" + filename);
    }


    @GetMapping("/seller/collectibles")
    public ResponseEntity<List<Collectible>> getAllForSeller(Authentication authentication) {
        Long sellerId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        List<Collectible> list = collectibleService.findAllBySellerId(sellerId);
        return ResponseEntity.ok(list);
    }


    @PostMapping("/seller/collectibles")
    public ResponseEntity<Collectible> createForSeller(
            @Valid @RequestBody CollectibleRequest request,
            Authentication authentication) {

        Long sellerId = ((UserDetailsImpl) authentication.getPrincipal()).getId();

        Collectible toCreate = new Collectible();
        toCreate.setName(request.getName());
        toCreate.setDescription(request.getDescription());
        toCreate.setPrice(request.getPrice());
        toCreate.setImageUrl(request.getImageUrl());
        toCreate.setCertificateUrl(request.getCertificateUrl());
        toCreate.setSport(request.getSport());
        toCreate.setPlayer(request.getPlayer());
        toCreate.setCategory(request.getCategory());
        toCreate.setSellerId(sellerId);

        Collectible saved = collectibleService.create(toCreate);
        URI location = URI.create("/api/seller/collectibles/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }


    @GetMapping("/seller/collectibles/{id}")
    public ResponseEntity<Collectible> getOneForSeller(
            @PathVariable Long id,
            Authentication authentication) {

        Optional<Collectible> maybe = collectibleService.findById(id);
        if (maybe.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Collectible existing = maybe.get();

        Long sellerId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        if (!existing.getSellerId().equals(sellerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(existing);
    }


    @PutMapping("/seller/collectibles/{id}")
    public ResponseEntity<Collectible> updateForSeller(
            @PathVariable Long id,
            @Valid @RequestBody CollectibleRequest updates,
            Authentication authentication) {

        Long sellerId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        Optional<Collectible> maybeExisting = collectibleService.findById(id);

        if (maybeExisting.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Collectible existing = maybeExisting.get();

        if (!existing.getSellerId().equals(sellerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (existing.getApproved() != null && existing.getApproved()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        existing.setPrice(updates.getPrice());
        Collectible saved = collectibleService.update(id, existing);
        return ResponseEntity.ok(saved);
    }


    @DeleteMapping("/seller/collectibles/{id}")
    public ResponseEntity<Void> deleteForSeller(
            @PathVariable Long id,
            Authentication authentication) {

        Long sellerId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        Optional<Collectible> maybeExisting = collectibleService.findById(id);

        if (maybeExisting.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Collectible existing = maybeExisting.get();

        if (!existing.getSellerId().equals(sellerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        collectibleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
