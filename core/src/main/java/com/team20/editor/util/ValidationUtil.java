package com.team20.editor.util;

/**
 * 输入验证工具类
 */
public class ValidationUtil {
    
    /**
     * 验证行号是否有效
     * 
     * @param line 行号
     * @param maxLine 最大行号
     * @return true 如果有效
     */
    public static boolean isValidLine(int line, int maxLine) {
        return line >= 1 && line <= maxLine;
    }
    
    /**
     * 验证列号是否有效
     * 
     * @param col 列号
     * @param maxCol 最大列号
     * @return true 如果有效
     */
    public static boolean isValidColumn(int col, int maxCol) {
        return col >= 1 && col <= maxCol;
    }
    
    /**
     * 验证文件路径格式
     * 
     * @param filePath 文件路径
     * @return true 如果有效
     */
    public static boolean isValidFilePath(String filePath) {
        // TODO: 实现
        return !StringUtil.isEmpty(filePath);
    }
}
