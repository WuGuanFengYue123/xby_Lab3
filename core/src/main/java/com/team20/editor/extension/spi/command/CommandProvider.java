package com.team20.editor.extension.spi.command;

import com.team20.editor.domain.command.CommandDescriptor;
import com.team20.editor.extension.registry.CommandRegistry;

import java.util.List;

/**
 * 命令提供者 SPI。
 *
 * 新增方法 registerFactories(CommandRegistry)：
 * - provider 可以在此方法中把需要的 factories 注册到 registry（避免在构造器中注册）
 */
public interface CommandProvider {
    String getProviderName();

    List<CommandDescriptor> getCommandDescriptors();

    /**
     * Lifecycle hook invoked by the registry *after* the provider has been loaded.
     * Providers should register factories via the supplied registry here.
     *
     * Default no-op to preserve backward compatibility.
     */
    default void registerFactories(CommandRegistry registry) {
        // no-op by default
    }
}