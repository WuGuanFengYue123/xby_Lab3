package com.team20.editor.monitoring.logging;

/**
 * 控制台日志输出
 */
public class ConsoleLogSink implements LogSink {

    @Override
    public void write(String message) {
        System.out.println(message);
    }

    @Override
    public void flush() {
        System.out.flush();
    }

    @Override
    public void close() {
        // 控制台不需要关闭
    }
}