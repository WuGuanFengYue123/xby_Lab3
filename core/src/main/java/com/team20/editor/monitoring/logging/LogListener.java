package com.team20.editor.monitoring.logging;

import com.team20.editor.infrastructure.event.CommandEvent;
import com.team20.editor.infrastructure.event.Event;
import com.team20.editor.infrastructure.event.EventListener;

import java.lang.reflect.Method;
import java.time.Instant;

/**
 * LogListener adapted to a tolerant calling strategy.
 * Uses reflection to call sink.log(...) to avoid compilation-time dependency on the exact LogSink API.
 */
public class LogListener implements EventListener {

    private final LogSink sink;

    public LogListener(LogSink sink) {
        this.sink = sink;
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof CommandEvent)) return;
        CommandEvent cmd = (CommandEvent) event;
        String timestamp = null;
        String command = null;
        try {
            // try to read timestamp and command name via common getters
            try {
                Method m = cmd.getClass().getMethod("getTimestamp");
                Object o = m.invoke(cmd);
                timestamp = o == null ? null : o.toString();
            } catch (NoSuchMethodException ignored) {
                try {
                    Method m2 = cmd.getClass().getMethod("timestamp");
                    Object o = m2.invoke(cmd);
                    timestamp = o == null ? null : o.toString();
                } catch (NoSuchMethodException ignored2) {
                    timestamp = Instant.now().toString();
                }
            }
            try {
                Method m = cmd.getClass().getMethod("getCommandName");
                Object o = m.invoke(cmd);
                command = o == null ? "" : o.toString();
            } catch (NoSuchMethodException ignored) {
                try {
                    Method m2 = cmd.getClass().getMethod("commandName");
                    Object o = m2.invoke(cmd);
                    command = o == null ? "" : o.toString();
                } catch (NoSuchMethodException ignored2) {
                    command = "";
                }
            }
        } catch (Exception e) {
            // ignore reflection failures
        }

        // Use reflection to call sink.log to avoid compile-time dependency on the exact signature
        try {
            Method logMethod = null;
            // try sink.log(String, String)
            try {
                logMethod = sink.getClass().getMethod("log", String.class, String.class);
                logMethod.invoke(sink, timestamp == null ? Instant.now().toString() : timestamp, command);
            } catch (NoSuchMethodException ex) {
                // try sink.write(String) or sink.append(String) fallbacks
                try {
                    Method m = sink.getClass().getMethod("log", String.class);
                    m.invoke(sink, (timestamp == null ? Instant.now().toString() : timestamp) + " " + command);
                } catch (NoSuchMethodException ex2) {
                    // last resort: try write(String)
                    try {
                        Method m2 = sink.getClass().getMethod("write", String.class);
                        m2.invoke(sink, (timestamp == null ? Instant.now().toString() : timestamp) + " " + command);
                    } catch (NoSuchMethodException ignored) {
                        // nothing more we can do
                    }
                }
            }
        } catch (Exception e) {
            // logging must never break the application
        }
    }
}