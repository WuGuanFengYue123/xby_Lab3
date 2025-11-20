package com.team20.editor.extension.spi.editor;

import com.team20.editor.domain.editor.Editor;

import java.util.List;
import java.util.function.Function;

/**
 * 编辑器提供者 SPI。
 *
 * 该接口包含一个嵌套的 EditorRegistration 描述类型，便于 provider
 * 在注册时返回该 provider 支持的编辑器类型与工厂函数（兼容以前使用的 registration pattern）。
 *
 * 责任：
 * - getSupportedExtensions()：返回 provider 支持的扩展名列表（如 "txt"）
 * - getEditorRegistrations()：返回 provider 提供的 EditorRegistration 列表，注册项包含一个 factory 用于创建 Editor 实例
 *
 * 说明：我们在这里统一签名以兼容各种实现并便于 AutoLoadingEditorRegistry 使用。
 */
public interface EditorProvider {

    /**
     * 描述一个编辑器的注册信息：类型名 + 工厂函数（接收 filepath，返回 Editor）。
     */
    class EditorRegistration {
        private final String editorType;
        private final Function<String, Editor> factory;
        private final List<String> supportedExtensions;

        public EditorRegistration(String editorType, Function<String, Editor> factory, List<String> supportedExtensions) {
            this.editorType = editorType;
            this.factory = factory;
            this.supportedExtensions = supportedExtensions;
        }

        public String getEditorType() {
            return editorType;
        }

        public Function<String, Editor> getFactory() {
            return factory;
        }

        public List<String> getSupportedExtensions() {
            return supportedExtensions;
        }
    }

    /**
     * 返回 provider 的名称（短标识），便于日志/诊断使用。
     */
    String getProviderName();

    /**
     * 返回 provider 提供的注册项（可能是 1 个或多个 EditorRegistration）。
     */
    List<EditorRegistration> getEditorRegistrations();
}