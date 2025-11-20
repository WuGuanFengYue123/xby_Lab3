package com.team20.editor.representation.tree.adapters;

import com.team20.editor.domain.editor.Editor;
import com.team20.editor.representation.tree.AbstractNodeAdapter;

import java.util.HashMap;
import java.util.Map;

public class EditorNodeAdapter extends AbstractNodeAdapter {

    private final Editor editor;

    public EditorNodeAdapter(Editor editor) {
        this.editor = editor;
    }

    @Override
    public String getId() {
        return "editor:" + editor.getName();
    }

    @Override
    public String getName() {
        return editor.getName();
    }

    @Override
    public String getType() {
        return "editor";
    }

    @Override
    public Map<String, Object> attributes() {
        Map<String, Object> map = new HashMap<>();
        String content = editor.getContent();
        map.put("contentLength", content == null ? 0 : content.length());
        map.put("canUndo", editor.canUndo());
        map.put("canRedo", editor.canRedo());
        return map;
    }
}