package com.team20.editor.core.editor;

/**
 * 编辑器接口
 * 
 * 定义所有编辑器必须实现的基本操作
 */
public interface Editor {
    
    /**
     * 获取文件路径
     * 
     * @return 文件路径
     */
    String getFilePath();
    
    /**
     * 获取文件内容
     * 
     * @return 文件内容字符串
     */
    String getContent();
    
    /**
     * 加载文件内容
     */
    void load();
    
    /**
     * 保存文件内容
     */
    void save();
    
    /**
     * 检查文件是否已修改
     * 
     * @return true 如果已修改
     */
    boolean isModified();
    
    /**
     * 设置修改标记
     * 
     * @param modified 是否已修改
     */
    void setModified(boolean modified);
    
    /**
     * 撤销操作
     * 
     * @return true 如果撤销成功
     */
    boolean undo();
    
    /**
     * 重做操作
     * 
     * @return true 如果重做成功
     */
    boolean redo();
}
