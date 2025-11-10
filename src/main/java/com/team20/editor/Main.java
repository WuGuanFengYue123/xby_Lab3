package com.team20.editor;

import com.team20.editor.bootstrap.ApplicationContext;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.domain.command.registry.AutoLoadingCommandRegistry;
import com.team20.editor.representation.tree.Node;
import com.team20.editor.representation.tree.NodeAdapterFactory;
import com.team20.editor.representation.tree.NodeTreeBuilder;

import java.time.Instant;

public final class Main {

    public static void main(String[] args) {
        banner();
        ApplicationContext context = new ApplicationContext();
        Workspace workspace = context.createWorkspace();

        NodeAdapterFactory.NodeAdaptContext adaptContext = new NodeAdapterFactory.NodeAdaptContext() {
            @Override
            public <T> T get(Class<T> type) {
                if (type.equals(Workspace.class))
                    return type.cast(workspace);
                if (type.equals(AutoLoadingCommandRegistry.class))
                    return type.cast(context.commandRegistry());
                return null;
            }
        };

        NodeTreeBuilder builder = new NodeTreeBuilder(adaptContext, context.nodeAdapterProviders());
        Node root = builder.build(workspace);

        System.out.println("Tree View:");
        printTree(root, 0);
        System.out.println();
        System.out.println(context.dumpSummary());
        System.out.println();
        System.out.println("Team20 Text Editor ready. Type 'help' for commands.");
    }

    private static void banner() {
        System.out.println("========================================");
        System.out.println("  Team20 Text Editor v1.0.0");
        System.out.println("========================================");
        System.out.println("启动时间: " + Instant.now());
        System.out.println("Java 版本: " + System.getProperty("java.version"));
        System.out.println("操作系统: " + System.getProperty("os.name"));
        System.out.println("----------------------------------------");
    }

    private static void printTree(Node node, int depth) {
        indent(depth);
        System.out.printf("├─ (%s) %s %s%n", node.getType(), node.getName(), node.attributes());
        for (Node child : node.children()) {
            printTree(child, depth + 1);
        }
    }

    private static void indent(int d) {
        for (int i = 0; i < d; i++)
            System.out.print("│  ");
    }
}