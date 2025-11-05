package com.team20.editor.core.event;

/**
 * 命令执行事件
 */
public class CommandEvent extends Event {
    
    private final String commandName;
    private final String commandArgs;
    
    public CommandEvent(String source, String commandName, String commandArgs) {
        super(source);
        this.commandName = commandName;
        this.commandArgs = commandArgs;
    }
    
    @Override
    public String getEventType() {
        return "COMMAND_EXECUTED";
    }
    
    public String getCommandName() {
        return commandName;
    }
    
    public String getCommandArgs() {
        return commandArgs;
    }
}
