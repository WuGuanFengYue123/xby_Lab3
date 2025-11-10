package com.team20.editor.domain.command.registry;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.command.CommandDescriptor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AutoLoadingCommandRegistry {

    private final Map<String, CommandDescriptor> registry = new ConcurrentHashMap<>();

    public void registerDescriptor(CommandDescriptor descriptor) {
        registry.put(descriptor.name(), descriptor);
    }

    public boolean has(String name) {
        return registry.containsKey(name);
    }

    public Command create(String name) {
        CommandDescriptor descriptor = registry.get(name);
        return descriptor == null ? null : descriptor.factory().get();
    }

    public Set<String> names() {
        return Collections.unmodifiableSet(registry.keySet());
    }

    public Optional<CommandDescriptor> descriptor(String name) {
        return Optional.ofNullable(registry.get(name));
    }
}