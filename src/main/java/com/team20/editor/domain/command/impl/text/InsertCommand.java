package com.team20.editor.domain.command.impl.text;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.editor.Editor;
import com.team20.editor.domain.editor.text.TextEditor;

/**
 * Insert operation (placeholder: append for demo).
 */
public class InsertCommand implements Command {

    private final Editor editor;
    private final int position;
    private final String text;

    public InsertCommand(Editor editor, int position, String text) {
        this.editor = editor;
        this.position = position;
        this.text = text;
    }

    public InsertCommand() {
        this.editor = null;
        this.position = 0;
        this.text = "";
    }

    @Override
    public void execute() {
        if (editor instanceof TextEditor te) {
            // no direct insert API: append for demo
            te.append(text);
            System.out.println("[InsertCommand] appended as placeholder");
        } else {
            System.out.println("[InsertCommand] skipped (no TextEditor)");
        }
    }
}