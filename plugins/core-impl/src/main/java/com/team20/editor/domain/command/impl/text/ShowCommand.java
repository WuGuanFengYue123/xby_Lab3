package com.team20.editor.domain.command.impl.text;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.editor.text.TextEditor;
import com.team20.editor.domain.workspace.Workspace;

/**
 * 显示文本命令（按行显示，不修改文件，不入 undo 栈）。
 *
 * 用法：
 * - show 显示全文
 * - show start:end 显示指定行区间（包含 end）
 * - show start 显示指定单行（等同于 show start:start）
 *
 * 规则：
 * - 行号从 1 开始
 * - 若 end 超出总行数，自动截断到最后一行
 * - 若 start 超出总行数，抛出 IllegalArgumentException
 */
public class ShowCommand implements Command {

    private final Integer startLine;
    private final Integer endLine;

    public ShowCommand() {
        this(null, null);
    }

    public ShowCommand(Integer startLine, Integer endLine) {
        this.startLine = startLine;
        this.endLine = endLine;
    }

    @Override
    public void execute(Workspace workspace) {
        if (workspace.getActiveEditor() == null) {
            System.out.println("没有打开的文件");
            return;
        }

        if (!(workspace.getActiveEditor() instanceof TextEditor)) {
            System.out.println("当前文件不是文本文件");
            return;
        }

        TextEditor editor = (TextEditor) workspace.getActiveEditor();
        String content = editor.getContent();
        if (content == null)
            content = "";

        // 按行分割（保留末尾空行）
        String[] lines = content.split("\\r?\\n", -1);
        int total = lines.length;
        // special-case: empty content => total == 1 && lines[0].isEmpty()
        boolean isEmptyFile = (total == 1 && lines[0].isEmpty());

        try {
            int start;
            int end;
            if (startLine == null && endLine == null) {
                // 全文
                if (isEmptyFile) {
                    // nothing to print
                } else {
                    start = 1;
                    end = total;
                    printRange(lines, start, end);
                }
            } else if (startLine != null && endLine == null) {
                // only start -> print that line
                start = startLine;
                end = startLine;
                validateAndPrint(lines, start, end, isEmptyFile);
            } else {
                // both provided (可能 end < start 会被视为错误)
                start = startLine == null ? 1 : startLine;
                end = endLine == null ? total : endLine;
                validateAndPrint(lines, start, end, isEmptyFile);
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("错误: " + ex.getMessage());
        }

        // 发布事件（显示不记入 undo）
        String args = (startLine != null || endLine != null)
                ? String.format("%s:%s", startLine == null ? "" : startLine.toString(),
                        endLine == null ? "" : endLine.toString())
                : "";
        workspace.publishCommandEvent("show", args);
    }

    private void validateAndPrint(String[] lines, int start, int end, boolean isEmptyFile) {
        if (start < 1)
            throw new IllegalArgumentException("起始行号必须 >= 1");
        if (end < start)
            throw new IllegalArgumentException("结束行号必须 >= 起始行号");
        if (isEmptyFile) {
            throw new IllegalArgumentException("文件为空，没有可显示的行");
        }
        int total = lines.length;
        if (start > total)
            throw new IllegalArgumentException("起始行号超出文件总行数: " + total);
        // 对 end 做截断（友好行为）
        int realEnd = Math.min(end, total);
        printRange(lines, start, realEnd);
    }

    private void printRange(String[] lines, int start, int end) {
        for (int i = start; i <= end; i++) {
            String line = lines[i - 1];
            System.out.printf("%d: %s%n", i, line);
        }
    }

    @Override
    public String toString() {
        if (startLine != null && endLine != null) {
            return String.format("show %d:%d", startLine, endLine);
        } else if (startLine != null) {
            return String.format("show %d", startLine);
        }
        return "show";
    }
}