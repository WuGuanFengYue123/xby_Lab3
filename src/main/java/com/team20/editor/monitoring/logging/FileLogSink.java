package com.team20.editor.monitoring.logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * 文件日志输出
 */
public class FileLogSink implements LogSink {
    private final Path filepath;
    private BufferedWriter writer;

    public FileLogSink(Path filepath) {
        this.filepath = filepath;
        try {
            this.writer = new BufferedWriter(new FileWriter(filepath.toFile(), true));
        } catch (IOException e) {
            System.err.println("无法打开日志文件: " + e.getMessage());
        }
    }

    @Override
    public void write(String message) {
        if (writer != null) {
            try {
                writer.write(message);
                writer.newLine();
            } catch (IOException e) {
                System.err.println("写入日志失败: " + e.getMessage());
            }
        }
    }

    @Override
    public void flush() {
        if (writer != null) {
            try {
                writer.flush();
            } catch (IOException e) {
                System.err.println("刷新日志失败: " + e.getMessage());
            }
        }
    }

    @Override
    public void close() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                System.err.println("关闭日志文件失败: " + e.getMessage());
            }
        }
    }
}