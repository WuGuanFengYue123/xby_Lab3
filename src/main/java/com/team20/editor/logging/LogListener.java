package com.team20.editor.logging;

import com.team20.editor.core.event.Event;
import com.team20.editor.core.event.EventListener;
import com.team20.editor.core.event.CommandEvent;

/**
 * 日志事件监听器
 * 
 * 监听命令执行事件并记录日志
 */
public class LogListener implements EventListener {
    
    private Logger logger;
    
    /**
     * 构造函数
     * 
     * @param logger 日志记录器
     */
    public LogListener(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public void onEvent(Event event) {
        // TODO: 处理命令事件并记录日志
    }
    
    @Override
    public Class<? extends Event> getEventType() {
        return CommandEvent.class;
    }
}
