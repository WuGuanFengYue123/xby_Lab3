package com.team20.editor.extension.registry;

import com.team20.editor.extension.spi.editor.EditorProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Auto-loading registry for editor providers.
 *
 * Adapted to the unified EditorProvider.getEditorRegistrations() API.
 */
public class AutoLoadingEditorRegistry {

    private final List<EditorProvider> providers = new ArrayList<>();

    public AutoLoadingEditorRegistry() {
        ServiceLoader<EditorProvider> loader = ServiceLoader.load(EditorProvider.class);
        for (EditorProvider p : loader) {
            providers.add(p);
        }
    }

    public List<EditorProvider> getProviders() {
        return List.copyOf(providers);
    }

    /**
     * Return all editor registrations discovered from providers.
     */
    public List<EditorProvider.EditorRegistration> getRegistrations() {
        List<EditorProvider.EditorRegistration> out = new ArrayList<>();
        for (EditorProvider p : providers) {
            List<EditorProvider.EditorRegistration> regs = p.getEditorRegistrations();
            if (regs != null) out.addAll(regs);
        }
        return out;
    }
}