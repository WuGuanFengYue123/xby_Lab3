# Workspace Module

## 职责
- 管理当前会话的全局状态
- 协调多文件编辑
- 工作区状态持久化与恢复

## 核心类
- `Workspace.java`: 工作区管理器
- `WorkspaceState.java`: 工作区状态数据类
- `WorkspaceMemento.java`: 备忘录模式实现（持久化）

## 设计模式
- Memento Pattern（备忘录模式）
- Observer Pattern（发布工作区事件）

## 职责清单
- [x] 管理已打开文件列表
- [x] 维护当前活动文件
- [x] 文件修改状态跟踪
- [x] 工作区状态序列化
- [x] 启动时恢复工作区
