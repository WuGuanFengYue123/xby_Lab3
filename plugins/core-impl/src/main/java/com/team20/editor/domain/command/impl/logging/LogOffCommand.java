package com.team20.editor.domain.command.impl.logging;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.editor.Editor;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.extension.registry.DefaultCommandRegistry;

/**
 * log-off [file] - disable logging for specified file or current active file.
 *
 * New behaviour (no marker files):
 * - Update centralized workspace flag (workspace.setLoggingEnabled(name,
 * false))
 * - Persist workspace state (best-effort)
 */
public class LogOffCommand implements Command {

    private final String filepath;

    public LogOffCommand() {
        this.filepath = null;
    }

    public LogOffCommand(String filepath) {
        this.filepath = (filepath == null || filepath.isBlank()) ? null : filepath.trim();
    }

    @Override
    public void execute(Workspace workspace) {
        String target = filepath;
        if (target == null) {
            Editor active = workspace.getActiveEditor();
            if (active == null) {
                System.out.println("没有打开的文件");
                return;
            }
            target = active.getName();
        }

        // Update centralized workspace state
        try {
            workspace.setLoggingEnabled(target, false);
        } catch (Throwable ignored) {
        }

        // persist workspace state (best-effort)
        try {
            var ctx = DefaultCommandRegistry.getApplicationContext();
            if (ctx != null && ctx.persistenceManager() != null) {
                ctx.persistenceManager().saveWorkspaceState(".workspace.state", workspace.getState());
            }
        } catch (Throwable ignored) {
        }

        System.out.println("日志已禁用: " + new java.io.File(target).getName());
    }

    @Override
    public String toString() {
        return "log-off " + (filepath == null ? "" : filepath);
    }
}