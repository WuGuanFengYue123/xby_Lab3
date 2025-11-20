package com.team20.editor.representation.tree.adapters;

import com.team20.editor.representation.tree.AbstractNodeAdapter;

import java.util.Map;

public class CommandTypeNodeAdapter extends AbstractNodeAdapter {

    private final String commandName;
    private final String description;

    public CommandTypeNodeAdapter(String commandName, String description) {
        this.commandName = commandName;
        this.description = description;
    }

    @Override
    public String getId() {
        return "commandType:" + commandName;
    }

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public String getType() {
        return "command-type";
    }

    @Override
    public Map<String, Object> attributes() {
        return Map.of("description", description);
    }
}