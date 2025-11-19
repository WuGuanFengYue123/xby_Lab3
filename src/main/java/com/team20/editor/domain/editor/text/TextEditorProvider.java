package com.team20.editor.domain.editor.text;

import com.team20.editor.domain.editor.Editor;
import com.team20.editor.extension.spi.editor.EditorProvider;

import java.util.List;
import java.util.function.Function;

/**
 * TextEditorProvider - 提供 TextEditor 的 EditorRegistration。
 *
 * 此类仅包含 provider 实现，放在独立文件中以避免与 TextEditor 类重复定义冲突。
 */
public class TextEditorProvider implements EditorProvider {

    @Override
    public String getProviderName() {
        return "core-text";
    }

    @Override
    public List<EditorRegistration> getEditorRegistrations() {
        Function<String, Editor> factory = (path) -> new TextEditor(path);
        return List.of(new EditorRegistration("text", factory, List.of("txt", "text")));
    }
}