package com.team20.editor.domain.command.impl.text;

import com.team20.editor.domain.editor.text.TextEditor;

/**
 * Replace command now delegates common behavior to AbstractUndoableTextCommand.
 */
public class ReplaceCommand extends AbstractUndoableTextCommand {

    private final int line;
    private final int col;
    private final int length;
    private final String text;

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
    protected void apply(TextEditor editor) {
        editor.replace(line, col, length, text);
    }

    @Override
    protected String getCommandName() {
        return "replace";
    }

    @Override
    protected String formatArgs() {
        return String.format("%d:%d %d \"%s\"", line, col, length, text);
    }

    @Override
    public String toString() {
        return String.format("replace %d:%d %d \"%s\"", line, col, length, text);
    }
}