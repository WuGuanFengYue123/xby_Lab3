package com.team20.editor.plugin.logging;

import com.team20.editor.extension.spi.command.CommandProvider;
import com.team20.editor.extension.registry.CommandRegistry;

import java.util.List;

/**
 * Registers logging command factories: log-on, log-off, log-show.
 *
 * Factories create command instances explicitly to match available
 * constructors.
 */
public class LoggingCommandProvider implements CommandProvider {

    @Override
    public String getProviderName() {
        return "logging-provider";
    }

    @Override
    public List<com.team20.editor.domain.command.CommandDescriptor> getCommandDescriptors() {
        return List.of();
    }

    @Override
    public void registerFactories(CommandRegistry registry) {
        // log-on [file]
        registry.registerFactory("log-on", (rawArgs) -> {
            if (rawArgs == null || rawArgs.isBlank()) {
                return new com.team20.editor.domain.command.impl.logging.LogOnCommand();
            } else {
                return new com.team20.editor.domain.command.impl.logging.LogOnCommand(rawArgs.trim());
            }
        });

        // log-off [file]
        registry.registerFactory("log-off", (rawArgs) -> {
            if (rawArgs == null || rawArgs.isBlank()) {
                return new com.team20.editor.domain.command.impl.logging.LogOffCommand();
            } else {
                return new com.team20.editor.domain.command.impl.logging.LogOffCommand(rawArgs.trim());
            }
        });

        // log-show [file]
        registry.registerFactory("log-show", (rawArgs) -> {
            if (rawArgs == null || rawArgs.isBlank()) {
                return new com.team20.editor.domain.command.impl.logging.LogShowCommand();
            } else {
                return new com.team20.editor.domain.command.impl.logging.LogShowCommand(rawArgs.trim());
            }
        });
    }
}