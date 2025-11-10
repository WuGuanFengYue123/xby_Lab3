package com.team20.editor.infrastructure.persistence;

import com.team20.editor.domain.workspace.WorkspaceState;

public interface Serializer {
    String serialize(WorkspaceState state);

    WorkspaceState deserialize(String raw);

    String format();
}