package com.team20.editor.core.command.impl.workspace;

import com.team20.editor.core.command.Command;
import com.team20.editor.core.workspace.Workspace;

/**
 * 显示编辑器列表命令
 */
public class EditorListCommand implements Command {
    
    private Workspace workspace;
    
    public EditorListCommand(Workspace workspace) {
        this.workspace = workspace;
    }
    
    @Override
    public void execute() {
        // TODO: 实现列表显示逻辑
    }
    
    @Override
    public String getName() {
        return "editor-list";
    }
}
