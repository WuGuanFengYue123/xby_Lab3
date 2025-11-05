package com.team20.editor.core.command.impl.text;

import com.team20.editor.core.command.Command;
import com.team20.editor.core.editor.text.TextEditor;

/**
 * 显示文本内容命令
 */
public class ShowCommand implements Command {
    
    private TextEditor editor;
    private int startLine;
    private int endLine;
    
    public ShowCommand(TextEditor editor, int startLine, int endLine) {
        this.editor = editor;
        this.startLine = startLine;
        this.endLine = endLine;
    }
    
    @Override
    public void execute() {
        // TODO: 实现显示逻辑
    }
    
    @Override
    public String getName() {
        return "show";
    }
}
