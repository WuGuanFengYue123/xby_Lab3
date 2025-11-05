package com.team20.editor.core.command.impl.workspace;

import com.team20.editor.core.command.Command;
import com.team20.editor.core.workspace.Workspace;

/**
 * 关闭文件命令
 */
public class CloseCommand implements Command {
    
    private Workspace workspace;
    private String filePath;
    
    public CloseCommand(Workspace workspace, String filePath) {
        this.workspace = workspace;
        this.filePath = filePath;
    }
    
    @Override
    public void execute() {
        // TODO: 实现关闭逻辑
    }
    
    @Override
    public String getName() {
        return "close";
    }
}
