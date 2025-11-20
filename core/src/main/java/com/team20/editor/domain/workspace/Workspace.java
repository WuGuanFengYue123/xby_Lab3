package com.team20.editor.domain.workspace;

import com.team20.editor.domain.editor.Editor;
import com.team20.editor.infrastructure.event.CommandEvent;
import com.team20.editor.infrastructure.event.EventPublisher;
import com.team20.editor.infrastructure.event.WorkspaceEvent;

import java.util.*;

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
     * 获取工作区状态（用于持久化）
     */
    public WorkspaceState getState() {
        WorkspaceState state = new WorkspaceState();
        state.setEditorCount(editorList.size());
        state.setActiveEditorName(activeEditor == null ? null : activeEditor.getName());
        // 可以扩展更多字段
        return state;
    }

    /**
     * 从状态恢复（用于持久化）
     */
    public void restoreState(WorkspaceState state) {
        // 暂时保留接口，后续实现
    }

    @Override
    public String toString() {
        return String.format("Workspace[editors=%d, active=%s]",
                editorList.size(),
                activeEditor != null ? activeEditor.getName() : "none");
    }
}