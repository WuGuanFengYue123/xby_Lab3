# SPI & Core Interface Guidelines

此补丁新增了若干核心接口（Editor、EditorProvider、Serializer），用于明确模块边界并为后续迭代（新增编辑器类型、序列化格式、日志持久化）留出扩展点。

设计要点：
- 使用 ServiceLoader SPI（extension.spi.*）作为可插拔点。ApplicationContext 应在启动时尝试加载 Provider；若未找到任何实现，回退到内置实现（fallback）。
- 保持向后兼容：不要在同一提交中修改现有实现类的行为。先添加接口，再逐步让实现类 implement 接口或添加适配器。
- 日志模块初期保留为可选：实现 LogSink/LogListener 接口，但文件写入逻辑可在后续迭代实现。

接下来步骤：
1. 在 ApplicationContext 中实现 ServiceLoader 加载 EditorProvider/SerializerProvider，若为空则注册 TextEditorProvider/JsonSerializer（fallback）。
2. 让现有 TextEditor 类实现 domain.editor.Editor（或提供适配器）。
3. 添加单元测试覆盖 TextEditor 与 CommandInvoker 的基本行为。
