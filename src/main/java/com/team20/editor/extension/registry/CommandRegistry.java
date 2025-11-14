package com.team20.editor.extension.registry;

import com.team20.editor.domain.command.Command;
import java.util.Set;

/**
 * 命令注册表接口
 */
public interface CommandRegistry {
    void register(String name, Command command);

    Command getCommand(String name);

    boolean hasCommand(String name);

    Set<String> getCommandNames();
}