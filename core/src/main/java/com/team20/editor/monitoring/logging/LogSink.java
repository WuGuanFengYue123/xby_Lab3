package com.team20.editor.monitoring.logging;

/**
 * 日志输出接口
 */
public interface LogSink {
    void write(String message);

    void flush();

    void close();
}