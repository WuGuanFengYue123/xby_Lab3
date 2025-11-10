package com.team20.editor.domain.editor;

public abstract class AbstractEditor implements Editor {

    protected final String name;

    protected AbstractEditor(String name) {
        this.name = name == null ? "untitled" : name;
    }

    @Override
    public String getName() {
        return name;
    }

    protected String content() {
        return "";
    }

    @Override
    public String getContent() {
        String c = content();
        return c == null ? "" : c;
    }

    @Override
    public boolean canUndo() {
        return false;
    }

    @Override
    public boolean canRedo() {
        return false;
    }

    @Override
    public void undo() {
        // default no-op
    }

    @Override
    public void redo() {
        // default no-op
    }
}