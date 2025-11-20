package com.team20.editor.representation.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 命令解析器
 *
 * 职责：
 * - 解析用户输入
 * - 提取命令名和参数
 * - 处理引号包裹的参数
 */
public class CommandParser {

    // 匹配双引号或单引号包裹的参数，或不包含空白的 token
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\"([^\"]*)\"|'([^']*)'|(\\S+)");

    /**
     * 解析命令行输入
     *
     * @param input 用户输入
     * @return 解析结果（命令名 + 参数数组），如果输入为空返回 null
     */
    public ParsedCommand parse(String input) {
        if (input == null)
            return null;
        String line = input.trim();
        if (line.isEmpty())
            return null;

        List<String> tokens = new ArrayList<>();
        Matcher m = TOKEN_PATTERN.matcher(line);
        while (m.find()) {
            if (m.group(1) != null) {
                tokens.add(m.group(1)); // 双引号
            } else if (m.group(2) != null) {
                tokens.add(m.group(2)); // 单引号
            } else {
                tokens.add(m.group(3)); // 无引号 token
            }
        }

        if (tokens.isEmpty())
            return null;

        String commandName = tokens.get(0);
        String[] args = tokens.size() > 1 ? tokens.subList(1, tokens.size()).toArray(new String[0]) : new String[0];
        return new ParsedCommand(commandName, args);
    }

    /**
     * 解析结果类
     */
    public static class ParsedCommand {
        private final String commandName;
        private final String[] args;

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