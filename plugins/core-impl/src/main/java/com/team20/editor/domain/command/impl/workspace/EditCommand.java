package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.editor.Editor;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.infrastructure.persistence.PersistenceManager;
import com.team20.editor.extension.registry.DefaultCommandRegistry;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * EditCommand: open or switch to an editor for a given filepath.
 * If the file exists on disk, load its content via PersistenceManager.
 * Auto-enable logging if first non-empty line is "# log".
 * Also, every time the file is opened and logging is enabled (marker exists),
 * append a "session start at ..." line to the .<filename>.log file.
 */
public class EditCommand implements Command {

    private final com.team20.editor.extension.registry.EditorFactory editorFactory;
    private final PersistenceManager persistenceManager;
    private final String filepath;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");

    public EditCommand(com.team20.editor.extension.registry.EditorFactory editorFactory,
            PersistenceManager persistenceManager,
            String filepath) {
        this.editorFactory = editorFactory;
        this.persistenceManager = persistenceManager;
        this.filepath = filepath;
    }

    @Override
    public void execute(Workspace workspace) {
        try {
            // If editor already open, make active
            Editor existing = workspace.getEditor(filepath);
            if (existing != null) {
                workspace.setActiveEditor(existing);
                System.out.println("切换到已打开文件: " + filepath);
                return;
            }

            // Try load from persistence if available
            String content = null;
            try {
                content = persistenceManager.load(filepath);
            } catch (Exception ignored) {
            }

            Editor editor = editorFactory.createEditor(filepath);
            if (content != null) {
                editor.loadContent(content);
            } else {
                editor.loadContent("");
            }
            workspace.addEditor(editor);
            workspace.setActiveEditor(editor);
            System.out.println("已打开文件: " + filepath);

            // Auto-enable logging if first non-empty line equals "# log"
            boolean autoEnabled = false;
            if (content != null) {
                String firstLine = extractFirstLine(content);
                if ("# log".equals(firstLine)) {
                    var logOn = new com.team20.editor.domain.command.impl.logging.LogOnCommand(filepath);
                    logOn.execute(workspace);
                    autoEnabled = true;
                }
            }

            // If logging was already enabled (marker exists) and we did NOT auto-enable
            // just now,
            // append a session-start line to the .<filename>.log to mark this open as a new
            // session.
            if (!autoEnabled) {
                appendSessionIfLoggingEnabled(filepath);
            }
        } catch (Exception ex) {
            System.out.println("打开失败: " + ex.getMessage());
        }
    }

    private String extractFirstLine(String content) {
        String[] lines = content.split("\\r?\\n", -1);
        for (String l : lines) {
            if (l != null && !l.isBlank())
                return l.trim();
        }
        return "";
    }

    private void appendSessionIfLoggingEnabled(String filepath) {
        try {
            String safeName = new File(filepath).getName();
            File marker = new File("." + safeName + ".log.enabled");
            if (marker.exists()) {
                String logName = "." + safeName + ".log";
                try (PrintWriter pw = new PrintWriter(new FileWriter(logName, true))) {
                    String session = LocalDateTime.now().format(FORMATTER);
                    pw.println("session start at " + session);
                } catch (Exception e) {
                    System.err
                            .println("Warning: failed to write session header for " + safeName + ": " + e.getMessage());
                }
            }
        } catch (Throwable t) {
            // tolerate errors
        }
    }
}