package com.team20.editor.plugin.workspace;

import com.team20.editor.extension.spi.command.CommandProvider;
import com.team20.editor.extension.registry.CommandRegistry;

import java.util.List;

/**
 * Provider for workspace-related commands (init, edit, load, save, close,
 * editor-list, dir-tree, undo, redo, ...).
 * Factories create command instances from rawArgs (String).
 */
public class WorkspaceCommandProvider implements CommandProvider {

        public WorkspaceCommandProvider() {
                // no-op ctor
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
                // register close
                registry.registerFactory("close",
                                (rawArgs) -> new com.team20.editor.domain.command.impl.workspace.CloseCommand(rawArgs));

                // register edit (open/switch)
                registry.registerFactory("edit",
                                (rawArgs) -> new com.team20.editor.domain.command.impl.workspace.EditCommand(
                                                com.team20.editor.extension.registry.DefaultCommandRegistry
                                                                .getApplicationContext().editorFactory(),
                                                com.team20.editor.extension.registry.DefaultCommandRegistry
                                                                .getApplicationContext()
                                                                .persistenceManager(),
                                                rawArgs));

                // register init (file [with-log])
                registry.registerFactory("init", (rawArgs) -> {
                        if (rawArgs == null || rawArgs.isBlank()) {
                                return new com.team20.editor.domain.command.impl.workspace.InitCommand(null, false);
                        }
                        String[] parts = rawArgs.trim().split("\\s+");
                        String path = parts.length >= 1 ? parts[0] : null;
                        boolean withLog = (parts.length >= 2) && "with-log".equalsIgnoreCase(parts[1]);
                        return new com.team20.editor.domain.command.impl.workspace.InitCommand(path, withLog);
                });

                // register load
                registry.registerFactory("load",
                                (rawArgs) -> new com.team20.editor.domain.command.impl.workspace.LoadCommand(
                                                com.team20.editor.extension.registry.DefaultCommandRegistry
                                                                .getApplicationContext().editorFactory(),
                                                com.team20.editor.extension.registry.DefaultCommandRegistry
                                                                .getApplicationContext()
                                                                .persistenceManager(),
                                                rawArgs));

                // register save
                registry.registerFactory("save",
                                (rawArgs) -> new com.team20.editor.domain.command.impl.workspace.SaveCommand(
                                                com.team20.editor.extension.registry.DefaultCommandRegistry
                                                                .getApplicationContext()
                                                                .persistenceManager(),
                                                rawArgs));

                // register editor-list
                registry.registerFactory("editor-list",
                                (rawArgs) -> new com.team20.editor.domain.command.impl.workspace.EditorListCommand());

                // register dir-tree
                registry.registerFactory("dir-tree", (rawArgs) -> {
                        String p = (rawArgs == null || rawArgs.isBlank()) ? "." : rawArgs.trim();
                        return new com.team20.editor.domain.command.impl.workspace.DirTreeCommand(p);
                });

                // register undo and redo using no-arg constructors (match
                // UndoCommand/RedoCommand implementations)
                registry.registerFactory("undo",
                                (rawArgs) -> new com.team20.editor.domain.command.impl.workspace.UndoCommand());
                registry.registerFactory("redo",
                                (rawArgs) -> new com.team20.editor.domain.command.impl.workspace.RedoCommand());
                registry.registerFactory("exit",
                                (rawArgs) -> new com.team20.editor.domain.command.impl.workspace.ExitCommand());
                registry.registerFactory("debug-inspect",
                                rawArgs -> new com.team20.editor.domain.command.impl.workspace.DebugInspectCommand());
        }
}