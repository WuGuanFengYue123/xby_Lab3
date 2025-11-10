package com.team20.editor.extension.registry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.team20.editor.domain.editor.Editor;
import com.team20.editor.extension.spi.editor.EditorProvider;

/**
 * 自动加载的编辑器注册中心，与现有 EditorRegistry 并行存在。
 */
public class AutoLoadingEditorRegistry {

    private final Map<String, EditorProvider.EditorRegistration> registrations = new ConcurrentHashMap<>();

    public AutoLoadingEditorRegistry() {
        loadViaServiceLoader();
    }

    public AutoLoadingEditorRegistry(Collection<EditorProvider> providers) {
        providers.forEach(this::registerProvider);
    }

    private void loadViaServiceLoader() {
        ServiceLoader.load(EditorProvider.class).forEach(this::registerProvider);
    }

    public void registerProvider(EditorProvider provider) {
        for (EditorProvider.EditorRegistration r : provider.editors()) {
            registrations.put(r.type(), r);
        }
    }

    public boolean supports(String type) {
        return registrations.containsKey(type);
    }

    public Editor create(String type) {
        EditorProvider.EditorRegistration r = registrations.get(type);
        return r == null ? null : r.factory().create();
    }

    public Set<String> types() {
        return Collections.unmodifiableSet(registrations.keySet());
    }

    public Optional<EditorProvider.EditorRegistration> registration(String type) {
        return Optional.ofNullable(registrations.get(type));
    }
}
