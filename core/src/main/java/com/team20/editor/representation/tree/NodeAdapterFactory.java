package com.team20.editor.representation.tree;

public interface NodeAdapterFactory {
    Class<?> supportsType();

    Node adapt(Object source, NodeAdaptContext ctx);

    interface NodeAdaptContext {
        <T> T get(Class<T> type);
    }
}
