/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.combat;

import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.modules.combat.TrollBow;
import cascade.features.setting.Setting;
import cascade.util.EntityUtil;
import cascade.util.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Quiver
extends Module {
    public Setting<Boolean> notMoving = this.register(new Setting<Boolean>("NotMoving", true));

    public Quiver() {
        super("Quiver", Module.Category.COMBAT, "Shoots urself with arrows");
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        CPacketPlayerDigging p;
        if (e.getPacket() instanceof CPacketPlayerDigging && !Quiver.fullNullCheck() && this.isEnabled() && TrollBow.getInstance().isDisabled() && (p = (CPacketPlayerDigging)e.getPacket()).getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM && InventoryUtil.heldItem((Item)Items.BOW, InventoryUtil.Hand.Both)) {
            if (this.notMoving.getValue().booleanValue() && EntityUtil.isMoving()) {
                return;
            }
            mc.getConnection().sendPacket((Packet)new CPacketPlayer.Rotation(Quiver.mc.player.rotationYaw, -90.0f, true));
        }
    }
}

