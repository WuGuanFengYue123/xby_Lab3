package com.team20.editor.extension.spi.command;

import com.team20.editor.domain.command.Command;
import java.util.function.Supplier;

/**
 * 命令元数据描述。可留在 domain，也可放入 SPI (取决于架构偏好)。
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