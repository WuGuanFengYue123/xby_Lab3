package com.team20.editor.domain.command;

import java.util.function.Supplier;

/**
 * 命令元数据描述。
 */
public record CommandDescriptor(
        String name,
        Supplier<Command> factory,
        String description,
        boolean undoable) {
    public static CommandDescriptor of(String name, Supplier<Command> factory) {
        return new CommandDescriptor(name, factory, "", false);
    }

    public static CommandDescriptor of(String name, Supplier<Command> factory, String description) {
        return new CommandDescriptor(name, factory, description, false);
    }

    public static CommandDescriptor undoable(String name, Supplier<Command> factory, String description) {
        return new CommandDescriptor(name, factory, description, true);
    }
}
