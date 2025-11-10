package com.team20.editor.infrastructure.persistence;

import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.domain.workspace.WorkspaceState;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

/**
 * 简单的持久化管理器示例：把 workspace 状态读写到文件（使用已注册的 Serializer）。
 *
 * 注意：这里假定 Workspace 提供 getState() 返回 WorkspaceState，
 * 并且 WorkspaceState 提供 toWorkspace() 方法恢复一个 Workspace 实例。
 */
public class PersistenceManager {

    private final Serializer defaultSerializer;

    public PersistenceManager(Serializer defaultSerializer) {
        this.defaultSerializer = defaultSerializer;
    }

    public void save(Workspace workspace, Path target) throws IOException {
        if (workspace == null)
            throw new IllegalArgumentException("workspace is null");
        WorkspaceState state = workspace.getState();
        String raw = defaultSerializer.serialize(state);
        Files.createDirectories(target.getParent());
        Files.writeString(target, raw, StandardCharsets.UTF_8);
    }

    public Workspace load(Path source) throws IOException {
        if (source == null || !Files.exists(source))
            return new Workspace();
        String raw = Files.readString(source, StandardCharsets.UTF_8);
        WorkspaceState state = defaultSerializer.deserialize(raw);
        if (state == null)
            return new Workspace();
        return state.toWorkspace();
    }
}