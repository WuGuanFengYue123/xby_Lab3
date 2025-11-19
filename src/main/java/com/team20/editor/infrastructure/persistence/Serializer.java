package com.team20.editor.infrastructure.persistence;

/**
 * 通用序列化器接口（可用于 WorkspaceState、日志等）
 */
public interface Serializer<T> {
    String serialize(T obj) throws Exception;
    T deserialize(String raw) throws Exception;
}