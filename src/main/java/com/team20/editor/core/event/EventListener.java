package com.team20.editor.core.event;

/**
 * 事件监听器接口
 */
public interface EventListener {
    
    /**
     * 处理事件
     * 
     * @param event 事件对象
     */
    void onEvent(Event event);
    
    /**
     * 获取监听器关注的事件类型
     * 
     * @return 事件类型的 Class 对象
     */
    Class<? extends Event> getEventType();
}
