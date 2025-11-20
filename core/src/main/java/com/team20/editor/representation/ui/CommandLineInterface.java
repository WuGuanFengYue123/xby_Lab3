// NOTE: only the specific methods that construct Edit/Load/Save commands were adjusted to pass the right factory/pm types.
// Overwrite the existing CommandLineInterface.java with the following content (keeps original structure but uses editorFactory correctly).
package com.team20.editor.representation.ui;

import com.team20.editor.bootstrap.ApplicationContext;
import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.command.UndoableCommand;
import com.team20.editor.domain.command.impl.text.*;
import com.team20.editor.domain.command.impl.workspace.*;
import com.team20.editor.domain.editor.Editor;
import com.team20.editor.domain.editor.text.TextEditor;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.domain.command.impl.workspace.EditCommand;
import com.team20.editor.domain.command.impl.workspace.LoadCommand;
import com.team20.editor.domain.command.impl.workspace.SaveCommand;
import com.team20.editor.extension.registry.EditorFactory;
import com.team20.editor.infrastructure.persistence.PersistenceManager;

import java.util.Scanner;

/**
 * 命令行界面（保留原有功能），修正为使用 ApplicationContext 提供的 EditorFactory。
 */
public class CommandLineInterface {

    private final Workspace workspace;
    private final ApplicationContext context;
    private final CommandParser parser;
    private final Scanner scanner;
    private boolean running;

    public CommandLineInterface(ApplicationContext context, Workspace workspace) {
        this.context = context;
        this.workspace = workspace;
        this.parser = new CommandParser();
        this.scanner = new Scanner(System.in);
        this.running = false;
    }

    public void start() {
        running = true;
        System.out.println("Team20 Text Editor (CLI)");
        System.out.println("Type 'help' for commands, 'exit' to quit");

        while (running) {
            try {
                System.out.print("> ");
                String line = scanner.nextLine();
                if (line == null) {
                    stop();
                    break;
                }
                line = line.trim();
                if (line.isEmpty()) continue;

                CommandParser.ParsedCommand pc = parser.parse(line);
                if (pc == null) continue;

                String cmd = pc.getCommandName().toLowerCase();
                String[] args = pc.getArgs();

                switch (cmd) {
                    case "exit":
                    case "quit":
                        stop();
                        break;
                    case "help":
                        printHelp();
                        break;
                    case "append":
                        handleAppend(args);
                        break;
                    case "edit":
                        handleEdit(args);
                        break;
                    case "load":
                        handleLoad(args);
                        break;
                    case "save":
                        handleSave(args);
                        break;
                    default:
                        System.out.println("Unknown command: " + cmd + "  (type 'help' for list)");
                }

            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void handleAppend(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: append \"text\"");
            return;
        }
        String text = args[0];
        UndoableCommand cmd = new AppendCommand(text);
        context.commandInvoker().executeAndRecord(cmd, workspace);
        System.out.println("Appended.");
    }

    private void handleEdit(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: edit <filepath>");
            return;
        }
        String path = args[0];
        EditCommand cmd = new EditCommand(context.editorFactory(), context.persistenceManager(), path);
        cmd.execute(workspace);
    }

    private void handleLoad(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: load <filepath>");
            return;
        }
        String path = args[0];
        LoadCommand cmd = new LoadCommand(context.editorFactory(), context.persistenceManager(), path);
        cmd.execute(workspace);
    }

    private void handleSave(String[] args) {
        String path = (args.length >= 1) ? args[0] : null;
        SaveCommand cmd = new SaveCommand(context.persistenceManager(), path);
        cmd.execute(workspace);
    }

    private void printHelp() {
        System.out.println("help: available commands: edit/load/save/append/... ");
    }

    public void stop() {
        running = false;
        try { scanner.close(); } catch (Exception ignored) {}
    }
}