package com.team20.editor.extension.spi.editor;

import com.team20.editor.domain.editor.Editor;

/**
 * 编辑器提供者接口（SPI）
 */
public interface EditorProvider {
    /**
     * 获取支持的文件扩展名（如 "txt", "xml"）
     */
    String getSupportedExtension();

    /**
     * 创建编辑器实例
     */
    Editor createEditor(String filepath);

    /**
     * 编辑器类型名称
     */
    String getEditorType();
}
