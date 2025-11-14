package com.team20.editor.representation.ui;

import com.team20.editor.bootstrap.ApplicationContext;
import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.command.UndoableCommand;
import com.team20.editor.domain.command.CommandInvoker;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.domain.editor.Editor;
import com.team20.editor.domain.editor.text.TextEditor;
import com.team20.editor.domain.command.impl.text.AppendCommand;
import com.team20.editor.domain.command.impl.text.InsertCommand;
import com.team20.editor.domain.command.impl.text.ReplaceCommand;
import com.team20.editor.domain.command.impl.text.DeleteCommand;
import com.team20.editor.domain.command.impl.text.ShowCommand;
import com.team20.editor.domain.command.impl.workspace.EditCommand;
import com.team20.editor.domain.command.impl.workspace.CloseCommand;
import com.team20.editor.domain.command.impl.workspace.LoadCommand;
import com.team20.editor.domain.command.impl.workspace.SaveCommand;

import java.util.Scanner;

/**
 * 命令行界面
 *
 * - 负责主循环：读取用户输入、解析、执行并显示结果
 * - 将可撤销命令交由 CommandInvoker 执行并记录历史
 */
public class CommandLineInterface {

    private final Workspace workspace;
    private final CommandInvoker invoker;
    private final ApplicationContext context;
    private final CommandParser parser;
    private final Scanner scanner;
    private boolean running;

    /**
     * 构造函数
     *
     * @param context   应用上下文（包含 persistence / registry / invoker 等）
     * @param workspace 工作区
     */
    public CommandLineInterface(ApplicationContext context, Workspace workspace) {
        this.context = context;
        this.workspace = workspace;
        this.invoker = context.commandInvoker();
        this.parser = new CommandParser();
        this.scanner = new Scanner(System.in);
        this.running = false;
    }

    /**
     * 启动 CLI
     */
    public void start() {
        running = true;
        System.out.println("Team20 Text Editor");
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
                if (line.isEmpty())
                    continue;

                CommandParser.ParsedCommand pc = parser.parse(line);
                if (pc == null)
                    continue;

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
                    case "init":
                        handleInit(args);
                        break;
                    case "append":
                        handleAppend(args);
                        break;
                    case "insert":
                        handleInsert(args);
                        break;
                    case "replace":
                        handleReplace(args);
                        break;
                    case "delete":
                        handleDelete(args);
                        break;
                    case "show":
                        handleShow(args);
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
                    case "close":
                        handleClose(args);
                        break;
                    case "editor-list":
                    case "editors":
                        listEditors();
                        break;
                    case "status":
                        showStatus();
                        break;
                    case "undo":
                    case "redo":
                        handleUndoRedo(cmd);
                        break;
                    default:
                        System.out.println("Unknown command: " + cmd + "  (type 'help' for list)");
                }

            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void printHelp() {
        System.out.println("Available commands:");
        System.out.println("  init <name>                 - create a new empty file and open it");
        System.out.println("  edit <filepath>             - open or switch to a file");
        System.out.println("  load <filepath>             - load a file from disk");
        System.out.println("  save [filepath]             - save current file (optional path)");
        System.out.println("  close [filepath]            - close current or specified file");
        System.out.println("  editor-list                 - list opened editors");
        System.out.println("  append \"text\"              - append a new line with text to file (undoable)");
        System.out.println("  insert line:col \"text\"     - insert text at line:col (undoable)");
        System.out.println("  replace line:col length \"text\" - replace length chars from position (undoable)");
        System.out.println("  delete line:col length      - delete length chars from position (undoable)");
        System.out.println("  show [start:end]            - display lines");
        System.out.println("  undo / redo                 - undo or redo last edit");
        System.out.println("  status                      - show workspace status");
        System.out.println("  help                        - this help");
        System.out.println("  exit                        - quit");
    }

