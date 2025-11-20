package com.team20.editor.domain.command.impl.text;

import com.team20.editor.domain.command.UndoableCommand;
import com.team20.editor.domain.editor.text.TextEditor;
import com.team20.editor.domain.workspace.Workspace;

/**
 * 插入文本命令
 */
public class InsertCommand implements UndoableCommand {

    private final int line;
    private final int col;
    private final String text;
    private TextEditor.EditorSnapshot beforeSnapshot;

    public InsertCommand(int line, int col, String text) {
        if (line < 1 || col < 1) {
            throw new IllegalArgumentException("行号和列号必须从 1 开始");
        }
        if (text == null) {
            throw new IllegalArgumentException("插入的文本不能为 null");
        }
        this.line = line;
        this.col = col;
        this.text = text;
    }

    @Override
    public void execute(Workspace workspace) {
        TextEditor editor = getTextEditor(workspace);

        beforeSnapshot = editor.createSnapshot();
        editor.insert(line, col, text);

        workspace.publishCommandEvent("insert",
                String.format("%d:%d \"%s\"", line, col, text));
    }

    @Override
    public void undo(Workspace workspace) {
        if (beforeSnapshot == null) {
            throw new IllegalStateException("无法撤销：命令尚未执行");
        }

        TextEditor editor = getTextEditor(workspace);
        editor.restoreSnapshot(beforeSnapshot);

        workspace.publishCommandEvent("undo", "insert");
    }

    @Override
    public void redo(Workspace workspace) {
        TextEditor editor = getTextEditor(workspace);
        TextEditor.EditorSnapshot temp = beforeSnapshot;
        execute(workspace);
        beforeSnapshot = temp;

        workspace.publishCommandEvent("redo", "insert");
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
        return String.format("insert %d:%d \"%s\"", line, col, text);
    }
}
