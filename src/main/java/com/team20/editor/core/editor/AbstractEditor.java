package com.team20.editor.core.editor;

/**
 * 编辑器抽象基类
 * 
 * 提供编辑器的通用实现
 */
public abstract class AbstractEditor implements Editor {
    
    protected String filePath;
    protected boolean modified;
    
    // TODO: undo/redo 历史栈
    
    /**
     * 构造函数
     * 
     * @param filePath 文件路径
     */
    public AbstractEditor(String filePath) {
        this.filePath = filePath;
        this.modified = false;
    }
    
    @Override
    public String getFilePath() {
        return filePath;
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
    public boolean undo() {
        // TODO: 实现通用 undo 逻辑
        return false;
    }
    
    @Override
    public boolean redo() {
        // TODO: 实现通用 redo 逻辑
        return false;
    }
    
    // 子类必须实现的抽象方法
    @Override
    public abstract String getContent();
    
    @Override
    public abstract void load();
    
    @Override
    public abstract void save();
}
