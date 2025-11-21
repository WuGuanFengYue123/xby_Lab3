# DesignPatternLab
For course: *Advanced Software Development Techniques 25*

# Team20 Text Editor

> Command-line modular text editor | Design Patterns Practice Project  
> **Team**: Team20 | **Maintainer**: @team20 | **Updated**: 2025-11-21

---

## 📋 Lab1 功能概述

Lab1 实现了一个**基于命令行的文本编辑器**，支持以下核心功能：

- **工作区管理**：支持同时打开多个文本文件，管理活动文件和编辑器状态
- **文本编辑**：基本的文本编辑操作（追加、插入、删除、替换、显示）
- **撤销/重做**：支持编辑操作的撤销和重做
- **日志记录**：可选的命令执行日志，支持 `.filename.log` 格式
- **状态持久化**：工作区状态自动保存到 `.workspace.state`，下次启动时恢复

---

## 🚀 快速开始

### 前置要求
- Java 17 或更高版本
- Maven 3.6 或更高版本

### 1️⃣ 克隆项目
```bash
git clone https://github.com/zzk39/DesignPatternLab.git
cd DesignPatternLab
#请根据页面最下方脚本配置环境
```

### 2️⃣ 编译项目
```bash
# Linux/macOS/WSL
./mvnw clean package

# Windows
mvnw.cmd clean package
```

### 3️⃣ 运行程序
```bash
# Linux/macOS/WSL
./build.sh run

# Windows
build.bat run
```

或者直接使用 Maven：
```bash
mvn exec:java -pl core -Dexec.mainClass="com.team20.editor.Main"
```

### 4️⃣ 运行测试
```bash
# 运行所有测试
mvn test

# 运行特定模块的测试
mvn test -pl core
mvn test -pl plugins/core-impl
```

---

## 📝 命令速查表

### 工作区命令
| 命令 | 功能 | 示例 |
|------|------|------|
| `load <file>` | 加载文件 | `load test.txt` |
| `save [file\|all]` | 保存文件 | `save` / `save test.txt` / `save all` |
| `init <file> [with-log]` | 创建新缓冲区 | `init new.txt` / `init log.txt with-log` |
| `close [file]` | 关闭文件 | `close` / `close test.txt` |
| `edit <file>` | 切换活动文件 | `edit test.txt` |
| `editor-list` | 显示文件列表 | `editor-list` |
| `dir-tree [path]` | 显示目录树 | `dir-tree` / `dir-tree src` |
| `undo` | 撤销 | `undo` |
| `redo` | 重做 | `redo` |
| `exit` | 退出程序 | `exit` |

### 文本编辑命令

| 命令 | 功能 | 示例 |
|------|------|------|
| `append "text"` | 追加文本 | `append "Hello World"` |
| `insert <line:col> "text"` | 插入文本 | `insert 1:1 "Hello"` |
| `delete <line:col> <len>` | 删除字符 | `delete 1:1 5` |
| `replace <line:col> <len> "text"` | 替换文本 | `replace 1:1 5 "Hi"` |
| `show [start:end]` | 显示内容 | `show` / `show 1:10` |

### 日志命令

| 命令 | 功能 | 示例 |
|------|------|------|
| `log-on [file]` | 启用日志 | `log-on` / `log-on test.txt` |
| `log-off [file]` | 关闭日志 | `log-off` |
| `log-show [file]` | 显示日志 | `log-show` |

---

## 💡 使用示例

### 示例 1: 创建并编辑文件
```bash
> init hello.txt
Created new buffer: hello.txt

> append "Hello, World!"
Appended line.

> append "Welcome to Team20 Editor"
Appended line.

> show
1: Hello, World!
2: Welcome to Team20 Editor

> save
Saved: hello.txt
```

### 示例 2: 启用日志记录
```bash
> init log-test.txt with-log
Created new buffer: log-test.txt (logging enabled)

> append "First line"
Appended line.

> save
Saved: log-test.txt

> log-show
session start at 20251121 18:05:00
20251121 18:05:05 append "First line"
20251121 18:05:10 save
```

