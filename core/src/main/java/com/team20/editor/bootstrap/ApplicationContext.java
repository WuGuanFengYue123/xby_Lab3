package com.team20.editor.bootstrap;

import com.team20.editor.domain.command.registry.AutoLoadingCommandRegistry;
import com.team20.editor.domain.command.CommandInvoker;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.extension.registry.EditorFactory;
import com.team20.editor.extension.spi.editor.EditorProvider;
import com.team20.editor.extension.spi.serialization.SerializerProvider;
import com.team20.editor.infrastructure.event.EventBus;
import com.team20.editor.infrastructure.event.EventPublisher;
import com.team20.editor.infrastructure.event.SimpleEventBus;
import com.team20.editor.infrastructure.persistence.Serializer;
import com.team20.editor.infrastructure.persistence.PersistenceManager;
import com.team20.editor.monitoring.logging.LogSink;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 应用程序上下文（严格插件化）
 *
 * 说明：
 * - 不再直接构造任何具体实现（例如 ConsoleLogSink、JsonSerializer、TextEditor 等）。
 * - 运行时通过 ServiceLoader 加载所需的 SPI/实现：
 *   - EditorProvider(s) -> 注入 EditorFactory
 *   - SerializerProvider -> 获取 Serializer 用于 PersistenceManager
 *   - LogSink implementations -> 选择第一个（可改为策略选择）
 * - 严格策略：若任一必需实现缺失，则在构造时抛 IllegalStateException 并中止启动。
 */
public final class ApplicationContext {

    private final EventBus eventBus;
    private final AutoLoadingCommandRegistry commandRegistry;
    private final LogSink logSink;
    private final CommandInvoker commandInvoker;
    private final PersistenceManager persistenceManager;
    private final EditorFactory editorFactory;
    private final List<EditorProvider> editorProviders = new ArrayList<>();

    public ApplicationContext() {
        this.eventBus = new SimpleEventBus();
        this.commandRegistry = new AutoLoadingCommandRegistry();
        this.commandInvoker = new CommandInvoker();

        // 严格加载：SerializerProvider -> Serializer -> PersistenceManager
        Serializer serializer = loadSerializer();
        this.persistenceManager = new PersistenceManager(serializer);

        // 严格加载：LogSink（实现必须由插件提供）
        this.logSink = loadLogSink();

        // 加载 EditorProvider（SPI）并强制至少有一个
        loadEditorProviders();
        if (editorProviders.isEmpty()) {
            throw new IllegalStateException(
                    "没有找到任何 EditorProvider 实现。请确保在类路径中包含至少一个实现并在对应模块的 META-INF/services/com.team20.editor.extension.spi.editor.EditorProvider 中声明实现类。");
        }
        this.editorFactory = new EditorFactory(this.editorProviders);
    }

    private Serializer loadSerializer() {
        ServiceLoader<SerializerProvider> loader = ServiceLoader.load(SerializerProvider.class);
        for (SerializerProvider sp : loader) {
            Serializer s = sp.getSerializer();
            if (s != null) return s;
        }
        throw new IllegalStateException("没有找到任何 SerializerProvider 实现（用于 Persistence）。请提供一个插件实现。");
    }

    private LogSink loadLogSink() {
        ServiceLoader<LogSink> loader = ServiceLoader.load(LogSink.class);
        for (LogSink ls : loader) {
            if (ls != null) return ls;
        }
        throw new IllegalStateException("没有找到任何 LogSink 实现。请提供一个实现并在 META-INF/services/com.team20.editor.monitoring.logging.LogSink 中注册。");
    }

    private void loadEditorProviders() {
        ServiceLoader<EditorProvider> loader = ServiceLoader.load(EditorProvider.class);
        for (EditorProvider p : loader) {
            editorProviders.add(p);
        }
    }

    /**
     * 创建并返回一个新的 Workspace，同时把事件发布器注入到 Workspace 中。
     */
    public Workspace createWorkspace() {
        Workspace ws = new Workspace();
        try {
            if (eventBus instanceof EventPublisher) {
                ws.setEventPublisher((EventPublisher) eventBus);
            }
        } catch (Throwable t) {
            // ignore
        }
        return ws;
    }

    // Getters
    public EventBus eventBus() {
        return eventBus;
    }

    public AutoLoadingCommandRegistry commandRegistry() {
        return commandRegistry;
    }

    public LogSink logSink() {
        return logSink;
    }

    public CommandInvoker commandInvoker() {
        return commandInvoker;
    }

    public PersistenceManager persistenceManager() {
        return persistenceManager;
    }

    /**
     * 返回 EditorFactory（通过 SPI 提供 provider）
     */
    public EditorFactory editorFactory() {
        return editorFactory;
    }

    public List<EditorProvider> editorProviders() {
        return List.copyOf(editorProviders);
    }

    public String dumpSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Application Context Summary:\n");
        sb.append("- Editor providers: ").append(editorProviders.size()).append("\n");
        sb.append("- Undo available: ").append(commandInvoker.canUndo()).append("\n");
        sb.append("- Redo available: ").append(commandInvoker.canRedo()).append("\n");
        return sb.toString();
    }
}