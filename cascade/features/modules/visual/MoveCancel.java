/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.visual;

import cascade.event.events.Render3DEvent;
import cascade.features.modules.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MoveCancel
extends Module {
    public MoveCancel() {
        super("MoveCancel", Module.Category.VISUAL, "Cancels various animations");
    }

    @Override
    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        for (EntityPlayer player : MoveCancel.mc.world.playerEntities) {
            if (player.getName() == MoveCancel.mc.player.getName()) continue;
            player.limbSwing = 0.0f;
            player.limbSwingAmount = 0.0f;
            player.prevLimbSwingAmount = 0.0f;
            player.rotationYawHead = 0.0f;
            player.rotationPitch = 0.0f;
            player.rotationYaw = 0.0f;
        }
    }
}

