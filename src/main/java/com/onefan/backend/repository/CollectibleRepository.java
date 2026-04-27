package com.onefan.backend.repository;

import com.onefan.backend.model.Collectible;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CollectibleRepository extends JpaRepository<Collectible, Long> {
    List<Collectible> findAllBySellerId(Long sellerId);
    List<Collectible> findAllByApprovedTrueAndSoldFalse();
    List<Collectible> findAllByOwnerId(Long ownerId); // ← Add this!
}
