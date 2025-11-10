package com.team20.editor.domain.command;

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
}
