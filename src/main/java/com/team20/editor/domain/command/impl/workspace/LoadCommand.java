package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.editor.text.TextEditor;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.infrastructure.persistence.PersistenceManager;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * load <filepath>：从磁盘加载文件到工作区（如果已打开则切换）
 *
 * 注意：CLI 解析应在创建该命令时传入路径参数；
 * 如果在 CommandRegistry 中以无参工厂注册，CLI 可以直接创建 new LoadCommand(pm) 并设置参数后执行。
 */
public class LoadCommand implements Command {

    private final PersistenceManager pm;
    private String filepath; // 设置为相对或绝对路径

    public LoadCommand(PersistenceManager pm) {
        this.pm = pm;
    }

    public LoadCommand(PersistenceManager pm, String filepath) {
        this.pm = pm;
        this.filepath = filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public void execute(Workspace workspace) {
        if (filepath == null || filepath.isBlank()) {
            System.out.println("Usage: load <filepath>");
            return;
        }
        try {
            Path p = Paths.get(filepath);
            // load returns a TextEditor instance (we implement here)
            String raw = pm.readFile(p);
            TextEditor editor = new TextEditor(p.toString());
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