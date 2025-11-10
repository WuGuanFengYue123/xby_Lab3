package com.team20.editor.infrastructure.event;

public abstract class Event {
    private final long timestamp = System.currentTimeMillis();

    public long timestamp() {
        return timestamp;
    }
}