package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.editor.Editor;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.bootstrap.ApplicationContext;
import com.team20.editor.extension.registry.DefaultCommandRegistry;
import com.team20.editor.domain.command.impl.workspace.SaveCommand;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Console;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * close：关闭当前活动编辑器（或指定文件）
 *
 * 行为：
 * - 当编辑器已被修改（isModified == true）或内存内容与磁盘文件内容不同时，提示用户是否保存（y/n）。
 * - 如果用户选择保存，在运行时通过 DefaultCommandRegistry.getApplicationContext()
 * 获得 ApplicationContext，再使用 SaveCommand 执行保存操作。
 *
 * 注意：
 * - 使用 System.console() 优先读取用户交互；当 console 为 null 时退回到
 * BufferedReader(System.in)。
 * - 不在此处关闭 System.in。
 */
public class CloseCommand implements Command {

    private String filepath;

    public CloseCommand() {
    }

    public CloseCommand(String filepath) {
        this.filepath = filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public void execute(Workspace workspace) {
        if (filepath == null || filepath.isBlank()) {
            Editor active = workspace.getActiveEditor();
            if (active == null) {
                System.out.println("没有打开的文件");
                return;
            }
            String name = active.getName();
            if (isEditorDirty(active)) {
                boolean save = promptYesNo(String.format("文件 '%s' 已修改，是否保存? (y/n) ", name));
                if (save) {
                    if (!attemptSave(active, workspace)) {
                        System.out.println("保存失败，取消关闭操作");
                        return;
                    }
                }
            }
            // 发布 close 事件（在移除编辑器之前），以便日志监听器能定位到目标文件
            try {
                workspace.publishCommandEvent("close", name);
            } catch (Throwable ignored) {
            }
            workspace.removeEditor(active);
            System.out.println("已关闭活动文件");
        } else {
            Editor e = workspace.getEditor(filepath);
            if (e == null) {
                System.out.println("未找到文件: " + filepath);
                return;
            }
            String name = e.getName();
            if (isEditorDirty(e)) {
                boolean save = promptYesNo(String.format("文件 '%s' 已修改，是否保存? (y/n) ", name));
                if (save) {
                    if (!attemptSave(e, workspace)) {
                        System.out.println("保存失败，取消关闭操作");
                        return;
                    }
                }
            }
            // 发布 close 事件（在移除编辑器之前）
            try {
                workspace.publishCommandEvent("close", name);
            } catch (Throwable ignored) {
            }
            workspace.removeEditor(e);
            System.out.println("已关闭: " + filepath);
        }
    }

    /**
     * 公共提示方法（可供 ExitCommand 调用）。
     */
    public static boolean promptYesNo(String message) {
        try {
            Console console = System.console();
            String line = null;
            if (console != null) {
                line = console.readLine(message);
            } else {
                System.out.print(message);
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                line = br.readLine();
            }
            if (line == null)
                return false;
            String t = line.trim().toLowerCase();
            return t.equals("y") || t.equals("yes");
        } catch (Exception ex) {
            // on IO problems, default to not saving
            return false;
        }
    }

    /**
     * 更健壮的“脏”检查（现在为 public，以便 ExitCommand 直接复用）：
     * - 若 editor.isModified() 返回 true 则认为已修改；
     * - 否则尝试读取磁盘文件内容并与 editor.getContent() 比较，不同则认为已修改。
     */
    public static boolean isEditorDirty(Editor editor) {
        try {
            // if the editor explicitly reports modified -> it's dirty
            try {
                if (editor.isModified()) {
                    return true;
                }
            } catch (Throwable ignored) {
                // if method not available or throws, fall through to content comparison
            }

            // Try to compare in-memory content vs file on disk.
            String editorContent = null;
            try {
                editorContent = editor.getContent();
                if (editorContent == null)
                    editorContent = "";
            } catch (Throwable t) {
                // if we cannot read editor content, fall back to isModified (already checked)
                return false;
            }

            String filepath = editor.getName();
            if (filepath == null || filepath.isBlank()) {
                // no associated file path -> treat non-empty content as dirty
                return !editorContent.isBlank();
            }

            // Prefer using ApplicationContext's PersistenceManager if available
            try {
                ApplicationContext ctx = DefaultCommandRegistry.getApplicationContext();
                if (ctx != null && ctx.persistenceManager() != null) {
                    try {
                        String disk = ctx.persistenceManager().load(filepath);
                        if (disk == null)
                            disk = "";
                        return !disk.equals(editorContent);
                    } catch (IOException io) {
                        // cannot read file: if editor has any content, consider it dirty
                        return !editorContent.isBlank();
                    } catch (Throwable ignored) {
                        // fallthrough to plain file read
                    }
                }
            } catch (Throwable ignored) {
            }

            // Fallback: try direct file read
            try {
                Path p = Path.of(filepath);
                if (!Files.exists(p)) {
                    return !editorContent.isBlank();
                }
                String disk = Files.readString(p);
                if (disk == null)
                    disk = "";
                return !disk.equals(editorContent);
            } catch (Throwable t) {
                // on any IO error, be conservative: if editor has content, consider it dirty
                return !editorContent.isBlank();
            }
        } catch (Throwable t) {
            // in case of unexpected errors, be conservative: return false so we won't block
            // close
            return false;
        }
    }

    /**
     * Attempt to save the editor using SaveCommand and ApplicationContext.
     * Returns true on success, false on failure.
     *
     * 设为 public 以便 ExitCommand 重用。
     */
    public static boolean attemptSave(Editor editor, Workspace workspace) {
        try {
            ApplicationContext ctx = DefaultCommandRegistry.getApplicationContext();
            if (ctx == null) {
                System.out.println("无法保存：ApplicationContext 未就绪");
                return false;
            }
            // Use SaveCommand to perform saving so we reuse existing save logic.
            // Pass editor.getName() as path to ensure SaveCommand saves that file.
            SaveCommand saveCmd = new SaveCommand(ctx.persistenceManager(), editor.getName());
            saveCmd.execute(workspace);
            return true;
        } catch (Exception ex) {
            System.out.println("保存时发生错误: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public String toString() {
        return "close " + (filepath == null ? "" : filepath);
    }
}