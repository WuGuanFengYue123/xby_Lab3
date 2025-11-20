package com.team20.editor;

import com.team20.editor.bootstrap.ApplicationContext;
import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.command.UndoableCommand;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.extension.registry.DefaultCommandRegistry;

import java.time.Instant;
import java.util.Scanner;

/**
 * Program entry: CLI loop.
 * Uses DefaultCommandRegistry to create commands by name + raw args.
 */
public final class Main {

    public static void main(String[] args) {
        banner();

        try {
            ApplicationContext context = new ApplicationContext();
            Workspace workspace = context.createWorkspace();

            // Inject ApplicationContext into registry so plugin factories can access core
            // services.
            DefaultCommandRegistry.setApplicationContext(context);

            System.out.println(context.dumpSummary());
            System.out.println();
            System.out.println("Team20 Text Editor ready.");
            System.out.println("使用 'init test.txt' 创建文件开始编辑");
            System.out.println("输入 'help' 查看所有命令，'exit' 退出");
            System.out.println();

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
                if (!scanner.hasNextLine())
                    break;
                String input = scanner.nextLine().trim();
                if (input.isEmpty())
                    continue;
                if ("exit".equalsIgnoreCase(input) || "quit".equalsIgnoreCase(input)) {
                    System.out.println("退出编辑器...");
                    break;
                }
                if ("help".equalsIgnoreCase(input)) {
                    printHelp();
                    continue;
                }

                // Use registry to create command
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
                    continue;
                }

                // fallback: keep init special-cased in core
                if (commandName.equals("init")) {
                    Command init = parseInitCommand(rawArgs, workspace, context);
                    if (init != null)
                        init.execute(workspace);
                    continue;
                }

                System.out.println("未知命令: " + input);
                System.out.println("输入 'help' 查看帮助");
            } catch (Exception e) {
                System.err.println("错误: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static Command parseInitCommand(String args, Workspace workspace, ApplicationContext context) {
        if (args == null || args.isBlank()) {
            throw new IllegalArgumentException("init 命令需要文件名: init <filename>");
        }
        String filename = args.trim();
        return new Command() {
            @Override
            public void execute(Workspace ws) {
                var factory = context.editorFactory();
                var editor = factory.createEditor(filename);
                editor.loadContent("");
                ws.addEditor(editor);
                ws.setActiveEditor(editor);
                System.out.println("已创建文件: " + filename);
                System.out.println("提示：使用 'append \"text\"' 添加内容");
            }
        };
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