package com.bitcli.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BitCliConfigTest {

    @Test
    void loadReturnsDefaultConfigWhenFileMissing(@TempDir Path tempHome) {
        String originalHome = System.getProperty("user.home");
        System.setProperty("user.home", tempHome.toString());
        try {
            BitCliConfig config = BitCliConfig.load();

            assertEquals("glm", config.getDefaultProvider());
            assertTrue(config.getProviders().isEmpty());
        } finally {
            if (originalHome == null) {
                System.clearProperty("user.home");
            } else {
                System.setProperty("user.home", originalHome);
            }
        }
    }

    @Test
    void saveAndLoadRoundTripsProviderConfig(@TempDir Path tempHome) {
        String originalHome = System.getProperty("user.home");
        System.setProperty("user.home", tempHome.toString());
        try {
            BitCliConfig config = new BitCliConfig();
            config.setDefaultProvider("deepseek");
            config.getProviders().put(
                    "deepseek",
                    new BitCliConfig.ProviderConfig("test-key", "https://api.example.com", "deepseek-v4"));

            config.save();
            BitCliConfig loaded = BitCliConfig.load();

            assertEquals("deepseek", loaded.getDefaultProvider());
            assertEquals("test-key", loaded.getApiKey("deepseek"));
            assertEquals("https://api.example.com", loaded.getBaseUrl("deepseek"));
            assertEquals("deepseek-v4", loaded.getModel("deepseek"));
        } finally {
            if (originalHome == null) {
                System.clearProperty("user.home");
            } else {
                System.setProperty("user.home", originalHome);
            }
        }
    }
}
