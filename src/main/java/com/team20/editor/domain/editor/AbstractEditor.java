package com.team20.editor.domain.editor;

/**
 * 抽象编辑器基类
 */
public abstract class AbstractEditor implements Editor {
    private final String name;
    private boolean modified;

    public AbstractEditor(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("编辑器名称不能为空");
        }
        this.name = name;
        this.modified = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFilepath() {
        return name;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    @Override
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    @Override
    public String getContent() {
        return content();
    }

    /**
     * 子类实现具体的内容获取逻辑
     */
    protected abstract String content();

    @Override
    public boolean canUndo() {
        return false; // 默认不支持，子类可覆盖
    }

    @Override
    public boolean canRedo() {
        return false; // 默认不支持，子类可覆盖
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("此编辑器不支持撤销操作");
    }

    @Override
    public void redo() {
        throw new UnsupportedOperationException("此编辑器不支持重做操作");
    }

    @Override
    public String toString() {
        return String.format("%s[name=%s, modified=%s]",
                getClass().getSimpleName(), name, modified);
    }
}