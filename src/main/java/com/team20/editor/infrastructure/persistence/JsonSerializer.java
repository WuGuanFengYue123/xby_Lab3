package com.team20.editor.infrastructure.persistence;

import java.util.Objects;

/**
 * Minimal JsonSerializer shim to satisfy Serializer SPI.
 */
public class JsonSerializer implements Serializer<Object> {

    @Override
    public String serialize(Object obj) throws Exception {
        if (obj == null) return "null";
        return Objects.toString(obj);
    }

    @Override
    public Object deserialize(String raw) throws Exception {
        // not implemented: core ships a minimal placeholder
        throw new UnsupportedOperationException("JsonSerializer.deserialize(String) not implemented in core; provide a plugin serializer for full support.");
    }
}