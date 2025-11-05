package com.team20.editor.ui;

import com.team20.editor.core.workspace.Workspace;
import com.team20.editor.core.command.CommandInvoker;
import java.util.Scanner;

/**
 * 命令行界面
 * 
 * 职责：
 * - 主循环：读取用户输入
 * - 解析命令
 * - 执行命令
 * - 显示结果
 */
public class CommandLineInterface {
    
    private Workspace workspace;
    private CommandInvoker invoker;
    private CommandParser parser;
    private Scanner scanner;
    private boolean running;
    
    /**
     * 构造函数
     * 
     * @param workspace 工作区
     * @param invoker 命令调用器
     */
    public CommandLineInterface(Workspace workspace, CommandInvoker invoker) {
        this.workspace = workspace;
        this.invoker = invoker;
        this.parser = new CommandParser();
        this.scanner = new Scanner(System.in);
        this.running = false;
    }
    
    /**
     * 启动 CLI
     */
    public void start() {
        // TODO: 实现主循环
        running = true;
        System.out.println("Team20 Text Editor");
        System.out.println("Type 'help' for commands, 'exit' to quit");
        
        while (running) {
            // TODO: 显示提示符
            // TODO: 读取输入
            // TODO: 解析命令
            // TODO: 执行命令
            // TODO: 显示结果
        }
    }
    
    /**
     * 停止 CLI
     */
    public void stop() {
        running = false;
        scanner.close();
    }
}
