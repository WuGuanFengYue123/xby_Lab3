package com.team20.editor.domain.command.impl.logging;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.workspace.Workspace;

/**
 * 关闭日志（最小适配）：execute 接受 Workspace 参数以匹配新的 Command 接口契约。
 */
public class LogOffCommand implements Command {
    private final Workspace workspace;
    private final String source;

    public LogOffCommand(Workspace workspace, String source) {
        this.workspace = workspace;
        this.source = source;
    }

    public LogOffCommand() {
        this(new Workspace(), "default");
    }

    @Override
    public void execute(Workspace workspace) {
        Workspace ws = workspace != null ? workspace : this.workspace;
        // TODO: 实际逻辑（例如通过 ws 取消日志监听器）
        System.out.println("[log:off] logging disabled via " + source);
    }
}