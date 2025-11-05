package com.team20.editor.ui;

/**
 * 命令解析器
 * 
 * 职责：
 * - 解析用户输入
 * - 提取命令名和参数
 * - 处理引号包裹的参数
 */
public class CommandParser {
    
    /**
     * 解析命令行输入
     * 
     * @param input 用户输入
     * @return 解析结果（命令名 + 参数数组）
     */
    public ParsedCommand parse(String input) {
        // TODO: 实现解析逻辑
        // 1. 分割命令名和参数
        // 2. 处理引号包裹的参数
        // 3. 返回 ParsedCommand 对象
        return null;
    }
    
    /**
     * 解析结果类
     */
    public static class ParsedCommand {
        private String commandName;
        private String[] args;
        
        public ParsedCommand(String commandName, String[] args) {
            this.commandName = commandName;
            this.args = args;
        }
        
        public String getCommandName() {
            return commandName;
        }
        
        public String[] getArgs() {
            return args;
        }
    }
}
