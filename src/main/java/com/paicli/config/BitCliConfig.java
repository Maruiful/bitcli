package com.paicli.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BitCliConfig {

    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private String defaultProvider = "glm";
    private Map<String, ProviderConfig> providers = new LinkedHashMap<>();

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProviderConfig {
        private String apiKey;
        private String baseUrl;
        private String model;

        public ProviderConfig() {
        }

        public ProviderConfig(String apiKey, String baseUrl, String model) {
            this.apiKey = apiKey;
            this.baseUrl = baseUrl;
            this.model = model;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }
    }

    public static BitCliConfig load() {
        return load(defaultHomeDir());
    }

    public static BitCliConfig load(Path homeDir) {
        Path configFile = configFile(homeDir);
        if (!Files.exists(configFile)) {
            return new BitCliConfig();
        }
        try {
            return MAPPER.readValue(configFile.toFile(), BitCliConfig.class);
        } catch (IOException e) {
            throw new IllegalStateException("读取 bit cli 配置失败: " + e.getMessage(), e);
        }
    }

    public void save() {
        save(defaultHomeDir());
    }

    public void save(Path homeDir) {
        Path configFile = configFile(homeDir);
        try {
            Files.createDirectories(configFile.getParent());
            MAPPER.writeValue(configFile.toFile(), this);
        } catch (IOException e) {
            throw new IllegalStateException("保存 bit cli 配置失败: " + e.getMessage(), e);
        }
    }

    public String getApiKey(String provider) {
        ProviderConfig providerConfig = providers.get(provider);
        return providerConfig == null ? null : providerConfig.getApiKey();
    }

    public String getBaseUrl(String provider) {
        ProviderConfig providerConfig = providers.get(provider);
        return providerConfig == null ? null : providerConfig.getBaseUrl();
    }

    public String getModel(String provider) {
        ProviderConfig providerConfig = providers.get(provider);
        return providerConfig == null ? null : providerConfig.getModel();
    }

    public String getDefaultProvider() {
        return defaultProvider;
    }

    public void setDefaultProvider(String defaultProvider) {
        this.defaultProvider = defaultProvider;
    }

    public Map<String, ProviderConfig> getProviders() {
        return providers;
    }

    public void setProviders(Map<String, ProviderConfig> providers) {
        this.providers = providers;
    }

    private static Path defaultHomeDir() {
        return Path.of(System.getProperty("user.home"));
    }

    private static Path configFile(Path homeDir) {
        return homeDir.resolve(".bitcli").resolve("config.json");
    }
}
