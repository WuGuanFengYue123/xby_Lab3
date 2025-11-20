package com.team20.editor.representation.tree.providers;

import com.team20.editor.domain.command.registry.AutoLoadingCommandRegistry;
import com.team20.editor.domain.command.CommandDescriptor;
import com.team20.editor.domain.editor.Editor;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.extension.spi.node.NodeAdapterProvider;
import com.team20.editor.representation.tree.Node;
import com.team20.editor.representation.tree.NodeAdapterFactory;
import com.team20.editor.representation.tree.adapters.CommandTypeNodeAdapter;
import com.team20.editor.representation.tree.adapters.EditorNodeAdapter;
import com.team20.editor.representation.tree.adapters.WorkspaceNodeAdapter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Core node adapter provider — adapt to the unified NodeAdapterProvider API.
 */
public class CoreNodeAdapterProvider implements NodeAdapterProvider {

    @Override
    public String getName() {
        return "core-node-adapter";
    }

    @Override
    public List<NodeAdapterFactory> getAdapterFactories() {
        return List.of(
                new NodeAdapterFactory() {
                    @Override
                    public Class<?> supportsType() {
                        return Workspace.class;
                    }

                    @Override
                    public Node adapt(Object source, NodeAdaptContext ctx) {
                        return new WorkspaceNodeAdapter((Workspace) source);
                    }
                },
                new NodeAdapterFactory() {
                    @Override
                    public Class<?> supportsType() {
                        return Editor.class;
                    }

                    @Override
                    public Node adapt(Object source, NodeAdaptContext ctx) {
                        return new EditorNodeAdapter((Editor) source);
                    }
                },
                new NodeAdapterFactory() {
                    @Override
                    public Class<?> supportsType() {
                        return AutoLoadingCommandRegistry.class;
                    }

                    @Override
                    public Node adapt(Object source, NodeAdaptContext ctx) {
                        AutoLoadingCommandRegistry reg = (AutoLoadingCommandRegistry) source;
                        List<Node> children = reg.getCommandNames().stream()
                                .map(name -> {
                                    String desc = reg.getCommandDescription(name);
                                    String d = desc == null ? "" : desc;
                                    return (Node) new CommandTypeNodeAdapter(name, d);
                                }).collect(Collectors.toList());
                        // 用 WorkspaceNodeAdapter 作为聚合容器（轻量 hack）
                        return new WorkspaceNodeAdapter(new Workspace()) {
                            @Override
                            public String getId() {
                                return "command-types";
                            }

                            @Override
                            public String getName() {
                                return "CommandTypes";
                            }

                            @Override
                            public String getType() {
                                return "command-types-root";
                            }

                            @Override
                            public java.util.Map<String, Object> attributes() {
                                return java.util.Map.of("count", children.size());
                            }

                            @Override
                            public java.util.List<Node> children() {
                                return children;
                            }
                        };
                    }
                });
    }
}