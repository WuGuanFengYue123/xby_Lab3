package com.team20.editor.monitoring.logging;

public interface LogSink {
    void write(LogLevel level, String message, long timestamp);

    enum LogLevel {
        INFO, WARN, ERROR, DEBUG
    }
}
