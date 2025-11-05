package com.team20.editor.core.command.impl.workspace;

import com.team20.editor.core.command.Command;
import com.team20.editor.core.workspace.Workspace;

/**
 * 保存文件命令
 */
public class SaveCommand implements Command {
    
    private Workspace workspace;
    private String target; // file path or "all"
    
    public SaveCommand(Workspace workspace, String target) {
        this.workspace = workspace;
        this.target = target;
    }
    
    @Override
    public void execute() {
        // TODO: 实现保存逻辑
    }
    
    @Override
    public String getName() {
        return "save";
    }
}
