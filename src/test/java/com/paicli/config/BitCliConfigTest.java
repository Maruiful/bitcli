package com.paicli.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BitCliConfigTest {

    @Test
    void loadReturnsDefaultConfigWhenFileMissing(@TempDir Path tempHome) {
        BitCliConfig config = BitCliConfig.load(tempHome);

        assertEquals("glm", config.getDefaultProvider());
        assertTrue(config.getProviders().isEmpty());
    }

    @Test
    void saveAndLoadRoundTripsProviderConfig(@TempDir Path tempHome) {
        BitCliConfig config = new BitCliConfig();
        config.setDefaultProvider("deepseek");
        config.getProviders().put(
                "deepseek",
                new BitCliConfig.ProviderConfig("test-key", "https://api.example.com", "deepseek-v4"));

        config.save(tempHome);
        BitCliConfig loaded = BitCliConfig.load(tempHome);

        assertEquals("deepseek", loaded.getDefaultProvider());
        assertEquals("test-key", loaded.getApiKey("deepseek"));
        assertEquals("https://api.example.com", loaded.getBaseUrl("deepseek"));
        assertEquals("deepseek-v4", loaded.getModel("deepseek"));
    }
}
