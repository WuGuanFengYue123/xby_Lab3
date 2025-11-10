package com.team20.editor.domain.command.impl.text;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.editor.Editor;
import com.team20.editor.domain.editor.text.TextEditor;

/**
 * Delete operation (simple placeholder).
 */
public class DeleteCommand implements Command {

    private final Editor editor;
    private final int length;

    public DeleteCommand(Editor editor, int length) {
        this.editor = editor;
        this.length = length;
    }

    public DeleteCommand() {
        this.editor = null;
        this.length = 0;
    }

    @Override
    public void execute() {
        if (editor instanceof TextEditor te) {
            String content = te.getContent();
            if (content != null && content.length() > 0) {
                int newLen = Math.max(0, content.length() - length);
                // naive delete: replace buffer by substring
                // TextEditor only exposes append()/getContent() in current API,
                // so we fallback to printing (or extend TextEditor if needed).
                System.out.println("[DeleteCommand] requested delete, but TextEditor lacks remove API in this demo");
            } else {
                System.out.println("[DeleteCommand] nothing to delete");
            }
        } else {
            System.out.println("[DeleteCommand] skipped (no TextEditor)");
        }
    }
}