package com.team20.editor.domain.workspace;

/**
 * 工作区备忘录
 * 
 * 设计模式：Memento Pattern
 * 职责：保存和恢复工作区状态
 */
public class WorkspaceMemento {

    private final WorkspaceState state;

    /**
     * 构造函数
     * 
     * @param state 工作区状态
     */
    public WorkspaceMemento(WorkspaceState state) {
        this.state = state;
    }

    /**
     * 获取保存的状态
     * 
     * @return 工作区状态
     */
    public WorkspaceState getState() {
        return state;
    }
}
