package com.team20.editor.representation.tree;

import java.util.List;
import java.util.Map;

public interface Node {
    String getId();

    String getName();

    String getType();

    List<Node> children();

    Map<String, Object> attributes();

    default void accept(NodeVisitor visitor) {
        visitor.visit(this);
        for (Node c : children())
            c.accept(visitor);
    }
}
