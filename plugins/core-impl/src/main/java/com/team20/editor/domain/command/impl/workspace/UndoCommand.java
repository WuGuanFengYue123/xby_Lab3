package com.team20.editor.domain.command.impl.workspace;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.workspace.Workspace;
import com.team20.editor.extension.registry.DefaultCommandRegistry;
import com.team20.editor.bootstrap.ApplicationContext;

import java.lang.reflect.Method;

/**
 * undo - undo last undoable operation.
 *
 * This command uses the ApplicationContext.commandInvoker() to perform undo.
 * It attempts to call undo(Workspace) if available; otherwise falls back to
 * undo().
 * Provides user-friendly messages when undo is not available.
 */
public class UndoCommand implements Command {

    @Override
    public void execute(Workspace workspace) {
        ApplicationContext ctx = DefaultCommandRegistry.getApplicationContext();
        if (ctx == null) {
            System.out.println("无法执行 undo：ApplicationContext 未就绪");
            return;
        }
        var invoker = ctx.commandInvoker();
        if (invoker == null) {
            System.out.println("无法执行 undo：CommandInvoker 未就绪");
            return;
        }

        try {
            // Try undo(Workspace) first
            try {
                Method m = invoker.getClass().getMethod("undo", Workspace.class);
                m.invoke(invoker, workspace);
                System.out.println("撤销成功");
                return;
            } catch (NoSuchMethodException ignored) {
            }

            // Fallback to no-arg undo()
            try {
                Method m2 = invoker.getClass().getMethod("undo");
                m2.invoke(invoker);
                System.out.println("撤销成功");
                return;
            } catch (NoSuchMethodException ignored) {
            }

            System.out.println("撤销操作不可用：CommandInvoker 未实现 undo 方法");
        } catch (Throwable t) {
            System.out.println("撤销失败: " + t.getMessage());
        }
    }

    @Override
    public String toString() {
        return "undo";
    }
}