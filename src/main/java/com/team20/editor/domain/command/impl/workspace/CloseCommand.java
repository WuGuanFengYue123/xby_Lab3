package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.editor.Editor;
import com.team20.editor.domain.workspace.Workspace;

/**
 * close：关闭当前活动编辑器（或指定文件）
 */
public class CloseCommand implements Command {

    private String filepath;

    public CloseCommand() {
    }

    public CloseCommand(String filepath) {
        this.filepath = filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public void execute(Workspace workspace) {
        if (filepath == null || filepath.isBlank()) {
            Editor active = workspace.getActiveEditor();
            if (active == null) {
                System.out.println("没有打开的文件");
                return;
            }
            workspace.removeEditor(active);
            System.out.println("已关闭活动文件");
        } else {
            Editor e = workspace.getEditor(filepath);
            if (e == null) {
                System.out.println("未找到文件: " + filepath);
                return;
            }
            workspace.removeEditor(e);
            System.out.println("已关闭: " + filepath);
        }
    }

    @Override
    public String toString() {
        return "close " + (filepath == null ? "" : filepath);
    }
}