# Editor Module

## 职责
- 定义编辑器抽象接口
- 提供编辑器基类实现

## 核心接口/类
- `Editor.java`: 编辑器接口
- `AbstractEditor.java`: 编辑器抽象基类
- `text/TextEditor.java`: 文本编辑器实现（Lab1）
- `xml/XmlEditor.java`: XML编辑器实现（Lab2 - 待添加）

## 扩展点
使用 EditorRegistry 注册新类型编辑器

## Lab1 实现
文本编辑器支持：
- append, insert, delete, replace
- show 显示内容
- undo/redo

## Lab2 扩展
添加 XML 编辑器：
1. 创建 `xml/XmlEditor.java`
2. 实现 `EditorFactory`
3. 注册: `EditorRegistry.register(".xml", factory)`
