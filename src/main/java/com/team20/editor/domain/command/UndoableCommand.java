package com.team20.editor.domain.command;

import com.team20.editor.domain.workspace.Workspace;

/**
 * 可撤销命令接口，扩展自 Command。
 *
 * 规范：
 * - execute/undo/redo 都接收 Workspace 上下文。
 */
public interface UndoableCommand extends Command {
    @Override
    void execute(Workspace workspace);

    /**
     * 撤销上一次 execute 的效果（在相同的 Workspace 上恢复）。
     */
    void undo(Workspace workspace);

    /**
     * 重做（重新应用被撤销的操作）。
     */
    void redo(Workspace workspace);
}