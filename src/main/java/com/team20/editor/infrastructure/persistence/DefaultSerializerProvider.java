package com.team20.editor.infrastructure.persistence;

import java.util.Collection;
import java.util.List;

import com.team20.editor.extension.spi.serialization.SerializerProvider;

/**
 * 默认序列化提供者（目前只有 JSON，后续新增 XML/YAML 只加新 Provider）。
 */
public class DefaultSerializerProvider implements SerializerProvider {
    @Override
    public Collection<Serializer> serializers() {
        return List.of(new JsonSerializer());
    }
}
