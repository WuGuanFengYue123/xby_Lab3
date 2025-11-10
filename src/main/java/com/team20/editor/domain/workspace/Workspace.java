package com.team20.editor.domain.workspace;

import com.team20.editor.domain.editor.Editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 简化的 Workspace，增加 getState() 用于持久化。
 */
public class Workspace {

    private final List<Editor> editors = new ArrayList<>();
    private Editor active;

    public void addEditor(Editor editor) {
        if (editor == null)
            return;
        editors.add(editor);
        if (active == null)
            active = editor;
    }

    public List<Editor> getEditors() {
        return Collections.unmodifiableList(editors);
    }

    public Editor getActiveEditor() {
        return active;
    }

    public void setActiveEditor(Editor e) {
        if (editors.contains(e))
            active = e;
    }

    // 持久化相关：将当前 workspace 转为 WorkspaceState
    public WorkspaceState getState() {
        WorkspaceState s = new WorkspaceState();
        s.setEditorCount(editors.size());
        s.setActiveEditorName(active == null ? null : active.getName());
        // 以后可扩展更多字段
        return s;
    }
}