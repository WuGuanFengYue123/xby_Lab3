package com.team20.editor.infrastructure.event;

public interface EventBus {
    <E extends Event> void subscribe(Class<E> type, EventListener<E> listener);

    void publish(Event event);

    int listenerCount(Class<?> type);
}