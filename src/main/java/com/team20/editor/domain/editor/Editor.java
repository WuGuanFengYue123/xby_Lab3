package com.team20.editor.domain.editor;

/**
 * 统一编辑器接口。保持语义稳定以满足 LSP。
 */
public interface Editor {
    String getName(); // 永不返回 null

    String getContent(); // 永不返回 null，空内容用 ""

    boolean canUndo();

    boolean canRedo();

    void undo();

    void redo();
}