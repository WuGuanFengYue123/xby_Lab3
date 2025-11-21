package com.team20.editor.representation.ui;

import com.team20.editor.bootstrap.ApplicationContext;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.domain.command.Command;
import com.team20.editor.extension.registry.DefaultCommandRegistry;

import java.util.Scanner;

/**
 * CLI adapter: delegate command creation to DefaultCommandRegistry (plugin
 * factories).
 */
public class CommandLineInterface {

    private final Workspace workspace;
    private final ApplicationContext context;
    private final Scanner scanner;

    public CommandLineInterface(ApplicationContext context, Workspace workspace) {
        this.context = context;
        this.workspace = workspace;
        this.scanner = new Scanner(System.in);
        // ensure registry sees the application context
        DefaultCommandRegistry.setApplicationContext(context);
    }

    public void start() {
        System.out.println("Team20 Text Editor (CLI)");
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            if (line == null)
                break;
            line = line.trim();
            if (line.isEmpty())
                continue;
            if ("exit".equalsIgnoreCase(line) || "quit".equalsIgnoreCase(line))
                break;
            if ("help".equalsIgnoreCase(line)) {
                printHelp();
                continue;
            }

            String[] parts = line.split("\\s+", 2);
            String cmd = parts[0].toLowerCase();
            String args = parts.length > 1 ? parts[1] : "";

            Command command = DefaultCommandRegistry.getInstance().create(cmd, args);
            if (command != null) {
                command.execute(workspace);
            } else {
                System.out.println("Unknown command: " + cmd);
            }
        }
    }

    private void printHelp() {
        System.out.println("help: available commands: edit/load/save/append/... ");
    }
}