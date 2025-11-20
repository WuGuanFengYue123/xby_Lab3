package com.team20.editor.domain.command.impl.logging;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.workspace.Workspace;

/**
 * 显示日志（最小适配）：execute 接受 Workspace 参数以匹配新的 Command 接口契约。
 */
public class LogShowCommand implements Command {
    private final Workspace workspace;
    private final String source;

    public LogShowCommand(Workspace workspace, String source) {
        this.workspace = workspace;
        this.source = source;
    }

    public LogShowCommand() {
        this(new Workspace(), "default");
    }

    @Override
    public void execute(Workspace workspace) {
        Workspace ws = workspace != null ? workspace : this.workspace;
        // TODO: 实际逻辑（例如读取 workspace 相关日志并打印）
        System.out.println("[log:show] (no buffer yet) via " + source);
    }
}