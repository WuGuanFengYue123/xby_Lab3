package com.team20.editor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Main 类测试
 */
class MainTest {
    
    @Test
    void testMainExists() {
        // 测试 Main 类是否存在
        assertNotNull(Main.class);
    }
    
    @Test
    void testMainMethodExists() throws NoSuchMethodException {
        // 测试 main 方法是否存在
        assertNotNull(Main.class.getMethod("main", String[].class));
    }
    
    @Test
    void testHelloWorld() {
        // 简单测试：确保程序可以运行不抛异常
        assertDoesNotThrow(() -> {
            Main.main(new String[]{});
        });
    }
}
