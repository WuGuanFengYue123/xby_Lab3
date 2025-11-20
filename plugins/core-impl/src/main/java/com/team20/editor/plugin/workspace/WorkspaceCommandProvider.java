package com.team20.editor.plugin.workspace;

import com.team20.editor.extension.spi.command.CommandProvider;
import com.team20.editor.extension.registry.CommandRegistry;
import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.command.CommandDescriptor; // domain descriptor
import com.team20.editor.domain.command.impl.workspace.CloseCommand;
import com.team20.editor.domain.command.impl.workspace.EditCommand;
import com.team20.editor.domain.command.impl.workspace.SaveCommand;
import com.team20.editor.domain.command.impl.workspace.LoadCommand;
import com.team20.editor.domain.command.impl.workspace.RedoCommand;
import com.team20.editor.domain.command.impl.workspace.EditorListCommand;
import com.team20.editor.bootstrap.ApplicationContext;
import com.team20.editor.extension.registry.DefaultCommandRegistry;

import java.util.List;

/**
 * Workspace provider: register factories that create workspace-related
 * commands.
 */
public class WorkspaceCommandProvider implements CommandProvider {

    public WorkspaceCommandProvider() {
        // no registry work in ctor
    }

    @Override
    public String getProviderName() {
        return "workspace-provider";
    }

    @Override
    public List<com.team20.editor.domain.command.CommandDescriptor> getCommandDescriptors() {
        return List.of();
    }

    @Override
    public void registerFactories(CommandRegistry registry) {
        registry.registerFactory("save", (rawArgs) -> {
            ApplicationContext ctx = DefaultCommandRegistry.getApplicationContext();
            String path = (rawArgs == null || rawArgs.isBlank()) ? null : rawArgs.trim();
            return new SaveCommand(ctx.persistenceManager(), path);
        });

        registry.registerFactory("load", (rawArgs) -> {
            ApplicationContext ctx = DefaultCommandRegistry.getApplicationContext();
            if (rawArgs == null || rawArgs.isBlank()) {
                throw new IllegalArgumentException("load requires a filepath");
            }
            return new LoadCommand(ctx.editorFactory(), ctx.persistenceManager(), rawArgs.trim());
        });

        registry.registerFactory("edit", (rawArgs) -> {
            ApplicationContext ctx = DefaultCommandRegistry.getApplicationContext();
            if (rawArgs == null || rawArgs.isBlank()) {
                throw new IllegalArgumentException("edit requires a filepath");
            }
            return new EditCommand(ctx.editorFactory(), ctx.persistenceManager(), rawArgs.trim());
        });

        registry.registerFactory("close", (rawArgs) -> {
            String path = (rawArgs == null || rawArgs.isBlank()) ? null : rawArgs.trim();
            return new CloseCommand(path);
        });

        registry.registerFactory("redo", (rawArgs) -> {
            ApplicationContext ctx = DefaultCommandRegistry.getApplicationContext();
            return new RedoCommand(ctx.commandInvoker());
        });

        registry.registerFactory("editor-list", (rawArgs) -> new EditorListCommand());
    }
}