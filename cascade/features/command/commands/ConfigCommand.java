/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 */
package cascade.features.command.commands;

import cascade.Mod;
import cascade.features.command.Command;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigCommand
extends Command {
    public ConfigCommand() {
        super("config", new String[]{"<save/load>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            ConfigCommand.sendMessage("You`ll find the config files in your gameProfile directory under oyvey/config", true, false);
            return;
        }
        if (commands.length == 2) {
            if ("list".equals(commands[0])) {
                String configs = "Configs: ";
                File file = new File("oyvey/");
                List directories = Arrays.stream(file.listFiles()).filter(File::isDirectory).filter(f -> !f.getName().equals("util")).collect(Collectors.toList());
                StringBuilder builder = new StringBuilder(configs);
                for (File file1 : directories) {
                    builder.append(file1.getName() + ", ");
                }
                configs = builder.toString();
                ConfigCommand.sendMessage(configs, true, false);
            } else {
                ConfigCommand.sendMessage("Not a valid command... Possible usage: <list>", true, false);
            }
        }
        if (commands.length >= 3) {
            switch (commands[0]) {
                case "save": {
                    Mod.configManager.saveConfig(commands[1]);
                    ConfigCommand.sendMessage((Object)ChatFormatting.GREEN + "Config '" + commands[1] + "' has been saved.", true, false);
                    return;
                }
                case "load": {
                    if (Mod.configManager.configExists(commands[1])) {
                        Mod.configManager.loadConfig(commands[1]);
                        ConfigCommand.sendMessage((Object)ChatFormatting.GREEN + "Config '" + commands[1] + "' has been loaded.", true, false);
                    } else {
                        ConfigCommand.sendMessage((Object)ChatFormatting.RED + "Config '" + commands[1] + "' does not exist.", true, false);
                    }
                    return;
                }
            }
            ConfigCommand.sendMessage("Not a valid command... Possible usage: <save/load>", true, false);
        }
    }
}

