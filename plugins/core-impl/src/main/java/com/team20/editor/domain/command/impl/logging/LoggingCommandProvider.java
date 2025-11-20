package com.team20.editor.domain.command.impl.logging;

import com.team20.editor.domain.command.CommandDescriptor;
import com.team20.editor.extension.spi.command.CommandProvider;

import java.util.List;

/**
 * Adapt LoggingCommandProvider to the unified CommandProvider SPI.
 */
public class LoggingCommandProvider implements CommandProvider {

    @Override
    public String getProviderName() {
        return "logging-core";
    }

    @Override
    public List<CommandDescriptor> getCommandDescriptors() {
        return List.of();
    }
}