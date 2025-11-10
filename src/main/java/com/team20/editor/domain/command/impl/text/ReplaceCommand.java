package com.team20.editor.domain.command.impl.text;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.editor.Editor;
import com.team20.editor.domain.editor.text.TextEditor;

/**
 * Replace operation (placeholder).
 */
public class ReplaceCommand implements Command {

    private final Editor editor;
    private final String from;
    private final String to;

    public ReplaceCommand(Editor editor, String from, String to) {
        this.editor = editor;
        this.from = from;
        this.to = to;
    }

    public ReplaceCommand() {
        this.editor = null;
        this.from = "";
        this.to = "";
    }

    @Override
    public void execute() {
        if (editor instanceof TextEditor te) {
            // TextEditor currently provides append/getContent only; do demo replace by
            // message
            System.out.println("[ReplaceCommand] replace requested (demo placeholder)");
        } else {
            System.out.println("[ReplaceCommand] skipped (no TextEditor)");
        }
    }
}