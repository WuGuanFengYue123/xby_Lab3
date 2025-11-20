package com.team20.editor.representation.tree;

import com.team20.editor.extension.spi.node.NodeAdapterProvider;
import java.util.ServiceLoader;

/**
 * NodeTreeBuilder - small compatibility change: use NodeAdapterProvider.getAdapterFactories()
 */
public class NodeTreeBuilder {

    public NodeTreeBuilder() {
        // example usage: iterate providers and their adapter factories
        ServiceLoader<NodeAdapterProvider> loader = ServiceLoader.load(NodeAdapterProvider.class);
        for (NodeAdapterProvider p : loader) {
            var factories = p.getAdapterFactories(); // new unified API
            if (factories == null) continue;
            for (var f : factories) {
                // register or use the factory (implementation-specific)
            }
        }
    }
}