package com.team20.editor.core.command.impl.text;

import com.team20.editor.core.command.UndoableCommand;
import com.team20.editor.core.editor.text.TextEditor;

/**
 * 插入文本命令
 */
public class InsertCommand implements UndoableCommand {
    
    private TextEditor editor;
    private int line;
    private int col;
    private String text;
    
    public InsertCommand(TextEditor editor, int line, int col, String text) {
        this.editor = editor;
        this.line = line;
        this.col = col;
        this.text = text;
    }
    
    @Override
    public void execute() {
        // TODO: 实现插入逻辑
    }
    
    @Override
    public void undo() {
        // TODO: 实现撤销逻辑
    }
    
    @Override
    public boolean isUndoable() {
        return true;
    }
    
    @Override
    public String getName() {
        return "insert";
    }
}
