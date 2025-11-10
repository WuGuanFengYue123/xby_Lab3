package com.team20.editor.domain.editor.text;

import com.team20.editor.domain.editor.Editor;
import com.team20.editor.extension.spi.editor.EditorProvider;

import java.util.Collection;
import java.util.List;

public class TextEditorProvider implements EditorProvider {
    @Override
    public Collection<EditorRegistration> editors() {
        return List.of(
                EditorRegistration.of("text", new EditorRegistration.Factory() {
                    @Override
                    public Editor create() {
                        return new TextEditor("untitled");
                    }
                }, "Plain text editor"));
    }
}