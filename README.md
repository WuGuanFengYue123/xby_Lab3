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
│   ├── classes
│   │   ├── com
│   │   │   └── team20
│   │   │       └── editor
│   │   │           ├── bootstrap
│   │   │           │   └── ApplicationContext.class
│   │   │           ├── domain
│   │   │           │   ├── command
│   │   │           │   │   ├── Command.class
│   │   │           │   │   ├── CommandDescriptor.class
│   │   │           │   │   ├── CommandInvoker.class
│   │   │           │   │   ├── impl
│   │   │           │   │   │   ├── logging
│   │   │           │   │   │   │   ├── LoggingCommandProvider.class
│   │   │           │   │   │   │   ├── LogOffCommand.class
│   │   │           │   │   │   │   ├── LogOnCommand.class
│   │   │           │   │   │   │   └── LogShowCommand.class
│   │   │           │   │   │   ├── text
│   │   │           │   │   │   │   ├── AppendCommand.class
│   │   │           │   │   │   │   ├── DeleteCommand.class
│   │   │           │   │   │   │   ├── InsertCommand.class
│   │   │           │   │   │   │   ├── ReplaceCommand.class
│   │   │           │   │   │   │   └── ShowCommand.class
│   │   │           │   │   │   └── workspace
│   │   │           │   │   │       ├── CloseCommand.class
│   │   │           │   │   │       ├── EditCommand.class
│   │   │           │   │   │       ├── EditorListCommand.class
│   │   │           │   │   │       ├── LoadCommand.class
│   │   │           │   │   │       └── SaveCommand.class
│   │   │           │   │   ├── registry
│   │   │           │   │   │   └── AutoLoadingCommandRegistry.class
│   │   │           │   │   └── UndoableCommand.class
│   │   │           │   ├── editor
│   │   │           │   │   ├── AbstractEditor.class
│   │   │           │   │   ├── Editor.class
│   │   │           │   │   └── text
│   │   │           │   │       ├── TextEditor.class
│   │   │           │   │       ├── TextEditorProvider$1.class
│   │   │           │   │       └── TextEditorProvider.class
│   │   │           │   └── workspace
│   │   │           │       ├── Workspace.class
│   │   │           │       ├── WorkspaceMemento.class
│   │   │           │       └── WorkspaceState.class
│   │   │           ├── extension
│   │   │           │   ├── registry
│   │   │           │   │   ├── AutoLoadingEditorRegistry.class
│   │   │           │   │   ├── CommandRegistry.class
│   │   │           │   │   ├── EditorFactory.class
│   │   │           │   │   ├── EditorRegistry.class
│   │   │           │   │   └── TextEditorFactory.class
│   │   │           │   └── spi
│   │   │           │       ├── command
│   │   │           │       │   ├── CommandDescriptor.class
│   │   │           │       │   └── CommandProvider.class
│   │   │           │       ├── editor
│   │   │           │       │   ├── EditorProvider$EditorRegistration$Factory.class
│   │   │           │       │   ├── EditorProvider$EditorRegistration.class
│   │   │           │       │   └── EditorProvider.class
│   │   │           │       ├── node
│   │   │           │       │   └── NodeAdapterProvider.class
│   │   │           │       └── serialization
│   │   │           │           └── SerializerProvider.class
│   │   │           ├── infrastructure
│   │   │           │   ├── event
│   │   │           │   │   ├── CommandEvent.class
│   │   │           │   │   ├── EventBus.class
│   │   │           │   │   ├── Event.class
│   │   │           │   │   ├── EventListener.class
│   │   │           │   │   ├── EventPublisher.class
│   │   │           │   │   ├── SimpleEventBus.class
│   │   │           │   │   └── WorkspaceEvent.class
│   │   │           │   └── persistence
│   │   │           │       ├── DefaultSerializerProvider.class
│   │   │           │       ├── JsonSerializer.class
│   │   │           │       ├── PersistenceManager.class
│   │   │           │       └── Serializer.class
│   │   │           ├── Main$1.class
│   │   │           ├── Main.class
│   │   │           ├── monitoring
│   │   │           │   └── logging
│   │   │           │       ├── ConsoleLogSink.class
│   │   │           │       ├── FileLogSink.class
│   │   │           │       ├── Logger.class
│   │   │           │       ├── LogListener.class
│   │   │           │       ├── LogSink$LogLevel.class
│   │   │           │       └── LogSink.class
│   │   │           ├── representation
│   │   │           │   ├── tree
│   │   │           │   │   ├── AbstractNodeAdapter.class
│   │   │           │   │   ├── adapters
│   │   │           │   │   │   ├── CommandTypeNodeAdapter.class
│   │   │           │   │   │   ├── EditorNodeAdapter.class
│   │   │           │   │   │   ├── RootNodeAdapter.class
│   │   │           │   │   │   └── WorkspaceNodeAdapter.class
│   │   │           │   │   ├── NodeAdapterFactory$NodeAdaptContext.class
│   │   │           │   │   ├── NodeAdapterFactory.class
│   │   │           │   │   ├── Node.class
│   │   │           │   │   ├── NodeTreeBuilder.class
│   │   │           │   │   ├── NodeVisitor.class
│   │   │           │   │   └── providers
│   │   │           │   │       ├── CoreNodeAdapterProvider$1.class
│   │   │           │   │       ├── CoreNodeAdapterProvider$2.class
│   │   │           │   │       ├── CoreNodeAdapterProvider$3$1.class
│   │   │           │   │       ├── CoreNodeAdapterProvider$3.class
│   │   │           │   │       └── CoreNodeAdapterProvider.class
│   │   │           │   └── ui
│   │   │           │       ├── CommandLineInterface.class
│   │   │           │       ├── CommandParser$ParsedCommand.class
│   │   │           │       ├── CommandParser.class
│   │   │           │       └── OutputFormatter.class
│   │   │           └── util
│   │   │               ├── FileUtil.class
│   │   │               ├── StringUtil.class
│   │   │               └── ValidationUtil.class
│   │   └── META-INF
│   │       └── services
│   │           ├── com.team20.editor.extension.spi.command.CommandProvider
│   │           ├── com.team20.editor.extension.spi.editor.EditorProvider
│   │           ├── com.team20.editor.extension.spi.node.NodeAdapterProvider
│   │           └── com.team20.editor.extension.spi.serialization.SerializerProvider
│   ├── generated-sources
│   │   └── annotations
│   └── maven-status
│       └── maven-compiler-plugin
│           └── compile
│               └── default-compile
│                   ├── createdFiles.lst
│                   └── inputFiles.lst
├── verify_env.bat
├── verify_env.sh
├── 设计模式_lab1.md
└── 设计模式Lab说明.md
```

---
