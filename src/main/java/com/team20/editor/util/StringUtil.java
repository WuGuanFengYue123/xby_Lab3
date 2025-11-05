package com.team20.editor.util;

/**
 * 字符串处理工具类
 */
public class StringUtil {
    
    /**
     * 检查字符串是否为空或 null
     * 
     * @param str 字符串
     * @return true 如果为空或 null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 解析带引号的字符串
     * 
     * @param quoted 带引号的字符串
     * @return 去除引号后的字符串
     */
    public static String unquote(String quoted) {
        // TODO: 实现
        return null;
    }
    
    /**
     * 分割命令行参数（考虑引号）
     * 
     * @param input 输入字符串
     * @return 参数数组
     */
    public static String[] splitArguments(String input) {
        // TODO: 实现
        // 需要处理引号包裹的参数
        return null;
    }
}
