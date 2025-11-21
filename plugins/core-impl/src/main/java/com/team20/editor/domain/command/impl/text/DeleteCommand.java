package com.team20.editor.domain.command.impl.text;

import com.team20.editor.domain.editor.text.TextEditor;

/**
 * Delete command now delegates common behavior to AbstractUndoableTextCommand.
 */
public class DeleteCommand extends AbstractUndoableTextCommand {

    private final int line;
    private final int col;
    private final int length;

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
    protected void apply(TextEditor editor) {
        editor.delete(line, col, length);
    }

    @Override
    protected String getCommandName() {
        return "delete";
    }

    @Override
    protected String formatArgs() {
        return String.format("%d:%d %d", line, col, length);
    }

    @Override
    public String toString() {
        return String.format("delete %d:%d %d", line, col, length);
    }
}