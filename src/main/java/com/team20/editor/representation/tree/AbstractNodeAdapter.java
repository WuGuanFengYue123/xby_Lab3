package com.team20.editor.representation.tree;

import java.util.List;
import java.util.Map;

public abstract class AbstractNodeAdapter implements Node {

    @Override
    public List<Node> children() {
        return List.of();
    }

    @Override
    public Map<String, Object> attributes() {
        return Map.of();
    }
}