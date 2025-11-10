package com.team20.editor.monitoring.logging;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 可选：文件日志输出（后续只新增，不修改原 Logger）。
 */
public class FileLogSink implements LogSink {

    private final Path path;

    public FileLogSink(Path path) {
        this.path = path;
    }

    @Override
    public void write(LogLevel level, String message, long timestamp) {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(path, java.nio.charset.StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND))) {
            out.printf("[%s] %s%n", level, message);
        } catch (IOException e) {
            // 这里可扩展为错误回退策略
            System.err.println("Log write failed: " + e.getMessage());
        }
    }
}
