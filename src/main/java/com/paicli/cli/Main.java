package com.paicli.cli;

import com.paicli.config.PaiCliConfig;
import org.jline.reader.LineReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    private static final String VERSION = "0.2.0";
    private static final String HISTORY_FILE_PROPERTY = "paicli.history.file";
    private static final String HISTORY_SIZE_PROPERTY = "paicli.history.size";
    private static final String HISTORY_FILE_SIZE_PROPERTY = "paicli.history.fileSize";
    private static final String DEFAULT_HISTORY_FILE_NAME = "input.history";
    private static final String DEFAULT_CHROME_DEVTOOLS_MCP_JSON =
            """
            {
              "mcpServers": {
                "chrome-devtools": {
                  "command": "npx",
                  "args": ["-y", "chrome-devtools-mcp@latest", "--isolated=true"]
                }
              }
            }
            """;

    public static void main(String[] args) {
        if (args.length > 0 && "--version".equals(args[0])) {
            System.out.println("bit cli " + VERSION);
            return;
        }

        if (args.length > 0 && "--init-config".equals(args[0])) {
            McpConfigBootstrapResult result = ensureDefaultMcpConfig(Path.of(System.getProperty("user.home")));
            System.out.println(result.message());
            return;
        }

        PaiCliConfig config = PaiCliConfig.load();
        System.out.println("bit cli core cli ready");
        System.out.println("default provider: " + config.getDefaultProvider());
        System.out.println("tip: run with --init-config to create the default MCP config");
    }

    static McpConfigBootstrapResult ensureDefaultMcpConfig(Path userHome) {
        Path configFile = userHome.resolve(".paicli").resolve("mcp.json");
        try {
            if (Files.notExists(configFile)) {
                Files.createDirectories(configFile.getParent());
                Files.writeString(configFile, DEFAULT_CHROME_DEVTOOLS_MCP_JSON);
                return new McpConfigBootstrapResult(true, "已创建默认 chrome-devtools MCP 配置");
            }
            String content = Files.readString(configFile);
            if (!content.contains("\"chrome-devtools\"")) {
                return new McpConfigBootstrapResult(false, "检测到现有 mcp.json 未配置 chrome-devtools");
            }
            return new McpConfigBootstrapResult(false, "");
        } catch (IOException e) {
            throw new IllegalStateException("初始化默认 MCP 配置失败: " + e.getMessage(), e);
        }
    }

    static void configureHistory(LineReader lineReader, Path homeDir) {
        if (lineReader == null) {
            return;
        }
        Path historyFile = resolveHistoryFile(homeDir);
        try {
            Files.createDirectories(historyFile.getParent());
            lineReader.setVariable(LineReader.HISTORY_FILE, historyFile);
            lineReader.setVariable(LineReader.HISTORY_SIZE, historySize());
            lineReader.setVariable(LineReader.HISTORY_FILE_SIZE, historyFileSize());
            lineReader.setOpt(LineReader.Option.HISTORY_IGNORE_SPACE);
            lineReader.setOpt(LineReader.Option.HISTORY_IGNORE_DUPS);
            lineReader.setOpt(LineReader.Option.HISTORY_REDUCE_BLANKS);
            lineReader.setOpt(LineReader.Option.DISABLE_EVENT_EXPANSION);
            lineReader.getHistory().load();
        } catch (IOException ignored) {
        }
    }

    static Path resolveHistoryFile(Path homeDir) {
        String configured = firstNonBlank(System.getProperty(HISTORY_FILE_PROPERTY), System.getenv("PAICLI_HISTORY_FILE"));
        if (configured != null) {
            return normalizeHistoryFile(Path.of(configured));
        }
        Path base = homeDir == null ? Path.of(System.getProperty("user.home")) : homeDir;
        return base.resolve(".paicli").resolve("history").resolve(DEFAULT_HISTORY_FILE_NAME)
                .toAbsolutePath().normalize();
    }

    static Path normalizeHistoryFile(Path configured) {
        Path path = configured.toAbsolutePath().normalize();
        if (Files.isDirectory(path)) {
            return path.resolve(DEFAULT_HISTORY_FILE_NAME).toAbsolutePath().normalize();
        }
        return path;
    }

    static void clearLineReaderHistory(LineReader lineReader) {
        if (lineReader == null || lineReader.getHistory() == null) {
            return;
        }
        try {
            lineReader.getHistory().purge();
        } catch (IOException ignored) {
        }
    }

    private static int historySize() {
        return configuredPositiveInt(HISTORY_SIZE_PROPERTY, "PAICLI_HISTORY_SIZE", 2_000);
    }

    private static int historyFileSize() {
        return configuredPositiveInt(HISTORY_FILE_SIZE_PROPERTY, "PAICLI_HISTORY_FILE_SIZE", 10_000);
    }

    private static int configuredPositiveInt(String property, String env, int fallback) {
        String raw = firstNonBlank(System.getProperty(property), System.getenv(env));
        if (raw == null) {
            return fallback;
        }
        try {
            int value = Integer.parseInt(raw.trim());
            return value > 0 ? value : fallback;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }

    record McpConfigBootstrapResult(boolean created, String message) {
    }
}
