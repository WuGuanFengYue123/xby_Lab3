package com.team20.editor.domain.editor.text;

import com.team20.editor.domain.editor.Editor;
import com.team20.editor.extension.spi.editor.EditorProvider;

import java.util.List;
import java.util.function.Function;

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