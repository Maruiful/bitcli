# Bit CLI

Bit CLI 是这个新仓库当前对外展示的产品名称。

当前第一步已完成这些基础内容：

- Maven 项目结构与可执行主入口
- 本地配置文件读写
- 默认 `chrome-devtools` MCP 配置初始化
- `.env.example` 与基础忽略规则

## 环境要求

- JDK 17
- Maven 3.9+

## 本地运行

```bash
mvn test
mvn package
java -jar target/bitcli-1.0-SNAPSHOT.jar
java -jar target/bitcli-1.0-SNAPSHOT.jar --version
java -jar target/bitcli-1.0-SNAPSHOT.jar --init-config
```

## 当前阶段说明

这一阶段已完成新仓库的启动骨架，Agent、Plan、Memory、RAG、MCP 管理器等主能力模块暂未实现。

按照当前命名规则，`Bit CLI` 作为用户可见名称使用；包名、类名、文件名等内部标识暂不在这一阶段统一改名。
