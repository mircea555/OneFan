package com.onefan.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "collectibles")
public class Collectible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private double price;

    @Column(length = 255)
    private String imageUrl;

    @Column(nullable = false, length = 50)
    private String sport;

    @Column(nullable = false, length = 100)
    private String player;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(length = 500)
    private String certificateUrl;

    @Column(length = 100)
    private String onChainTxSignature;

    @Column(nullable = false)
    private boolean authenticated = false;

    @Column(nullable = true)
    private Boolean approved;

    @Column
    private Long ownerId;

    @Column(nullable = false)
    private boolean sold = false;

    @Column(nullable = false)
    private Long sellerId;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getPlayer() { return player; }
    public void setPlayer(String player) { this.player = player; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCertificateUrl() { return certificateUrl; }
    public void setCertificateUrl(String certificateUrl) { this.certificateUrl = certificateUrl; }

    public String getOnChainTxSignature() { return onChainTxSignature; }
    public void setOnChainTxSignature(String onChainTxSignature) { this.onChainTxSignature = onChainTxSignature; }

    public boolean isAuthenticated() { return authenticated; }
    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }

    public Boolean getApproved() { return approved; }
    public void setApproved(Boolean approved) { this.approved = approved; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public boolean isSold() { return sold; }
    public void setSold(boolean sold) { this.sold = sold; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

}
