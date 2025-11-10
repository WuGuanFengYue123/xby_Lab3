package com.team20.editor.infrastructure.event;

public interface EventListener<E extends Event> {
    void onEvent(E event);
}
