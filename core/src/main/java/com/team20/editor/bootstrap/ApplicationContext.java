package com.team20.editor.bootstrap;

import com.team20.editor.domain.command.registry.AutoLoadingCommandRegistry;
import com.team20.editor.domain.command.CommandInvoker;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.domain.workspace.WorkspaceState;
import com.team20.editor.extension.registry.EditorFactory;
import com.team20.editor.extension.spi.editor.EditorProvider;
import com.team20.editor.extension.spi.serialization.SerializerProvider;
import com.team20.editor.infrastructure.event.EventBus;
import com.team20.editor.infrastructure.event.EventPublisher;
import com.team20.editor.infrastructure.event.SimpleEventBus;
import com.team20.editor.infrastructure.persistence.Serializer;
import com.team20.editor.infrastructure.persistence.PersistenceManager;
import com.team20.editor.monitoring.logging.LogSink;
import com.team20.editor.monitoring.logging.LogListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * 应用程序上下文（严格插件化）
 *
 * 说明：
 * - 不再直接构造任何具体实现（例如 ConsoleLogSink、JsonSerializer、TextEditor 等）。
 * - 运行时通过 ServiceLoader 加载所需的 SPI/实现：
 * - EditorProvider(s) -> 注入 EditorFactory
 * - SerializerProvider -> 获取 Serializer 用于 PersistenceManager
 * - LogSink implementations -> 选择第一个（可改为策略选择）
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

    // keep a reference to the log listener so we can inject Workspace later
    private final LogListener logListener;

    public ApplicationContext() {
        this.eventBus = new SimpleEventBus();

        // Subscribe LogListener so it receives CommandEvent and writes per-file logs
        LogListener listener = new LogListener();
        this.logListener = listener;
        try {
            this.eventBus.subscribe(listener);
        } catch (Throwable t) {
            // protect startup if listener fails to initialize — log to stderr but continue
            System.err.println("Warning: LogListener failed to subscribe: " + t.getMessage());
        }

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
        // Register help command so "help" is available through the command registry.
        // Use an inline Command implementation to avoid depending on external impl
        // packages
        try {
            this.commandRegistry.registerFactory("help", rawArgs -> new com.team20.editor.domain.command.Command() {
                @Override
                public void execute(Workspace workspace) {
                    try {
                        System.out.println(ApplicationContext.this.showHelp());
                    } catch (Throwable t) {
                        System.err.println("Failed to display help: " + t.getMessage());
                    }
                }

                @Override
                public String toString() {
                    return "help";
                }
            });
        } catch (Throwable t) {
            System.err.println("Warning: failed to register help command factory: " + t.getMessage());
        }
    }

    private Serializer loadSerializer() {
        ServiceLoader<SerializerProvider> loader = ServiceLoader.load(SerializerProvider.class);
        for (SerializerProvider sp : loader) {
            Serializer s = sp.getSerializer();
            if (s != null)
                return s;
        }
        throw new IllegalStateException("没有找到任何 SerializerProvider 实现（用于 Persistence）。请提供一个插件实现。");
    }

    private LogSink loadLogSink() {
        ServiceLoader<LogSink> loader = ServiceLoader.load(LogSink.class);
        for (LogSink ls : loader) {
            if (ls != null)
                return ls;
        }
        throw new IllegalStateException(
                "没有找到任何 LogSink 实现。请提供一个实现并在 META-INF/services/com.team20.editor.monitoring.logging.LogSink 中注册。");
    }

    private void loadEditorProviders() {
        ServiceLoader<EditorProvider> loader = ServiceLoader.load(EditorProvider.class);
        for (EditorProvider p : loader) {
            editorProviders.add(p);
        }
    }

    /**
     * 创建并返回一个新的 Workspace，同时把事件发布器注入到 Workspace 中。
     * Optionally loads workspace state from .workspace.state and migrates legacy
     * .*.log.enabled markers.
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

        // inject Workspace into LogListener so it can consult runtime flags
        try {
            if (this.logListener != null) {
                this.logListener.setWorkspace(ws);
            }
        } catch (Throwable ignored) {
        }

        // Restore workspace state if available
        try {
            loadWorkspaceState(ws);
        } catch (Throwable t) {
            System.err.println("Warning: Failed to restore workspace state: " + t.getMessage());
        }

        // Perform one-time migration from legacy .*.log.enabled markers
        try {
            migrateLegacyLogMarkers(ws);
        } catch (Throwable t) {
            System.err.println("Warning: Failed to migrate legacy log markers: " + t.getMessage());
        }

        return ws;
    }

    /**
     * Load workspace state from .workspace.state if it exists.
     */
    private void loadWorkspaceState(Workspace ws) {
        Path stateFile = Path.of(".workspace.state");
        if (!Files.exists(stateFile)) {
            return;
        }

        try {
            WorkspaceState state = persistenceManager.loadWorkspaceState(".workspace.state");
            if (state != null) {
                ws.restoreState(state);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load workspace state: " + e.getMessage());
        }
    }

    /**
     * Migrate legacy .*.log.enabled marker files to workspace logging flags.
     * This is a one-time migration that imports and deletes the legacy markers.
     */
    private void migrateLegacyLogMarkers(Workspace ws) {
        try {
            Path currentDir = Path.of(".");
            List<Path> markerFiles = Files.list(currentDir)
                    .filter(p -> {
                        String name = p.getFileName().toString();
                        return name.startsWith(".") && name.endsWith(".log.enabled");
                    })
                    .collect(Collectors.toList());

            for (Path marker : markerFiles) {
                String fileName = marker.getFileName().toString();
                // Extract the original filename: .filename.log.enabled -> filename
                String originalFile = fileName.substring(1, fileName.length() - ".log.enabled".length());

                // Enable logging for this file in workspace
                ws.setLoggingEnabled(originalFile, true);

                // Delete the legacy marker
                try {
                    Files.deleteIfExists(marker);
                } catch (IOException e) {
                    System.err.println("Warning: Could not delete legacy marker " + fileName + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Error during legacy marker migration: " + e.getMessage());
        }
    }

    /**
     * Save current workspace state to .workspace.state
     */
    public void saveWorkspaceState(Workspace ws) throws IOException {
        if (ws == null) {
            return;
        }
        WorkspaceState state = ws.getState();
        persistenceManager.saveWorkspaceState(".workspace.state", state);
    }

    /**
     * Returns help text for available commands.
     * This can be used by Main.java and by CLI help command.
     */
    public String showHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("Team20 Text Editor - Available Commands\n");
        sb.append("========================================\n");
        sb.append("\n");
        sb.append("Workspace Commands:\n");
        sb.append("  load <file>                  - Load file from disk\n");
        sb.append("  save [file|all]              - Save current/specified/all files\n");
        sb.append("  init <file> [with-log]       - Create new buffer (with optional '# log' header)\n");
        sb.append("  close [file]                 - Close current or specified file\n");
        sb.append("  edit <file>                  - Switch active file\n");
        sb.append("  editor-list                  - List all open editors\n");
        sb.append("  dir-tree [path]              - Show directory tree\n");
        sb.append("  undo                         - Undo last operation\n");
        sb.append("  redo                         - Redo last undone operation\n");
        sb.append("  exit                         - Exit program\n");
        sb.append("\n");
        sb.append("Text Edit Commands:\n");
        sb.append("  append \"text\"                - Append text as new line (Undoable)\n");
        sb.append("  insert line:col \"text\"       - Insert text at position (Undoable)\n");
        sb.append("  delete line:col length       - Delete characters (Undoable)\n");
        sb.append("  replace line:col len \"text\"  - Replace text (Undoable)\n");
        sb.append("  show [start:end]             - Display content\n");
        sb.append("\n");
        sb.append("Logging Commands:\n");
        sb.append("  log-on [file]                - Enable logging\n");
        sb.append("  log-off [file]               - Disable logging\n");
        sb.append("  log-show [file]              - Show log file\n");
        sb.append("\n");
        sb.append("Other:\n");
        sb.append("  help                         - Show this help\n");
        sb.append("========================================\n");
        return sb.toString();
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
        for (EditorProvider provider : editorProviders) {
            sb.append("  * ").append(provider.getProviderName()).append("\n");
        }
        // Only access getProviders() if the registry is AutoLoadingCommandRegistry
        if (commandRegistry instanceof AutoLoadingCommandRegistry) {
            AutoLoadingCommandRegistry autoRegistry = (AutoLoadingCommandRegistry) commandRegistry;
            sb.append("- Command providers: ").append(autoRegistry.getProviders().size()).append("\n");
        }
        sb.append("- Available commands: ").append(commandRegistry.getCommandNames().size()).append("\n");
        return sb.toString();
    }
}