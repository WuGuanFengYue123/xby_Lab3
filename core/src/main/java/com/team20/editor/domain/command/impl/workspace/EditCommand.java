package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.editor.Editor;
import com.team20.editor.domain.editor.text.TextEditor;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.extension.registry.EditorFactory;
import com.team20.editor.infrastructure.persistence.PersistenceManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * edit <filepath>：切换/打开文件。如果文件已存在于 workspace 中则切换，否则尝试从磁盘加载或创建新文件。
 *
 * 现在通过 EditorFactory 创建编辑器实例（强制使用 SPI 提供的 provider）。
 */
public class EditCommand implements Command {

    private final PersistenceManager pm;
    private final EditorFactory factory;
    private String filepath;

    public EditCommand(EditorFactory factory, PersistenceManager pm) {
        this.factory = factory;
        this.pm = pm;
    }

    public EditCommand(EditorFactory factory, PersistenceManager pm, String filepath) {
        this.factory = factory;
        this.pm = pm;
        this.filepath = filepath;
    }

    public void setFilepath(String filepath) { this.filepath = filepath; }

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
                Editor editor = factory.createEditor(p.toString());
                editor.loadContent(raw);
                workspace.addEditor(editor);
                workspace.setActiveEditor(editor);
                System.out.println("已打开并加载: " + p.toString());
            } else {
                // 新建文件（空内容） - 使用 factory 创建编辑器实例
                Editor editor = factory.createEditor(p.toString());
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