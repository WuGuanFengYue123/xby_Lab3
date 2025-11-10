# Adapter + Composite 树结构设计

## 目的
将现有对象（Workspace、Editor、Command 等）通过适配器转化为统一的 Node，使其：
- 可统一遍历展示
- 可扩展增加新节点类型（新增文件即可）
- 不修改原始业务类

## 扩展方式
新增新类型（例如 XmlEditor）时：
1. 编写 XmlEditorAdapter extends AbstractNodeAdapter
2. 在一个自定义 Provider 中注册工厂
3. 不改已有任何类

## 节点层次建议
Root
 ├── Workspace
 │    ├── Editors
 │    │     ├── Editor(text-1)
 │    │     └── Editor(text-2)
 │    ├── Commands
 │    │     ├── log:on
 │    │     └── load
 │    └── State
 └── Registry
      ├── EditorTypes
      └── CommandTypes

详见源码适配器实现。
