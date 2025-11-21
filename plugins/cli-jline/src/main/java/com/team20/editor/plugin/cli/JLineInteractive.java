package com.team20.editor.plugin.cli;

import com.team20.editor.bootstrap.ApplicationContext;
import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.command.UndoableCommand;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.extension.registry.DefaultCommandRegistry;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * JLineInteractive: interactive loop using JLine (supports up/down history,
 * editing, etc).
 *
 * Important: this interactive loop delegates ALL commands (including "exit")
 * to the command registry (DefaultCommandRegistry). Do NOT special-case "exit"
 * here; the ExitCommand implementation (a plugin) must handle prompt/save/exit.
 */
public class JLineInteractive {

    public static void run(ApplicationContext context, Workspace workspace) throws Exception {
        Terminal terminal = null;
        boolean isDumb = false;

        try {
            terminal = TerminalBuilder.builder().system(true).build();
        } catch (Throwable t) {
            System.err.println("JLine: system terminal unavailable: " + t.getMessage());
        }

        if (terminal == null) {
            try {
                terminal = TerminalBuilder.builder().streams(System.in, System.out).build();
            } catch (Throwable t) {
                System.err.println("JLine: streams terminal unavailable: " + t.getMessage());
            }
        }

        if (terminal == null) {
            try {
                terminal = TerminalBuilder.builder().dumb(true).build();
                isDumb = true;
                System.err.println("JLine: created DUMB terminal (limited features).");
            } catch (Throwable t) {
                System.err.println("JLine: cannot create any terminal: " + t.getMessage());
                terminal = null;
            }
        }

        Path historyFile = Path.of(System.getProperty("user.home", "."), ".team20_history");

        if (terminal != null && !isDumb) {
            LineReader reader = null;
            try {
                DefaultParser parser = new DefaultParser();
                reader = LineReaderBuilder.builder()
                        .terminal(terminal)
                        .parser(parser)
                        .variable(LineReader.HISTORY_FILE, historyFile.toString())
                        .build();
                try {
                    DefaultHistory hist = (DefaultHistory) reader.getHistory();
                    hist.attach(reader);
                } catch (Exception ignored) {
                }

                System.out.println("Using JLine interactive console (history ↑/↓ supported).");
                interactiveLoopWithReader(reader, context, workspace);
                return;
            } catch (Throwable t) {
                System.err.println("JLine interactive failed: " + t.getMessage());
                try {
                    terminal.close();
                } catch (Exception ignored) {
                }
            }
        }

        System.out.println("JLine not available or limited; falling back to basic console with persistent history.");
        simpleFallbackConsole(context, workspace, historyFile);
    }

    private static void interactiveLoopWithReader(LineReader reader, ApplicationContext context, Workspace workspace) {
        while (true) {
            String line;
            try {
                line = reader.readLine("> ");
            } catch (UserInterruptException e) {
                // Ctrl-C -> ignore and continue
                continue;
            } catch (EndOfFileException e) {
                // Ctrl-D -> exit (EOF) — we treat as end-of-input and break
                System.out.println("退出编辑器...");
                break;
            } catch (Throwable t) {
                System.err.println("Reader error: " + t.getMessage());
                break;
            }

            if (line == null)
                break;
            String input = line.trim();
            if (input.isEmpty())
                continue;

            // Normalize input (strip surrounding single/double quotes)
            String normalized = normalizeInput(input);

            // If user asked for help, display ApplicationContext help directly if
            // available,
            // otherwise fall through to usual command handling.
            if ("help".equalsIgnoreCase(normalized)) {
                if (context != null) {
                    System.out.println(context.showHelp());
                } else {
                    // no context available — delegate to registry (if present)
                    executeCommandLine(normalized, context, workspace);
                }
                continue;
            }

            // Delegate all commands (including "exit") to registry/executor
            executeCommandLine(input, context, workspace);
            // Note: ExitCommand (plugin) is expected to call System.exit(0) or otherwise
            // terminate.
            // If it returns without exiting, we continue the loop.
        }
    }

    private static String normalizeInput(String input) {
        String s = input.trim();
        if (s.length() >= 2) {
            if ((s.startsWith("'") && s.endsWith("'")) || (s.startsWith("\"") && s.endsWith("\""))) {
                s = s.substring(1, s.length() - 1).trim();
            }
        }
        return s;
    }

    private static void simpleFallbackConsole(ApplicationContext context, Workspace workspace, Path historyFile) {
        java.io.Console cons = System.console();
        java.util.List<String> history = new java.util.ArrayList<>();
        // load history if possible
        try {
            File hf = historyFile.toFile();
            if (hf.exists()) {
                java.nio.file.Files.lines(hf.toPath()).forEach(history::add);
            } else {
                hf.getParentFile().mkdirs();
                hf.createNewFile();
            }
        } catch (Exception ignored) {
        }

        java.util.Scanner scanner = new java.util.Scanner(System.in);
        while (true) {
            try {
                System.out.print("> ");
                if (!scanner.hasNextLine())
                    break;
                String line = scanner.nextLine();
                if (line == null)
                    break;
                String input = line.trim();
                if (input.isEmpty())
                    continue;

                // support history list
                if ("history".equalsIgnoreCase(input)) {
                    for (int i = 0; i < history.size(); i++) {
                        System.out.printf("%d: %s%n", i + 1, history.get(i));
                    }
                    continue;
                }
                // support recall !n
                if (input.startsWith("!")) {
                    try {
                        int idx = Integer.parseInt(input.substring(1).trim());
                        if (idx >= 1 && idx <= history.size()) {
                            input = history.get(idx - 1);
                            System.out.println(input);
                        } else {
                            System.out.println("Invalid history index");
                            continue;
                        }
                    } catch (NumberFormatException nfe) {
                        System.out.println("Invalid history recall syntax");
                        continue;
                    }
                }

                // Delegate all commands (including exit) to command execution so plugin can
                // handle exit properly.
                executeCommandLine(input, context, workspace);

                history.add(input);
                // append to history file
                try {
                    java.nio.file.Files.writeString(historyFile, input + System.lineSeparator(),
                            java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                } catch (Exception ignored) {
                }
            } catch (Throwable t) {
                System.err.println("Console error: " + t.getMessage());
                break;
            }
        }
        scanner.close();
    }

    private static void executeCommandLine(String input, ApplicationContext context, Workspace workspace) {
        try {
            String[] parts = input.split("\\s+", 2);
            String commandName = parts[0].toLowerCase();
            String rawArgs = parts.length > 1 ? parts[1] : "";

            Command command = DefaultCommandRegistry.getInstance().create(commandName, rawArgs);
            if (command != null) {
                if (command instanceof UndoableCommand uc) {
                    context.commandInvoker().executeAndRecord(uc, workspace);
                } else {
                    command.execute(workspace);
                }
            } else {
                System.out.println("未知命令: " + input);
                System.out.println("输入 'help' 查看帮助");
            }
        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
        }
    }
}