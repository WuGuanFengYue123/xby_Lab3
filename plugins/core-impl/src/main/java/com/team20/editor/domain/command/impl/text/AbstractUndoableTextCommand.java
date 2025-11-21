package com.team20.editor.domain.command.impl.text;

import com.team20.editor.domain.command.UndoableCommand;
import com.team20.editor.domain.editor.text.TextEditor;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.extension.registry.DefaultCommandRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Base class for text-editing UndoableCommands.
 *
 * Responsibilities:
 * - take/restore snapshot for undo/redo
 * - call abstract apply(TextEditor) to perform actual change
 * - publish command events after apply
 * - mark editor as modified (try explicit API first, fallback to reflection)
 *
 * Concrete subclasses must implement:
 * - void apply(TextEditor editor) throws Exception
 * - String getCommandName()
 * - String formatArgs()
 */
public abstract class AbstractUndoableTextCommand implements UndoableCommand {

    protected TextEditor.EditorSnapshot beforeSnapshot;

    @Override
    public final void execute(Workspace workspace) {
        TextEditor editor = getTextEditor(workspace);

        // take snapshot
        beforeSnapshot = editor.createSnapshot();

        try {
            apply(editor);
        } catch (Throwable t) {
            throw new RuntimeException("执行命令失败: " + t.getMessage(), t);
        }

        // mark modified
        markEditorModified(editor);

        // publish event
        try {
            workspace.publishCommandEvent(getCommandName(), formatArgs());
        } catch (Throwable ignored) {
        }
    }

    @Override
    public final void undo(Workspace workspace) {
        if (beforeSnapshot == null) {
            throw new IllegalStateException("无法撤销：命令尚未执行");
        }
        TextEditor editor = getTextEditor(workspace);
        editor.restoreSnapshot(beforeSnapshot);

        try {
            workspace.publishCommandEvent("undo", getCommandName());
        } catch (Throwable ignored) {
        }
    }

    @Override
    public final void redo(Workspace workspace) {
        TextEditor.EditorSnapshot temp = beforeSnapshot;
        execute(workspace);
        beforeSnapshot = temp;
        try {
            workspace.publishCommandEvent("redo", getCommandName());
        } catch (Throwable ignored) {
        }
    }

    protected abstract void apply(TextEditor editor) throws Exception;

    protected abstract String getCommandName();

    /**
     * Format arguments for event publishing (default empty)
     */
    protected String formatArgs() {
        return "";
    }

    protected TextEditor getTextEditor(Workspace workspace) {
        if (workspace.getActiveEditor() == null) {
            throw new IllegalStateException("没有打开的文件");
        }
        if (!(workspace.getActiveEditor() instanceof TextEditor)) {
            throw new IllegalStateException("当前文件不是文本文件");
        }
        return (TextEditor) workspace.getActiveEditor();
    }

    /**
     * Try to mark the editor as modified:
     * 1) try setModified(boolean)
     * 2) try markModified()
     * 3) try to set a boolean field named "modified"
     * All failures are swallowed to avoid raising at runtime.
     */
    protected void markEditorModified(TextEditor editor) {
        try {
            // try setModified(boolean)
            try {
                Method setModified = editor.getClass().getMethod("setModified", boolean.class);
                setModified.invoke(editor, true);
                return;
            } catch (NoSuchMethodException ignored) {
            }

            // try markModified()
            try {
                Method mark = editor.getClass().getMethod("markModified");
                mark.invoke(editor);
                return;
            } catch (NoSuchMethodException ignored) {
            }

            // try boolean field "modified"
            try {
                Field f = editor.getClass().getDeclaredField("modified");
                f.setAccessible(true);
                f.setBoolean(editor, true);
                return;
            } catch (NoSuchFieldException ignored) {
            }

            // last resort: if editor has a setState/flags API, skip (avoid assumptions)
        } catch (Throwable ignored) {
            // swallow any reflection exceptions
        }
    }
}