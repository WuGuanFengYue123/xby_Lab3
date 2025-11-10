package com.team20.editor.representation.tree;

import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.extension.spi.node.NodeAdapterProvider;
import com.team20.editor.representation.tree.adapters.RootNodeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 构建树的入口，接受 NodeAdapterProvider 列表（由 ApplicationContext 通过 ServiceLoader 提供）
 */
public class NodeTreeBuilder {

    private final NodeAdapterFactory.NodeAdaptContext ctx;
    private final List<NodeAdapterFactory> factories = new ArrayList<>();

    public NodeTreeBuilder(NodeAdapterFactory.NodeAdaptContext ctx,
            List<NodeAdapterProvider> providers) {
        this.ctx = ctx;
        providers.forEach(p -> factories.addAll(p.factories()));
    }

    public Node build(Workspace workspace) {
        List<Node> children = new ArrayList<>();
        for (NodeAdapterFactory f : factories) {
            if (f.supportsType().isAssignableFrom(Workspace.class)) {
                children.add(f.adapt(workspace, ctx));
            }
            workspace.getEditors().forEach(ed -> {
                if (f.supportsType().isAssignableFrom(ed.getClass())) {
                    children.add(f.adapt(ed, ctx));
                }
            });
        }
        return new RootNodeAdapter(children);
    }
}