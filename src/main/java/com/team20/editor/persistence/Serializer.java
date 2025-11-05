package com.team20.editor.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 序列化工具类
 * 
 * 使用 Gson 进行 JSON 序列化
 */
public class Serializer {
    
    private Gson gson;
    
    /**
     * 构造函数
     */
    public Serializer() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }
    
    /**
     * 将对象序列化为 JSON 字符串
     * 
     * @param obj 对象
     * @return JSON 字符串
     */
    public String toJson(Object obj) {
        return gson.toJson(obj);
    }
    
    /**
     * 从 JSON 字符串反序列化对象
     * 
     * @param json JSON 字符串
     * @param clazz 类对象
     * @param <T> 类型参数
     * @return 反序列化后的对象
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}
