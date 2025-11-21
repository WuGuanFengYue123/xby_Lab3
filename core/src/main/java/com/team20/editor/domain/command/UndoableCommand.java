package com.team20.editor.domain.command;

import com.team20.editor.domain.workspace.Workspace;

/**
 * 可撤销命令接口，扩展自 Command。
 *
 * 约定：
 * - execute/undo/redo 都接收 Workspace，上层（如 CommandInvoker）将按类型处理记录与回放。
 */
public interface UndoableCommand extends Command {
    @Override
    void execute(Workspace workspace);

    /**
     * 撤销本条命令在 workspace 上的影响。
     */
    void undo(Workspace workspace);

    /**
     * 重做本条命令在 workspace 上的影响。
     */
    void redo(Workspace workspace);
}