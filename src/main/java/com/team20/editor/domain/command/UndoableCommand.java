package com.team20.editor.domain.command;

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
}
