package com.team20.editor.monitoring.logging;

import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.infrastructure.event.CommandEvent;
import com.team20.editor.infrastructure.event.Event;
import com.team20.editor.infrastructure.event.EventListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.format.DateTimeFormatter;

/**
 * LogListener:
 * - Writes per-file logs only when Workspace reports logging enabled for that
 * file.
 * - No fallback to marker files or "# log" header.
 */
public class LogListener implements EventListener {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");

    // injected at runtime by ApplicationContext.createWorkspace(...)
    private Workspace workspace;

    public LogListener() {
    }

    /**
     * Set workspace instance so LogListener can consult runtime flags.
     * Called from ApplicationContext.createWorkspace(...)
     */
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof CommandEvent))
            return;
        CommandEvent cmd = (CommandEvent) event;

        String ts;
        try {
            ts = cmd.getFormattedTimestamp();
        } catch (Throwable t) {
            ts = java.time.LocalDateTime.now().format(FORMATTER);
        }

        String cmdName = safeString(cmd.getCommandName());
        String args = safeString(cmd.getArguments());
        String filepath = safeString(cmd.getFilepath());

        if (filepath.isEmpty()) {
            return;
        }

        // Only consult workspace flag; do NOT check marker files or file content.
        boolean enabled = false;
        try {
            if (workspace != null) {
                enabled = workspace.isLoggingEnabled(filepath);
            }
        } catch (Throwable ignored) {
        }

        if (!enabled)
            return;

        String safeName = new File(filepath).getName();
        File logFile = new File("." + safeName + ".log");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true))) {
            String entry = String.format("%s %s%s", ts, cmdName, args.isEmpty() ? "" : " " + args);
            bw.write(entry);
            bw.newLine();
            bw.flush();
        } catch (Exception io) {
            String warn = "Warning: failed to write per-file log '" + safeName + "': " + io.getMessage();
            System.err.println(warn);
        }
    }

    private static String safeString(String s) {
        return s == null ? "" : s;
    }
}