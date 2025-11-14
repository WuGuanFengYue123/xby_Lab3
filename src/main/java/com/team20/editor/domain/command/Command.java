package com.team20.editor.domain.command;

import com.team20.editor.domain.workspace.Workspace;

/**
 * 命令接口（统一签名）
 *
 * 所有命令在执行时都会接收 Workspace 上下文，便于命令在上下文中操作编辑器、发布事件或做持久化。
 */
public interface Command {
    /**
     * 执行命令，所有命令都接收 Workspace 作为上下文。
     *
     * @param workspace 当前工作区
     */
    void execute(Workspace workspace);
}