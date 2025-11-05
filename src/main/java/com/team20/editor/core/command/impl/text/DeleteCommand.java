package com.team20.editor.core.command.impl.text;

import com.team20.editor.core.command.UndoableCommand;
import com.team20.editor.core.editor.text.TextEditor;

/**
 * 删除文本命令
 */
public class DeleteCommand implements UndoableCommand {
    
    private TextEditor editor;
    private int line;
    private int col;
    private int length;
    private String deletedText; // 用于 undo
    
    public DeleteCommand(TextEditor editor, int line, int col, int length) {
        this.editor = editor;
        this.line = line;
        this.col = col;
        this.length = length;
    }
    
    @Override
    public void execute() {
        // TODO: 保存被删除的文本
        // TODO: 实现删除逻辑
    }
    
    @Override
    public void undo() {
        // TODO: 恢复被删除的文本
    }
    
    @Override
    public boolean isUndoable() {
        return true;
    }
    
    @Override
    public String getName() {
        return "delete";
    }
}
