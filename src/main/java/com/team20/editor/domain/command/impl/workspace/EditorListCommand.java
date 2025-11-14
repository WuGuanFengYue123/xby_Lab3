package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.workspace.Workspace;

/**
 * 显示编辑器列表命令
 */
public class EditorListCommand implements Command {

    @Override
    public void execute(Workspace workspace) {
        System.out.println("打开的文件列表:");
        if (!workspace.hasEditors()) {
            System.out.println("  (无)");
            return;
        }

        workspace.getEditors().forEach(editor -> {
            String marker = (editor == workspace.getActiveEditor()) ? "> " : "  ";
            String modifiedMarker = editor.isModified() ? "*" : "";
            System.out.println(marker + editor.getName() + modifiedMarker);
        });
    }
}