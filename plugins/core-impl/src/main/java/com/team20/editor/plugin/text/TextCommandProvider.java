package com.team20.editor.plugin.text;

import com.team20.editor.extension.spi.command.CommandProvider;
import com.team20.editor.extension.registry.CommandRegistry;
import com.team20.editor.domain.command.Command;
import com.team20.editor.domain.command.CommandDescriptor; // domain descriptor
import com.team20.editor.domain.command.impl.text.AppendCommand;
import com.team20.editor.domain.command.impl.text.InsertCommand;
import com.team20.editor.domain.command.impl.text.DeleteCommand;
import com.team20.editor.domain.command.impl.text.ReplaceCommand;
import com.team20.editor.domain.command.impl.text.ShowCommand;

import java.util.List;

/**
 * TextCommandProvider registers factories for text commands via
 * registerFactories.
 */
public class TextCommandProvider implements CommandProvider {

    public TextCommandProvider() {
        // constructor left empty (avoid interacting with registry here)
    }

    @Override
    public String getProviderName() {
        return "text-provider";
    }

    @Override
    public List<com.team20.editor.domain.command.CommandDescriptor> getCommandDescriptors() {
        return List.of(); // factories registered in registerFactories
    }

    @Override
    public void registerFactories(CommandRegistry registry) {
        registry.registerFactory("append", (rawArgs) -> {
            String text = extractQuotedText(rawArgs);
            return new AppendCommand(text);
        });

        registry.registerFactory("insert", (rawArgs) -> {
            String[] parts = rawArgs.split("\\s+", 2);
            String[] pos = parts[0].split(":");
            int line = Integer.parseInt(pos[0]);
            int col = Integer.parseInt(pos[1]);
            String text = extractQuotedText(parts[1]);
            return new InsertCommand(line, col, text);
        });

        registry.registerFactory("delete", (rawArgs) -> {
            String[] parts = rawArgs.split("\\s+");
            String[] pos = parts[0].split(":");
            int line = Integer.parseInt(pos[0]);
            int col = Integer.parseInt(pos[1]);
            int len = Integer.parseInt(parts[1]);
            return new DeleteCommand(line, col, len);
        });

        registry.registerFactory("replace", (rawArgs) -> {
            String[] parts = rawArgs.split("\\s+", 3);
            String[] pos = parts[0].split(":");
            int line = Integer.parseInt(pos[0]);
            int col = Integer.parseInt(pos[1]);
            int len = Integer.parseInt(parts[1]);
            String text = extractQuotedText(parts[2]);
            return new ReplaceCommand(line, col, len, text);
        });

        registry.registerFactory("show", (rawArgs) -> {
            if (rawArgs == null || rawArgs.isBlank()) {
                return new ShowCommand();
            }
            String[] bits = rawArgs.split(":");
            int start = Integer.parseInt(bits[0]);
            int end = Integer.parseInt(bits[1]);
            return new ShowCommand(start, end);
        });
    }

    private static String extractQuotedText(String input) {
        input = input.trim();
        if (input.startsWith("\"") && input.endsWith("\"")) {
            return input.substring(1, input.length() - 1);
        }
        throw new IllegalArgumentException("文本参数必须用双引号包裹: " + input);
    }
}