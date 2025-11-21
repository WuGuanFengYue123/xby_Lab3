package com.team20.editor.domain.command.impl.logging;

import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.editor.Editor;
import com.team20.editor.domain.workspace.Workspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * log-show [file] - print the contents of .<filename>.log (or current active
 * file's log).
 */
public class LogShowCommand implements Command {

    private final String filepath;

    public LogShowCommand() {
        this.filepath = null;
    }

    public LogShowCommand(String filepath) {
        this.filepath = (filepath == null || filepath.isBlank()) ? null : filepath.trim();
    }

    @Override
    public void execute(Workspace workspace) {

        String target = filepath;
        if (target == null) {
            Editor active = workspace.getActiveEditor();
            if (active == null) {
                System.out.println("没有打开的文件");
                return;
            }
            target = active.getName();
        }
        String safeName = new File(target).getName();
        File logFile = new File("." + safeName + ".log");
        if (!logFile.exists()) {
            System.out.println("未找到日志文件: " + logFile.getName());
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception ex) {
            System.out.println("读取日志失败: " + ex.getMessage());
        }
    }

    @Override
    public String toString() {
        return "log-show " + (filepath == null ? "" : filepath);
    }
}