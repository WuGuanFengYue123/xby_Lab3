package com.team20.editor.infrastructure.event;

public interface EventPublisher {
    void publish(Event event);
}