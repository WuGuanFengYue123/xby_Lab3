package com.team20.editor.core.event;

import java.time.LocalDateTime;

/**
 * 事件基类
 * 
 * 设计模式：Observer Pattern
 */
public abstract class Event {
    
    private final LocalDateTime timestamp;
    private final String source;
    
    /**
     * 构造函数
     * 
     * @param source 事件源
     */
    public Event(String source) {
        this.timestamp = LocalDateTime.now();
        this.source = source;
    }
    
    /**
     * 获取事件时间戳
     * 
     * @return 时间戳
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * 获取事件源
     * 
     * @return 事件源
     */
    public String getSource() {
        return source;
    }
    
    /**
     * 获取事件类型
     * 
     * @return 事件类型字符串
     */
    public abstract String getEventType();
}
