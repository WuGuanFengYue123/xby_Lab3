package com.team20.editor.domain.editor;

/**
 * 编辑器基础接口（统一编辑器契约）
 */
public interface Editor {
    /** 标识（通常为文件路径或名称） */
    String getName();

    /** 以文本方式返回全部内容 */
    String getContent();

    /** 当前文件是否已修改（未保存） */
    boolean isModified();

    /** 标记文件为已修改或已保存 */
    void setModified(boolean modified);

    /**
     * 用整个字符串加载内容（用于 load / init / "另存为" 时的装载）。
     *
     * @param content 完整文件内容（可能包含多行，使用 '\n' 分隔）
     */
    void loadContent(String content);

    /**
     * 是否能撤销（默认 false）。某些编辑器可覆盖以提供真实的 undo 能力。
     */
    default boolean canUndo() {
        return false;
    }

    /**
     * 是否能重做（默认 false）。某些编辑器可覆盖以提供真实的 redo 能力。
     */
    default boolean canRedo() {
        return false;
    }
}