package com.team20.editor.domain.editor;

/**
 * 抽象编辑器：提供 Editor 接口的常见实现（name / modified 管理）
 *
 * 具体编辑器应继承此类并实现 content() 与 loadContent(...)。
 */
public abstract class AbstractEditor implements Editor {

    private final String name;
    private boolean modified = false;

    protected AbstractEditor(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * 返回当前编辑器的全部文本表示。Concrete class 实现 content() 来返回真实内容。
     */
    @Override
    public String getContent() {
        return content();
    }

    /**
     * 子类应实现该方法，将内部行结构拼接为单个字符串返回（通常用 '\n' 分隔）。
     */
    protected abstract String content();

    @Override
    public boolean isModified() {
        return modified;
    }

    @Override
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    /**
     * 由具体编辑器实现：把整个文件内容加载到内部数据结构（例如按行拆分到 List<String>）。
     *
     * @param content 完整文件文本（可能包含多行）
     */
    @Override
    public abstract void loadContent(String content);
}