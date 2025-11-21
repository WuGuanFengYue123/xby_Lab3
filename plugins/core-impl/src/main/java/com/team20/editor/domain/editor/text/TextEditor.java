package com.team20.editor.domain.editor.text;

import com.team20.editor.domain.editor.AbstractEditor;

import java.util.ArrayList;
import java.util.List;

/**
 * 文本编辑器实现（基于行数组）
 *
 * 仅包含 TextEditor 类；不要在此文件中放置 TextEditorProvider 或其它 public 类。
 */
public class TextEditor extends AbstractEditor {

    private final List<String> lines = new ArrayList<>();

    public TextEditor(String name) {
        super(name);
        // 初始化为一个空行，便于在空文件时插入
        lines.clear();
        lines.add("");
        setModified(false);
    }

    @Override
    protected String content() {
        return String.join("\n", lines);
    }

    public void append(String text) {
        if (text == null) throw new IllegalArgumentException("text is null");
        String[] parts = text.split("\\r?\\n", -1);

        if (lines.isEmpty()) {
            // no lines -> add all parts
            for (String p : parts) lines.add(p);
        } else {
            int last = lines.size() - 1;
            // If single-line append: prefer replacing the empty last line instead of adding a new one
            if (parts.length == 1) {
                if (lines.get(last).isEmpty()) {
                    // empty last line -> replace it
                    lines.set(last, parts[0]);
                } else {
                    // non-empty last line -> add as a new line
                    lines.add(parts[0]);
                }
            } else {
                // multi-line append:
                // if last line is empty, set it to first part; otherwise append first part to last line
                if (lines.get(last).isEmpty()) {
                    lines.set(last, parts[0]);
                } else {
                    lines.set(last, lines.get(last) + parts[0]);
                }
                for (int i = 1; i < parts.length; i++) {
                    lines.add(parts[i]);
                }
            }
        }
        setModified(true);
    }

    public void insert(int line, int col, String text) {
        validatePositionForInsert(line, col);

        if (text == null) throw new IllegalArgumentException("text is null");

        String existing = lines.get(line - 1);
        int colIndex = col - 1;

        String[] parts = text.split("\\r?\\n", -1);

        if (parts.length == 1) {
            String newLine = existing.substring(0, colIndex) + parts[0] + existing.substring(colIndex);
            lines.set(line - 1, newLine);
        } else {
            String before = existing.substring(0, colIndex);
            String after = existing.substring(colIndex);
            lines.set(line - 1, before + parts[0]);
            for (int i = 1; i < parts.length - 1; i++) {
                lines.add(line - 1 + i, parts[i]);
            }
            lines.add(line - 1 + parts.length - 1, parts[parts.length - 1] + after);
        }
        setModified(true);
    }

    public String delete(int line, int col, int length) {
        validatePosition(line, col, "删除");
        if (length < 0) throw new IllegalArgumentException("length < 0");
        if (length == 0) return "";

        String cur = lines.get(line - 1);
        int colIndex = col - 1;
        if (colIndex + length > cur.length()) {
            throw new IllegalArgumentException("删除长度超出行尾");
        }
        String deleted = cur.substring(colIndex, colIndex + length);
        String updated = cur.substring(0, colIndex) + cur.substring(colIndex + length);
        lines.set(line - 1, updated);
        setModified(true);
        return deleted;
    }

    public String replace(int line, int col, int length, String text) {
        String deleted = delete(line, col, length);
        insert(line, col, text);
        return deleted;
    }

    public String show(Integer startLine, Integer endLine) {
        if (lines.isEmpty()) return "(空文件)";

        int start = (startLine == null) ? 1 : startLine;
        int end = (endLine == null) ? lines.size() : endLine;

        if (start < 1 || start > lines.size()) throw new IllegalArgumentException("起始行号超出范围");
        if (end < start) throw new IllegalArgumentException("结束行号不能小于起始行号");
        if (end > lines.size()) end = lines.size();

        StringBuilder sb = new StringBuilder();
        for (int i = start; i <= end; i++) {
            sb.append(String.format("%d: %s%n", i, lines.get(i - 1)));
        }
        return sb.toString();
    }

    public int getLineCount() {
        return (lines.isEmpty() || (lines.size()==1 && lines.get(0).isEmpty())) ? 0 : lines.size();
    }

    public String getLine(int line) {
        if (line < 1 || line > lines.size()) throw new IllegalArgumentException("行号超出范围");
        return lines.get(line - 1);
    }

    @Override
    public void loadContent(String content) {
        lines.clear();
        if (content == null || content.isEmpty()) {
            lines.add("");
        } else {
            String[] parts = content.split("\\r?\\n", -1);
            for (String p : parts) lines.add(p);
        }
        setModified(false);
    }

    private void validatePosition(int line, int col, String op) {
        if (getLineCount() == 0) {
            if (line != 1 || col != 1) {
                throw new IllegalArgumentException("空文件只能在 1:1 位置" + op);
            }
            lines.add("");
            return;
        }
        if (line < 1 || line > lines.size()) {
            throw new IllegalArgumentException("行号超出范围");
        }
        String cur = lines.get(line - 1);
        if (col < 1 || col > cur.length() + 1) {
            throw new IllegalArgumentException("列号超出范围");
        }
    }

    private void validatePositionForInsert(int line, int col) {
        validatePosition(line, col, "插入");
    }

    public EditorSnapshot createSnapshot() {
        return new EditorSnapshot(new ArrayList<>(lines), isModified());
    }

    public void restoreSnapshot(EditorSnapshot snapshot) {
        lines.clear();
        lines.addAll(snapshot.lines);
        setModified(snapshot.modified);
    }

    public static class EditorSnapshot {
        private final List<String> lines;
        private final boolean modified;

        public EditorSnapshot(List<String> lines, boolean modified) {
            this.lines = lines;
            this.modified = modified;
        }

        public List<String> getLines() {
            return lines;
        }

        public boolean isModified() {
            return modified;
        }
    }
}