package com.team20.editor.core.command.impl.workspace;

import com.team20.editor.core.command.Command;
import com.team20.editor.core.workspace.Workspace;

/**
 * 加载文件命令
 */
public class LoadCommand implements Command {
    
    private Workspace workspace;
    private String filePath;
    
    public LoadCommand(Workspace workspace, String filePath) {
        this.workspace = workspace;
        this.filePath = filePath;
    }
    
    @Override
    public void execute() {
        // TODO: 实现加载逻辑
    }
    
    @Override
    public String getName() {
        return "load";
    }
}
