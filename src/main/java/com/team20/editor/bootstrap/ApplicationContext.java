package com.team20.editor.bootstrap;

import com.team20.editor.domain.command.registry.AutoLoadingCommandRegistry;
import com.team20.editor.domain.command.CommandInvoker;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.infrastructure.event.EventBus;
import com.team20.editor.infrastructure.event.EventPublisher;
import com.team20.editor.infrastructure.event.SimpleEventBus;
import com.team20.editor.infrastructure.persistence.JsonSerializer;
import com.team20.editor.infrastructure.persistence.PersistenceManager;
import com.team20.editor.monitoring.logging.ConsoleLogSink;
import com.team20.editor.monitoring.logging.LogSink;

/**
 * 应用程序上下文（兼容多版本 API）
 *
 * 说明：
 * - 为了兼容仓库中不同实现的注册表/Provider 接口，此类避免直接调用
 * 可能不存在的方法（如 registerDescriptor、names() 等）。
 * - 使用反射安全地尝试获取可用统计信息以生成摘要，不再在初始化时强制注册内置命令。
 */
public final class ApplicationContext {

    private final EventBus eventBus;
    private final AutoLoadingCommandRegistry commandRegistry;
    private final LogSink logSink;
    private final CommandInvoker commandInvoker;
    private final PersistenceManager persistenceManager;

    public ApplicationContext() {
        this.eventBus = new SimpleEventBus();
        this.commandRegistry = new AutoLoadingCommandRegistry();
        this.logSink = new ConsoleLogSink();

        this.commandInvoker = new CommandInvoker();
        this.persistenceManager = new PersistenceManager(new JsonSerializer());

        // 不在此处做强制的命令注册（以免调用不存在的 API）
        // 如果你希望注册内置命令，请在一个实现了 register 库方法的地方完成注册。
    }

    /**
     * 创建并返回一个新的 Workspace，同时把事件发布器注入到 Workspace 中。
     * 之所以在这里做注入，是为了保证 Main 或其他启动代码可以直接
     * 通过 context.createWorkspace() 获得已配置的 Workspace。
     */
    public Workspace createWorkspace() {
        Workspace ws = new Workspace();
        try {
            // EventBus 实现通常也实现了 EventPublisher（SimpleEventBus 在本仓库中应可用）
            if (eventBus instanceof EventPublisher) {
                ws.setEventPublisher((EventPublisher) eventBus);
            } else {
                // 尝试通过反射降级适配：查找 publish 方法并包装为 EventPublisher
                // 如果没有 EventPublisher 接口可用，则跳过注入
                // (保持兼容，避免 ClassCastException)
                // no-op
            }
        } catch (Throwable t) {
            // 保底：如果注入失败，仍返回 workspace（不影响后续手动注入）
        }
        return ws;
    }

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
     * 生成一个友好的摘要。因为不同代码版本的 registry/eventbus 可能没有相同方法,
     * 这里使用反射尝试读取：command count 与 event listener count；失败则回退到 N/A。
     */
    public String dumpSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Application Context Summary:\n");

        // commands count: try registry.names() -> Collection or getCommandCount()
        String cmdCount = "N/A";
        try {
            var m = commandRegistry.getClass().getMethod("names");
            Object names = m.invoke(commandRegistry);
            if (names instanceof java.util.Collection) {
                cmdCount = String.valueOf(((java.util.Collection<?>) names).size());
            } else {
                cmdCount = names == null ? "0" : names.toString();
            }
        } catch (NoSuchMethodException ignored) {
            try {
                var m2 = commandRegistry.getClass().getMethod("getCommandCount");
                Object r = m2.invoke(commandRegistry);
                cmdCount = r == null ? "0" : String.valueOf(r);
            } catch (Exception ignored2) {
                cmdCount = "N/A";
            }
        } catch (Exception e) {
            cmdCount = "N/A";
        }
        sb.append("- Commands: ").append(cmdCount).append("\n");

        // event listeners: try SimpleEventBus.listenerCount(Class) or
        // getListenerCount()
        String listenerCount = "N/A";
        try {
            if (eventBus != null) {
                var cls = eventBus.getClass();
                try {
                    var m = cls.getMethod("listenerCount", Class.class);
                    Object r = m.invoke(eventBus, new Object[] { null });
                    listenerCount = r == null ? "0" : String.valueOf(r);
                } catch (NoSuchMethodException ex) {
                    // try getListenerCount()
                    var m2 = cls.getMethod("getListenerCount");
                    Object r2 = m2.invoke(eventBus);
                    listenerCount = r2 == null ? "0" : String.valueOf(r2);
                }
            }
        } catch (Exception e) {
            listenerCount = "N/A";
        }
        sb.append("- Event Listeners: ").append(listenerCount).append("\n");

        // undo/redo availability
        sb.append("- Undo available: ").append(commandInvoker.canUndo()).append("\n");
        sb.append("- Redo available: ").append(commandInvoker.canRedo()).append("\n");

        return sb.toString();
    }
}