package com.team20.editor.domain.workspace;

/**
 * 简单的持久化状态对象（演示用）。
 * 实际项目中可包含编辑器列表、buffer、光标、undo 栈等。
 */
public class WorkspaceState {
    private int editorCount;
    private String activeEditorName;

    public int getEditorCount() {
        return editorCount;
    }

    public void setEditorCount(int editorCount) {
        this.editorCount = editorCount;
    }

    public String getActiveEditorName() {
        return activeEditorName;
    }

    public void setActiveEditorName(String activeEditorName) {
        this.activeEditorName = activeEditorName;
    }

    // 将状态恢复为 Workspace 实例（简化）
    public Workspace toWorkspace() {
        Workspace ws = new Workspace();
        // 这里只恢复最小信息：创建指定数量的占位编辑器（名字用 activeEditorName / untitled）
        for (int i = 0; i < editorCount; i++) {
            // 使用 domain editor 实现创建占位文本编辑器
            com.team20.editor.domain.editor.text.TextEditor te = new com.team20.editor.domain.editor.text.TextEditor(
                    "untitled-" + i);
            ws.addEditor(te);
        }
        if (activeEditorName != null && ws.getEditors().size() > 0) {
            ws.setActiveEditor(ws.getEditors().get(0)); // 简化：将第一个设为活动
        }
        return ws;
    }
}