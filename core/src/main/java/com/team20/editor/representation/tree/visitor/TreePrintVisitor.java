package com.team20.editor.representation.tree.visitor;

import com.team20.editor.representation.tree.Node;
import com.team20.editor.representation.tree.NodeVisitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreePrintVisitor implements NodeVisitor {

    private final Map<String, Integer> depthMap = new HashMap<>();
    private final Map<String, Boolean> lastChildMap = new HashMap<>();

    @Override
    public void visit(Node node) {
        int depth = depthMap.getOrDefault(node.getId(), 0);
        boolean isLast = lastChildMap.getOrDefault(node.getId(), true);

        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < depth - 1; i++) {
            prefix.append("│   ");
        }
        if (depth > 0) {
            prefix.append(isLast ? "└── " : "├── ");
        }

        System.out.println(prefix + node.getName());

        List<Node> children = node.children();
        for (int i = 0; i < children.size(); i++) {
            Node c = children.get(i);
            depthMap.put(c.getId(), depth + 1);
            lastChildMap.put(c.getId(), i == children.size() - 1);
        }
    }
}
