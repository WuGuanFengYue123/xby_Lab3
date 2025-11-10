package com.team20.editor.domain.command.impl.text;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.editor.Editor;

/**
 * Show editor content (prints to stdout).
 */
public class ShowCommand implements Command {

    private final Editor editor;

    public ShowCommand(Editor editor) {
        this.editor = editor;
    }

    public ShowCommand() {
        this.editor = null;
    }

    @Override
    public void execute() {
        if (editor != null) {
            System.out.println("[ShowCommand] content:");
            System.out.println(editor.getContent());
        } else {
            System.out.println("[ShowCommand] no editor to show");
        }
    }
}