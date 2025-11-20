package com.team20.editor.infrastructure.event;

/**
 * 事件监听器接口（非泛型版，统一接收 Event）。
 *
 * 说明：部分代码之前以泛型 EventListener<T> 使用，但在仓库中有混用；为了简化兼容性，我们用最通用的签名：
 * void onEvent(Event)
 */
public interface EventListener {
    void onEvent(Event event);
}