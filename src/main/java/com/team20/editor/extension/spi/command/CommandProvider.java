package com.team20.editor.extension.spi.command;

import com.team20.editor.domain.command.CommandDescriptor;

import java.util.List;

/**
 * 命令提供者 SPI。
 *
 * 统一方法：
 * - getProviderName(): 返回提供者名称
 * - getCommandDescriptors(): 返回该 provider 提供的命令描述（CommandDescriptor）
 */
public interface CommandProvider {
    String getProviderName();
    List<CommandDescriptor> getCommandDescriptors();
}