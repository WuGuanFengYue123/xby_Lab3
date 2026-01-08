package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.representation.tree.Node;
import com.team20.editor.representation.tree.adapters.DirectoryNodeAdapter;
import com.team20.editor.representation.tree.visitor.TreePrintVisitor;

import java.io.File;

public class DirTreeCommand implements Command {

    private final String path;

    public DirTreeCommand(String path) {
        this.path = (path == null || path.isBlank()) ? "." : path.trim();
    }

    @Override
    public void execute(Workspace workspace) {
        File root = new File(path);
        if (!root.exists()) {
            System.out.println("路径不存在: " + path);
            return;
        }

        Node rootNode = new DirectoryNodeAdapter(root);
        rootNode.accept(new TreePrintVisitor());
    }

    @Override
    public String toString() {
        return "dir-tree " + path;
    }
}
