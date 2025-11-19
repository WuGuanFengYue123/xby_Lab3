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
}