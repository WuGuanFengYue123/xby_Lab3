package com.team20.editor.domain.command.impl.logging;

import com.team20.editor.domain.command.CommandDescriptor;
import com.team20.editor.extension.spi.command.CommandProvider;

import java.util.Collection;
import java.util.List;

public class LoggingCommandProvider implements CommandProvider {
    @Override
    public Collection<CommandDescriptor> descriptors() {
        return List.of(
                CommandDescriptor.of("log:on", LogOnCommand::new, "Enable logging"),
                CommandDescriptor.of("log:off", LogOffCommand::new, "Disable logging"),
                CommandDescriptor.of("log:show", LogShowCommand::new, "Show log buffer"));
    }
}
