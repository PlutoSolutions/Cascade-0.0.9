/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 */
package cascade.features.modules.player;

import cascade.Mod;
import cascade.features.command.Command;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.Timer;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;

public class MCF
extends Module {
    public Setting<Boolean> hold = this.register(new Setting<Boolean>("Hold", false));
    public Setting<Integer> holdFor = this.register(new Setting<Object>("HoldFor", Integer.valueOf(500), Integer.valueOf(100), Integer.valueOf(1000), v -> this.hold.getValue()));
    private boolean clicked = false;
    Timer timer = new Timer();

    public MCF() {
        super("MCF ", Module.Category.PLAYER, "Middle Click Friend");
    }

    @Override
    public void onUpdate() {
        if (MCF.mc.gameSettings.keyBindPickBlock.isKeyDown()) {
            if (!this.clicked && MCF.mc.currentScreen == null) {
                if (this.hold.getValue().booleanValue()) {
                    if (this.timer.passedMs(this.holdFor.getValue().intValue())) {
                        this.onClick();
                        this.timer.reset();
                    }
                } else {
                    this.onClick();
                }
            }
            this.clicked = true;
        } else {
            this.clicked = false;
        }
    }

    private void onClick() {
        Entity entity;
        RayTraceResult result = MCF.mc.objectMouseOver;
        if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY && (entity = result.entityHit) instanceof EntityPlayer) {
            if (Mod.friendManager.isFriend(entity.getName())) {
                Mod.friendManager.removeFriend(entity.getName());
                Command.sendMessage((Object)ChatFormatting.BOLD + "" + (Object)ChatFormatting.RED + entity.getName() + " has been removed from friends", true, false);
            } else {
                Mod.friendManager.addFriend(entity.getName());
                Command.sendMessage((Object)ChatFormatting.BOLD + "" + (Object)ChatFormatting.GREEN + entity.getName() + " has been added to friends", true, false);
            }
        }
        this.clicked = true;
    }
}

