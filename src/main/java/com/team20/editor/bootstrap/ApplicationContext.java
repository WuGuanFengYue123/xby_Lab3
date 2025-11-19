package com.team20.editor.bootstrap;

import com.team20.editor.domain.command.registry.AutoLoadingCommandRegistry;
import com.team20.editor.domain.command.CommandInvoker;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.extension.registry.EditorFactory;
import com.team20.editor.extension.spi.editor.EditorProvider;
import com.team20.editor.infrastructure.event.EventBus;
import com.team20.editor.infrastructure.event.EventPublisher;
import com.team20.editor.infrastructure.event.SimpleEventBus;
import com.team20.editor.infrastructure.persistence.JsonSerializer;
import com.team20.editor.infrastructure.persistence.PersistenceManager;
import com.team20.editor.monitoring.logging.ConsoleLogSink;
import com.team20.editor.monitoring.logging.LogSink;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 应用程序上下文（强制要求至少一个 EditorProvider 实现）
 *
 * 现在会把通过 ServiceLoader 发现到的 EditorProvider 注入到 EditorFactory，
 * 并在构造时在没有任何 provider 的情况下抛出异常（强制 SPI）。
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
        this.logSink = new ConsoleLogSink();

        this.commandInvoker = new CommandInvoker();
        this.persistenceManager = new PersistenceManager(new JsonSerializer());

        // 加载 EditorProvider（SPI）
        loadEditorProviders();

        // 强制至少有一个 provider
        if (editorProviders.isEmpty()) {
            throw new IllegalStateException(
                    "没有找到任何 EditorProvider 实现。请确保已在类路径中添加 editor provider 的实现并在 META-INF/services/com.team20.editor.extension.spi.editor.EditorProvider 中声明。");
        }

        // 初始化 EditorFactory（注入已发现的 provider 列表）
        this.editorFactory = new EditorFactory(this.editorProviders);
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