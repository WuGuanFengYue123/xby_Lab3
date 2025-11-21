package com.team20.editor.representation.tree.adapters;

import com.team20.editor.representation.tree.AbstractNodeAdapter;
import com.team20.editor.representation.tree.Node;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class RootNodeAdapter extends AbstractNodeAdapter {

    private final List<Node> children;

    public RootNodeAdapter(List<Node> children) {
        this.children = children;
    }

    @Override
    public String getId() {
        return "root";
    }

    @Override
    public String getName() {
        return "Root";
    }

    @Override
    public String getType() {
        return "root";
    }

    @Override
    public List<Node> children() {
        return children;
    }

    @Override
    public Map<String, Object> attributes() {
        return Map.of("timestamp", Instant.now().toString(),
                "childCount", children.size());
    }
}