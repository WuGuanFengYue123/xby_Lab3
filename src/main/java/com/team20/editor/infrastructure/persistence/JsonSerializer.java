package com.team20.editor.infrastructure.persistence;

import com.google.gson.Gson;
import com.team20.editor.domain.workspace.WorkspaceState;

public class JsonSerializer implements Serializer {
    private final Gson gson = new Gson();

    @Override
    public String serialize(WorkspaceState state) {
        return gson.toJson(state);
    }

    @Override
    public WorkspaceState deserialize(String raw) {
        return gson.fromJson(raw, WorkspaceState.class);
    }

    @Override
    public String format() {
        return "json";
    }
}