package com.team20.editor.extension.spi.editor;

import com.team20.editor.domain.editor.Editor;
import java.util.Collection;

public interface EditorProvider {
    Collection<EditorRegistration> editors();

    record EditorRegistration(String type, Factory factory, String description) {
        public interface Factory {
            Editor create();
        }

        public static EditorRegistration of(String type, Factory factory, String description) {
            return new EditorRegistration(type, factory, description);
        }
    }
}
