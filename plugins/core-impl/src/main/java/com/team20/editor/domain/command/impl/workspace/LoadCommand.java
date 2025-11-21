package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.editor.Editor;
import com.team20.editor.domain.editor.text.TextEditor;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.extension.registry.EditorFactory;
import com.team20.editor.infrastructure.persistence.PersistenceManager;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * load <filepath>：从磁盘加载文件到工作区（通过 EditorFactory 创建 Editor）
 *
 * 构造时注入 EditorFactory 与 PersistenceManager。
 */
public class LoadCommand implements Command {

    private final PersistenceManager pm;
    private final EditorFactory editorFactory;
    private String filepath; // 设置为相对或绝对路径

    public LoadCommand(EditorFactory factory, PersistenceManager pm) {
        this.editorFactory = factory;
        this.pm = pm;
    }

    public LoadCommand(EditorFactory factory, PersistenceManager pm, String filepath) {
        this.editorFactory = factory;
        this.pm = pm;
        this.filepath = filepath;
    }

    public void setFilepath(String filepath) { this.filepath = filepath; }

    @Override
    public void execute(Workspace workspace) {
        if (filepath == null || filepath.isBlank()) {
            System.out.println("Usage: load <filepath>");
            return;
        }
        try {
            Path p = Paths.get(filepath);
            String raw = pm.readFile(p);
            // 使用 factory 创建 editor（provider 负责决定类型）
            Editor editor = editorFactory.createEditor(p.toString());
            editor.loadContent(raw);
            workspace.addEditor(editor);
            workspace.setActiveEditor(editor);
            System.out.println("已加载文件: " + p.toString());
            workspace.publishWorkspaceEvent("fileLoaded", p.toString());
        } catch (Exception e) {
            System.out.println("加载失败: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "load " + (filepath == null ? "" : filepath);
    }
}