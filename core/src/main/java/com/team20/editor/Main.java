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
 *
 * NOTE: For "exit"/"quit" we require a plugin-provided "exit" command from the
 * registry.
 * If the registry returns null, we refuse to perform a fallback exit and
 * instead
 * inform the user that a plugin implementing graceful exit is required.
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

            // Try to let an optional CLI plugin take over (provides up/down history).
            // If plugin class not present, fall back to built-in interactive loop.
            try {
                Class<?> pluginClazz = Class.forName("com.team20.editor.plugin.cli.JLineInteractive");
                try {
                    // static method: run(ApplicationContext, Workspace)
                    java.lang.reflect.Method m = pluginClazz.getMethod("run",
                            ApplicationContext.class, Workspace.class);
                    m.invoke(null, context, workspace);
                    // plugin handled the interactive loop; exit main
                    return;
                } catch (NoSuchMethodException | IllegalAccessException
                        | java.lang.reflect.InvocationTargetException ex) {
                    System.err.println("可选 CLI 插件存在但无法调用入口 run(ApplicationContext,Workspace): " + ex.getMessage());
                    // fall back to built-in loop
                }
            } catch (ClassNotFoundException ignored) {
                // plugin not present -> continue with built-in loop
            }

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

                // Handle exit/quit by delegating to the command registry (plugin must provide
                // ExitCommand).
                if ("exit".equalsIgnoreCase(input) || "quit".equalsIgnoreCase(input)) {
                    try {
                        Command exitCommand = DefaultCommandRegistry.getInstance().create("exit", "");
                        if (exitCommand != null) {
                            if (exitCommand instanceof UndoableCommand uc) {
                                context.commandInvoker().executeAndRecord(uc, workspace);
                            } else {
                                exitCommand.execute(workspace);
                            }
                            // ExitCommand should take care of terminating the process (e.g. System.exit).
                            // If it returns, break as a safeguard (but normally plugin will exit).
                            break;
                        } else {
                            // NO FALLBACK: require plugin-provided exit
                            System.out.println("错误：未找到可用的 'exit' 命令实现。");
                            System.out.println("请确保已部署负责优雅退出的插件（例如 plugins/core-impl 中的 ExitCommand），然后重试。");
                            // Do NOT break or exit; continue loop and wait for user to install/enable
                            // plugin or run an explicit command.
                            continue;
                        }
                    } catch (Throwable t) {
                        System.err.println("尝试执行 'exit' 命令时发生错误: " + t.getMessage());
                        // Do NOT fallback to immediate exit; allow user to inspect error and continue.
                        continue;
                    }
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

                System.out.println("未知命令: " + input);
                System.out.println("输入 'help' 查看帮助");
            } catch (Exception e) {
                System.err.println("错误: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static void printHelp() {
        System.out.println("========================================");
        System.out.println("可用命令:");
        System.out.println("----------------------------------------");
        System.out.println("文件操作:");
        System.out.println("  init <filename> [with-log]  - 创建文件（可选 with-log 在首行写入 '# log' 并启用日志）");
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
        System.out.println();
        System.out.println("编辑控制:");
        System.out.println("  undo / redo                 - 撤销 / 重做");
        System.out.println("  status                      - 显示当前状态");
        System.out.println("  help                        - 显示帮助");
        System.out.println("  exit/quit                   - 退出（必须由插件提供 'exit' 命令）");
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