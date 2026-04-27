package com.onefan.backend.service;

import com.onefan.backend.model.Collectible;
import java.util.List;
import java.util.Optional;

public interface CollectibleService {

    List<Collectible> findAllPublic();

    List<Collectible> findAll();
    Optional<Collectible> findById(Long id);

    List<Collectible> findAllBySellerId(Long sellerId);

    List<Collectible> findAllByOwnerId(Long ownerId);

    Collectible create(Collectible collectible);

    Collectible update(Long id, Collectible collectible);

    Collectible approveAndAuthenticate(Collectible collectible);

    Collectible reject(Collectible collectible);

    void delete(Long id);



}
