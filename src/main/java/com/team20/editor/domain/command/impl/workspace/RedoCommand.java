package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.command.CommandInvoker;
import com.team20.editor.domain.workspace.Workspace;

/**
 * 触发 redo 的命令（会调用 CommandInvoker.redo）
 */
public class RedoCommand implements Command {

    private final CommandInvoker invoker;

    public RedoCommand(CommandInvoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public void execute(Workspace workspace) {
        try {
            invoker.redo(workspace);
            System.out.println("Redo executed.");
        } catch (IllegalStateException e) {
            System.out.println("Nothing to redo.");
        }
    }

    @Override
    public String toString() {
        return "redo";
    }
}