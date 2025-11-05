package com.team20.editor.core.event;

/**
 * 工作区事件
 */
public class WorkspaceEvent extends Event {
    
    private final String eventType;
    private final String filePath;
    
    public WorkspaceEvent(String source, String eventType, String filePath) {
        super(source);
        this.eventType = eventType;
        this.filePath = filePath;
    }
    
    @Override
    public String getEventType() {
        return eventType;
    }
    
    public String getFilePath() {
        return filePath;
    }
}
