package com.team20.editor.core.command.impl.workspace;

import com.team20.editor.core.command.Command;
import com.team20.editor.core.workspace.Workspace;

/**
 * 切换活动文件命令
 */
public class EditCommand implements Command {
    
    private Workspace workspace;
    private String filePath;
    
    public EditCommand(Workspace workspace, String filePath) {
        this.workspace = workspace;
        this.filePath = filePath;
    }
    
    @Override
    public void execute() {
        // TODO: 实现切换逻辑
    }
    
    @Override
    public String getName() {
        return "edit";
    }
}
