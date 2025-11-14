package com.team20.editor.infrastructure.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 简单的事件总线实现
 */
public class SimpleEventBus implements EventBus {
    private final List<EventListener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void subscribe(EventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void unsubscribe(EventListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void publish(Event event) {
        if (event == null) {
            return;
        }

        for (EventListener listener : listeners) {
            try {
                listener.onEvent(event);
            } catch (Exception e) {
                System.err.println("事件处理失败: " + e.getMessage());
                // 不中断其他监听器的处理
            }
        }
    }

    public int getListenerCount() {
        return listeners.size();
    }
}