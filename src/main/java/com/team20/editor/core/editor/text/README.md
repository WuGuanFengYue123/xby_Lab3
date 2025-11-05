# Text Editor Implementation

## 职责
- 文本内容管理（行数组结构）
- 基本编辑操作实现
- 内容显示

## 核心类
- `TextEditor.java`: 文本编辑器实现
- `TextContent.java`: 文本内容数据结构

## 数据结构
使用 `List<String>` 存储文本，每个元素是一行

## 支持的操作
- `append(text)`: 追加行
- `insert(line, col, text)`: 插入文本
- `delete(line, col, len)`: 删除字符
- `replace(line, col, len, text)`: 替换文本
- `show(startLine, endLine)`: 显示内容

## 注意事项
- 行号、列号从 1 开始
- 删除不可跨行
- 文件编码: UTF-8
