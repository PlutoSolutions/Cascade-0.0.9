/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  org.lwjgl.input.Keyboard
 */
package cascade.features.command.commands;

import cascade.Mod;
import cascade.features.command.Command;
import cascade.features.modules.Module;
import cascade.features.setting.Bind;
import com.mojang.realmsclient.gui.ChatFormatting;
import org.lwjgl.input.Keyboard;

public class BindCommand
extends Command {
    public BindCommand() {
        super("bind", new String[]{"<module>", "<bind>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            BindCommand.sendMessage("Please specify a module.", true, false);
            return;
        }
        String rkey = commands[1];
        String moduleName = commands[0];
        Module module = Mod.moduleManager.getModuleByName(moduleName);
        if (module == null) {
            BindCommand.sendMessage("Unknown module '" + module + "'!", true, false);
            return;
        }
        if (rkey == null) {
            BindCommand.sendMessage(module.getName() + " is bound to " + (Object)ChatFormatting.GRAY + module.getBind().toString(), true, false);
            return;
        }
        int key = Keyboard.getKeyIndex((String)rkey.toUpperCase());
        if (rkey.equalsIgnoreCase("none")) {
            key = -1;
        }
        if (key == 0) {
            BindCommand.sendMessage("Unknown key '" + rkey + "'!", true, false);
            return;
        }
        module.bind.setValue(new Bind(key));
        BindCommand.sendMessage("Bind for " + (Object)ChatFormatting.GREEN + module.getName() + (Object)ChatFormatting.WHITE + " set to " + (Object)ChatFormatting.GRAY + rkey.toUpperCase(), true, false);
    }
}

