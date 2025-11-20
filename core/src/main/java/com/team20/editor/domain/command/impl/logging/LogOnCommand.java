package com.team20.editor.domain.command.impl.logging;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.workspace.Workspace;

/**
 * 开启日志（最小适配）：execute 接受 Workspace 参数以匹配新的 Command 接口契约。
 */
public class LogOnCommand implements Command {
    private final Workspace workspace;
    private final String source;

    public LogOnCommand(Workspace workspace, String source) {
        this.workspace = workspace;
        this.source = source;
    }

    // 无参构造用于 Provider 默认创建（占位 Workspace）
    public LogOnCommand() {
        this(new Workspace(), "default");
    }

    @Override
    public void execute(Workspace workspace) {
        // 使用传入的 workspace（优先使用参数），保留旧的 source 字段用于日志来源显示
        Workspace ws = workspace != null ? workspace : this.workspace;
        // TODO: 实际逻辑（例如通过 ws 注册日志监听器）
        System.out.println("[log:on] logging enabled via " + source);
    }
}