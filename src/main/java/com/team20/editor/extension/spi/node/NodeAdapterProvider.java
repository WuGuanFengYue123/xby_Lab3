package com.team20.editor.extension.spi.node;

import com.team20.editor.representation.tree.AbstractNodeAdapter;

import java.util.List;

/**
 * 节点适配器提供者接口（SPI）
 */
public interface NodeAdapterProvider {
    /**
     * 获取此提供者提供的所有适配器
     */
    List<AbstractNodeAdapter<?>> getAdapters();

    /**
     * 提供者名称
     */
    String getName();
}