### 示例 3: 多文件编辑
```bash
> load file1.txt
Loaded: file1.txt

> load file2.txt
Loaded: file2.txt

> editor-list
* file2.txt
  file1.txt

> edit file1.txt
Switched to: file1.txt

> editor-list
* file1.txt
  file2.txt
```

---

## 📂 文件存储位置

### 工作区状态
- **文件**: `.workspace.state`
- **位置**: 程序运行目录
- **内容**: 打开的文件列表、当前活动文件、日志开关状态等
- **格式**: JSON

### 日志文件
- **文件**: `.filename.log` (例如 `.test.txt.log`)
- **位置**: 程序运行目录
- **内容**: 命令执行历史和时间戳
- **格式**: 纯文本，每行一条命令记录

### 遗留标记迁移
- 旧版本使用 `.filename.log.enabled` 文件标记日志开关
- Lab1 启动时自动迁移到工作区状态，并删除旧标记文件

---

## 🏗️ 项目结构

```
DesignPatternLab/
├── core/                           # 核心模块
│   └── src/
│       ├── main/java/com/team20/editor/
│       │   ├── bootstrap/          # 应用程序引导和上下文
│       │   ├── domain/             # 领域模型
│       │   │   ├── command/        # 命令模式实现
│       │   │   ├── editor/         # 编辑器接口和实现
│       │   │   └── workspace/      # 工作区管理
│       │   ├── extension/          # 扩展点和 SPI
│       │   ├── infrastructure/     # 基础设施（事件、持久化）
│       │   ├── monitoring/         # 日志和监控
│       │   └── Main.java           # 程序入口
│       └── test/                   # 单元测试
├── plugins/                        # 插件模块
│   ├── core-impl/                  # 核心命令实现
│   └── cli-jline/                  # JLine CLI 插件
├── README.md                       # 本文档
├── pom.xml                         # Maven 项目配置
└── build.sh / build.bat           # 构建脚本
```

---

## 🧪 测试

项目使用 JUnit 5 进行单元测试和集成测试。

### 运行测试
```bash
# 运行所有测试
mvn test

# 运行指定模块测试
mvn test -pl core
mvn test -pl plugins/core-impl

# 运行特定测试类
mvn test -Dtest=WorkspaceTest

# 查看测试覆盖率
mvn test jacoco:report
```

### 测试结构
- `core/src/test/` - 核心模块测试
- `plugins/*/src/test/` - 插件模块测试

---

## 🔧 开发环境设置

### 方法 1: Docker + VS Code（推荐）

1. 安装 Docker 和 VS Code
2. 安装 VS Code 扩展：Dev Containers
3. 打开项目文件夹
4. 选择 "Reopen in Container"
5. 容器会自动配置 Java 17 和 Maven

### 方法 2: 本地环境

#### Linux/macOS/WSL
```bash
# 配置环境
./setup_env.sh

# 验证环境
./verify_env.sh

# 运行程序
./build.sh run
```

#### Windows
```cmd
REM 配置环境
setup_env.bat

REM 验证环境
verify_env.bat

REM 运行程序
build.bat run
```

---

## 📚 设计模式应用

Lab1 应用了以下设计模式：

1. **命令模式 (Command Pattern)**: 实现可撤销/重做的编辑操作
2. **备忘录模式 (Memento Pattern)**: 工作区状态的持久化和恢复
3. **观察者模式 (Observer Pattern)**: 事件系统和日志监听
4. **工厂模式 (Factory Pattern)**: 编辑器和命令的创建
5. **单例模式 (Singleton Pattern)**: 命令注册表和应用程序上下文
6. **策略模式 (Strategy Pattern)**: 序列化和日志输出策略
7. **适配器模式 (Adapter Pattern)**: 树形视图适配器

---

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 📄 许可证

本项目仅供课程学习使用。

---

## 📞 联系方式

- **项目仓库**: https://github.com/zzk39/DesignPatternLab
- **课程**: Advanced Software Development Techniques 25
- **团队**: Team20
