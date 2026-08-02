# Bit CLI

Bit CLI 是面向编码工作流的 Java Agent CLI，当前仓库已经包含核心命令行能力、Agent 与规划流程、Memory / RAG、MCP、Browser、Renderer、Runtime、Skill 与微信通道等主要模块。

## 环境要求

- JDK 17
- Maven 3.9+

## 本地运行

```bash
mvn package
java -jar target/bitcli-1.0-SNAPSHOT.jar
java -jar target/bitcli-1.0-SNAPSHOT.jar --version
java -jar target/bitcli-1.0-SNAPSHOT.jar --init-config
java -jar target/bitcli-1.0-SNAPSHOT.jar runtime serve
```

## 当前实现范围

- CLI 主入口、配置加载与历史记录
- Agent、Plan and Execute 与任务编排
- Memory、RAG 与项目上下文能力
- MCP 管理、资源读取与工具调用链路
- Browser 集成、Prompt 组装与 Runtime API
- Inline Renderer、TUI、Skill、Snapshot 与扩展通道

## 命名说明

`Bit CLI` 用作用户可见名称；现有包名、类名、文件名、目录名以及部分内部接口标识保持当前结构不变。
