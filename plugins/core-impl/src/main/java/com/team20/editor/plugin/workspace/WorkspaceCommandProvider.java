package com.team20.editor.plugin.workspace;

import com.team20.editor.domain.command.CommandDescriptor;
import com.team20.editor.extension.spi.command.CommandProvider;
import com.team20.editor.extension.registry.CommandRegistry;

import java.util.List;

/**
 * Unified WorkspaceCommandProvider - 注册 workspace 相关命令的工厂
 *
 * - 与新的 EditCommand(String) 签名兼容（edit 只切换到已打开的文件）
 * - Registers common workspace commands: load, init, save, close, edit,
 * editor-list,
 * dir-tree, undo, redo, exit, debug-inspect
 */
public class WorkspaceCommandProvider implements CommandProvider {

        public WorkspaceCommandProvider() {
                // no-op
        }

        @Override
        public String getProviderName() {
                return "workspace-provider";
        }

        @Override
        public List<CommandDescriptor> getCommandDescriptors() {
                // Keep empty or populate with descriptors if you have metadata definitions.
                return List.of();
        }

        @Override
        public void registerFactories(CommandRegistry registry) {
                // close
                registry.registerFactory("close",
                                (rawArgs) -> new com.team20.editor.domain.command.impl.workspace.CloseCommand(rawArgs));

                // edit (switch to already-open editor)
                registry.registerFactory("edit",
                                (rawArgs) -> {
                                        String arg = (rawArgs == null) ? null : rawArgs.trim();
                                        if (arg == null || arg.isBlank()) {
                                                return null;
                                        }
                                        return new com.team20.editor.domain.command.impl.workspace.EditCommand(arg);
                                });

                // init (file [with-log])
                registry.registerFactory("init", (rawArgs) -> {
                        if (rawArgs == null || rawArgs.isBlank()) {
                                return new com.team20.editor.domain.command.impl.workspace.InitCommand(null, false);
                        }
                        String[] parts = rawArgs.trim().split("\\s+");
                        String path = parts.length >= 1 ? parts[0] : null;
                        boolean withLog = (parts.length >= 2) && "with-log".equalsIgnoreCase(parts[1]);
                        return new com.team20.editor.domain.command.impl.workspace.InitCommand(path, withLog);
                });

                // load
                registry.registerFactory("load",
                                (rawArgs) -> new com.team20.editor.domain.command.impl.workspace.LoadCommand(
                                                com.team20.editor.extension.registry.DefaultCommandRegistry
                                                                .getApplicationContext().editorFactory(),
                                                com.team20.editor.extension.registry.DefaultCommandRegistry
                                                                .getApplicationContext().persistenceManager(),
                                                rawArgs));

                // save
                registry.registerFactory("save",
                                (rawArgs) -> new com.team20.editor.domain.command.impl.workspace.SaveCommand(
                                                com.team20.editor.extension.registry.DefaultCommandRegistry
                                                                .getApplicationContext().persistenceManager(),
                                                rawArgs));

                // editor-list
                registry.registerFactory("editor-list",
                                (rawArgs) -> new com.team20.editor.domain.command.impl.workspace.EditorListCommand());

                // dir-tree
                registry.registerFactory("dir-tree", (rawArgs) -> {
                        String p = (rawArgs == null || rawArgs.isBlank()) ? "." : rawArgs.trim();
                        return new com.team20.editor.domain.command.impl.workspace.DirTreeCommand(p);
                });

                // undo / redo
                registry.registerFactory("undo",
                                (rawArgs) -> new com.team20.editor.domain.command.impl.workspace.UndoCommand());
                registry.registerFactory("redo",
                                (rawArgs) -> new com.team20.editor.domain.command.impl.workspace.RedoCommand());

                // exit
                registry.registerFactory("exit",
                                (rawArgs) -> new com.team20.editor.domain.command.impl.workspace.ExitCommand());

                // debug-inspect
                registry.registerFactory("debug-inspect",
                                (rawArgs) -> new com.team20.editor.domain.command.impl.workspace.DebugInspectCommand());
        }

        @Override
        public String toString() {
                return "WorkspaceCommandProvider";
        }
}