package com.team20.editor.domain.command.registry;

import com.team20.editor.domain.command.Command;
import com.team20.editor.extension.spi.command.CommandProvider;
import com.team20.editor.extension.registry.CommandRegistry;

import java.util.*;
import java.util.function.Function;
import java.util.ServiceLoader;

/**
 * Auto-loading command registry (domain implementation).
 * - loads providers via ServiceLoader
 * - calls provider.registerFactories(this) so providers may register factories
 * safely
 * - also adapts provider.getCommandDescriptors() if provided
 */
public class AutoLoadingCommandRegistry implements CommandRegistry {

    private final Map<String, Command> concreteCommands = new HashMap<>();
    private final Map<String, Function<String, Command>> factories = new HashMap<>();
    private final Map<String, String> descriptions = new HashMap<>();
    private final List<CommandProvider> providers = new ArrayList<>();

    public AutoLoadingCommandRegistry() {
        ServiceLoader<CommandProvider> loader = ServiceLoader.load(CommandProvider.class);
        for (CommandProvider p : loader) {
            providers.add(p);
            // Allow provider to register factories safely (registry instance is 'this')
            try {
                p.registerFactories(this);
            } catch (Throwable t) {
                System.err.println(
                        "Warning: provider " + p.getProviderName() + " threw in registerFactories: " + t.getMessage());
            }
            // Also process any returned descriptors (backwards-compat)
            try {
                List<?> descs = p.getCommandDescriptors();
                if (descs != null) {
                    for (Object od : descs) {
                        if (od == null)
                            continue;
                        if (od instanceof com.team20.editor.extension.spi.command.CommandDescriptor d) {
                            String name = d.name();
                            descriptions.putIfAbsent(name, d.description());
                            factories.putIfAbsent(name, (raw) -> d.factory().get());
                        } else if (od instanceof com.team20.editor.domain.command.CommandDescriptor dd) {
                            String name = dd.name();
                            descriptions.putIfAbsent(name, dd.description());
                            factories.putIfAbsent(name, (raw) -> dd.factory().get());
                        }
                    }
                }
            } catch (Throwable t) {
                System.err.println("Warning: provider " + p.getProviderName() + " threw during descriptor handling: "
                        + t.getMessage());
            }
        }
    }

    @Override
    public synchronized void register(String name, Command command) {
        if (name == null || command == null)
            return;
        concreteCommands.put(name, command);
    }

    @Override
    public synchronized void registerFactory(String name, Function<String, Command> factory) {
        if (name == null || factory == null)
            return;
        factories.put(name, factory);
    }

    @Override
    public synchronized Command create(String name, String rawArgs) {
        if (name == null)
            return null;
        Function<String, Command> f = factories.get(name);
        if (f != null) {
            return f.apply(rawArgs);
        }
        return concreteCommands.get(name);
    }

    @Override
    public synchronized Command getCommand(String name) {
        return concreteCommands.get(name);
    }

    @Override
    public synchronized boolean hasCommand(String name) {
        return factories.containsKey(name) || concreteCommands.containsKey(name);
    }

    @Override
    public synchronized Set<String> getCommandNames() {
        Set<String> out = new HashSet<>();
        out.addAll(concreteCommands.keySet());
        out.addAll(factories.keySet());
        return Collections.unmodifiableSet(out);
    }

    @Override
    public synchronized String getCommandDescription(String name) {
        return descriptions.get(name);
    }

    public List<CommandProvider> getProviders() {
        return List.copyOf(providers);
    }
}