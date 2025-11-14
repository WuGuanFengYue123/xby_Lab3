package com.team20.editor.infrastructure.event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 命令执行事件
 */
public class CommandEvent implements Event {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");

    private final String commandName;
    private final String arguments;
    private final LocalDateTime timestamp;
    private final String filepath;

    public CommandEvent(String commandName, String arguments, String filepath) {
        this.commandName = commandName;
        this.arguments = arguments;
        this.timestamp = LocalDateTime.now();
        this.filepath = filepath;
    }

    @Override
    public String getType() {
        return "command";
    }

    @Override
    public Object getData() {
        return this;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getArguments() {
        return arguments;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(FORMATTER);
    }

    @Override
    public String toString() {
        String args = arguments.isEmpty() ? "" : " " + arguments;
        return String.format("%s %s%s", getFormattedTimestamp(), commandName, args);
    }
}