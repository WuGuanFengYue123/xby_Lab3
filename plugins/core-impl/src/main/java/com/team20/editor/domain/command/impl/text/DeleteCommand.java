package com.team20.editor.domain.command.impl.text;

import com.team20.editor.domain.command.UndoableCommand;
import com.team20.editor.domain.editor.text.TextEditor;
import com.team20.editor.domain.workspace.Workspace;

/**
 * 删除文本命令
 */
public class DeleteCommand implements UndoableCommand {

    private final int line;
    private final int col;
    private final int length;
    private TextEditor.EditorSnapshot beforeSnapshot;

    public DeleteCommand(int line, int col, int length) {
        if (line < 1 || col < 1) {
            throw new IllegalArgumentException("行号和列号必须从 1 开始");
        }
        if (length < 0) {
            throw new IllegalArgumentException("删除长度不能为负数");
        }
        this.line = line;
        this.col = col;
        this.length = length;
    }

    @Override
    public void execute(Workspace workspace) {
        TextEditor editor = getTextEditor(workspace);

        beforeSnapshot = editor.createSnapshot();
        editor.delete(line, col, length);

        workspace.publishCommandEvent("delete",
                String.format("%d:%d %d", line, col, length));
    }

    @Override
    public void undo(Workspace workspace) {
        if (beforeSnapshot == null) {
            throw new IllegalStateException("无法撤销：命令尚未执行");
        }

        TextEditor editor = getTextEditor(workspace);
        editor.restoreSnapshot(beforeSnapshot);

        workspace.publishCommandEvent("undo", "delete");
    }

    @Override
    public void redo(Workspace workspace) {
        TextEditor editor = getTextEditor(workspace);
        TextEditor.EditorSnapshot temp = beforeSnapshot;
        execute(workspace);
        beforeSnapshot = temp;

        workspace.publishCommandEvent("redo", "delete");
    }

    private TextEditor getTextEditor(Workspace workspace) {
        if (workspace.getActiveEditor() == null) {
            throw new IllegalStateException("没有打开的文件");
        }
        if (!(workspace.getActiveEditor() instanceof TextEditor)) {
            throw new IllegalStateException("当前文件不是文本文件");
        }
        return (TextEditor) workspace.getActiveEditor();
    }

    @Override
    public String toString() {
        return String.format("delete %d:%d %d", line, col, length);
    }
}