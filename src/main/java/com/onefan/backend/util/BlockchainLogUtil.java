package com.onefan.backend.util;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;

public class BlockchainLogUtil {
    private static final String LOG_PATH = "src/main/resources/static/logs/solana.log";

    public static synchronized void appendLog(String msg) {
        try {
            Files.createDirectories(Paths.get("src/main/resources/static/logs"));
            String entry = Instant.now() + " | " + msg + System.lineSeparator();
            Files.write(Paths.get(LOG_PATH), entry.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
