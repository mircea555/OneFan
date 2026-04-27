package com.onefan.backend.service.impl;

import com.onefan.backend.service.SolanaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class SolanaServiceImpl implements SolanaService {

    @Value("${solana.helper-url}") // e.g., http://localhost:8001
    private String helperUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String initializeHashStoreOnChain() throws Exception {
        return null;
    }

    @Override
    public String storeHashOnChain(byte[] combinedBytes) throws Exception {
        // Convert bytes to Base64 for HTTP transport
        String base64 = Base64.getEncoder().encodeToString(combinedBytes);
        Map<String, String> request = new HashMap<>();
        request.put("hash_bytes_b64", base64);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                helperUrl + "/store_hash", entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map body = response.getBody();
            if (body != null && body.containsKey("tx_signature")) {
                return (String) body.get("tx_signature");
            }
            throw new RuntimeException("No tx_signature in response");
        } else {
            throw new RuntimeException("Failed to store hash on Solana: " + response.getStatusCode());
        }
    }
}
