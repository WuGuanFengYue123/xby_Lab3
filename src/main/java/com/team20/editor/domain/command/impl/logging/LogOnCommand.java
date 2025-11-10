package com.team20.editor.domain.command.impl.logging;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.workspace.Workspace;

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
    public void execute() {
        // TODO: 实际逻辑（可向 workspace 注册日志监听）
        System.out.println("[log:on] logging enabled via " + source);
    }
}