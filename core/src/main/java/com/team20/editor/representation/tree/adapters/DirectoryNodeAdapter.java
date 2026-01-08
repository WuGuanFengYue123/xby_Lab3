package com.team20.editor.representation.tree.adapters;

import com.team20.editor.representation.tree.AbstractNodeAdapter;
import com.team20.editor.representation.tree.Node;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DirectoryNodeAdapter extends AbstractNodeAdapter {

    private final File file;

    public DirectoryNodeAdapter(File file) {
        this.file = file;
    }

    @Override
    public String getId() {
        return file.getAbsolutePath();
    }

    @Override
    public String getName() {
        return file.getName().isEmpty() ? file.getPath() : file.getName();
    }

    @Override
    public String getType() {
        return file.isDirectory() ? "directory" : "file";
    }

    @Override
    public List<Node> children() {
        if (!file.isDirectory())
            return List.of();

        File[] children = file.listFiles();
        if (children == null)
            return List.of();

        Arrays.sort(children,
                Comparator.comparing(File::isFile)
                        .thenComparing(f -> f.getName().toLowerCase())
        );

        return Arrays.stream(children)
                .map(DirectoryNodeAdapter::new)
                .collect(Collectors.toList());
    }
}