    private void handleInit(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: init <name>");
            return;
        }
        String name = args[0];
        TextEditor editor = new TextEditor(name);
        editor.loadContent("");
        workspace.addEditor(editor);
        workspace.setActiveEditor(editor);
        System.out.println("已创建文件: " + name);
    }

    private void handleAppend(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: append \"text\"");
            return;
        }
        String text = args[0];
        // Use domain AppendCommand (Undoable)
        UndoableCommand cmd = new AppendCommand(text);
        invoker.executeAndRecord(cmd, workspace);
        System.out.println("已追加文本");
    }

    private void handleInsert(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: insert line:col \"text\"");
            return;
        }
        String pos = args[0];
        String text = args[1];
        int[] lc = parseLineCol(pos);
        if (lc == null) {
            System.out.println("Invalid position: " + pos);
            return;
        }
        UndoableCommand cmd = new InsertCommand(lc[0], lc[1], text);
        invoker.executeAndRecord(cmd, workspace);
        System.out.println("已插入文本");
    }

    private void handleReplace(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: replace line:col length \"text\"");
            return;
        }
        String pos = args[0];
        String lenStr = args[1];
        String text = args[2];
        int[] lc = parseLineCol(pos);
        if (lc == null) {
            System.out.println("Invalid position: " + pos);
            return;
        }
        int length;
        try {
            length = Integer.parseInt(lenStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid length: " + lenStr);
            return;
        }
        UndoableCommand cmd = new ReplaceCommand(lc[0], lc[1], length, text);
        invoker.executeAndRecord(cmd, workspace);
        System.out.println("已替换文本");
    }

    private void handleDelete(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: delete line:col length");
            return;
        }
        String pos = args[0];
        String lenStr = args[1];
        int[] lc = parseLineCol(pos);
        if (lc == null) {
            System.out.println("Invalid position: " + pos);
            return;
        }
        int length;
        try {
            length = Integer.parseInt(lenStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid length: " + lenStr);
            return;
        }
        UndoableCommand cmd = new DeleteCommand(lc[0], lc[1], length);
        invoker.executeAndRecord(cmd, workspace);
        System.out.println("已删除文本");
    }

    private void handleShow(String[] args) {
        Integer start = null, end = null;
        if (args.length >= 1) {
            String spec = args[0];
            if (spec.contains(":")) {
                String[] parts = spec.split(":", 2);
                try {
                    start = parts[0].isEmpty() ? null : Integer.parseInt(parts[0]);
                    end = parts[1].isEmpty() ? null : Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid show range: " + spec);
                    return;
                }
            } else {
                try {
                    start = Integer.parseInt(spec);
                    end = start;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid line number: " + spec);
                    return;
                }
            }
        }
        Command showCmd = new ShowCommand(start, end);
        showCmd.execute(workspace);
    }

    private void handleEdit(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: edit <filepath>");
            return;
        }
        String path = args[0];
        EditCommand cmd = new EditCommand(context.persistenceManager(), path);
        cmd.execute(workspace);
    }

    private void handleLoad(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: load <filepath>");
            return;
        }
        String path = args[0];
        LoadCommand cmd = new LoadCommand(context.persistenceManager(), path);
        cmd.execute(workspace);
    }

    private void handleSave(String[] args) {
        String path = (args.length >= 1) ? args[0] : null;
        SaveCommand cmd = new SaveCommand(context.persistenceManager(), path);
        cmd.execute(workspace);
    }

    private void handleClose(String[] args) {
        String path = (args.length >= 1) ? args[0] : null;
        CloseCommand cmd = new CloseCommand(path);
        cmd.execute(workspace);
    }

    private void listEditors() {
        if (!workspace.hasEditors()) {
            System.out.println("没有打开的编辑器");
            return;
        }
        int i = 0;
        for (Editor e : workspace.getEditors()) {
            String activeMark = (e == workspace.getActiveEditor()) ? " *active*" : "";
            System.out.printf("%d: %s%s%n", ++i, e.getName(), activeMark);
        }
    }

    private void showStatus() {
        System.out.println("当前工作区: " + workspace);
        System.out.println("打开编辑器数: " + workspace.getEditorCount());
        Editor active = workspace.getActiveEditor();
        System.out.println("当前活动编辑器: " + (active == null ? "(none)" : active.getName()));
        System.out.println("Undo 可用: " + invoker.canUndo() + ", Redo 可用: " + invoker.canRedo());
    }

    private void handleUndoRedo(String cmd) {
        try {
            if ("undo".equals(cmd)) {
                invoker.undo(workspace);
                System.out.println("已撤销");
            } else {
                invoker.redo(workspace);
                System.out.println("已重做");
            }
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private int[] parseLineCol(String spec) {
        if (spec == null || !spec.contains(":"))
            return null;
        String[] parts = spec.split(":", 2);
        try {
            int line = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);
            return new int[] { line, col };
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 停止 CLI
     */
    public void stop() {
        running = false;
        // don't close System.in scanner if other code might use it; close here to be
        // safe
        try {
            scanner.close();
        } catch (Exception ignored) {
        }
    }
}