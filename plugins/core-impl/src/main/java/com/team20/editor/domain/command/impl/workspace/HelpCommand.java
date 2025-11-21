package com.team20.editor.domain.command.impl;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.bootstrap.ApplicationContext;

/**
 * HelpCommand: prints application help (delegates to ApplicationContext).
 */
public class HelpCommand implements Command {

    private final ApplicationContext ctx;

    public HelpCommand(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void execute(Workspace workspace) {
        try {
            if (ctx != null) {
                System.out.println(ctx.showHelp());
            } else {
                System.out.println("Help is not available (application context missing).");
            }
        } catch (Throwable t) {
            System.err.println("Failed to show help: " + t.getMessage());
        }
    }

    @Override
    public String toString() {
        return "help";
    }
}