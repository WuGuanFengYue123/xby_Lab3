package com.team20.editor.infrastructure.event;

public class CommandEvent extends Event {
    private final String commandName;

    public CommandEvent(String commandName) {
        this.commandName = commandName;
    }

    public String commandName() {
        return commandName;
    }
}