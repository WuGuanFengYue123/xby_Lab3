package com.team20.editor.util;

import java.io.*;
import java.nio.file.*;
import java.util.List;

/**
 * 文件操作工具类
 */
public class FileUtil {
    
    /**
     * 读取文件所有行
     * 
     * @param filePath 文件路径
     * @return 行列表
     * @throws IOException 如果读取失败
     */
    public static List<String> readAllLines(String filePath) throws IOException {
        // TODO: 实现
        return null;
    }
    
    /**
     * 写入文件所有行
     * 
     * @param filePath 文件路径
     * @param lines 行列表
     * @throws IOException 如果写入失败
     */
    public static void writeAllLines(String filePath, List<String> lines) throws IOException {
        // TODO: 实现
    }
    
    /**
     * 检查文件是否存在
     * 
     * @param filePath 文件路径
     * @return true 如果存在
     */
    public static boolean exists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
    
    /**
     * 从文件路径提取扩展名
     * 
     * @param filePath 文件路径
     * @return 扩展名（包括点，如 ".txt"）
     */
    public static String getExtension(String filePath) {
        // TODO: 实现
        return null;
    }
    
    /**
     * 从文件路径提取文件名
     * 
     * @param filePath 文件路径
     * @return 文件名
     */
    public static String getFileName(String filePath) {
        // TODO: 实现
        return null;
    }
}
