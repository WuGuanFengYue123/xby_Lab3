package com.team20.editor.core.command;

/**
 * 命令接口
 * 
 * 设计模式：Command Pattern
 */
public interface Command {
    
    /**
     * 执行命令
     */
    void execute();
    
    /**
     * 获取命令名称
     * 
     * @return 命令名称
     */
    String getName();
}
