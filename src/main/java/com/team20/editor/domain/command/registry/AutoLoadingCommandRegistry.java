package com.team20.editor.domain.command.registry;

import com.team20.editor.domain.command.Command;
import com.team20.editor.extension.registry.CommandRegistry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 命令注册表
 */
public class AutoLoadingCommandRegistry implements CommandRegistry {
    private final Map<String, Command> commands = new ConcurrentHashMap<>();
    private final Map<String, String> commandDescriptions = new ConcurrentHashMap<>();

    public AutoLoadingCommandRegistry() {
        // 空构造函数
    }

    @Override
    public void register(String name, Command command) {
        if (name == null || command == null) {
            throw new IllegalArgumentException("命令名称和命令实例不能为 null");
        }
        commands.put(name.toLowerCase(), command);
    }

    @Override
    public Command getCommand(String name) {
        if (name == null) {
            return null;
        }
        return commands.get(name.toLowerCase());
    }

    @Override
    public boolean hasCommand(String name) {
        return name != null && commands.containsKey(name.toLowerCase());
    }

    @Override
    public Set<String> getCommandNames() {
        return new HashSet<>(commands.keySet());
    }

    /**
     * 注册命令描述
     */
    public void registerDescription(String name, String description) {
        commandDescriptions.put(name.toLowerCase(), description);
    }

    /**
     * 获取命令描述
     */
    public String getCommandDescription(String name) {
        return commandDescriptions.get(name.toLowerCase());
    }

    /**
     * 获取注册的命令数量
     */
    public int getCommandCount() {
        return commands.size();
    }

    @Override
    public String toString() {
        return String.format("CommandRegistry[commands=%d]", commands.size());
    }
}