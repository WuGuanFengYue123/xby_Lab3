package com.team20.editor.infrastructure.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleEventBus implements EventBus {
    private final Map<Class<?>, List<EventListener<?>>> listeners = new ConcurrentHashMap<>();

    @Override
    public <E extends Event> void subscribe(Class<E> type, EventListener<E> listener) {
        listeners.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    @Override
    public void publish(Event event) {
        if (event == null)
            return;
        List<EventListener<?>> ls = listeners.get(event.getClass());
        if (ls == null)
            return;
        for (EventListener<?> l : ls) {
            @SuppressWarnings("unchecked")
            EventListener<Event> cast = (EventListener<Event>) l;
            cast.onEvent(event);
        }
    }

    @Override
    public int listenerCount(Class<?> type) {
        List<?> ls = listeners.get(type);
        return ls == null ? 0 : ls.size();
    }
}