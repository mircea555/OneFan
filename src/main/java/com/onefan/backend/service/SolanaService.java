package com.onefan.backend.service;

import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.PublicKey;
public interface SolanaService {
    String initializeHashStoreOnChain() throws Exception;
    /** Store a 32-byte SHA-256 hash on-chain */
    String storeHashOnChain(byte[] combinedBytes) throws Exception;
}
