# DesignPatternLab
For course: *Advanced Software Development Techniques 25*

# Team20 文本编辑器

> 基于命令行的模块化文本编辑器 | 设计模式实践项目  
> **团队**: Team20 | **维护者**: @team20 | **更新**: 2025-11-05

---

## 🚀 快速开始（3 步）

### 所有平台通用
方法 1: Docker + VS Code（推荐，零配置）

方法 2: 本地环境 macos/linux/wsl/windows





# 方法 1: Docker + VS Code（推荐，零配置）

### 1️⃣ 克隆项目
git clone https://github.com/zzk39/design_pattern_lab.git

cd design_pattern_lab

### 2️⃣ 用 VS Code 打开
### 3️⃣ 在弹出提示中点击 "Reopen in Container"
### 或按 F1 → 输入 "Reopen in Container"
#### ✅ 自动完成！容器会自动：
####   - 安装 Java 17
####   - 配置 Maven
####   - 编译项目
####   - 运行验证

### 4️⃣ 配置环境（自动安装配置）
./setup_env.sh

### 5️⃣ 验证环境（容器内自动打开终端）
./verify_env.sh

### 6️⃣ 运行程序
./build.sh run


# 方法 2: 本地环境 macos/linux/wsl/windows（3 步）

### 1️⃣ 克隆项目
git clone https://github.com/zzk39/design_pattern_lab.git

cd design_pattern_lab

### 2️⃣ 配置环境（自动安装配置）
./setup_env.sh      # Linux/macOS/WSL

setup_env.bat       # Windows

# 3️⃣ 验证并运行
./verify_env.sh && ./build.sh run      # Linux/macOS/WSL

verify_env.bat && build.bat run        # Windows




## 📁 完整目录树

# 项目结构 (Tree 视图)

> 该视图以 Markdown 形式渲染，效果与 `tree` 命令一致，便于在 GitHub/VS Code 中直观查看。

```text
.
├── build.bat
├── build.sh
├── docs
│   ├── api
│   ├── design
│   │   └── ADAPTER_TREE.md
│   └── user-guide
├── mvnw
├── mvnw.cmd
├── pom.xml
├── README.md
├── scripts
├── setup_env.bat
├── setup_env.sh
├── setup_project_env.sh
├── setup_project.sh
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── team20
│   │   │           └── editor
│   │   │               ├── bootstrap
│   │   │               │   └── ApplicationContext.java
│   │   │               ├── domain
│   │   │               │   ├── command
│   │   │               │   │   ├── CommandDescriptor.java
│   │   │               │   │   ├── CommandInvoker.java
│   │   │               │   │   ├── Command.java
│   │   │               │   │   ├── impl
│   │   │               │   │   │   ├── logging
│   │   │               │   │   │   │   ├── LoggingCommandProvider.java
│   │   │               │   │   │   │   ├── LogOffCommand.java
│   │   │               │   │   │   │   ├── LogOnCommand.java
│   │   │               │   │   │   │   └── LogShowCommand.java
│   │   │               │   │   │   ├── text
│   │   │               │   │   │   │   ├── AppendCommand.java
│   │   │               │   │   │   │   ├── DeleteCommand.java
│   │   │               │   │   │   │   ├── InsertCommand.java
│   │   │               │   │   │   │   ├── ReplaceCommand.java
│   │   │               │   │   │   │   └── ShowCommand.java
│   │   │               │   │   │   └── workspace
│   │   │               │   │   │       ├── CloseCommand.java
│   │   │               │   │   │       ├── EditCommand.java
│   │   │               │   │   │       ├── EditorListCommand.java
│   │   │               │   │   │       ├── LoadCommand.java
│   │   │               │   │   │       └── SaveCommand.java
│   │   │               │   │   ├── README.md
│   │   │               │   │   ├── registry
│   │   │               │   │   │   └── AutoLoadingCommandRegistry.java
│   │   │               │   │   └── UndoableCommand.java
│   │   │               │   ├── editor
│   │   │               │   │   ├── AbstractEditor.java
│   │   │               │   │   ├── Editor.java
│   │   │               │   │   ├── README.md
│   │   │               │   │   └── text
│   │   │               │   │       ├── README.md
│   │   │               │   │       ├── TextEditor.java
│   │   │               │   │       └── TextEditorProvider.java
│   │   │               │   └── workspace
│   │   │               │       ├── README.md
│   │   │               │       ├── Workspace.java
│   │   │               │       ├── WorkspaceMemento.java
│   │   │               │       └── WorkspaceState.java
│   │   │               ├── extension
│   │   │               │   ├── registry
│   │   │               │   │   ├── AutoLoadingEditorRegistry.java
│   │   │               │   │   ├── CommandRegistry.java
│   │   │               │   │   ├── EditorFactory.java
│   │   │               │   │   ├── EditorRegistry.java
│   │   │               │   │   └── TextEditorFactory.java
│   │   │               │   └── spi
│   │   │               │       ├── command
│   │   │               │       │   ├── CommandDescriptor.java
│   │   │               │       │   └── CommandProvider.java
│   │   │               │       ├── editor
│   │   │               │       │   └── EditorProvider.java
│   │   │               │       ├── node
│   │   │               │       │   └── NodeAdapterProvider.java
│   │   │               │       └── serialization
│   │   │               │           └── SerializerProvider.java
│   │   │               ├── infrastructure
│   │   │               │   ├── event
│   │   │               │   │   ├── CommandEvent.java
│   │   │               │   │   ├── EventBus.java
│   │   │               │   │   ├── Event.java
│   │   │               │   │   ├── EventListener.java
│   │   │               │   │   ├── EventPublisher.java
│   │   │               │   │   ├── SimpleEventBus.java
│   │   │               │   │   └── WorkspaceEvent.java
│   │   │               │   └── persistence
│   │   │               │       ├── DefaultSerializerProvider.java
│   │   │               │       ├── JsonSerializer.java
│   │   │               │       ├── PersistenceManager.java
│   │   │               │       └── Serializer.java
│   │   │               ├── Main.java
│   │   │               ├── monitoring
│   │   │               │   └── logging
│   │   │               │       ├── ConsoleLogSink.java
│   │   │               │       ├── FileLogSink.java
│   │   │               │       ├── Logger.java
│   │   │               │       ├── LogListener.java
│   │   │               │       └── LogSink.java
│   │   │               ├── representation
│   │   │               │   ├── tree
│   │   │               │   │   ├── AbstractNodeAdapter.java
│   │   │               │   │   ├── adapters
│   │   │               │   │   │   ├── CommandTypeNodeAdapter.java
│   │   │               │   │   │   ├── EditorNodeAdapter.java
│   │   │               │   │   │   ├── RootNodeAdapter.java
│   │   │               │   │   │   └── WorkspaceNodeAdapter.java
│   │   │               │   │   ├── NodeAdapterFactory.java
│   │   │               │   │   ├── Node.java
│   │   │               │   │   ├── NodeTreeBuilder.java
│   │   │               │   │   ├── NodeVisitor.java
│   │   │               │   │   └── providers
│   │   │               │   │       └── CoreNodeAdapterProvider.java
│   │   │               │   └── ui
│   │   │               │       ├── CommandLineInterface.java
│   │   │               │       ├── CommandParser.java
│   │   │               │       └── OutputFormatter.java
│   │   │               └── util
│   │   │                   ├── FileUtil.java
│   │   │                   ├── StringUtil.java
│   │   │                   └── ValidationUtil.java
│   │   └── resources
│   │       ├── config
│   │       ├── META-INF
│   │       │   └── services
│   │       │       ├── com.team20.editor.extension.spi.command.CommandProvider
│   │       │       ├── com.team20.editor.extension.spi.editor.EditorProvider
│   │       │       ├── com.team20.editor.extension.spi.node.NodeAdapterProvider
│   │       │       └── com.team20.editor.extension.spi.serialization.SerializerProvider
│   │       └── templates
│   └── test
│       ├── java
│       │   └── com
│       │       └── team20
│       │           └── editor
│       │               ├── core
│       │               │   ├── command
│       │               │   │   └── impl
│       │               │   │       ├── logging
│       │               │   │       ├── text
│       │               │   │       └── workspace
│       │               │   ├── editor
│       │               │   │   └── text
│       │               │   ├── event
│       │               │   └── workspace
│       │               ├── logging
│       │               ├── MainTest.java
│       │               ├── persistence
│       │               ├── registry
│       │               ├── ui
│       │               └── util
│       └── resources
├── target
├── verify_env.bat
├── verify_env.sh
├── 设计模式_lab1.md
└── 设计模式Lab说明.md
```

