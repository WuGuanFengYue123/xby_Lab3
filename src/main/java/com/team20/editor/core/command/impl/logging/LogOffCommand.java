package com.team20.editor.core.command.impl.logging;

import com.team20.editor.core.command.Command;
import com.team20.editor.core.workspace.Workspace;

/**
 * 关闭日志命令
 */
public class LogOffCommand implements Command {
    
    private Workspace workspace;
    private String filePath;
    
    public LogOffCommand(Workspace workspace, String filePath) {
        this.workspace = workspace;
        this.filePath = filePath;
    }
    
    @Override
    public void execute() {
        // TODO: 实现关闭日志逻辑
    }
    
    @Override
    public String getName() {
        return "log-off";
    }
}
