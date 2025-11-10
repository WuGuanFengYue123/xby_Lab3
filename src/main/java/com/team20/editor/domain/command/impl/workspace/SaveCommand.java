package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.workspace.Workspace;

public class SaveCommand implements Command {

    private final Workspace workspace;
    private final String target;

    public SaveCommand(Workspace workspace, String target) {
        this.workspace = workspace;
        this.target = target;
    }

    public SaveCommand() {
        this.workspace = null;
        this.target = null;
    }

    @Override
    public void execute() {
        System.out.println("[SaveCommand] save to: " + target + " (placeholder)");
    }
}