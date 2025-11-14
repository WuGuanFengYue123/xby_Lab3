package com.team20.editor;

import com.team20.editor.bootstrap.ApplicationContext;
import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.command.UndoableCommand;
import com.team20.editor.domain.command.impl.text.*;
import com.team20.editor.domain.command.impl.workpace.*;
import com.team20.editor.domain.command.impl.workspace.*;
import com.team20.editor.domain.editor.text.TextEditor;
import com.team20.editor.domain.workspace.Workspace;

import java.time.Instant;
import java.util.Scanner;

/**
 * 程序入口（CLI 主循环）
 *
 * 说明：
 * - parseCommand 现在需要 ApplicationContext，以便创建带依赖的命令（如 Save/Load/Edit/Undo/Redo）
 * - runInteractiveLoop 在执行命令时会检查命令是否为 UndoableCommand，如果是则通过 CommandInvoker
 * 执行并记录历史
 */
public final class Main {

    public static void main(String[] args) {
        banner();

        try {
            ApplicationContext context = new ApplicationContext();
            Workspace workspace = context.createWorkspace();

            System.out.println(context.dumpSummary());
            System.out.println();
            System.out.println("Team20 Text Editor ready.");
            System.out.println("使用 'init test.txt' 创建文件开始编辑");
            System.out.println("输入 'help' 查看所有命令，'exit' 退出");
            System.out.println();

            // 启动交互循环（传入 context 以便创建带依赖的命令）
            runInteractiveLoop(context, workspace);
        } catch (Exception e) {
            System.err.println("启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runInteractiveLoop(ApplicationContext context, Workspace workspace) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.print("> ");

                if (!scanner.hasNextLine()) {
                    break;
                }

                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    continue;
                }

                if ("exit".equalsIgnoreCase(input) || "quit".equalsIgnoreCase(input)) {
                    System.out.println("退出编辑器...");
                    break;
                }

                if ("help".equalsIgnoreCase(input)) {
                    printHelp();
                    continue;
                }

                // 解析并执行命令（parseCommand 现在需要 context）
                Command command = parseCommand(input, workspace, context);
                if (command != null) {
                    // 如果是可撤销命令，走 invoker 记录历史；否则直接执行
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

        scanner.close();
    }

    private static Command parseCommand(String input, Workspace workspace, ApplicationContext context) {
        String[] parts = input.split("\\s+", 2);
        String commandName = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        try {
            switch (commandName) {
                case "init":
                    return parseInitCommand(args, workspace);
                case "append":
                    return new AppendCommand(extractQuotedText(args));
                case "insert":
                    return parseInsertCommand(args);
                case "delete":
                    return parseDeleteCommand(args);
                case "replace":
                    return parseReplaceCommand(args);
                case "show":
                    return parseShowCommand(args);
                case "status":
                    return createStatusCommand(workspace);
                // file & workspace operations (use context.persistenceManager() and
                // context.commandInvoker())
                case "save":
                    // save [filepath]
                    String savePath = args.isBlank() ? null : args.trim();
                    return new SaveCommand(context.persistenceManager(), savePath);
                case "load":
                    // load <filepath>
                    if (args.isBlank()) {
                        throw new IllegalArgumentException("load 需要参数：load <filepath>");
                    }
                    return new LoadCommand(context.persistenceManager(), args.trim());
                case "edit":
                    if (args.isBlank()) {
                        throw new IllegalArgumentException("edit 需要参数：edit <filepath>");
                    }
                    return new EditCommand(context.persistenceManager(), args.trim());
                case "close":
                    String closePath = args.isBlank() ? null : args.trim();
                    return new CloseCommand(closePath);
                case "undo":
                    // Undo is performed via invoker; represent as a simple command that calls
                    // invoker.undo
                    return new Command() {
                        @Override
                        public void execute(Workspace ws) {
                            try {
                                context.commandInvoker().undo(ws);
                                System.out.println("已撤销");
                            } catch (IllegalStateException e) {
                                System.out.println("Nothing to undo.");
                            }
                        }
                    };
                case "redo":
                    return new Command() {
                        @Override
                        public void execute(Workspace ws) {
                            try {
                                context.commandInvoker().redo(ws);
                                System.out.println("已重做");
                            } catch (IllegalStateException e) {
                                System.out.println("Nothing to redo.");
                            }
                        }
                    };
                case "editor-list":
                case "editors":
                    return new Command() {
                        @Override
                        public void execute(Workspace ws) {
                            if (!ws.hasEditors()) {
                                System.out.println("没有打开的编辑器");
                                return;
                            }
                            int i = 0;
                            for (var e : ws.getEditors()) {
                                String activeMark = (e == ws.getActiveEditor()) ? " *active*" : "";
                                System.out.printf("%d: %s%s%n", ++i, e.getName(), activeMark);
                            }
                        }
                    };
                default:
                    return null;
            }
        } catch (Exception e) {
            System.err.println("命令解析失败: " + e.getMessage());
            return null;
        }
    }

    private static Command parseInitCommand(String args, Workspace workspace) {
        if (args.isEmpty()) {
            throw new IllegalArgumentException("init 命令需要文件名: init <filename>");
        }

        String filename = args.trim();

        // 创建一个简单的初始化命令
        return new Command() {
            @Override
            public void execute(Workspace ws) {
                TextEditor editor = new TextEditor(filename);
                editor.loadContent(""); // 空文件
                ws.addEditor(editor);
                ws.setActiveEditor(editor);
                System.out.println("已创建文件: " + filename);
                System.out.println("提示：使用 'append \"text\"' 添加内容");
            }
        };
    }

    private static Command createStatusCommand(Workspace workspace) {
        return new Command() {
            @Override
            public void execute(Workspace ws) {
                if (ws.getActiveEditor() == null) {
                    System.out.println("没有打开的文件");
                } else {
                    System.out.println("当前文件: " + ws.getActiveEditor().getName());
                    System.out.println("修改状态: " + (ws.getActiveEditor().isModified() ? "已修改" : "未修改"));
                    if (ws.getActiveEditor() instanceof TextEditor) {
                        TextEditor te = (TextEditor) ws.getActiveEditor();
                        System.out.println("行数: " + te.getLineCount());
                    }
                }
            }
        };
    }

    private static Command parseInsertCommand(String args) {
        String[] parts = args.split("\\s+", 2);
        if (parts.length < 2) {
            throw new IllegalArgumentException("insert 命令格式: insert <line:col> \"text\"");
        }

        String[] position = parts[0].split(":");
        if (position.length != 2) {
            throw new IllegalArgumentException("位置格式错误，应为 line:col");
        }

        int line = Integer.parseInt(position[0]);
        int col = Integer.parseInt(position[1]);
        String text = extractQuotedText(parts[1]);

        return new InsertCommand(line, col, text);
    }

    private static Command parseDeleteCommand(String args) {
        String[] parts = args.split("\\s+");
        if (parts.length < 2) {
            throw new IllegalArgumentException("delete 命令格式: delete <line:col> <length>");
        }

        String[] position = parts[0].split(":");
        if (position.length != 2) {
            throw new IllegalArgumentException("位置格式错误，应为 line:col");
        }

        int line = Integer.parseInt(position[0]);
        int col = Integer.parseInt(position[1]);
        int length = Integer.parseInt(parts[1]);

        return new DeleteCommand(line, col, length);
    }

    private static Command parseReplaceCommand(String args) {
        String[] parts = args.split("\\s+", 3);
        if (parts.length < 3) {
            throw new IllegalArgumentException("replace 命令格式: replace <line:col> <length> \"text\"");
        }

        String[] position = parts[0].split(":");
        if (position.length != 2) {
            throw new IllegalArgumentException("位置格式错误，应为 line:col");
        }

        int line = Integer.parseInt(position[0]);
        int col = Integer.parseInt(position[1]);
        int length = Integer.parseInt(parts[1]);
        String text = extractQuotedText(parts[2]);

        return new ReplaceCommand(line, col, length, text);
    }

    private static Command parseShowCommand(String args) {
        if (args.isEmpty()) {
            return new ShowCommand();
        }

        String[] parts = args.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("show 命令格式: show [startLine:endLine]");
        }

        int startLine = Integer.parseInt(parts[0]);
        int endLine = Integer.parseInt(parts[1]);

        return new ShowCommand(startLine, endLine);
    }

    private static String extractQuotedText(String input) {
        input = input.trim();
        if (input.startsWith("\"") && input.endsWith("\"")) {
            return input.substring(1, input.length() - 1);
        }
        throw new IllegalArgumentException("文本参数必须用双引号包裹");
    }

    private static void printHelp() {
        System.out.println("========================================");
        System.out.println("可用命令:");
        System.out.println("----------------------------------------");
        System.out.println("文件操作:");
        System.out.println("  init <filename>             - 创建/打开文件");
        System.out.println("  edit <filepath>             - 打开/切换文件");
        System.out.println("  load <filepath>             - 从磁盘加载文件");
        System.out.println("  save [filepath]             - 保存当前文件 (可选路径)");
        System.out.println("  close [filepath]            - 关闭当前或指定文件");
        System.out.println("  editor-list                 - 列出打开的编辑器");
        System.out.println();
        System.out.println("文本编辑:");
        System.out.println("  append \"text\"              - 追加文本（作为新行，Undoable）");
        System.out.println("  insert line:col \"text\"     - 插入文本（Undoable）");
        System.out.println("  replace line:col len \"text\" - 替换文本（Undoable）");
        System.out.println("  delete line:col length      - 删除字符（Undoable）");
        System.out.println("  show [start:end]            - 显示指定行");
        System.out.println();
        System.out.println("编辑控制:");
        System.out.println("  undo / redo                 - 撤销 / 重做");
        System.out.println("  status                      - 显示当前状态");
        System.out.println("  help                        - 显示帮助");
        System.out.println("  exit/quit                   - 退出");
        System.out.println("========================================");
        System.out.println();
        System.out.println("示例:");
        System.out.println("  > init test.txt");
        System.out.println("  > append \"Hello World\"");
        System.out.println("  > show");
        System.out.println("  > insert 1:7 \"Beautiful \"");
        System.out.println("  > show");
        System.out.println("========================================");
    }

    private static void banner() {
        System.out.println("========================================");
        System.out.println("  Team20 Text Editor v1.0.0");
        System.out.println("========================================");
        System.out.println("启动时间: " + Instant.now());
        System.out.println("Java 版本: " + System.getProperty("java.version"));
        System.out.println("操作系统: " + System.getProperty("os.name"));
        System.out.println("----------------------------------------");
    }
}