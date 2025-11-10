package com.team20.editor.bootstrap;

import com.team20.editor.domain.command.registry.AutoLoadingCommandRegistry;
import com.team20.editor.domain.editor.text.TextEditorProvider;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.extension.spi.command.CommandProvider;
import com.team20.editor.extension.spi.editor.EditorProvider;
import com.team20.editor.extension.spi.serialization.SerializerProvider;
import com.team20.editor.extension.spi.node.NodeAdapterProvider;
import com.team20.editor.infrastructure.event.EventBus;
import com.team20.editor.infrastructure.event.SimpleEventBus;
import com.team20.editor.infrastructure.persistence.JsonSerializer;
import com.team20.editor.infrastructure.persistence.Serializer;
import com.team20.editor.monitoring.logging.ConsoleLogSink;
import com.team20.editor.monitoring.logging.LogSink;
import com.team20.editor.domain.command.CommandDescriptor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 应用装配上下文：集中管理可扩展点实例。
 * 仅依赖接口 + ServiceLoader (符合 DIP/OCP)。
 */
public final class ApplicationContext {

    private final EventBus eventBus;
    private final AutoLoadingCommandRegistry commandRegistry;
    private final List<LogSink> logSinks;
    private final Map<String, Serializer> serializers;
    private final List<EditorProvider> editorProviders;
    private final List<CommandProvider> commandProviders;
    private final List<NodeAdapterProvider> nodeAdapterProviders;

    public ApplicationContext() {
        this.eventBus = new SimpleEventBus();
        this.commandRegistry = new AutoLoadingCommandRegistry();
        this.logSinks = new ArrayList<>(List.of(new ConsoleLogSink()));
        this.serializers = buildSerializers();
        this.editorProviders = load(EditorProvider.class);
        this.commandProviders = load(CommandProvider.class);
        this.nodeAdapterProviders = load(NodeAdapterProvider.class);

        // 注册命令（将 Provider 中的描述加入命令注册表）
        for (CommandProvider cp : commandProviders) {
            cp.descriptors().forEach(commandRegistry::registerDescriptor);
        }

        // 预注册默认编辑器提供者（如果 ServiceLoader 没找到）
        if (editorProviders.stream().noneMatch(p -> p.editors().stream().anyMatch(r -> r.type().equals("text")))) {
            editorProviders.add(new TextEditorProvider());
        }
    }

    private Map<String, Serializer> buildSerializers() {
        Map<String, Serializer> map = new HashMap<>();
        map.put("json", new JsonSerializer());
        for (SerializerProvider sp : load(SerializerProvider.class)) {
            for (Serializer s : sp.serializers()) {
                map.put(s.format(), s);
            }
        }
        return Collections.unmodifiableMap(map);
    }

    private <T> List<T> load(Class<T> type) {
        ServiceLoader<T> loader = ServiceLoader.load(type);
        List<T> list = new ArrayList<>();
        loader.forEach(list::add);
        return list;
    }

    public EventBus eventBus() {
        return eventBus;
    }

    public AutoLoadingCommandRegistry commandRegistry() {
        return commandRegistry;
    }

    public List<LogSink> logSinks() {
        return Collections.unmodifiableList(logSinks);
    }

    public Serializer serializer(String fmt) {
        return serializers.get(fmt);
    }

    public Set<String> supportedFormats() {
        return serializers.keySet();
    }

    public List<EditorProvider> editorProviders() {
        return Collections.unmodifiableList(editorProviders);
    }

    public List<CommandProvider> commandProviders() {
        return Collections.unmodifiableList(commandProviders);
    }

    public List<NodeAdapterProvider> nodeAdapterProviders() {
        return Collections.unmodifiableList(nodeAdapterProviders);
    }

    public String dumpSummary() {
        return """
                ApplicationContext Summary
                --------------------------
                Commands loaded: %d
                Command names : %s
                Editor providers: %d
                Serializers: %s
                Log sinks: %d
                EventBus listeners (CommandEvent): %d
                """
                .formatted(
                        commandRegistry.names().size(),
                        commandRegistry.names(),
                        editorProviders.size(),
                        supportedFormats().stream().collect(Collectors.joining(", ")),
                        logSinks.size(),
                        eventBus.listenerCount(com.team20.editor.infrastructure.event.CommandEvent.class));
    }

    // 示例：创建默认 Workspace（后续可注入/恢复）
    public Workspace createWorkspace() {
        return new Workspace();
    }
}