/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.player;

import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiAim
extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Custom));
    public Setting<Integer> yaw = this.register(new Setting<Object>("Yaw", Integer.valueOf(90), Integer.valueOf(-90), Integer.valueOf(90), v -> this.mode.getValue() == Mode.Custom));
    public Setting<Integer> pitch = this.register(new Setting<Object>("Pitch", Integer.valueOf(90), Integer.valueOf(-90), Integer.valueOf(90), v -> this.mode.getValue() == Mode.Custom));
    public Setting<Integer> spinSpeed = this.register(new Setting<Object>("SpinSpeed", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(50), v -> this.mode.getValue() == Mode.Spin));
    int nextValue;

    public AntiAim() {
        super("AntiAim", Module.Category.PLAYER, "ion kno");
    }

    @Override
    public void onUpdate() {
        this.nextValue += this.spinSpeed.getValue().intValue();
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (this.isDisabled()) {
            return;
        }
        if (e.getPacket() instanceof CPacketPlayer && !AntiAim.mc.player.isHandActive()) {
            switch (this.mode.getValue()) {
                case Custom: {
                    ((CPacketPlayer)e.getPacket()).yaw = this.yaw.getValue().intValue();
                    ((CPacketPlayer)e.getPacket()).pitch = this.pitch.getValue().intValue();
                    break;
                }
                case Spin: {
                    ((CPacketPlayer)e.getPacket()).yaw = this.nextValue;
                    ((CPacketPlayer)e.getPacket()).pitch = this.nextValue;
                }
            }
        }
    }

    public static enum Mode {
        Custom,
        Spin;

    }
}

