package com.team20.editor.extension.registry;

import com.team20.editor.domain.editor.Editor;
import com.team20.editor.extension.spi.editor.EditorProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * EditorFactory - 根据已发现的 EditorProvider 的注册项选择合适的编辑器实现并创建实例。
 *
 * 适配统一后的 EditorProvider#getEditorRegistrations() API。
 */
public final class EditorFactory {

    private final List<EditorProvider> providers = new ArrayList<>();

    public EditorFactory() {
        ServiceLoader<EditorProvider> loader = ServiceLoader.load(EditorProvider.class);
        for (EditorProvider p : loader) {
            providers.add(p);
        }
    }

    public EditorFactory(Iterable<EditorProvider> providers) {
        for (EditorProvider p : providers) {
            this.providers.add(Objects.requireNonNull(p));
        }
    }

    /**
     * 根据文件路径创建对应的 Editor 实例（查找 provider 的 registration 中的扩展名列表）。
     *
     * 若找到匹配的 registration，则用 registration.getFactory().apply(filepath) 创建实例。
     * 若没有找到精确扩展名匹配但只有一个 provider，则使用该 provider 的第一个 registration 作为后备。
     *
     * @param filepath 文件路径（可包含扩展名）
     * @return Editor 实例
     * @throws IllegalStateException 若没有任何 provider 或找不到支持的扩展名
     */
    public Editor createEditor(String filepath) {
        if (providers.isEmpty()) {
            throw new IllegalStateException("没有可用的 EditorProvider（SPI 未提供实现）");
        }
        String ext = extensionOf(filepath);
        // first try exact extension match across all registrations
        for (EditorProvider p : providers) {
            List<EditorProvider.EditorRegistration> regs = p.getEditorRegistrations();
            if (regs == null) continue;
            for (EditorProvider.EditorRegistration r : regs) {
                List<String> exts = r.getSupportedExtensions();
                if (exts != null) {
                    for (String s : exts) {
                        if (s != null && s.equalsIgnoreCase(ext)) {
                            return r.getFactory().apply(filepath);
                        }
                    }
                }
            }
        }
        // fallback: if only one provider and it has registrations, use its first registration
        if (providers.size() == 1) {
            List<EditorProvider.EditorRegistration> regs = providers.get(0).getEditorRegistrations();
            if (regs != null && !regs.isEmpty()) {
                return regs.get(0).getFactory().apply(filepath);
            }
        }
        throw new IllegalStateException("未找到支持扩展名 '" + ext + "' 的 EditorProvider");
    }

    private String extensionOf(String filepath) {
        if (filepath == null) return "";
        int idx = filepath.lastIndexOf('.');
        if (idx <= 0 || idx == filepath.length() - 1) return "";
        return filepath.substring(idx + 1);
    }

    public List<EditorProvider> getProviders() {
        return List.copyOf(providers);
    }
}