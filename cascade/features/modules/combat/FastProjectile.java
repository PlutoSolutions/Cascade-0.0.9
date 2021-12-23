/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.combat;

import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.InventoryUtil;
import cascade.util.Timer;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastProjectile
extends Module {
    public static FastProjectile INSTANCE;
    public Setting<Double> ticks = this.register(new Setting<Double>("Ticks", 1.0, 30.0, 50.0));
    Timer projectileTimer = new Timer();

    public FastProjectile() {
        super("FastProjectile", Module.Category.COMBAT, "Allows your projectiles to do more damage");
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerDigging && ((CPacketPlayerDigging)event.getPacket()).getAction().equals((Object)CPacketPlayerDigging.Action.RELEASE_USE_ITEM) && InventoryUtil.heldItem((Item)Items.BOW, InventoryUtil.Hand.Main) && this.projectileTimer.passedMs(100L)) {
            FastProjectile.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)FastProjectile.mc.player, CPacketEntityAction.Action.START_SPRINTING));
            Random projectileRandom = new Random();
            int tick = 0;
            while ((double)tick < this.ticks.getValue()) {
                double sin = -Math.sin(Math.toRadians(FastProjectile.mc.player.rotationYaw));
                double cos = Math.cos(Math.toRadians(FastProjectile.mc.player.rotationYaw));
                if (projectileRandom.nextBoolean()) {
                    FastProjectile.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(FastProjectile.mc.player.posX + sin * 100.0, FastProjectile.mc.player.posY, FastProjectile.mc.player.posZ + cos * 100.0, true));
                    FastProjectile.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(FastProjectile.mc.player.posX - sin * 100.0, FastProjectile.mc.player.posY, FastProjectile.mc.player.posZ - cos * 100.0, true));
                } else {
                    FastProjectile.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(FastProjectile.mc.player.posX - sin * 100.0, FastProjectile.mc.player.posY, FastProjectile.mc.player.posZ - cos * 100.0, true));
                    FastProjectile.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(FastProjectile.mc.player.posX + sin * 100.0, FastProjectile.mc.player.posY, FastProjectile.mc.player.posZ + cos * 100.0, true));
                }
                ++tick;
            }
            this.projectileTimer.reset();
        }
    }
}

