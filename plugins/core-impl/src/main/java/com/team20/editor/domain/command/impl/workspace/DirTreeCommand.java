package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.workspace.Workspace;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * dir-tree [path]
 *
 * Displays a directory tree for the given path (or current directory if path is
 * blank).
 *
 * Formatting uses: ├──, └── and │ for vertical bars.
 */
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
        printRoot(root);
    }

    private void printRoot(File root) {
        if (root.isFile()) {
            System.out.println(root.getName());
        } else {
            System.out.println(root.getName().isEmpty() ? root.getPath() : root.getName());
            traverse(root, "", true);
        }
    }

    private void traverse(File dir, String prefix, boolean isRoot) {
        File[] children = dir.listFiles();
        if (children == null || children.length == 0)
            return;

        // sort directories first, then files, both alphabetically
        Arrays.sort(children, Comparator.comparing(File::isFile) // directories (isFile false) first
                .thenComparing(f -> f.getName().toLowerCase()));

        for (int i = 0; i < children.length; i++) {
            File f = children[i];
            boolean last = (i == children.length - 1);
            String branch = last ? "└── " : "├── ";
            System.out.println(prefix + branch + f.getName());
            if (f.isDirectory()) {
                String childPrefix = prefix + (last ? "    " : "│   ");
                traverse(f, childPrefix, false);
            }
        }
    }

    @Override
    public String toString() {
        return "dir-tree " + path;
    }
}