package com.team20.editor.extension.spi.node;

import com.team20.editor.representation.tree.NodeAdapterFactory;

import java.util.List;

/**
 * Node 适配器提供者 SPI。
 *
 * - getName()：提供者名称
 * - getAdapterFactories()：返回该 provider 提供的 NodeAdapterFactory 列表
 *
 * 备注：统一命名为 getAdapterFactories()（替代以前可能的 factories()/factories(...)）
 */
public interface NodeAdapterProvider {
    String getName();
    List<NodeAdapterFactory> getAdapterFactories();
}