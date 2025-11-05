#!/bin/bash

##############################################
# Team20 Text Editor - Project Setup Script
# 创建完整项目结构 + Java 文件框架
##############################################

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
print_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
print_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
print_skip() { echo -e "${YELLOW}[SKIP]${NC} $1 (已存在)"; }

BASE_PACKAGE="com/team20/editor"
BASE_PACKAGE_DOT="com.team20.editor"

echo ""
echo "=========================================="
echo "  Team20 Text Editor - 项目结构创建"
echo "  包含 Java 文件框架"
echo "=========================================="
echo ""

##############################################
# 1. 创建目录
##############################################

print_info "创建目录结构..."
echo ""

# Main directories
mkdir -p src/main/java/${BASE_PACKAGE}/core/{workspace,event}
mkdir -p src/main/java/${BASE_PACKAGE}/core/editor/text
mkdir -p src/main/java/${BASE_PACKAGE}/core/command/impl/{workspace,text,logging}
mkdir -p src/main/java/${BASE_PACKAGE}/{logging,persistence,ui,util,registry}

# Test directories
mkdir -p src/test/java/${BASE_PACKAGE}/core/{workspace,event}
mkdir -p src/test/java/${BASE_PACKAGE}/core/editor/text
mkdir -p src/test/java/${BASE_PACKAGE}/core/command/impl/{workspace,text,logging}
mkdir -p src/test/java/${BASE_PACKAGE}/{logging,persistence,ui,util,registry}

# Resources
mkdir -p src/main/resources/{config,templates}
mkdir -p src/test/resources

# Docs and scripts
mkdir -p docs/{design,api,user-guide}
mkdir -p scripts

print_success "目录结构创建完成"
echo ""

##############################################
# Function: Create Java file if not exists
##############################################

create_java_file() {
    local filepath="$1"
    local content="$2"
    if [ ! -f "$filepath" ]; then
        echo "$content" > "$filepath"
        print_success "创建: $filepath"
    else
        print_skip "$filepath"
    fi
}

##############################################
# 2. 创建 Main.java
##############################################

print_info "创建程序入口..."
echo ""

create_java_file "src/main/java/${BASE_PACKAGE}/Main.java" "package ${BASE_PACKAGE_DOT};

/**
 * Team20 Text Editor - 程序入口
 * 
 * 职责：
 * - 初始化系统
 * - 注册编辑器工厂
 * - 注册命令
 * - 启动 CLI
 * 
 * @author Team20
 * @version 1.0.0
 */
public class Main {
    
    /**
     * 程序入口
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // TODO: 1. 初始化注册表
        // TODO: 2. 注册文本编辑器工厂
        // TODO: 3. 注册所有命令
        // TODO: 4. 创建工作区
        // TODO: 5. 恢复工作区状态
        // TODO: 6. 启动 CLI
        
        System.out.println(\"Team20 Text Editor v1.0.0\");
        System.out.println(\"Type 'help' for available commands\");
    }
}"

echo ""

##############################################
# 3. 创建 Workspace 模块
##############################################

print_info "创建 Workspace 模块..."
echo ""

create_java_file "src/main/java/${BASE_PACKAGE}/core/workspace/Workspace.java" "package ${BASE_PACKAGE_DOT}.core.workspace;

import ${BASE_PACKAGE_DOT}.core.editor.Editor;
import java.util.List;

/**
 * 工作区管理器
 * 
 * 职责：
 * - 管理多个编辑器实例
 * - 维护当前活动编辑器
 * - 跟踪文件修改状态
 * - 发布工作区事件
 * 
 * 设计模式：
 * - Memento Pattern（状态持久化）
 */
public class Workspace {
    
    // TODO: 实现字段
    // - 编辑器列表
    // - 当前活动编辑器
    // - 日志开关映射
    
    /**
     * 构造函数
     */
    public Workspace() {
        // TODO: 初始化
    }
    
    /**
     * 加载文件
     * 
     * @param filePath 文件路径
     * @return 编辑器实例
     */
    public Editor loadFile(String filePath) {
        // TODO: 实现
        return null;
    }
    
    /**
     * 保存文件
     * 
     * @param filePath 文件路径（null 表示当前活动文件）
     */
    public void saveFile(String filePath) {
        // TODO: 实现
    }
    
    /**
     * 保存所有文件
     */
    public void saveAll() {
        // TODO: 实现
    }
    
    /**
     * 关闭文件
     * 
     * @param filePath 文件路径（null 表示当前活动文件）
     */
    public void closeFile(String filePath) {
        // TODO: 实现
    }
    
    /**
     * 切换活动文件
     * 
     * @param filePath 文件路径
     */
    public void switchActiveFile(String filePath) {
        // TODO: 实现
    }
    
    /**
     * 获取当前活动编辑器
     * 
     * @return 活动编辑器
     */
    public Editor getActiveEditor() {
        // TODO: 实现
        return null;
    }
    
    /**
     * 获取所有打开的编辑器
     * 
     * @return 编辑器列表
     */
    public List<Editor> getAllEditors() {
        // TODO: 实现
        return null;
    }
    
    /**
     * 创建工作区备忘录
     * 
     * @return 备忘录对象
     */
    public WorkspaceMemento createMemento() {
        // TODO: 实现
        return null;
    }
    
