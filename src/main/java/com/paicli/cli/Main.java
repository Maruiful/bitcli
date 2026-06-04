package com.paicli.cli;

import com.paicli.config.BitCliConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    private static final String VERSION = "0.1.0";
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

        BitCliConfig config = BitCliConfig.load();
        System.out.println("bit cli skeleton ready");
        System.out.println("default provider: " + config.getDefaultProvider());
        System.out.println("tip: run with --init-config to create the default MCP config");
    }

    static McpConfigBootstrapResult ensureDefaultMcpConfig(Path homeDir) {
        Path configPath = homeDir.resolve(".bitcli").resolve("mcp.json");
        if (Files.exists(configPath)) {
            return new McpConfigBootstrapResult(false, "未检测到 chrome-devtools 缺省配置写入需求，保留现有 mcp.json");
        }

        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, DEFAULT_CHROME_DEVTOOLS_MCP_JSON);
            return new McpConfigBootstrapResult(true, "已创建默认 chrome-devtools MCP 配置");
        } catch (IOException e) {
            throw new IllegalStateException("初始化默认 MCP 配置失败: " + e.getMessage(), e);
        }
    }

    record McpConfigBootstrapResult(boolean created, String message) {
    }
}
