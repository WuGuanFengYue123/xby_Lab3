package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.editor.Editor;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.infrastructure.persistence.PersistenceManager;
import com.team20.editor.extension.registry.EditorFactory;

/**
 * LoadCommand: loads a file from persistence and adds editor to workspace.
 *
 * Note: removed legacy auto-enable-by-#log behaviour. To enable logging
 * explicitly call 'log-on'.
 */
public class LoadCommand implements Command {

    private final EditorFactory editorFactory;
    private final PersistenceManager persistenceManager;
    private final String filepath;

    public LoadCommand(EditorFactory editorFactory, PersistenceManager persistenceManager, String filepath) {
        this.editorFactory = editorFactory;
        this.persistenceManager = persistenceManager;
        this.filepath = filepath;
    }

    @Override
    public void execute(Workspace workspace) {
        try {
            String content = persistenceManager.load(filepath);
            Editor editor = editorFactory.createEditor(filepath);
            editor.loadContent(content);
            workspace.addEditor(editor);
            workspace.setActiveEditor(editor);
            System.out.println("已加载文件: " + filepath);
            // NOTE: no automatic enabling of logging based on file content.
        } catch (Exception ex) {
            System.out.println("加载失败: " + ex.getMessage());
        }
    }
}