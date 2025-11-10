package com.team20.editor.domain.editor.text;

import com.team20.editor.domain.editor.AbstractEditor;

public class TextEditor extends AbstractEditor {

    private final StringBuilder buf = new StringBuilder();

    public TextEditor(String name) {
        super(name);
    }

    @Override
    protected String content() {
        return buf.toString();
    }

    public void append(String text) {
        buf.append(text);
    }
}