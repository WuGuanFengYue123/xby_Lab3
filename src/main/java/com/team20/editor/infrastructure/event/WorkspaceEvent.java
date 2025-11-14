package com.team20.editor.infrastructure.event;

/**
 * 工作区事件
 */
public class WorkspaceEvent implements Event {
    private final String eventType;
    private final Object data;

    public WorkspaceEvent(String eventType, Object data) {
        this.eventType = eventType;
        this.data = data;
    }

    @Override
    public String getType() {
        return eventType;
    }

    @Override
    public Object getData() {
        return data;
    }
}