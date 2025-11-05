package com.team20.editor.registry;

import com.team20.editor.core.editor.Editor;

/**
 * 编辑器工厂接口
 */
public interface EditorFactory {
    
    /**
     * 创建编辑器实例
     * 
     * @param filePath 文件路径
     * @return 编辑器实例
     */
    Editor createEditor(String filePath);
    
    /**
     * 获取工厂支持的文件扩展名
     * 
     * @return 文件扩展名（如 ".txt"）
     */
    String getSupportedExtension();
}
