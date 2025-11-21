package com.team20.editor.domain.command.impl.text;

import com.team20.editor.domain.command.UndoableCommand;
import com.team20.editor.domain.editor.text.TextEditor;
import com.team20.editor.domain.workspace.Workspace;

/**
 * 追加文本命令：将一段文本作为新的一行追加到当前编辑器末尾。
 *
 * 规则：
 * - 若文件为空，直接写入文本（成为第 1 行）。
 * - 若文件最后以换行结束，直接在末尾追加文本（成为新的一行）。
 * - 若文件最后没有换行，先插入一个换行符，再追加文本（确保是新行）。
 * - 不在追加文本后再添加额外换行（避免产生多余空行）。
 */
public class AppendCommand implements UndoableCommand {

    private final String text;
    private TextEditor.EditorSnapshot beforeSnapshot;

    public AppendCommand(String text) {
        if (text == null) {
            throw new IllegalArgumentException("追加的文本不能为 null");
        }
        this.text = text;
    }

    @Override
    public void execute(Workspace workspace) {
        TextEditor editor = getTextEditor(workspace);

        // 保存快照用于 undo
        beforeSnapshot = editor.createSnapshot();

        String current = editor.getContent();
        String toAppend = text;

        if (current == null || current.isEmpty()) {
            // 空文件，直接追加文本（成为第 1 行）
            editor.append(toAppend);
        } else {
            // 如果末尾已经有换行，则直接追加；否则先补一个换行再追加
            if (current.endsWith("\n")) {
                editor.append(toAppend);
            } else {
                editor.append(System.lineSeparator() + toAppend);
            }
        }

        workspace.publishCommandEvent("append", String.format("\"%s\"", text));
    }

    @Override
    public void undo(Workspace workspace) {
        if (beforeSnapshot == null) {
            throw new IllegalStateException("无法撤销：命令尚未执行");
        }

        TextEditor editor = getTextEditor(workspace);
        editor.restoreSnapshot(beforeSnapshot);

        workspace.publishCommandEvent("undo", "append");
    }

    @Override
    public void redo(Workspace workspace) {
        // 重新执行，但保留原始快照
        TextEditor.EditorSnapshot temp = beforeSnapshot;
        execute(workspace);
        beforeSnapshot = temp; // 保持原始快照

        workspace.publishCommandEvent("redo", "append");
    }

    private TextEditor getTextEditor(Workspace workspace) {
        if (workspace.getActiveEditor() == null) {
            throw new IllegalStateException("没有打开的文件");
        }
        if (!(workspace.getActiveEditor() instanceof TextEditor)) {
            throw new IllegalStateException("当前文件不是文本文件");
        }
        return (TextEditor) workspace.getActiveEditor();
    }

    @Override
    public String toString() {
        return String.format("append \"%s\"", text);
    }
}