package com.team20.editor.infrastructure.event;

public class WorkspaceEvent extends Event {
    private final String type;

    public WorkspaceEvent(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}