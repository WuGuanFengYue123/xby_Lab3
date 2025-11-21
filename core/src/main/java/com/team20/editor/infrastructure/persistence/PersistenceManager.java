package com.team20.editor.infrastructure.persistence;

import com.team20.editor.domain.workspace.WorkspaceState;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

/**
 * 简单文件读写管理器（封装读写细节）。
 *
 * 兼容性：新增 load(String) / save(String, String) 便捷方法，以兼容插件中使用的 API。
 * 增强：对 Serializer 的序列化/反序列化异常做了适当的捕获并包装为 IOException，
 * 以便调用方以统一方式处理 I/O/序列化错误。
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

    /**
     * Convenience: load the file content by path string.
     * Throws IOException when reading fails.
     */
    public String load(String filepath) throws IOException {
        return readFile(Path.of(filepath));
    }

    /**
     * Convenience: save the given content to the file path.
     * Throws IOException when writing fails.
     */
    public void save(String filepath, String content) throws IOException {
        writeFile(Path.of(filepath), content);
    }

    /**
     * Access to the serializer (kept for API completeness).
     */
    public Serializer serializer() {
        return serializer;
    }

    /**
     * Save WorkspaceState to a file using the configured Serializer.
     * Wraps serialization errors in IOException.
     */
    public void saveWorkspaceState(String filepath, WorkspaceState state) throws IOException {
        if (state == null) throw new IllegalArgumentException("state is null");
        try {
            String payload = serializer.serialize(state); // may throw checked exception
            save(filepath, payload);
        } catch (IOException io) {
            // pass through IO exceptions from save(...)
            throw io;
        } catch (Exception ex) {
            // wrap any serialization-related exception
            throw new IOException("Failed to serialize WorkspaceState: " + ex.getMessage(), ex);
        }
    }

    /**
     * Load WorkspaceState from a file using the configured Serializer.
     * Returns null if file not found or empty.
     * Wraps deserialization errors in IOException.
     */
    public WorkspaceState loadWorkspaceState(String filepath) throws IOException {
        try {
            String payload = load(filepath);
            if (payload == null || payload.isBlank()) return null;
            Object obj;
            try {
                obj = serializer.deserialize(payload); // may throw checked exception
            } catch (Exception ex) {
                throw new IOException("Failed to deserialize WorkspaceState: " + ex.getMessage(), ex);
            }
            if (obj == null) return null;
            if (!(obj instanceof WorkspaceState)) {
                throw new IOException("Deserialized object is not a WorkspaceState, got: " + obj.getClass());
            }
            return (WorkspaceState) obj;
        } catch (IOException io) {
            // rethrow IO exceptions so callers can detect missing file vs other IO errors
            throw io;
        } catch (Throwable t) {
            // catch-all: wrap unexpected errors
            throw new IOException("Error loading WorkspaceState: " + t.getMessage(), t);
        }
    }
}