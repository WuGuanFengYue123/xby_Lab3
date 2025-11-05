package com.team20.editor.core.command.impl.text;

import com.team20.editor.core.command.UndoableCommand;
import com.team20.editor.core.editor.text.TextEditor;

/**
 * 追加文本命令
 */
public class AppendCommand implements UndoableCommand {
    
    private TextEditor editor;
    private String text;
    
    public AppendCommand(TextEditor editor, String text) {
        this.editor = editor;
        this.text = text;
    }
    
    @Override
    public void execute() {
        // TODO: 实现追加逻辑
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
        return "append";
    }
}
