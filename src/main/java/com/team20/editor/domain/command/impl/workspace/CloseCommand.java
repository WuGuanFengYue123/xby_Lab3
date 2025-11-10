package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.workspace.Workspace;

public class CloseCommand implements Command {

    private final Workspace workspace;
    private final String editorName;

    public CloseCommand(Workspace workspace, String editorName) {
        this.workspace = workspace;
        this.editorName = editorName;
    }

    public CloseCommand() {
        this.workspace = null;
        this.editorName = null;
    }

    @Override
    public void execute() {
        // placeholder: no real close API in Workspace demo
        System.out.println("[CloseCommand] requested close of: " + editorName + " (placeholder)");
    }
}