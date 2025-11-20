package com.team20.editor.infrastructure.persistence;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

/**
 * 简单文件读写管理器（封装读写细节）。
 */
public class PersistenceManager {

    private final Serializer serializer;

    public PersistenceManager(Serializer serializer) {
        this.serializer = serializer;
    }

    public String readFile(Path p) throws IOException {
        return Files.readString(p, StandardCharsets.UTF_8);
    }

    public void writeFile(Path p, String content) throws IOException {
        Files.createDirectories(p.getParent() == null ? Path.of(".") : p.getParent());
        Files.writeString(p, content, StandardCharsets.UTF_8);
    }
}