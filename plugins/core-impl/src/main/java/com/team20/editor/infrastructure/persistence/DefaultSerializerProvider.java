package com.team20.editor.infrastructure.persistence;

import com.team20.editor.extension.spi.serialization.SerializerProvider;

/**
 * Default serializer provider - adapts the project-local JsonSerializer to the SPI.
 *
 * Note: this class still returns a concrete JsonSerializer instance but now implements the SPI method.
 * If you later want no built-in implementations at all, move this class out into a plugin module.
 */
public class DefaultSerializerProvider implements SerializerProvider {
    @Override
    public Serializer getSerializer() {
        return new JsonSerializer();
    }
}