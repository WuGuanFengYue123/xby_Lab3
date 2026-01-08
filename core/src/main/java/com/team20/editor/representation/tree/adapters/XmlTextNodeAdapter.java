package com.team20.editor.representation.tree.adapters;

import com.team20.editor.representation.tree.AbstractNodeAdapter;
import com.team20.editor.representation.tree.Node;

import java.util.List;

public class XmlTextNodeAdapter extends AbstractNodeAdapter {

    private final org.w3c.dom.Node textNode; // 用完全限定名

    public XmlTextNodeAdapter(org.w3c.dom.Node textNode) {
        this.textNode = textNode;
    }

    @Override
    public String getId() {
        return "text@" + textNode.hashCode();
    }

    @Override
    public String getName() {
        String content = textNode.getTextContent();
        return "\"" + (content != null ? content.trim() : "") + "\"";
    }

    @Override
    public String getType() {
        return "text";
    }

    @Override
    public List<Node> children() {
        return List.of(); // 文本节点没有子节点
    }
}
