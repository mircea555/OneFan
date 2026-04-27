package com.onefan.backend.service.impl;

import com.onefan.backend.model.Collectible;
import com.onefan.backend.repository.CollectibleRepository;
import com.onefan.backend.service.CollectibleService;
import com.onefan.backend.service.SolanaService;
import com.onefan.backend.util.BlockchainLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.List;
import java.util.Optional;

@Service
public class CollectibleServiceImpl implements CollectibleService {

    @Autowired
    private CollectibleRepository collectibleRepository;

    @Autowired
    private SolanaService solanaService;

    @Override
    public List<Collectible> findAllPublic() {
        return collectibleRepository.findAllByApprovedTrueAndSoldFalse();
    }

    @Override
    public List<Collectible> findAll() {
        return collectibleRepository.findAll();
    }

    @Override
    public Optional<Collectible> findById(Long id) {
        return collectibleRepository.findById(id);
    }

    @Override
    public List<Collectible> findAllBySellerId(Long sellerId) {
        return collectibleRepository.findAllBySellerId(sellerId);
    }

    @Override
    public List<Collectible> findAllByOwnerId(Long ownerId) {
        return collectibleRepository.findAllByOwnerId(ownerId);
    }

    @Override
    public Collectible create(Collectible collectible) {
        collectible.setId(null);
        collectible.setAuthenticated(false);
        collectible.setOnChainTxSignature(null);
        collectible.setApproved(null);
        collectible.setSold(false);
        return collectibleRepository.save(collectible);
    }

    @Override
    public Collectible update(Long id, Collectible collectible) {
        collectible.setId(id);
        return collectibleRepository.save(collectible);
    }

    @Override
    public Collectible approveAndAuthenticate(Collectible collectible) {
        if (Boolean.TRUE.equals(collectible.getApproved()) ||
                Boolean.FALSE.equals(collectible.getApproved())) {
            throw new IllegalStateException("Already reviewed.");
        }
        collectible.setApproved(true);

        try {
            String certUrl = collectible.getCertificateUrl();
            if (certUrl == null || certUrl.isBlank()) {
                throw new IllegalArgumentException("Certificate URL is empty!");
            }
            if (certUrl.startsWith("http://") || certUrl.startsWith("https://")) {
                throw new IllegalArgumentException(
                        "Certificate path is an external URL, not a local file."
                );
            }

            // Remove "/uploads/" prefix if present
            final String prefix = "/uploads/";
            String relativePath = certUrl.startsWith(prefix)
                    ? certUrl.substring(prefix.length())
                    : certUrl;

            // Resolve file in uploads dir
            Path uploadsRoot = Paths.get("uploads").toAbsolutePath().normalize();
            Path certFile = uploadsRoot.resolve(relativePath).normalize();

            if (!certFile.startsWith(uploadsRoot)) {
                throw new SecurityException("Certificate path escapes upload directory!");
            }

            byte[] certBytes = Files.readAllBytes(certFile);

            String meta =
                    collectible.getName()        + "|"
                            + collectible.getDescription() + "|"
                            + collectible.getPrice()       + "|"
                            + collectible.getSport()       + "|"
                            + collectible.getPlayer()      + "|"
                            + collectible.getCategory()    + "|"
                            + collectible.getSellerId();

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] metaHash = md.digest(meta.getBytes("UTF-8"));

            byte[] combined = new byte[metaHash.length + certBytes.length];
            System.arraycopy(metaHash,    0, combined, 0,             metaHash.length);
            System.arraycopy(certBytes,   0, combined, metaHash.length, certBytes.length);

            // ---- Here is the only blockchain call! ----
            String txSignature = solanaService.storeHashOnChain(combined);

            collectible.setOnChainTxSignature(txSignature);
            collectible.setAuthenticated(true);

            BlockchainLogUtil.appendLog(
                    "APPROVED collectibleId=" + collectible.getId()
                            + " tx=" + txSignature
                            + " sellerId=" + collectible.getSellerId()
                            + " name=\"" + collectible.getName() + "\""
            );

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error during approval/authentication", e);
        }

        return collectibleRepository.save(collectible);
    }

    @Override
    public Collectible reject(Collectible collectible) {
        if (Boolean.TRUE.equals(collectible.getApproved()) ||
                Boolean.FALSE.equals(collectible.getApproved())) {
            throw new IllegalStateException("Already reviewed.");
        }
        collectible.setApproved(false);
        return collectibleRepository.save(collectible);
    }

    @Override
    public void delete(Long id) {
        collectibleRepository.deleteById(id);
    }
}