---

项目各主要分支简要说明（可复制粘贴）

- build.bat / build.sh  
  平台构建脚本（Windows / 类 Unix）。用于在不同操作系统上一键构建与运行项目。

- docs/  
  项目文档：包含设计（design）、API 文档（api）与用户指南（user-guide），便于新成员阅读架构与使用说明。

  - design/ADAPTER_TREE.md  
    说明如何把域对象适配为统一的树形 Node（Adapter + Composite），便于 UI 展示与扩展。

- mvnw / mvnw.cmd & pom.xml  
  Maven wrapper 与构建配置，保证在任意环境用固定的 Maven 版本构建与打包。

- src/main/java/com/team20/editor/bootstrap/ApplicationContext.java  
  应用上下文与装配点：通过 ServiceLoader 加载 Provider、组装 CommandRegistry、EditorRegistry、EventBus、LogSink 与 Serializer 等运行时组件。

- src/main/java/com/team20/editor/domain/**  
  领域模型（命令 / 编辑器 / 工作区）与核心接口。业务逻辑应尽量放在 domain 层并保持稳定契约。

- src/main/java/com/team20/editor/extension/**  
  扩展点（registry / spi）实现：所有可插拔的 Provider 接口与注册中心，支持通过添加类与 META-INF/services 文件扩展系统而无需修改核心代码（符合 OCP）。

- src/main/java/com/team20/editor/infrastructure/**  
  基础设施实现：事件总线、持久化、序列化器等底层实现，domain 层通过接口依赖这些实现（符合 DIP）。

- src/main/java/com/team20/editor/monitoring/logging/**  
  日志/监控扩展点：LogSink、ConsoleLogSink、FileLogSink 与 LogListener（可订阅 EventBus 事件进行日志记录）。

- src/main/java/com/team20/editor/representation/tree/**  
  视图模型与适配器（Node、NodeAdapterFactory、NodeTreeBuilder、各类 Adapter），实现 Adapter + Composite，用于构建统一的树形表示以供 UI/CLI 展示。

- src/main/java/com/team20/editor/representation/ui/**  
  CLI 相关实现（命令行解析、输出格式化），将用户输入映射到 Command 并执行。

- src/main/resources/META-INF/services/**  
  ServiceLoader 的资源定义文件（文件名为接口完全限定名），用于在运行时自动发现并加载实现类（例如 CommandProvider、EditorProvider、SerializerProvider、NodeAdapterProvider）。