package com.team20.editor.registry;

import com.team20.editor.core.command.Command;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 命令注册表（单例）
 * 
 * 职责：
 * - 注册命令工厂方法
 * - 根据命令名创建命令实例
 */
public class CommandRegistry {
    
    private static CommandRegistry instance;
    private Map<String, Function<String[], Command>> commandFactories;
    
    /**
     * 私有构造函数
     */
    private CommandRegistry() {
        this.commandFactories = new HashMap<>();
    }
    
    /**
     * 获取单例实例
     * 
     * @return CommandRegistry 实例
     */
    public static CommandRegistry getInstance() {
        if (instance == null) {
            instance = new CommandRegistry();
        }
        return instance;
    }
    
    /**
     * 注册命令工厂
     * 
     * @param commandName 命令名
     * @param factory 命令工厂方法
     */
    public void register(String commandName, Function<String[], Command> factory) {
        commandFactories.put(commandName, factory);
    }
    
    /**
     * 创建命令实例
     * 
     * @param commandName 命令名
     * @param args 命令参数
     * @return 命令实例
     */
    public Command createCommand(String commandName, String[] args) {
        // TODO: 实现创建逻辑
        return null;
    }
    
    /**
     * 检查命令是否已注册
     * 
     * @param commandName 命令名
     * @return true 如果已注册
     */
    public boolean isRegistered(String commandName) {
        return commandFactories.containsKey(commandName);
    }
}
