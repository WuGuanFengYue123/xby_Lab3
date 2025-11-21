package com.team20.editor.infrastructure.event;

public interface Event {
    String getType();

    Object getData();
}