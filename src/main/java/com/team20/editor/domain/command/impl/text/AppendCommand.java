package com.team20.editor.domain.command.impl.text;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.editor.Editor;
import com.team20.editor.domain.editor.text.TextEditor;

/**
 * Append text to a TextEditor (safe no-op if editor is null / not TextEditor).
 */
public class AppendCommand implements Command {

    private final Editor editor;
    private final String toAppend;

    public AppendCommand(Editor editor, String toAppend) {
        this.editor = editor;
        this.toAppend = toAppend;
    }

    // no-arg for ServiceLoader-friendly Provider usage (creates safe no-op)
    public AppendCommand() {
        this.editor = null;
        this.toAppend = "";
    }

    @Override
    public void execute() {
        if (editor instanceof TextEditor te) {
            te.append(toAppend);
            System.out.println("[AppendCommand] appended text");
        } else {
            // safe fallback
            System.out.println("[AppendCommand] skipped (no TextEditor)");
        }
    }
}