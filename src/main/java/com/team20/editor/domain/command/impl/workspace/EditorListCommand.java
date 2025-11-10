package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.domain.editor.Editor;

public class EditorListCommand implements Command {

    private final Workspace workspace;

    public EditorListCommand(Workspace workspace) {
        this.workspace = workspace;
    }

    public EditorListCommand() {
        this.workspace = null;
    }

    @Override
    public void execute() {
        if (workspace == null) {
            System.out.println("[EditorListCommand] no workspace");
            return;
        }
        System.out.println("[EditorListCommand] editors:");
        int i = 0;
        for (Editor e : workspace.getEditors()) {
            System.out.printf("  %d: %s%n", ++i, e.getName());
        }
    }
}