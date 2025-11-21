package com.team20.editor.domain.command.impl.text;

import com.team20.editor.domain.editor.text.TextEditor;

/**
 * Append command now delegates common behavior to AbstractUndoableTextCommand.
 */
public class AppendCommand extends AbstractUndoableTextCommand {

    private final String text;

    public AppendCommand(String text) {
        if (text == null) {
            throw new IllegalArgumentException("追加的文本不能为 null");
        }
        this.text = text;
    }

    @Override
    protected void apply(TextEditor editor) {
        String current = editor.getContent();
        String toAppend = text;

        if (current == null || current.isEmpty()) {
            editor.append(toAppend);
        } else {
            if (current.endsWith("\n")) {
                editor.append(toAppend);
            } else {
                editor.append(System.lineSeparator() + toAppend);
            }
        }
    }

    @Override
    protected String getCommandName() {
        return "append";
    }

    @Override
    protected String formatArgs() {
        return String.format("\"%s\"", text);
    }

    @Override
    public String toString() {
        return String.format("append \"%s\"", text);
    }
}