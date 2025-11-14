package com.team20.editor.infrastructure.event;

/**
 * 事件总线接口
 */
public interface EventBus extends EventPublisher {
    void subscribe(EventListener listener);

    void unsubscribe(EventListener listener);
}