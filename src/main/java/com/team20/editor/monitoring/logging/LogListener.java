package com.team20.editor.monitoring.logging;

import com.team20.editor.infrastructure.event.CommandEvent;
import com.team20.editor.infrastructure.event.EventListener;

/**
 * 日志事件监听器
 *
 * 监听命令执行事件并记录日志（使用 LogSink 接口，避免依赖不确定的 Logger API）
 */
public class LogListener implements EventListener<CommandEvent> {

    private final LogSink sink;

    /**
     * 构造函数
     *
     * @param sink 日志接收器（ConsoleLogSink / FileLogSink 等）
     */
    public LogListener(LogSink sink) {
        this.sink = sink;
    }

    @Override
    public void onEvent(CommandEvent event) {
        if (event == null)
            return;
        long ts = event.timestamp();
        String message = "[CommandEvent] " + (event.commandName() == null ? "(unknown)" : event.commandName());
        try {
            if (sink != null)
                sink.write(LogSink.LogLevel.INFO, message, ts);
            else
                System.out.println(message);
        } catch (Throwable t) {
            // 保底打印，避免因为日志写入导致事件流中断
            System.out.println(message + " (log failed: " + t.getMessage() + ")");
        }
    }
}