    /**
     * 从备忘录恢复工作区
     * 
     * @param memento 备忘录对象
     */
    public void restoreFromMemento(WorkspaceMemento memento) {
        // TODO: 实现
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/workspace/WorkspaceState.java" "package ${BASE_PACKAGE_DOT}.core.workspace;

import java.util.List;

/**
 * 工作区状态数据类
 * 
 * 用于序列化和反序列化工作区状态
 */
public class WorkspaceState {
    
    // TODO: 实现字段
    // - 打开的文件路径列表
    // - 当前活动文件路径
    // - 文件修改状态映射
    // - 日志开关映射
    
    /**
     * 构造函数
     */
    public WorkspaceState() {
        // TODO: 初始化
    }
    
    // TODO: Getter 和 Setter 方法
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/workspace/WorkspaceMemento.java" "package ${BASE_PACKAGE_DOT}.core.workspace;

/**
 * 工作区备忘录
 * 
 * 设计模式：Memento Pattern
 * 职责：保存和恢复工作区状态
 */
public class WorkspaceMemento {
    
    private final WorkspaceState state;
    
    /**
     * 构造函数
     * 
     * @param state 工作区状态
     */
    public WorkspaceMemento(WorkspaceState state) {
        this.state = state;
    }
    
    /**
     * 获取保存的状态
     * 
     * @return 工作区状态
     */
    public WorkspaceState getState() {
        return state;
    }
}"

echo ""

##############################################
# 4. 创建 Editor 模块
##############################################

print_info "创建 Editor 模块..."
echo ""

create_java_file "src/main/java/${BASE_PACKAGE}/core/editor/Editor.java" "package ${BASE_PACKAGE_DOT}.core.editor;

/**
 * 编辑器接口
 * 
 * 定义所有编辑器必须实现的基本操作
 */
public interface Editor {
    
    /**
     * 获取文件路径
     * 
     * @return 文件路径
     */
    String getFilePath();
    
    /**
     * 获取文件内容
     * 
     * @return 文件内容字符串
     */
    String getContent();
    
    /**
     * 加载文件内容
     */
    void load();
    
    /**
     * 保存文件内容
     */
    void save();
    
    /**
     * 检查文件是否已修改
     * 
     * @return true 如果已修改
     */
    boolean isModified();
    
    /**
     * 设置修改标记
     * 
     * @param modified 是否已修改
     */
    void setModified(boolean modified);
    
    /**
     * 撤销操作
     * 
     * @return true 如果撤销成功
     */
    boolean undo();
    
    /**
     * 重做操作
     * 
     * @return true 如果重做成功
     */
    boolean redo();
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/editor/AbstractEditor.java" "package ${BASE_PACKAGE_DOT}.core.editor;

/**
 * 编辑器抽象基类
 * 
 * 提供编辑器的通用实现
 */
public abstract class AbstractEditor implements Editor {
    
    protected String filePath;
    protected boolean modified;
    
    // TODO: undo/redo 历史栈
    
    /**
     * 构造函数
     * 
     * @param filePath 文件路径
     */
    public AbstractEditor(String filePath) {
        this.filePath = filePath;
        this.modified = false;
    }
    
    @Override
    public String getFilePath() {
        return filePath;
    }
    
    @Override
    public boolean isModified() {
        return modified;
    }
    
    @Override
    public void setModified(boolean modified) {
        this.modified = modified;
    }
    
    @Override
    public boolean undo() {
        // TODO: 实现通用 undo 逻辑
        return false;
    }
    
    @Override
    public boolean redo() {
        // TODO: 实现通用 redo 逻辑
        return false;
    }
    
    // 子类必须实现的抽象方法
    @Override
    public abstract String getContent();
    
    @Override
    public abstract void load();
    
    @Override
    public abstract void save();
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/editor/text/TextEditor.java" "package ${BASE_PACKAGE_DOT}.core.editor.text;

import ${BASE_PACKAGE_DOT}.core.editor.AbstractEditor;
import java.util.List;

/**
 * 文本编辑器实现
 * 
 * 职责：
 * - 管理文本内容（行数组）
 * - 实现基本编辑操作
 * - 支持 undo/redo
 * 
 * 数据结构：List<String>，每个元素是一行
 */
public class TextEditor extends AbstractEditor {
    
    private List<String> lines;
    
    /**
     * 构造函数
     * 
     * @param filePath 文件路径
     */
    public TextEditor(String filePath) {
        super(filePath);
        // TODO: 初始化 lines
    }
    
    @Override
    public String getContent() {
        // TODO: 将 lines 连接成字符串
        return null;
    }
    
    @Override
    public void load() {
        // TODO: 从文件加载内容到 lines
    }
    
    @Override
    public void save() {
        // TODO: 将 lines 保存到文件
    }
    
    /**
     * 追加文本到末尾
     * 
     * @param text 要追加的文本
     */
    public void append(String text) {
        // TODO: 实现
        setModified(true);
    }
    
    /**
     * 在指定位置插入文本
     * 
     * @param line 行号（从1开始）
     * @param col 列号（从1开始）
     * @param text 要插入的文本
     */
    public void insert(int line, int col, String text) {
        // TODO: 实现
        setModified(true);
    }
    
    /**
     * 删除指定位置的字符
     * 
     * @param line 行号（从1开始）
     * @param col 列号（从1开始）
     * @param length 删除长度
     */
    public void delete(int line, int col, int length) {
        // TODO: 实现
        setModified(true);
    }
    
    /**
     * 替换指定位置的字符
     * 
     * @param line 行号（从1开始）
     * @param col 列号（从1开始）
     * @param length 替换长度
     * @param text 新文本
     */
    public void replace(int line, int col, int length, String text) {
        // TODO: 实现
        setModified(true);
    }
    
    /**
     * 显示指定范围的内容
     * 
     * @param startLine 起始行（从1开始，0表示全部）
     * @param endLine 结束行（从1开始，0表示全部）
     * @return 格式化的文本内容
     */
    public String show(int startLine, int endLine) {
        // TODO: 实现
        return null;
    }
}"

echo ""

##############################################
# 5. 创建 Command 模块
##############################################

print_info "创建 Command 模块..."
echo ""

create_java_file "src/main/java/${BASE_PACKAGE}/core/command/Command.java" "package ${BASE_PACKAGE_DOT}.core.command;

/**
 * 命令接口
 * 
 * 设计模式：Command Pattern
 */
public interface Command {
    
    /**
     * 执行命令
     */
    void execute();
    
    /**
     * 获取命令名称
     * 
     * @return 命令名称
     */
    String getName();
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/command/UndoableCommand.java" "package ${BASE_PACKAGE_DOT}.core.command;

/**
 * 可撤销命令接口
 * 
 * 继承自 Command，增加 undo 功能
 */
public interface UndoableCommand extends Command {
    
    /**
     * 撤销命令
     */
    void undo();
    
    /**
     * 判断命令是否可撤销
     * 
     * @return true 如果可撤销
     */
    boolean isUndoable();
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/command/CommandInvoker.java" "package ${BASE_PACKAGE_DOT}.core.command;

import java.util.Stack;

/**
 * 命令调用器
 * 
 * 职责：
 * - 执行命令
 * - 管理 undo/redo 历史栈
 * - 发布命令事件
 */
public class CommandInvoker {
    
    private Stack<UndoableCommand> undoStack;
    private Stack<UndoableCommand> redoStack;
    
    /**
     * 构造函数
     */
    public CommandInvoker() {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }
    
    /**
     * 执行命令
     * 
     * @param command 要执行的命令
     */
    public void executeCommand(Command command) {
        // TODO: 实现
        // 1. 执行命令
        // 2. 如果是可撤销命令，压入 undoStack
        // 3. 清空 redoStack
        // 4. 发布命令执行事件
    }
    
    /**
     * 撤销上一个命令
     * 
     * @return true 如果撤销成功
     */
    public boolean undo() {
        // TODO: 实现
        return false;
    }
    
    /**
     * 重做上一个撤销的命令
     * 
     * @return true 如果重做成功
     */
    public boolean redo() {
        // TODO: 实现
        return false;
    }
    
    /**
     * 检查是否可以撤销
     * 
     * @return true 如果可以撤销
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    /**
     * 检查是否可以重做
     * 
     * @return true 如果可以重做
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
}"

# Workspace Commands
create_java_file "src/main/java/${BASE_PACKAGE}/core/command/impl/workspace/LoadCommand.java" "package ${BASE_PACKAGE_DOT}.core.command.impl.workspace;

import ${BASE_PACKAGE_DOT}.core.command.Command;
import ${BASE_PACKAGE_DOT}.core.workspace.Workspace;

/**
 * 加载文件命令
 */
public class LoadCommand implements Command {
    
    private Workspace workspace;
    private String filePath;
    
    public LoadCommand(Workspace workspace, String filePath) {
        this.workspace = workspace;
        this.filePath = filePath;
    }
    
    @Override
    public void execute() {
        // TODO: 实现加载逻辑
    }
    
    @Override
    public String getName() {
        return \"load\";
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/command/impl/workspace/SaveCommand.java" "package ${BASE_PACKAGE_DOT}.core.command.impl.workspace;

import ${BASE_PACKAGE_DOT}.core.command.Command;
import ${BASE_PACKAGE_DOT}.core.workspace.Workspace;

/**
 * 保存文件命令
 */
public class SaveCommand implements Command {
    
    private Workspace workspace;
    private String target; // file path or \"all\"
    
    public SaveCommand(Workspace workspace, String target) {
        this.workspace = workspace;
        this.target = target;
    }
    
    @Override
    public void execute() {
        // TODO: 实现保存逻辑
    }
    
    @Override
    public String getName() {
        return \"save\";
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/command/impl/workspace/CloseCommand.java" "package ${BASE_PACKAGE_DOT}.core.command.impl.workspace;

import ${BASE_PACKAGE_DOT}.core.command.Command;
import ${BASE_PACKAGE_DOT}.core.workspace.Workspace;

/**
 * 关闭文件命令
 */
public class CloseCommand implements Command {
    
    private Workspace workspace;
    private String filePath;
    
    public CloseCommand(Workspace workspace, String filePath) {
        this.workspace = workspace;
        this.filePath = filePath;
    }
    
    @Override
    public void execute() {
        // TODO: 实现关闭逻辑
    }
    
    @Override
    public String getName() {
        return \"close\";
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/command/impl/workspace/EditCommand.java" "package ${BASE_PACKAGE_DOT}.core.command.impl.workspace;

import ${BASE_PACKAGE_DOT}.core.command.Command;
import ${BASE_PACKAGE_DOT}.core.workspace.Workspace;

/**
 * 切换活动文件命令
 */
public class EditCommand implements Command {
    
    private Workspace workspace;
    private String filePath;
    
    public EditCommand(Workspace workspace, String filePath) {
        this.workspace = workspace;
        this.filePath = filePath;
    }
    
    @Override
    public void execute() {
        // TODO: 实现切换逻辑
    }
    
    @Override
    public String getName() {
        return \"edit\";
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/command/impl/workspace/EditorListCommand.java" "package ${BASE_PACKAGE_DOT}.core.command.impl.workspace;

import ${BASE_PACKAGE_DOT}.core.command.Command;
import ${BASE_PACKAGE_DOT}.core.workspace.Workspace;

/**
 * 显示编辑器列表命令
 */
public class EditorListCommand implements Command {
    
    private Workspace workspace;
    
    public EditorListCommand(Workspace workspace) {
        this.workspace = workspace;
    }
    
    @Override
    public void execute() {
        // TODO: 实现列表显示逻辑
    }
    
    @Override
    public String getName() {
        return \"editor-list\";
    }
}"

# Text Commands
create_java_file "src/main/java/${BASE_PACKAGE}/core/command/impl/text/AppendCommand.java" "package ${BASE_PACKAGE_DOT}.core.command.impl.text;

import ${BASE_PACKAGE_DOT}.core.command.UndoableCommand;
import ${BASE_PACKAGE_DOT}.core.editor.text.TextEditor;

/**
 * 追加文本命令
 */
public class AppendCommand implements UndoableCommand {
    
    private TextEditor editor;
    private String text;
    
    public AppendCommand(TextEditor editor, String text) {
        this.editor = editor;
        this.text = text;
    }
    
    @Override
    public void execute() {
        // TODO: 实现追加逻辑
    }
    
    @Override
    public void undo() {
        // TODO: 实现撤销逻辑
    }
    
    @Override
    public boolean isUndoable() {
        return true;
    }
    
    @Override
    public String getName() {
        return \"append\";
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/command/impl/text/InsertCommand.java" "package ${BASE_PACKAGE_DOT}.core.command.impl.text;

import ${BASE_PACKAGE_DOT}.core.command.UndoableCommand;
import ${BASE_PACKAGE_DOT}.core.editor.text.TextEditor;

/**
 * 插入文本命令
 */
public class InsertCommand implements UndoableCommand {
    
    private TextEditor editor;
    private int line;
    private int col;
    private String text;
    
    public InsertCommand(TextEditor editor, int line, int col, String text) {
        this.editor = editor;
        this.line = line;
        this.col = col;
        this.text = text;
    }
    
    @Override
    public void execute() {
        // TODO: 实现插入逻辑
    }
    
    @Override
    public void undo() {
        // TODO: 实现撤销逻辑
    }
    
    @Override
    public boolean isUndoable() {
        return true;
    }
    
    @Override
    public String getName() {
        return \"insert\";
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/command/impl/text/DeleteCommand.java" "package ${BASE_PACKAGE_DOT}.core.command.impl.text;

import ${BASE_PACKAGE_DOT}.core.command.UndoableCommand;
import ${BASE_PACKAGE_DOT}.core.editor.text.TextEditor;

/**
 * 删除文本命令
 */
public class DeleteCommand implements UndoableCommand {
    
    private TextEditor editor;
    private int line;
    private int col;
    private int length;
    private String deletedText; // 用于 undo
    
    public DeleteCommand(TextEditor editor, int line, int col, int length) {
        this.editor = editor;
        this.line = line;
        this.col = col;
        this.length = length;
    }
    
    @Override
    public void execute() {
        // TODO: 保存被删除的文本
        // TODO: 实现删除逻辑
    }
    
    @Override
    public void undo() {
        // TODO: 恢复被删除的文本
    }
    
    @Override
    public boolean isUndoable() {
        return true;
    }
    
    @Override
    public String getName() {
        return \"delete\";
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/command/impl/text/ReplaceCommand.java" "package ${BASE_PACKAGE_DOT}.core.command.impl.text;

import ${BASE_PACKAGE_DOT}.core.command.UndoableCommand;
import ${BASE_PACKAGE_DOT}.core.editor.text.TextEditor;

/**
 * 替换文本命令
 */
public class ReplaceCommand implements UndoableCommand {
    
    private TextEditor editor;
    private int line;
    private int col;
    private int length;
    private String newText;
    private String oldText; // 用于 undo
    
    public ReplaceCommand(TextEditor editor, int line, int col, int length, String newText) {
        this.editor = editor;
        this.line = line;
        this.col = col;
        this.length = length;
        this.newText = newText;
    }
    
    @Override
    public void execute() {
        // TODO: 保存旧文本
        // TODO: 实现替换逻辑
    }
    
    @Override
    public void undo() {
        // TODO: 恢复旧文本
    }
    
    @Override
    public boolean isUndoable() {
        return true;
    }
    
    @Override
    public String getName() {
        return \"replace\";
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/command/impl/text/ShowCommand.java" "package ${BASE_PACKAGE_DOT}.core.command.impl.text;

import ${BASE_PACKAGE_DOT}.core.command.Command;
import ${BASE_PACKAGE_DOT}.core.editor.text.TextEditor;

/**
 * 显示文本内容命令
 */
public class ShowCommand implements Command {
    
    private TextEditor editor;
    private int startLine;
    private int endLine;
    
    public ShowCommand(TextEditor editor, int startLine, int endLine) {
        this.editor = editor;
        this.startLine = startLine;
        this.endLine = endLine;
    }
    
    @Override
    public void execute() {
        // TODO: 实现显示逻辑
    }
    
    @Override
    public String getName() {
        return \"show\";
    }
}"

# Logging Commands
create_java_file "src/main/java/${BASE_PACKAGE}/core/command/impl/logging/LogOnCommand.java" "package ${BASE_PACKAGE_DOT}.core.command.impl.logging;

import ${BASE_PACKAGE_DOT}.core.command.Command;
import ${BASE_PACKAGE_DOT}.core.workspace.Workspace;

/**
 * 启用日志命令
 */
public class LogOnCommand implements Command {
    
    private Workspace workspace;
    private String filePath;
    
    public LogOnCommand(Workspace workspace, String filePath) {
        this.workspace = workspace;
        this.filePath = filePath;
    }
    
    @Override
    public void execute() {
        // TODO: 实现启用日志逻辑
    }
    
    @Override
    public String getName() {
        return \"log-on\";
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/command/impl/logging/LogOffCommand.java" "package ${BASE_PACKAGE_DOT}.core.command.impl.logging;

import ${BASE_PACKAGE_DOT}.core.command.Command;
import ${BASE_PACKAGE_DOT}.core.workspace.Workspace;

/**
 * 关闭日志命令
 */
public class LogOffCommand implements Command {
    
    private Workspace workspace;
    private String filePath;
    
    public LogOffCommand(Workspace workspace, String filePath) {
        this.workspace = workspace;
        this.filePath = filePath;
    }
    
    @Override
    public void execute() {
        // TODO: 实现关闭日志逻辑
    }
    
    @Override
    public String getName() {
        return \"log-off\";
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/command/impl/logging/LogShowCommand.java" "package ${BASE_PACKAGE_DOT}.core.command.impl.logging;

import ${BASE_PACKAGE_DOT}.core.command.Command;
import ${BASE_PACKAGE_DOT}.core.workspace.Workspace;

/**
 * 显示日志命令
 */
public class LogShowCommand implements Command {
    
    private Workspace workspace;
    private String filePath;
    
    public LogShowCommand(Workspace workspace, String filePath) {
        this.workspace = workspace;
        this.filePath = filePath;
    }
    
    @Override
    public void execute() {
        // TODO: 实现显示日志逻辑
    }
    
    @Override
    public String getName() {
        return \"log-show\";
    }
}"

echo ""

##############################################
# 6. 创建 Event 模块
##############################################

print_info "创建 Event 模块..."
echo ""

create_java_file "src/main/java/${BASE_PACKAGE}/core/event/Event.java" "package ${BASE_PACKAGE_DOT}.core.event;

import java.time.LocalDateTime;

/**
 * 事件基类
 * 
 * 设计模式：Observer Pattern
 */
public abstract class Event {
    
    private final LocalDateTime timestamp;
    private final String source;
    
    /**
     * 构造函数
     * 
     * @param source 事件源
     */
    public Event(String source) {
        this.timestamp = LocalDateTime.now();
        this.source = source;
    }
    
    /**
     * 获取事件时间戳
     * 
     * @return 时间戳
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * 获取事件源
     * 
     * @return 事件源
     */
    public String getSource() {
        return source;
    }
    
    /**
     * 获取事件类型
     * 
     * @return 事件类型字符串
     */
    public abstract String getEventType();
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/event/EventListener.java" "package ${BASE_PACKAGE_DOT}.core.event;

/**
 * 事件监听器接口
 */
public interface EventListener {
    
    /**
     * 处理事件
     * 
     * @param event 事件对象
     */
    void onEvent(Event event);
    
    /**
     * 获取监听器关注的事件类型
     * 
     * @return 事件类型的 Class 对象
     */
    Class<? extends Event> getEventType();
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/event/EventPublisher.java" "package ${BASE_PACKAGE_DOT}.core.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件发布器（单例）
 * 
 * 职责：
 * - 管理事件监听器
 * - 发布事件到相应监听器
 */
public class EventPublisher {
    
    private static EventPublisher instance;
    private Map<Class<? extends Event>, List<EventListener>> listeners;
    
    /**
     * 私有构造函数
     */
    private EventPublisher() {
        this.listeners = new HashMap<>();
    }
    
    /**
     * 获取单例实例
     * 
     * @return EventPublisher 实例
     */
    public static EventPublisher getInstance() {
        if (instance == null) {
            instance = new EventPublisher();
        }
        return instance;
    }
    
    /**
     * 注册事件监听器
     * 
     * @param listener 监听器
     */
    public void subscribe(EventListener listener) {
        // TODO: 实现注册逻辑
    }
    
    /**
     * 取消注册事件监听器
     * 
     * @param listener 监听器
     */
    public void unsubscribe(EventListener listener) {
        // TODO: 实现取消注册逻辑
    }
    
    /**
     * 发布事件
     * 
     * @param event 事件对象
     */
    public void publish(Event event) {
        // TODO: 实现事件发布逻辑
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/event/CommandEvent.java" "package ${BASE_PACKAGE_DOT}.core.event;

/**
 * 命令执行事件
 */
public class CommandEvent extends Event {
    
    private final String commandName;
    private final String commandArgs;
    
    public CommandEvent(String source, String commandName, String commandArgs) {
        super(source);
        this.commandName = commandName;
        this.commandArgs = commandArgs;
    }
    
    @Override
    public String getEventType() {
        return \"COMMAND_EXECUTED\";
    }
    
    public String getCommandName() {
        return commandName;
    }
    
    public String getCommandArgs() {
        return commandArgs;
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/core/event/WorkspaceEvent.java" "package ${BASE_PACKAGE_DOT}.core.event;

/**
 * 工作区事件
 */
public class WorkspaceEvent extends Event {
    
    private final String eventType;
    private final String filePath;
    
    public WorkspaceEvent(String source, String eventType, String filePath) {
        super(source);
        this.eventType = eventType;
        this.filePath = filePath;
    }
    
    @Override
    public String getEventType() {
        return eventType;
    }
    
    public String getFilePath() {
        return filePath;
    }
}"

echo ""

##############################################
# 7. 创建 Logging 模块
##############################################

print_info "创建 Logging 模块..."
echo ""

create_java_file "src/main/java/${BASE_PACKAGE}/logging/Logger.java" "package ${BASE_PACKAGE_DOT}.logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日志记录器
 * 
 * 职责：
 * - 将日志写入文件
 * - 管理日志会话
 */
public class Logger {
    
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern(\"yyyyMMdd HH:mm:ss\");
    
    private String logFilePath;
    private BufferedWriter writer;
    private boolean sessionStarted;
    
    /**
     * 构造函数
     * 
     * @param sourceFilePath 源文件路径
     */
    public Logger(String sourceFilePath) {
        // TODO: 生成日志文件路径 (.filename.log)
    }
    
    /**
     * 开始新的日志会话
     */
    public void startSession() {
        // TODO: 写入 session start 行
    }
    
    /**
     * 记录日志
     * 
     * @param message 日志消息
     */
    public void log(String message) {
        // TODO: 实现日志写入
    }
    
    /**
     * 关闭日志记录器
     */
    public void close() {
        // TODO: 关闭文件写入器
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/logging/LogListener.java" "package ${BASE_PACKAGE_DOT}.logging;

import ${BASE_PACKAGE_DOT}.core.event.Event;
import ${BASE_PACKAGE_DOT}.core.event.EventListener;
import ${BASE_PACKAGE_DOT}.core.event.CommandEvent;

/**
 * 日志事件监听器
 * 
 * 监听命令执行事件并记录日志
 */
public class LogListener implements EventListener {
    
    private Logger logger;
    
    /**
     * 构造函数
     * 
     * @param logger 日志记录器
     */
    public LogListener(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public void onEvent(Event event) {
        // TODO: 处理命令事件并记录日志
    }
    
    @Override
    public Class<? extends Event> getEventType() {
        return CommandEvent.class;
    }
}"

echo ""

##############################################
# 8. 创建 Persistence 模块
##############################################

print_info "创建 Persistence 模块..."
echo ""

create_java_file "src/main/java/${BASE_PACKAGE}/persistence/PersistenceManager.java" "package ${BASE_PACKAGE_DOT}.persistence;

import ${BASE_PACKAGE_DOT}.core.workspace.WorkspaceMemento;
import ${BASE_PACKAGE_DOT}.core.workspace.WorkspaceState;
import java.io.IOException;

/**
 * 持久化管理器
 * 
 * 职责：
 * - 保存工作区状态到文件
 * - 从文件恢复工作区状态
 */
public class PersistenceManager {
    
    private static final String STATE_FILE = \".workspace.state\";
    private Serializer serializer;
    
    /**
     * 构造函数
     */
    public PersistenceManager() {
        this.serializer = new Serializer();
    }
    
    /**
     * 保存工作区状态
     * 
     * @param memento 工作区备忘录
     * @throws IOException 如果保存失败
     */
    public void saveWorkspace(WorkspaceMemento memento) throws IOException {
        // TODO: 实现保存逻辑
        // 1. 获取 WorkspaceState
        // 2. 序列化为 JSON
        // 3. 写入文件
    }
    
    /**
     * 加载工作区状态
     * 
     * @return 工作区备忘录
     * @throws IOException 如果加载失败
     */
    public WorkspaceMemento loadWorkspace() throws IOException {
        // TODO: 实现加载逻辑
        // 1. 读取文件
        // 2. 反序列化为 WorkspaceState
        // 3. 创建并返回 WorkspaceMemento
        return null;
    }
    
    /**
     * 检查是否存在保存的工作区状态
     * 
     * @return true 如果存在
     */
    public boolean hasPersistedState() {
        // TODO: 实现检查逻辑
        return false;
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/persistence/Serializer.java" "package ${BASE_PACKAGE_DOT}.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 序列化工具类
 * 
 * 使用 Gson 进行 JSON 序列化
 */
public class Serializer {
    
    private Gson gson;
    
    /**
     * 构造函数
     */
    public Serializer() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }
    
    /**
     * 将对象序列化为 JSON 字符串
     * 
     * @param obj 对象
     * @return JSON 字符串
     */
    public String toJson(Object obj) {
        return gson.toJson(obj);
    }
    
    /**
     * 从 JSON 字符串反序列化对象
     * 
     * @param json JSON 字符串
     * @param clazz 类对象
     * @param <T> 类型参数
     * @return 反序列化后的对象
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}"

echo ""

##############################################
# 9. 创建 Registry 模块 (核心扩展点)
##############################################

print_info "创建 Registry 模块（核心扩展点）..."
echo ""

create_java_file "src/main/java/${BASE_PACKAGE}/registry/EditorFactory.java" "package ${BASE_PACKAGE_DOT}.registry;

import ${BASE_PACKAGE_DOT}.core.editor.Editor;

/**
 * 编辑器工厂接口
 */
public interface EditorFactory {
    
    /**
     * 创建编辑器实例
     * 
     * @param filePath 文件路径
     * @return 编辑器实例
     */
    Editor createEditor(String filePath);
    
    /**
     * 获取工厂支持的文件扩展名
     * 
     * @return 文件扩展名（如 \".txt\"）
     */
    String getSupportedExtension();
}"

create_java_file "src/main/java/${BASE_PACKAGE}/registry/EditorRegistry.java" "package ${BASE_PACKAGE_DOT}.registry;

import ${BASE_PACKAGE_DOT}.core.editor.Editor;
import java.util.HashMap;
import java.util.Map;

/**
 * 编辑器注册表（单例）
 * 
 * 职责：
 * - 注册编辑器工厂
 * - 根据文件扩展名创建编辑器
 * 
 * 设计模式：Registry Pattern + Factory Pattern
 * 
 * Lab2 扩展示例：
 * EditorRegistry.register(new XmlEditorFactory());
 */
public class EditorRegistry {
    
    private static EditorRegistry instance;
    private Map<String, EditorFactory> factories;
    
    /**
     * 私有构造函数
     */
    private EditorRegistry() {
        this.factories = new HashMap<>();
    }
    
    /**
     * 获取单例实例
     * 
     * @return EditorRegistry 实例
     */
    public static EditorRegistry getInstance() {
        if (instance == null) {
            instance = new EditorRegistry();
        }
        return instance;
    }
    
    /**
     * 注册编辑器工厂
     * 
     * @param factory 编辑器工厂
     */
    public void register(EditorFactory factory) {
        // TODO: 实现注册逻辑
    }
    
    /**
     * 创建编辑器
     * 
     * @param filePath 文件路径
     * @return 编辑器实例
     * @throws IllegalArgumentException 如果不支持该文件类型
     */
    public Editor createEditor(String filePath) {
        // TODO: 实现创建逻辑
        // 1. 从文件路径提取扩展名
        // 2. 查找对应的工厂
        // 3. 使用工厂创建编辑器
        return null;
    }
    
    /**
     * 检查是否支持该文件类型
     * 
     * @param extension 文件扩展名
     * @return true 如果支持
     */
    public boolean isSupported(String extension) {
        return factories.containsKey(extension);
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/registry/CommandRegistry.java" "package ${BASE_PACKAGE_DOT}.registry;

import ${BASE_PACKAGE_DOT}.core.command.Command;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 命令注册表（单例）
 * 
 * 职责：
 * - 注册命令工厂方法
 * - 根据命令名创建命令实例
 */
public class CommandRegistry {
    
    private static CommandRegistry instance;
    private Map<String, Function<String[], Command>> commandFactories;
    
    /**
     * 私有构造函数
     */
    private CommandRegistry() {
        this.commandFactories = new HashMap<>();
    }
    
    /**
     * 获取单例实例
     * 
     * @return CommandRegistry 实例
     */
    public static CommandRegistry getInstance() {
        if (instance == null) {
            instance = new CommandRegistry();
        }
        return instance;
    }
    
    /**
     * 注册命令工厂
     * 
     * @param commandName 命令名
     * @param factory 命令工厂方法
     */
    public void register(String commandName, Function<String[], Command> factory) {
        commandFactories.put(commandName, factory);
    }
    
    /**
     * 创建命令实例
     * 
     * @param commandName 命令名
     * @param args 命令参数
     * @return 命令实例
     */
    public Command createCommand(String commandName, String[] args) {
        // TODO: 实现创建逻辑
        return null;
    }
    
    /**
     * 检查命令是否已注册
     * 
     * @param commandName 命令名
     * @return true 如果已注册
     */
    public boolean isRegistered(String commandName) {
        return commandFactories.containsKey(commandName);
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/registry/TextEditorFactory.java" "package ${BASE_PACKAGE_DOT}.registry;

import ${BASE_PACKAGE_DOT}.core.editor.Editor;
import ${BASE_PACKAGE_DOT}.core.editor.text.TextEditor;

/**
 * 文本编辑器工厂
 */
public class TextEditorFactory implements EditorFactory {
    
    @Override
    public Editor createEditor(String filePath) {
        return new TextEditor(filePath);
    }
    
    @Override
    public String getSupportedExtension() {
        return \".txt\";
    }
}"

echo ""

##############################################
# 10. 创建 UI 模块
##############################################

print_info "创建 UI 模块..."
echo ""

create_java_file "src/main/java/${BASE_PACKAGE}/ui/CommandLineInterface.java" "package ${BASE_PACKAGE_DOT}.ui;

import ${BASE_PACKAGE_DOT}.core.workspace.Workspace;
import ${BASE_PACKAGE_DOT}.core.command.CommandInvoker;
import java.util.Scanner;

/**
 * 命令行界面
 * 
 * 职责：
 * - 主循环：读取用户输入
 * - 解析命令
 * - 执行命令
 * - 显示结果
 */
public class CommandLineInterface {
    
    private Workspace workspace;
    private CommandInvoker invoker;
    private CommandParser parser;
    private Scanner scanner;
    private boolean running;
    
    /**
     * 构造函数
     * 
     * @param workspace 工作区
     * @param invoker 命令调用器
     */
    public CommandLineInterface(Workspace workspace, CommandInvoker invoker) {
        this.workspace = workspace;
        this.invoker = invoker;
        this.parser = new CommandParser();
        this.scanner = new Scanner(System.in);
        this.running = false;
    }
    
    /**
     * 启动 CLI
     */
    public void start() {
        // TODO: 实现主循环
        running = true;
        System.out.println(\"Team20 Text Editor\");
        System.out.println(\"Type 'help' for commands, 'exit' to quit\");
        
        while (running) {
            // TODO: 显示提示符
            // TODO: 读取输入
            // TODO: 解析命令
            // TODO: 执行命令
            // TODO: 显示结果
        }
    }
    
    /**
     * 停止 CLI
     */
    public void stop() {
        running = false;
        scanner.close();
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/ui/CommandParser.java" "package ${BASE_PACKAGE_DOT}.ui;

/**
 * 命令解析器
 * 
 * 职责：
 * - 解析用户输入
 * - 提取命令名和参数
 * - 处理引号包裹的参数
 */
public class CommandParser {
    
    /**
     * 解析命令行输入
     * 
     * @param input 用户输入
     * @return 解析结果（命令名 + 参数数组）
     */
    public ParsedCommand parse(String input) {
        // TODO: 实现解析逻辑
        // 1. 分割命令名和参数
        // 2. 处理引号包裹的参数
        // 3. 返回 ParsedCommand 对象
        return null;
    }
    
    /**
     * 解析结果类
     */
    public static class ParsedCommand {
        private String commandName;
        private String[] args;
        
        public ParsedCommand(String commandName, String[] args) {
            this.commandName = commandName;
            this.args = args;
        }
        
        public String getCommandName() {
            return commandName;
        }
        
        public String[] getArgs() {
            return args;
        }
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/ui/OutputFormatter.java" "package ${BASE_PACKAGE_DOT}.ui;

import ${BASE_PACKAGE_DOT}.core.editor.Editor;
import java.util.List;

/**
 * 输出格式化工具
 * 
 * 职责：
 * - 格式化编辑器列表
 * - 格式化目录树
 * - 格式化错误消息
 */
public class OutputFormatter {
    
    /**
     * 格式化编辑器列表
     * 
     * @param editors 编辑器列表
     * @param activeEditor 当前活动编辑器
     * @return 格式化的字符串
     */
    public static String formatEditorList(List<Editor> editors, Editor activeEditor) {
        // TODO: 实现格式化逻辑
        // 格式：
        // * file1.txt [modified]
        //   file2.txt
        return null;
    }
    
    /**
     * 格式化目录树
     * 
     * @param path 目录路径
     * @return 格式化的字符串
     */
    public static String formatDirectoryTree(String path) {
        // TODO: 实现目录树格式化
        return null;
    }
    
    /**
     * 格式化成功消息
     * 
     * @param message 消息内容
     * @return 格式化的字符串
     */
    public static String formatSuccess(String message) {
        return \"[SUCCESS] \" + message;
    }
    
    /**
     * 格式化错误消息
     * 
     * @param message 消息内容
     * @return 格式化的字符串
     */
    public static String formatError(String message) {
        return \"[ERROR] \" + message;
    }
}"

echo ""

##############################################
# 11. 创建 Util 模块
##############################################

print_info "创建 Util 模块..."
echo ""

create_java_file "src/main/java/${BASE_PACKAGE}/util/FileUtil.java" "package ${BASE_PACKAGE_DOT}.util;

import java.io.*;
import java.nio.file.*;
import java.util.List;

/**
 * 文件操作工具类
 */
public class FileUtil {
    
    /**
     * 读取文件所有行
     * 
     * @param filePath 文件路径
     * @return 行列表
     * @throws IOException 如果读取失败
     */
    public static List<String> readAllLines(String filePath) throws IOException {
        // TODO: 实现
        return null;
    }
    
    /**
     * 写入文件所有行
     * 
     * @param filePath 文件路径
     * @param lines 行列表
     * @throws IOException 如果写入失败
     */
    public static void writeAllLines(String filePath, List<String> lines) throws IOException {
        // TODO: 实现
    }
    
    /**
     * 检查文件是否存在
     * 
     * @param filePath 文件路径
     * @return true 如果存在
     */
    public static boolean exists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
    
    /**
     * 从文件路径提取扩展名
     * 
     * @param filePath 文件路径
     * @return 扩展名（包括点，如 \".txt\"）
     */
    public static String getExtension(String filePath) {
        // TODO: 实现
        return null;
    }
    
    /**
     * 从文件路径提取文件名
     * 
     * @param filePath 文件路径
     * @return 文件名
     */
    public static String getFileName(String filePath) {
        // TODO: 实现
        return null;
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/util/StringUtil.java" "package ${BASE_PACKAGE_DOT}.util;

/**
 * 字符串处理工具类
 */
public class StringUtil {
    
    /**
     * 检查字符串是否为空或 null
     * 
     * @param str 字符串
     * @return true 如果为空或 null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 解析带引号的字符串
     * 
     * @param quoted 带引号的字符串
     * @return 去除引号后的字符串
     */
    public static String unquote(String quoted) {
        // TODO: 实现
        return null;
    }
    
    /**
     * 分割命令行参数（考虑引号）
     * 
     * @param input 输入字符串
     * @return 参数数组
     */
    public static String[] splitArguments(String input) {
        // TODO: 实现
        // 需要处理引号包裹的参数
        return null;
    }
}"

create_java_file "src/main/java/${BASE_PACKAGE}/util/ValidationUtil.java" "package ${BASE_PACKAGE_DOT}.util;

/**
 * 输入验证工具类
 */
public class ValidationUtil {
    
    /**
     * 验证行号是否有效
     * 
     * @param line 行号
     * @param maxLine 最大行号
     * @return true 如果有效
     */
    public static boolean isValidLine(int line, int maxLine) {
        return line >= 1 && line <= maxLine;
    }
    
    /**
     * 验证列号是否有效
     * 
     * @param col 列号
     * @param maxCol 最大列号
     * @return true 如果有效
     */
    public static boolean isValidColumn(int col, int maxCol) {
        return col >= 1 && col <= maxCol;
    }
    
    /**
     * 验证文件路径格式
     * 
     * @param filePath 文件路径
     * @return true 如果有效
     */
    public static boolean isValidFilePath(String filePath) {
        // TODO: 实现
        return !StringUtil.isEmpty(filePath);
    }
}"

echo ""

##############################################
# 12. 创建 pom.xml（如果不存在）
##############################################

print_info "检查 pom.xml..."
echo ""

if [ ! -f "pom.xml" ]; then
cat << 'EOF' > pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.team20</groupId>
    <artifactId>text-editor</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>Team20 Text Editor</name>
    <description>A modular command-line text editor with registry pattern</description>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <junit.version>5.9.3</junit.version>
        <mockito.version>5.3.1</mockito.version>
        <gson.version>2.10.1</gson.version>
    </properties>

    <dependencies>
        <!-- JSON serialization -->
        <dependency>
            <groupId>com
EOF
    print_success "创建: pom.xml"
else
    print_skip "pom.xml"
fi

echo ""
print_info "验证..."
echo ""
print_success "✅ 完成！"
echo "Java 文件数: $(find src/main/java -name '*.java' 2>/dev/null | wc -l)"
echo ""
