package com.team20.editor.core.editor.text;

import com.team20.editor.core.editor.AbstractEditor;
import java.util.List;

/**
 * 文本编辑器实现
 * 
 * 职责：
 * - 管理文本内容（行数组）
 * - 实现基本编辑操作
 * - 支持 undo/redo
 * 
 * 数据结构：List<String>，每个元素是一行
 */
public class TextEditor extends AbstractEditor {
    
    private List<String> lines;
    
    /**
     * 构造函数
     * 
     * @param filePath 文件路径
     */
    public TextEditor(String filePath) {
        super(filePath);
        // TODO: 初始化 lines
    }
    
    @Override
    public String getContent() {
        // TODO: 将 lines 连接成字符串
        return null;
    }
    
    @Override
    public void load() {
        // TODO: 从文件加载内容到 lines
    }
    
    @Override
    public void save() {
        // TODO: 将 lines 保存到文件
    }
    
    /**
     * 追加文本到末尾
     * 
     * @param text 要追加的文本
     */
    public void append(String text) {
        // TODO: 实现
        setModified(true);
    }
    
    /**
     * 在指定位置插入文本
     * 
     * @param line 行号（从1开始）
     * @param col 列号（从1开始）
     * @param text 要插入的文本
     */
    public void insert(int line, int col, String text) {
        // TODO: 实现
        setModified(true);
    }
    
    /**
     * 删除指定位置的字符
     * 
     * @param line 行号（从1开始）
     * @param col 列号（从1开始）
     * @param length 删除长度
     */
    public void delete(int line, int col, int length) {
        // TODO: 实现
        setModified(true);
    }
    
    /**
     * 替换指定位置的字符
     * 
     * @param line 行号（从1开始）
     * @param col 列号（从1开始）
     * @param length 替换长度
     * @param text 新文本
     */
    public void replace(int line, int col, int length, String text) {
        // TODO: 实现
        setModified(true);
    }
    
    /**
     * 显示指定范围的内容
     * 
     * @param startLine 起始行（从1开始，0表示全部）
     * @param endLine 结束行（从1开始，0表示全部）
     * @return 格式化的文本内容
     */
    public String show(int startLine, int endLine) {
        // TODO: 实现
        return null;
    }
}
