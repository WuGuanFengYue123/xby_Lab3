package com.team20.editor.representation.tree.adapters;

import com.team20.editor.representation.tree.AbstractNodeAdapter;
import com.team20.editor.representation.tree.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class XmlElementNodeAdapter extends AbstractNodeAdapter {

    private final Element element;

    public XmlElementNodeAdapter(Element element) {
        this.element = element;
    }

    @Override
    public String getId() {
        // 可以用 element 的 hashCode 保证唯一，也可以结合 tagName
        return element.getTagName() + "@" + element.hashCode();
    }

    @Override
    public String getName() {
        return element.getTagName();
    }

    @Override
    public String getType() {
        return "element";
    }

    @Override
    public List<Node> children() {
        List<Node> out = new ArrayList<>();
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            org.w3c.dom.Node n = nodes.item(i); // 用完全限定名代替 DomNode
            if (n instanceof Element el) {
                out.add(new XmlElementNodeAdapter(el));
            } else if (n.getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
                String text = n.getTextContent();
                if (text != null && !text.trim().isEmpty()) {
                    out.add(new XmlTextNodeAdapter(n));
                }
            }
        }
        return out;
    }
}

