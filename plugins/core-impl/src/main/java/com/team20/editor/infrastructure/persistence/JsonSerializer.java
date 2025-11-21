package com.team20.editor.infrastructure.persistence;

import java.util.Objects;

public class JsonSerializer implements Serializer<Object> {

    @Override
    public String serialize(Object obj) throws Exception {
        return obj == null ? "null" : Objects.toString(obj);
    }

    @Override
    public Object deserialize(String raw) throws Exception {
        throw new UnsupportedOperationException("Deserialize not implemented in core-impl placeholder");
    }
}