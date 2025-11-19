package com.team20.editor.extension.spi.editor;

import com.team20.editor.domain.editor.Editor;

/**
 * 编辑器提供者接口（SPI）
 */
public interface EditorProvider {
    /**
     * 支持的扩展名（不带点），例如 "txt"
     */
    String getSupportedExtension();

    /**
     * 根据文件路径创建 Editor 实例（实现决定名称如何填充）
     */
    Editor createEditor(String filepath);

    /**
     * 人类可读的编辑器类型名称，例如 "TextEditor"
     */
    String getEditorType();
}
