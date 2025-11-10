package com.team20.editor.extension.spi.command;

import com.team20.editor.domain.command.CommandDescriptor;
import java.util.Collection;

public interface CommandProvider {
    Collection<CommandDescriptor> descriptors();
}