package com.team20.editor.domain.editor;

/**
 * 统一编辑器接口
 */
public interface Editor {
    /**
     * 获取编辑器名称/文件路径
     */
    String getName();

    /**
     * 获取文件路径（与 getName 相同）
     */
    default String getFilepath() {
        return getName();
    }

    /**
     * 获取内容
     */
    String getContent();

    /**
     * 是否可以撤销
     */
    boolean canUndo();

    /**
     * 是否可以重做
     */
    boolean canRedo();

    /**
     * 撤销
     */
    void undo();

    /**
     * 重做
     */
    void redo();

    /**
     * 是否已修改
     */
    boolean isModified();

    /**
     * 设置修改状态
     */
    void setModified(boolean modified);
}