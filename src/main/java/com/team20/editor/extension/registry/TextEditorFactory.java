package com.team20.editor.extension.registry;

import com.team20.editor.domain.editor.Editor;
import com.team20.editor.domain.editor.text.TextEditor;

/**
 * 文本编辑器工厂
 */
public class TextEditorFactory implements EditorFactory {

    @Override
    public Editor createEditor(String filePath) {
        return new TextEditor(filePath);
    }

    @Override
    public String getSupportedExtension() {
        return ".txt";
    }
}
