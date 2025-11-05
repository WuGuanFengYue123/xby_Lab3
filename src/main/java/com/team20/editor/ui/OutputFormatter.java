package com.team20.editor.ui;

import com.team20.editor.core.editor.Editor;
import java.util.List;

/**
 * 输出格式化工具
 * 
 * 职责：
 * - 格式化编辑器列表
 * - 格式化目录树
 * - 格式化错误消息
 */
public class OutputFormatter {
    
    /**
     * 格式化编辑器列表
     * 
     * @param editors 编辑器列表
     * @param activeEditor 当前活动编辑器
     * @return 格式化的字符串
     */
    public static String formatEditorList(List<Editor> editors, Editor activeEditor) {
        // TODO: 实现格式化逻辑
        // 格式：
        // * file1.txt [modified]
        //   file2.txt
        return null;
    }
    
    /**
     * 格式化目录树
     * 
     * @param path 目录路径
     * @return 格式化的字符串
     */
    public static String formatDirectoryTree(String path) {
        // TODO: 实现目录树格式化
        return null;
    }
    
    /**
     * 格式化成功消息
     * 
     * @param message 消息内容
     * @return 格式化的字符串
     */
    public static String formatSuccess(String message) {
        return "[SUCCESS] " + message;
    }
    
    /**
     * 格式化错误消息
     * 
     * @param message 消息内容
     * @return 格式化的字符串
     */
    public static String formatError(String message) {
        return "[ERROR] " + message;
    }
}
