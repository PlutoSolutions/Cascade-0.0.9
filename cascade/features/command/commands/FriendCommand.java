/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 */
package cascade.features.command.commands;

import cascade.Mod;
import cascade.features.command.Command;
import cascade.manager.FriendManager;
import com.mojang.realmsclient.gui.ChatFormatting;

public class FriendCommand
extends Command {
    public FriendCommand() {
        super("friend", new String[]{"<add/del/name/clear>", "<name>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            if (Mod.friendManager.getFriends().isEmpty()) {
                FriendCommand.sendMessage("Friend list empty D:.", true, false);
            } else {
                String f = "Friends: ";
                for (FriendManager.Friend friend : Mod.friendManager.getFriends()) {
                    try {
                        f = f + friend.getUsername() + ", ";
                    }
                    catch (Exception exception) {}
                }
                FriendCommand.sendMessage(f, true, false);
            }
            return;
        }
        if (commands.length == 2) {
            switch (commands[0]) {
                case "reset": {
                    Mod.friendManager.onLoad();
                    FriendCommand.sendMessage("Friends got reset.", true, false);
                    return;
                }
            }
            FriendCommand.sendMessage(commands[0] + (Mod.friendManager.isFriend(commands[0]) ? " is friended." : " isn't friended."), true, false);
            return;
        }
        if (commands.length >= 2) {
            switch (commands[0]) {
                case "add": {
                    Mod.friendManager.addFriend(commands[1]);
                    FriendCommand.sendMessage((Object)ChatFormatting.GREEN + commands[1] + " has been friended", true, false);
                    return;
                }
                case "del": {
                    Mod.friendManager.removeFriend(commands[1]);
                    FriendCommand.sendMessage((Object)ChatFormatting.RED + commands[1] + " has been unfriended", true, false);
                    return;
                }
            }
            FriendCommand.sendMessage("Unknown Command, try friend add/del (name)", true, false);
        }
    }
}

