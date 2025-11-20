package com.team20.editor.extension.registry;

import com.team20.editor.domain.command.Command;

import java.util.Set;
import java.util.function.Function;

/**
 * Command registry interface.
 */
public interface CommandRegistry {

    void register(String name, Command command);

    void registerFactory(String name, Function<String, Command> factory);

    Command create(String name, String rawArgs);

    Command getCommand(String name);

    boolean hasCommand(String name);

    Set<String> getCommandNames();

    /**
     * Return the human-readable description for a command name if available (may be
     * null).
     */
    String getCommandDescription(String name);
}