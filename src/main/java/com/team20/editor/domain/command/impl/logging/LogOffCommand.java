package com.team20.editor.domain.command.impl.logging;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.workspace.Workspace;

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
    public void execute() {
        System.out.println("[log:off] logging disabled via " + source);
    }
}