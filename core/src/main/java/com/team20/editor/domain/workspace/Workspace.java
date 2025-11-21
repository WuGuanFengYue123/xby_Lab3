package com.team20.editor.domain.workspace;

import com.team20.editor.domain.editor.Editor;
import com.team20.editor.infrastructure.event.CommandEvent;
import com.team20.editor.infrastructure.event.EventPublisher;
import com.team20.editor.infrastructure.event.WorkspaceEvent;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 工作区 - 管理所有打开的编辑器
 */
public class Workspace {

    // 使用 Map 存储编辑器，key 为文件路径
    private final Map<String, Editor> editors = new HashMap<>();
    // 保持插入顺序，用于列表显示
    private final List<Editor> editorList = new ArrayList<>();
    // 当前活动编辑器
    private Editor activeEditor;
    // 事件发布器
    private EventPublisher eventPublisher;

    // per-editor runtime flags (persisted via WorkspaceState.loggingEnabled)
    private final Map<String, Boolean> loggingEnabled = new HashMap<>();

    public Workspace() {
    }

    /**
     * 设置事件发布器
     */
    public void setEventPublisher(EventPublisher publisher) {
        this.eventPublisher = publisher;
    }

    /**
     * 添加编辑器
     */
    public void addEditor(Editor editor) {
        if (editor == null) {
            return;
        }

        String filepath = editor.getName(); // 使用 getName() 作为标识
        if (!editors.containsKey(filepath)) {
            editors.put(filepath, editor);
            editorList.add(editor);
        }

        // 如果没有活动编辑器，设置为活动
        if (activeEditor == null) {
            activeEditor = editor;
        }

        // 保持 loggingEnabled map 与编辑器列表一致：若没有条目，默认 false
        loggingEnabled.putIfAbsent(filepath, Boolean.FALSE);
    }

    /**
     * 移除编辑器
     */
    public void removeEditor(Editor editor) {
        if (editor == null) {
            return;
        }

        String filepath = editor.getName();
        editors.remove(filepath);
        editorList.remove(editor);

        // remove logging flag (we keep consistency)
        loggingEnabled.remove(filepath);

        // 如果移除的是活动编辑器，切换到其他编辑器
        if (activeEditor == editor) {
            activeEditor = editorList.isEmpty() ? null : editorList.get(editorList.size() - 1);
        }
    }

    /**
     * 获取所有编辑器（不可修改）
     */
    public List<Editor> getEditors() {
        return Collections.unmodifiableList(editorList);
    }

    /**
     * 根据文件路径获取编辑器
     */
    public Editor getEditor(String filepath) {
        return editors.get(filepath);
    }

    /**
     * 获取当前活动编辑器
     */
    public Editor getActiveEditor() {
        return activeEditor;
    }

    /**
     * 设置活动编辑器
     */
    public void setActiveEditor(Editor editor) {
        if (editor != null && editorList.contains(editor)) {
            activeEditor = editor;
        }
    }

    /**
     * 检查是否有打开的编辑器
     */
    public boolean hasEditors() {
        return !editorList.isEmpty();
    }

    /**
     * 获取编辑器数量
     */
    public int getEditorCount() {
        return editorList.size();
    }

    /**
     * 发布命令事件
     */
    public void publishCommandEvent(String commandName, String arguments) {
        if (eventPublisher != null) {
            String filepath = activeEditor != null ? activeEditor.getName() : "";
            CommandEvent event = new CommandEvent(commandName, arguments, filepath);
            eventPublisher.publish(event);
        }
    }

    /**
     * 发布工作区事件
     */
    public void publishWorkspaceEvent(String eventType, Object data) {
        if (eventPublisher != null) {
            WorkspaceEvent event = new WorkspaceEvent(eventType, data);
            eventPublisher.publish(event);
        }
    }

    /**
     * 获取编辑器的日志开关状态（true 表示启用）
     */
    public boolean isLoggingEnabled(String filepath) {
        if (filepath == null)
            return false;
        Boolean v = loggingEnabled.get(filepath);
        return v != null && v;
    }

    /**
     * 设置某个编辑器的日志开关
     */
    public void setLoggingEnabled(String filepath, boolean enabled) {
        if (filepath == null)
            return;
        loggingEnabled.put(filepath, enabled);
    }

    /**
     * 返回当前的 logging map（不可修改视图）
     */
    public Map<String, Boolean> getLoggingEnabledMap() {
        return Collections.unmodifiableMap(new HashMap<>(loggingEnabled));
    }

    /**
     * 获取工作区状态（用于持久化）
     */
    public WorkspaceState getState() {
        WorkspaceState state = new WorkspaceState();
        state.setEditorCount(editorList.size());
        state.setActiveEditorName(activeEditor == null ? null : activeEditor.getName());
        // store editor names only (do not instantiate editors here)
        state.setEditorNames(editorList.stream().map(Editor::getName).collect(Collectors.toList()));
        // persist logging flags
        state.setLoggingEnabledMap(new HashMap<>(this.loggingEnabled));
        return state;
    }

    /**
     * 从状态恢复（用于持久化）
     *
     * Important: Workspace.restoreState must not directly instantiate concrete
     * Editor implementations.
     * The actual creation of Editor instances should be performed by higher-level
     * code (e.g. ApplicationContext
     * or PersistenceManager) using EditorFactory / EditorProvider so that concrete
     * editor implementations
     * remain in plugins.
     *
     * Here we restore only metadata and per-editor flags (like loggingEnabled).
     */
    public void restoreState(WorkspaceState state) {
        if (state == null)
            return;
        // restore logging flags and active editor name (editor instances will be
        // created later by plugin code)
        Map<String, Boolean> map = state.getLoggingEnabledMap();
        if (map != null) {
            loggingEnabled.clear();
            loggingEnabled.putAll(map);
        }
    }

    @Override
    public String toString() {
        return String.format("Workspace[editors=%d, active=%s]",
                editorList.size(),
                activeEditor != null ? activeEditor.getName() : "none");
    }
}