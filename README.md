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
├── .devcontainer/
│   ├── devcontainer.json
│   ├── Dockerfile
│   ├── launch.json
│   └── tasks.json
├── docs/
│   ├── api/
│   ├── design/
│   └── user-guide/
├── .editorconfig
├── .gitignore
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
├── mvnw
├── mvnw.cmd
├── pom.xml
├── README.md
├── scripts/
├── setup_env.bat
├── setup_env.sh
├── setup_project.sh
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── team20/
│   │   │           └── editor/
│   │   │               ├── Main.java
│   │   │               ├── core/
│   │   │               │   ├── command/
│   │   │               │   │   ├── Command.java
│   │   │               │   │   ├── CommandInvoker.java
│   │   │               │   │   ├── UndoableCommand.java
│   │   │               │   │   └── impl/
│   │   │               │   │       ├── logging/
│   │   │               │   │       │   ├── LogOffCommand.java
│   │   │               │   │       │   ├── LogOnCommand.java
│   │   │               │   │       │   └── LogShowCommand.java
│   │   │               │   │       ├── text/
│   │   │               │   │       │   ├── AppendCommand.java
│   │   │               │   │       │   ├── DeleteCommand.java
│   │   │               │   │       │   ├── InsertCommand.java
│   │   │               │   │       │   ├── ReplaceCommand.java
│   │   │               │   │       │   └── ShowCommand.java
│   │   │               │   │       └── workspace/
│   │   │               │   │           ├── CloseCommand.java
│   │   │               │   │           ├── EditCommand.java
│   │   │               │   │           ├── EditorListCommand.java
│   │   │               │   │           ├── LoadCommand.java
│   │   │               │   │           └── SaveCommand.java
│   │   │               │   ├── editor/
│   │   │               │   │   ├── AbstractEditor.java
│   │   │               │   │   ├── Editor.java
│   │   │               │   │   └── text/
│   │   │               │   │       ├── README.md
│   │   │               │   │       └── TextEditor.java
│   │   │               │   ├── event/
│   │   │               │   │   ├── CommandEvent.java
│   │   │               │   │   ├── Event.java
│   │   │               │   │   ├── EventListener.java
│   │   │               │   │   ├── EventPublisher.java
│   │   │               │   │   └── WorkspaceEvent.java
│   │   │               │   ├── workspace/
│   │   │               │   │   ├── README.md
│   │   │               │   │   ├── Workspace.java
│   │   │               │   │   ├── WorkspaceMemento.java
│   │   │               │   │   └── WorkspaceState.java
│   │   │               ├── logging/
│   │   │               │   ├── Logger.java
│   │   │               │   └── LogListener.java
│   │   │               ├── persistence/
│   │   │               │   ├── PersistenceManager.java
│   │   │               │   └── Serializer.java
│   │   │               ├── registry/
│   │   │               │   ├── CommandRegistry.java
│   │   │               │   ├── EditorFactory.java
│   │   │               │   ├── EditorRegistry.java
│   │   │               │   └── TextEditorFactory.java
│   │   │               ├── ui/
│   │   │               │   ├── CommandLineInterface.java
│   │   │               │   ├── CommandParser.java
│   │   │               │   └── OutputFormatter.java
│   │   │               └── util/
│   │   │                   ├── FileUtil.java
│   │   │                   ├── StringUtil.java
│   │   │                   └── ValidationUtil.java
│   │   └── resources/
│   │       ├── config/
│   │       └── templates/
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── team20/
│       │           └── editor/
│       │               ├── MainTest.java
│       │               └── core/ editor/ event/ workspace/ … (测试包结构同 main)
│       └── resources/
├── target/                      # 构建输出（已被 .gitignore 忽略）
├── verify_env.bat
├── verify_env.sh
├── 设计模式_lab1.md
└── 设计模式Lab说明.md
```

---
