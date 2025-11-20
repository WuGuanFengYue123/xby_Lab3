package com.team20.editor.monitoring.logging;

/**
 * 控制台日志实现（插件）
 *
 * 实现 core 的 LogSink 接口（write/flush/close），
 * 以便在严格插件化下，core 通过 ServiceLoader 能找到并使用该实现。
 */
public class ConsoleLogSink implements LogSink {

    @Override
    public void write(String message) {
        // 直接输出，保留简单的时间戳前缀（若上层已包含时间戳，请改为只输出 message）
        System.out.println(message);
    }

    @Override
    public void flush() {
        // 没有缓冲输出流，这里不需要特殊处理；保留为 no-op
        try {
            System.out.flush();
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void close() {
        // 控制台不需要关闭；保留为空实现以满足接口契约
    }
}