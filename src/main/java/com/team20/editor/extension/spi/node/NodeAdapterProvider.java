package com.team20.editor.extension.spi.node;

import com.team20.editor.representation.tree.NodeAdapterFactory;
import java.util.Collection;

public interface NodeAdapterProvider {
    Collection<NodeAdapterFactory> factories();
}