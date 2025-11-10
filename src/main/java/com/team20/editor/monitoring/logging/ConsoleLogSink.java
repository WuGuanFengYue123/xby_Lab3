package com.team20.editor.monitoring.logging;

public class ConsoleLogSink implements LogSink {
    @Override
    public void write(LogLevel level, String message, long timestamp) {
        System.out.printf("[%s] %s%n", level, message);
    }
}