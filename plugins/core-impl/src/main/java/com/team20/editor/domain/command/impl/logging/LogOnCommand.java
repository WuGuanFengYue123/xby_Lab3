package com.team20.editor.domain.command.impl.logging;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.extension.registry.DefaultCommandRegistry;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * log-on [file] - enable logging for specified file or current active file.
 *
 * New behaviour (no backward-compat marker files):
 * - Set workspace logging flag via workspace.setLoggingEnabled(...)
 * - Write session header to .<name>.log
 * - Persist workspace state (best-effort)
 */
public class LogOnCommand implements Command {

    private final String filepath;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");

    public LogOnCommand() {
        this.filepath = null;
    }

    public LogOnCommand(String filepath) {
        this.filepath = (filepath == null || filepath.isBlank()) ? null : filepath.trim();
    }

    @Override
    public void execute(Workspace workspace) {
        String target = filepath;
        if (target == null) {
            var active = workspace.getActiveEditor();
            if (active == null) {
                System.out.println("没有打开的文件");
                return;
            }
            target = active.getName();
        }

        String safeName = new File(target).getName();
        File logFile = new File("." + safeName + ".log");

        try {
            // ensure log file exists and write session header
            try (PrintWriter pw = new PrintWriter(new FileWriter(logFile, true))) {
                String session = LocalDateTime.now().format(FORMATTER);
                pw.println("session start at " + session);
            }

            // update centralized workspace state
            workspace.setLoggingEnabled(target, true);

            // persist workspace state (best-effort)
            try {
                var ctx = DefaultCommandRegistry.getApplicationContext();
                if (ctx != null && ctx.persistenceManager() != null) {
                    ctx.persistenceManager().saveWorkspaceState(".workspace.state", workspace.getState());
                }
            } catch (Throwable ignored) {
            }

            System.out.println("日志已启用: " + logFile.getName());
        } catch (Exception ex) {
            System.out.println("启用日志时发生错误: " + ex.getMessage());
        }
    }

    @Override
    public String toString() {
        return "log-on " + (filepath == null ? "" : filepath);
    }
}