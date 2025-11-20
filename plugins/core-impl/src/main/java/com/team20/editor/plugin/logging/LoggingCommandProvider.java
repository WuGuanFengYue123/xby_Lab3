package com.team20.editor.plugin.logging;

import com.team20.editor.extension.spi.command.CommandProvider;
import com.team20.editor.extension.registry.CommandRegistry;
import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.command.CommandDescriptor; // domain descriptor
import com.team20.editor.domain.command.impl.logging.LogOnCommand;
import com.team20.editor.domain.command.impl.logging.LogOffCommand;
import com.team20.editor.domain.command.impl.logging.LogShowCommand;

import java.util.List;

/**
 * Register logging command factories.
 */
public class LoggingCommandProvider implements CommandProvider {

    public LoggingCommandProvider() {
        // no registry work here
    }

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
        registry.registerFactory("log-on", (rawArgs) -> new LogOnCommand());
        registry.registerFactory("log-off", (rawArgs) -> new LogOffCommand());
        registry.registerFactory("log-show", (rawArgs) -> new LogShowCommand());
    }
}