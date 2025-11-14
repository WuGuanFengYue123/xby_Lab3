package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.editor.Editor;
import com.team20.editor.domain.editor.text.TextEditor;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.infrastructure.persistence.PersistenceManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * edit <filepath>：切换/打开文件。如果文件已存在于 workspace 中则切换，否则尝试从磁盘加载或创建新文件。
 */
public class EditCommand implements Command {

    private final PersistenceManager pm;
    private String filepath;

    public EditCommand(PersistenceManager pm) {
        this.pm = pm;
    }

    public EditCommand(PersistenceManager pm, String filepath) {
        this.pm = pm;
        this.filepath = filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public void execute(Workspace workspace) {
        if (filepath == null || filepath.isBlank()) {
            System.out.println("Usage: edit <filepath>");
            return;
        }
        Path p = Paths.get(filepath);
        Editor existing = workspace.getEditor(p.toString());
        if (existing != null) {
            workspace.setActiveEditor(existing);
            System.out.println("切换到已有编辑器: " + p.toString());
            return;
        }
        try {
            if (Files.exists(p)) {
                String raw = pm.readFile(p);
                TextEditor editor = new TextEditor(p.toString());
                editor.loadContent(raw);
                workspace.addEditor(editor);
                workspace.setActiveEditor(editor);
                System.out.println("已打开并加载: " + p.toString());
            } else {
                // 新建文件（空内容）
                TextEditor editor = new TextEditor(p.toString());
                editor.loadContent("");
                workspace.addEditor(editor);
                workspace.setActiveEditor(editor);
                System.out.println("已新建文件: " + p.toString());
            }
        } catch (Exception e) {
            System.out.println("打开失败: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "edit " + (filepath == null ? "" : filepath);
    }
}