package com.team20.editor.extension.spi.serialization;

import com.team20.editor.infrastructure.persistence.Serializer;

/**
 * 序列化器提供者 SPI：返回 Serializer 实例（比如用于持久化 Workspace 或 EditorState）。
 */
public interface SerializerProvider {
    /**
     * 返回一个 Serializer 实例（可能每次调用返回新的实例，或返回单例）。
     */
    Serializer getSerializer();
}