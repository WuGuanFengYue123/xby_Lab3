package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.workspace.Workspace;

public class LoadCommand implements Command {

    private final Workspace workspace;
    private final String source;

    public LoadCommand(Workspace workspace, String source) {
        this.workspace = workspace;
        this.source = source;
    }

    public LoadCommand() {
        this.workspace = null;
        this.source = null;
    }

    @Override
    public void execute() {
        System.out.println("[LoadCommand] load from: " + source + " (placeholder)");
    }
}