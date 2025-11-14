package com.team20.editor.extension.spi.command;

import com.team20.editor.domain.command.Command;
import java.util.Map;

/**
 * 命令提供者接口（SPI）
 */
public interface CommandProvider {
    /**
     * 获取此提供者提供的所有命令
     * 
     * @return 命令名称到命令实例的映射
     */
    Map<String, Command> getCommands();

    /**
     * 获取命令描述信息
     * 
     * @return 命令名称到描述文本的映射
     */
    Map<String, String> getCommandDescriptions();

    /**
     * 提供者名称
     */
    String getProviderName();
}