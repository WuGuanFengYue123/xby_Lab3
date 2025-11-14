package com.team20.editor.domain.editor.text;

import com.team20.editor.domain.editor.AbstractEditor;
import com.team20.editor.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文本编辑器实现
 * 使用行数组存储文本，每个元素是一行
 */
public class TextEditor extends AbstractEditor {

    private final List<String> lines = new ArrayList<>();

    public TextEditor(String name) {
        super(name);
    }

    @Override
    protected String content() {
        return String.join("\n", lines);
    }

    /**
     * 追加文本到文件末尾
     * 
     * @param text 要追加的文本
     */
    public void append(String text) {
        if (text == null) {
            throw new IllegalArgumentException("追加的文本不能为 null");
        }

        // 处理文本中的换行符，拆分为多行
        String[] newLines = text.split("\n", -1);

        if (lines.isEmpty()) {
            // 空文件，直接添加所有新行
            for (String line : newLines) {
                lines.add(line);
            }
        } else {
            // 非空文件，第一行追加到最后一行，其余作为新行
            int lastIndex = lines.size() - 1;
            lines.set(lastIndex, lines.get(lastIndex) + newLines[0]);

            for (int i = 1; i < newLines.length; i++) {
                lines.add(newLines[i]);
            }
        }

        setModified(true);
    }

    /**
     * 在指定位置插入文本
     * 
     * @param line 行号（从1开始）
     * @param col  列号（从1开始）
     * @param text 要插入的文本
     */
    public void insert(int line, int col, String text) {
        validatePosition(line, col, "插入");

        if (text == null) {
            throw new IllegalArgumentException("插入的文本不能为 null");
        }

        // 处理换行符
        String[] textLines = text.split("\n", -1);

        int lineIndex = line - 1;
        String currentLine = lines.get(lineIndex);
        int colIndex = col - 1;

        if (textLines.length == 1) {
            // 单行插入
            String newLine = currentLine.substring(0, colIndex)
                    + text
                    + currentLine.substring(colIndex);
            lines.set(lineIndex, newLine);
        } else {
            // 多行插入
            String beforeInsert = currentLine.substring(0, colIndex);
            String afterInsert = currentLine.substring(colIndex);

            // 第一行
            lines.set(lineIndex, beforeInsert + textLines[0]);

            // 中间行
            for (int i = 1; i < textLines.length - 1; i++) {
                lines.add(lineIndex + i, textLines[i]);
            }

            // 最后一行
            lines.add(lineIndex + textLines.length - 1,
                    textLines[textLines.length - 1] + afterInsert);
        }

        setModified(true);
    }

    /**
     * 删除指定位置的字符
     * 
     * @param line   行号（从1开始）
     * @param col    列号（从1开始）
     * @param length 删除长度
     * @return 被删除的文本（用于 undo）
     */
    public String delete(int line, int col, int length) {
        validatePosition(line, col, "删除");

        if (length < 0) {
            throw new IllegalArgumentException("删除长度不能为负数");
        }

        if (length == 0) {
            return "";
        }

        int lineIndex = line - 1;
        String currentLine = lines.get(lineIndex);
        int colIndex = col - 1;

        // 检查删除范围是否超出行尾
        if (colIndex + length > currentLine.length()) {
            throw new IllegalArgumentException(
                    String.format("删除长度超出行尾: 第 %d 行只有 %d 个字符，从第 %d 列开始无法删除 %d 个字符",
                            line, currentLine.length(), col, length));
        }

        // 保存被删除的文本
        String deletedText = currentLine.substring(colIndex, colIndex + length);

        // 执行删除
        String newLine = currentLine.substring(0, colIndex)
                + currentLine.substring(colIndex + length);
        lines.set(lineIndex, newLine);

        setModified(true);
        return deletedText;
    }

    /**
     * 替换指定位置的字符
     * 
     * @param line   行号（从1开始）
     * @param col    列号（从1开始）
     * @param length 替换长度
     * @param text   新文本
     * @return 被替换的文本（用于 undo）
     */
    public String replace(int line, int col, int length, String text) {
        // 先删除
        String deleted = delete(line, col, length);
        // 再插入
        insert(line, col, text);
        return deleted;
    }

    /**
     * 显示文本内容
     * 
     * @param startLine 起始行号（从1开始，null表示第一行）
     * @param endLine   结束行号（包含，null表示最后一行）
     * @return 格式化的文本内容
     */
    public String show(Integer startLine, Integer endLine) {
        if (lines.isEmpty()) {
            return "（空文件）";
        }

        int start = (startLine != null) ? startLine : 1;
        int end = (endLine != null) ? endLine : lines.size();

        // 验证范围
        if (start < 1 || start > lines.size()) {
            throw new IllegalArgumentException("起始行号超出范围: " + start);
        }
        if (end < 1 || end > lines.size()) {
            throw new IllegalArgumentException("结束行号超出范围: " + end);
        }
        if (start > end) {
            throw new IllegalArgumentException("起始行号不能大于结束行号");
        }

        StringBuilder result = new StringBuilder();
        for (int i = start - 1; i < end; i++) {
            result.append(String.format("%d: %s%n", i + 1, lines.get(i)));
        }
        return result.toString();
    }

    /**
     * 获取总行数
     */
    public int getLineCount() {
        return lines.isEmpty() ? 0 : lines.size();
    }

    /**
     * 获取指定行的内容
     */
    public String getLine(int line) {
        if (line < 1 || line > lines.size()) {
            throw new IllegalArgumentException("行号超出范围: " + line);
        }
        return lines.get(line - 1);
    }

    /**
     * 加载文件内容
     */
    public void loadContent(String content) {
        lines.clear();
        if (content == null || content.isEmpty()) {
            lines.add("");
            return;
        }

        String[] contentLines = content.split("\n", -1);
        for (String line : contentLines) {
            lines.add(line);
        }
        setModified(false);
    }

    /**
     * 验证位置是否合法
     */
    private void validatePosition(int line, int col, String operation) {
        if (lines.isEmpty()) {
            if (line != 1 || col != 1) {
                throw new IllegalArgumentException(
                        String.format("空文件只能在 1:1 位置%s", operation));
            }
            // 空文件，添加第一行
            lines.add("");
            return;
        }

        if (line < 1 || line > lines.size()) {
            throw new IllegalArgumentException(
                    String.format("行号超出范围: %d（文件共 %d 行）", line, lines.size()));
        }

        String currentLine = lines.get(line - 1);
        // 列号可以等于长度+1（表示在行尾后插入）
        if (col < 1 || col > currentLine.length() + 1) {
            throw new IllegalArgumentException(
                    String.format("列号超出范围: 第 %d 行共 %d 个字符，列号应在 1-%d 之间",
                            line, currentLine.length(), currentLine.length() + 1));
        }
    }

    /**
     * 获取快照（用于undo）
     */
    public EditorSnapshot createSnapshot() {
        return new EditorSnapshot(new ArrayList<>(lines), isModified());
    }

    /**
     * 恢复快照（用于undo）
     */
    public void restoreSnapshot(EditorSnapshot snapshot) {
        lines.clear();
        lines.addAll(snapshot.lines);
        setModified(snapshot.modified);
    }

    /**
     * 编辑器快照（用于 undo/redo）
     */
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