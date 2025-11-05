package com.team20.editor.registry;

import com.team20.editor.core.editor.Editor;
import java.util.HashMap;
import java.util.Map;

/**
 * 编辑器注册表（单例）
 * 
 * 职责：
 * - 注册编辑器工厂
 * - 根据文件扩展名创建编辑器
 * 
 * 设计模式：Registry Pattern + Factory Pattern
 * 
 * Lab2 扩展示例：
 * EditorRegistry.register(new XmlEditorFactory());
 */
public class EditorRegistry {
    
    private static EditorRegistry instance;
    private Map<String, EditorFactory> factories;
    
    /**
     * 私有构造函数
     */
    private EditorRegistry() {
        this.factories = new HashMap<>();
    }
    
    /**
     * 获取单例实例
     * 
     * @return EditorRegistry 实例
     */
    public static EditorRegistry getInstance() {
        if (instance == null) {
            instance = new EditorRegistry();
        }
        return instance;
    }
    
    /**
     * 注册编辑器工厂
     * 
     * @param factory 编辑器工厂
     */
    public void register(EditorFactory factory) {
        // TODO: 实现注册逻辑
    }
    
    /**
     * 创建编辑器
     * 
     * @param filePath 文件路径
     * @return 编辑器实例
     * @throws IllegalArgumentException 如果不支持该文件类型
     */
    public Editor createEditor(String filePath) {
        // TODO: 实现创建逻辑
        // 1. 从文件路径提取扩展名
        // 2. 查找对应的工厂
        // 3. 使用工厂创建编辑器
        return null;
    }
    
    /**
     * 检查是否支持该文件类型
     * 
     * @param extension 文件扩展名
     * @return true 如果支持
     */
    public boolean isSupported(String extension) {
        return factories.containsKey(extension);
    }
}
