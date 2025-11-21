package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.editor.Editor;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.infrastructure.persistence.PersistenceManager;
import com.team20.editor.bootstrap.ApplicationContext;
import com.team20.editor.extension.registry.DefaultCommandRegistry;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * init <file> [with-log]
 *
 * Behavior:
 * - refuse if file exists
 * - always create/truncate a fresh .<filename>.log when init succeeds
 * - if with-log:
 * - write editor file first line as exactly "# log" (no extra blank line)
 * - append a "session start at yyyyMMdd HH:mm:ss" line into .<filename>.log
 * - DO NOT create .<filename>.log.enabled marker (LogListener will detect "#
 * log")
 */
public class InitCommand implements Command {

    private final String filepath;
    private final boolean withLog;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");

    public InitCommand(String filepath, boolean withLog) {
        this.filepath = (filepath == null) ? null : filepath.trim();
        this.withLog = withLog;
    }

    @Override
    public void execute(Workspace workspace) {
        if (filepath == null || filepath.isBlank()) {
            System.out.println("用法: init <file> [with-log]");
            return;
        }

        try {
            File f = new File(filepath);
            if (f.exists()) {
                System.out.println("文件已存在: " + filepath);
                return;
            }

            ApplicationContext ctx = DefaultCommandRegistry.getApplicationContext();
            if (ctx == null) {
                System.out.println("初始化失败：ApplicationContext 未就绪");
                return;
            }
            PersistenceManager pm = ctx.persistenceManager();

            // prepare file content: withLog -> exactly "# log" (no trailing blank line)
            String content = withLog ? "# log" : "";

            try {
                pm.save(filepath, content);
            } catch (Exception ex) {
                System.out.println("创建文件失败: " + ex.getMessage());
                return;
            }

            // open editor and load content
            try {
                var factory = ctx.editorFactory();
                Editor editor = factory.createEditor(filepath);
                editor.loadContent(content);
                workspace.addEditor(editor);
                workspace.setActiveEditor(editor);
                System.out.println("已创建文件: " + filepath);
                System.out.println("提示：使用 'append \"text\"' 添加内容");
            } catch (Exception ex) {
                System.out.println("创建编辑器失败，但文件已创建: " + filepath + " (" + ex.getMessage() + ")");
            }

            // always create/truncate a fresh per-file log (overwrite mode)
            String safeName = new File(filepath).getName();
            File logFile = new File("." + safeName + ".log");
            try {
                try (FileWriter fw = new FileWriter(logFile, false)) {
                    // truncate/create empty file
                }
            } catch (Exception le) {
                System.err.println("Warning: 无法创建/清空日志文件: " + le.getMessage());
            }

            // If withLog, append session start line (but DO NOT create .log.enabled marker)
            if (withLog) {
                try (PrintWriter pw = new PrintWriter(new FileWriter(logFile, true))) {
                    String session = LocalDateTime.now().format(FORMATTER);
                    pw.println("session start at " + session);
                } catch (Exception le) {
                    System.err.println("Warning: 无法写入 session start 到日志文件: " + le.getMessage());
                }
                System.out.println("日志已启用: " + logFile.getName());
            }
        } catch (Throwable t) {
            System.out.println("init 发生异常: " + t.getMessage());
        }
    }

    @Override
    public String toString() {
        return "init " + (filepath == null ? "" : filepath) + (withLog ? " with-log" : "");
    }
}