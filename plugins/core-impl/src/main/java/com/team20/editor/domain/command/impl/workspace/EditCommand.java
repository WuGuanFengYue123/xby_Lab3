package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.editor.Editor;
import com.team20.editor.domain.workspace.Workspace;

/**
 * EditCommand: 切换活动文件。
 *
 * 行为修正：
 * - 只允许切换到已经在 workspace 中打开的编辑器。
 * - 若文件未打开，打印 "文件未打开: <file>"（不再尝试从磁盘加载或创建编辑器）。
 *
 * 这符合规范：edit <file> 仅切换活动文件，不负责加载/创建。
 */
public class EditCommand implements Command {

    private final String filepath;

    public EditCommand(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public void execute(Workspace workspace) {
        if (filepath == null || filepath.isBlank()) {
            System.out.println("用法: edit <file>");
            return;
        }

        Editor existing = workspace.getEditor(filepath);
        if (existing == null) {
            System.out.println("文件未打开: " + filepath);
            return;
        }

        workspace.setActiveEditor(existing);
        System.out.println("切换到已打开文件: " + filepath);
    }

    @Override
    public String toString() {
        return "edit " + (filepath == null ? "" : filepath);
    }
}