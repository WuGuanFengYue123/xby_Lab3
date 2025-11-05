package com.team20.editor.core.command.impl.text;

import com.team20.editor.core.command.UndoableCommand;
import com.team20.editor.core.editor.text.TextEditor;

/**
 * 替换文本命令
 */
public class ReplaceCommand implements UndoableCommand {
    
    private TextEditor editor;
    private int line;
    private int col;
    private int length;
    private String newText;
    private String oldText; // 用于 undo
    
    public ReplaceCommand(TextEditor editor, int line, int col, int length, String newText) {
        this.editor = editor;
        this.line = line;
        this.col = col;
        this.length = length;
        this.newText = newText;
    }
    
    @Override
    public void execute() {
        // TODO: 保存旧文本
        // TODO: 实现替换逻辑
    }
    
    @Override
    public void undo() {
        // TODO: 恢复旧文本
    }
    
    @Override
    public boolean isUndoable() {
        return true;
    }
    
    @Override
    public String getName() {
        return "replace";
    }
}
