package com.team20.editor.extension.registry;

import com.team20.editor.bootstrap.ApplicationContext;
// Use the single authoritative AutoLoadingCommandRegistry implemented in domain.registry
import com.team20.editor.domain.command.registry.AutoLoadingCommandRegistry;

/**
 * Singleton accessor for the command registry used by core and by plugin
 * factories.
 * Main will setApplicationContext(...) so factories can look up core services
 * at runtime.
 */
public final class DefaultCommandRegistry {

    private static final AutoLoadingCommandRegistry INSTANCE = new AutoLoadingCommandRegistry();

    // ApplicationContext is set by Main after constructing it so providers'
    // factories
    // can access core services (editorFactory, persistenceManager, commandInvoker,
    // ...).
    private static volatile ApplicationContext applicationContext;

    private DefaultCommandRegistry() {
    }

    public static AutoLoadingCommandRegistry getInstance() {
        return INSTANCE;
    }

    public static void setApplicationContext(ApplicationContext ctx) {
        applicationContext = ctx;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}