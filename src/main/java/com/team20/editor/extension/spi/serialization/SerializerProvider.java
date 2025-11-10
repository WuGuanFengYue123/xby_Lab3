package com.team20.editor.extension.spi.serialization;

import com.team20.editor.infrastructure.persistence.Serializer;
import java.util.Collection;

public interface SerializerProvider {
    Collection<Serializer> serializers();
}