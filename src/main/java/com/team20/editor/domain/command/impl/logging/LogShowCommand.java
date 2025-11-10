package com.team20.editor.domain.command.impl.logging;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.workspace.Workspace;

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
    public void execute() {
        System.out.println("[log:show] (no buffer yet) via " + source);
    }
}