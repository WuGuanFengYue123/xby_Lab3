package com.team20.editor.domain.command;

import com.team20.editor.domain.workspace.Workspace;

/**
 * 统一命令接口（所有命令都接受 Workspace 上下文）。
 *
 * 说明：
 * - 将所有命令的执行签名统一为 execute(Workspace) 以避免无参/有参并行存在导致混乱。
 * - 只包含 execute 方法；可撤销命令通过 UndoableCommand 扩展该接口。
 */
public interface Command {
    /**
     * 在给定的 Workspace 上执行命令。
     *
     * @param workspace 当前工作区
     */
    void execute(Workspace workspace);
}