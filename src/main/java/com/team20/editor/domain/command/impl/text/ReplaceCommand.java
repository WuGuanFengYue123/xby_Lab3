package com.team20.editor.domain.command.impl.text;

import com.team20.editor.domain.command.UndoableCommand;
import com.team20.editor.domain.editor.text.TextEditor;
import com.team20.editor.domain.workspace.Workspace;

/**
 * 替换文本命令
 */
public class ReplaceCommand implements UndoableCommand {

    private final int line;
    private final int col;
    private final int length;
    private final String text;
    private TextEditor.EditorSnapshot beforeSnapshot;

    public ReplaceCommand(int line, int col, int length, String text) {
        if (line < 1 || col < 1) {
            throw new IllegalArgumentException("行号和列号必须从 1 开始");
        }
        if (length < 0) {
            throw new IllegalArgumentException("替换长度不能为负数");
        }
        if (text == null) {
            throw new IllegalArgumentException("替换的文本不能为 null");
        }
        this.line = line;
        this.col = col;
        this.length = length;
        this.text = text;
    }

    @Override
    public void execute(Workspace workspace) {
        TextEditor editor = getTextEditor(workspace);

        beforeSnapshot = editor.createSnapshot();
        editor.replace(line, col, length, text);

        workspace.publishCommandEvent("replace",
                String.format("%d:%d %d \"%s\"", line, col, length, text));
    }

    @Override
    public void undo(Workspace workspace) {
        if (beforeSnapshot == null) {
            throw new IllegalStateException("无法撤销：命令尚未执行");
        }

        TextEditor editor = getTextEditor(workspace);
        editor.restoreSnapshot(beforeSnapshot);

        workspace.publishCommandEvent("undo", "replace");
    }

    @Override
    public void redo(Workspace workspace) {
        TextEditor editor = getTextEditor(workspace);
        TextEditor.EditorSnapshot temp = beforeSnapshot;
        execute(workspace);
        beforeSnapshot = temp;

        workspace.publishCommandEvent("redo", "replace");
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
        return String.format("replace %d:%d %d \"%s\"", line, col, length, text);
    }
}