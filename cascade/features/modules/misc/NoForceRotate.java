/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiDownloadTerrain
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraft.network.play.server.SPacketPlayerPosLook$EnumFlags
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.misc;

import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.mixin.mixins.accessor.ISPacketPlayerPosLook;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoForceRotate
extends Module {
    public Setting<Boolean> setPos = this.register(new Setting<Boolean>("PosFix", false));

    public NoForceRotate() {
        super("NoForceRotate", Module.Category.MISC, "Prevents you from rubberbanding");
    }

    @SubscribeEvent
    public void onReceive(PacketEvent.Receive event) {
        if (this.megaNullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook && !(NoForceRotate.mc.currentScreen instanceof GuiDownloadTerrain)) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
            ((ISPacketPlayerPosLook)packet).setYaw(NoForceRotate.mc.player.rotationYaw);
            ((ISPacketPlayerPosLook)packet).setPitch(NoForceRotate.mc.player.rotationPitch);
            packet.getFlags().remove((Object)SPacketPlayerPosLook.EnumFlags.X_ROT);
            packet.getFlags().remove((Object)SPacketPlayerPosLook.EnumFlags.Y_ROT);
            if (this.setPos.getValue().booleanValue()) {
                NoForceRotate.mc.player.setPosition(packet.getX(), packet.getY(), packet.getZ());
            }
        }
    }
}

