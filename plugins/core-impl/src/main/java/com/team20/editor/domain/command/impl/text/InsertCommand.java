package com.team20.editor.domain.command.impl.text;

import com.team20.editor.domain.editor.text.TextEditor;

/**
 * Insert command now delegates common behavior to AbstractUndoableTextCommand.
 */
public class InsertCommand extends AbstractUndoableTextCommand {

    private final int line;
    private final int col;
    private final String text;

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
    protected void apply(TextEditor editor) {
        editor.insert(line, col, text);
    }

    @Override
    protected String getCommandName() {
        return "insert";
    }

    @Override
    protected String formatArgs() {
        return String.format("%d:%d \"%s\"", line, col, text);
    }

    @Override
    public String toString() {
        return String.format("insert %d:%d \"%s\"", line, col, text);
    }
}