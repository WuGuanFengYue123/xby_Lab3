package com.team20.editor.representation.tree.adapters;

import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.representation.tree.AbstractNodeAdapter;

import java.util.HashMap;
import java.util.Map;

public class WorkspaceNodeAdapter extends AbstractNodeAdapter {

    private final Workspace workspace;

    public WorkspaceNodeAdapter(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public String getId() {
        return "workspace:" + Integer.toHexString(System.identityHashCode(workspace));
    }

    @Override
    public String getName() {
        return "Workspace";
    }

    @Override
    public String getType() {
        return "workspace";
    }

    @Override
    public Map<String, Object> attributes() {
        Map<String, Object> map = new HashMap<>();
        map.put("editorCount", workspace.getEditors().size());
        map.put("activeEditor", workspace.getActiveEditor() != null ? workspace.getActiveEditor().getName() : "(none)");
        return map;
    }
}