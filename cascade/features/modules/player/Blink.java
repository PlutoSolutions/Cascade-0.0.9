/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketChatMessage
 *  net.minecraft.network.play.client.CPacketClientStatus
 *  net.minecraft.network.play.client.CPacketConfirmTeleport
 *  net.minecraft.network.play.client.CPacketKeepAlive
 *  net.minecraft.network.play.client.CPacketTabComplete
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.player;

import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Blink
extends Module {
    public Setting<Boolean> strict = this.register(new Setting<Boolean>("Strict", false));
    public Setting<Float> factor = this.register(new Setting<Float>("Factor", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(10.0f)));
    private AtomicBoolean sending = new AtomicBoolean(false);
    private Queue<Packet> storedPackets = new LinkedList<Packet>();

    public Blink() {
        super("Blink", Module.Category.PLAYER, "Simulates lag");
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Send e) {
        Object packet = e.getPacket();
        if (this.sending.get() || !this.isEnabled()) {
            return;
        }
        if (!(packet instanceof CPacketChatMessage || packet instanceof CPacketConfirmTeleport || packet instanceof CPacketKeepAlive || packet instanceof CPacketTabComplete || packet instanceof CPacketClientStatus)) {
            e.setCanceled(true);
            this.storedPackets.add((Packet)e.getPacket());
        }
    }

    @Override
    public void onDisable() {
        if (Blink.fullNullCheck() || !this.isEnabled()) {
            return;
        }
        while (!this.storedPackets.isEmpty()) {
            mc.getConnection().sendPacket(this.storedPackets.poll());
        }
    }

    @Override
    public void onEnable() {
        if (Blink.fullNullCheck() || mc.isIntegratedServerRunning() || !this.isEnabled()) {
            this.disable();
            return;
        }
        this.sending.set(false);
        this.storedPackets.clear();
    }
}

