# Command Module

## 职责
- 实现命令模式
- 支持 undo/redo
- 命令注册与调用

## 核心接口/类
- `Command.java`: 命令接口
- `UndoableCommand.java`: 可撤销命令接口
- `CommandInvoker.java`: 命令调用器（管理历史栈）
- `impl/`: 具体命令实现

## 设计模式
- Command Pattern（命令模式）

## 命令分类

### 工作区命令 (impl/workspace/)
- LoadCommand
- SaveCommand
- CloseCommand
- EditCommand (切换文件)
- EditorListCommand

### 文本编辑命令 (impl/text/)
- AppendCommand
- InsertCommand
- DeleteCommand
- ReplaceCommand
- ShowCommand

### 日志命令 (impl/logging/)
- LogOnCommand
- LogOffCommand
- LogShowCommand

## 扩展点
使用 CommandRegistry 注册新命令

## Undo/Redo 实现
- 只有修改性命令进入历史栈
- 显示类命令不进入历史栈
