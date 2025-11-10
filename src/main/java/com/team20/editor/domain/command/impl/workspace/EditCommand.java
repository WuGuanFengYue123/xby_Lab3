package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.workspace.Workspace;

public class EditCommand implements Command {

    private final Workspace workspace;
    private final String editorName;

    public EditCommand(Workspace workspace, String editorName) {
        this.workspace = workspace;
        this.editorName = editorName;
    }

    public EditCommand() {
        this.workspace = null;
        this.editorName = null;
    }

    @Override
    public void execute() {
        System.out.println("[EditCommand] open/edit requested for: " + editorName + " (placeholder)");
    }
}