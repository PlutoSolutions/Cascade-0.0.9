/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketHeldItemChange
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItem
 *  net.minecraft.util.EnumHand
 */
package cascade.features.modules.player;

import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.InventoryUtil;
import cascade.util.Util;
import java.util.concurrent.TimeUnit;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

public class FastUse
extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("XPMode", Mode.Packet));
    public Setting<Integer> spoofs = this.register(new Setting<Object>("Spoofs", Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(10), v -> this.mode.getValue() != Mode.Vanilla));
    public Setting<Integer> offset = this.register(new Setting<Object>("Offset", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(2), v -> this.mode.getValue() == Mode.Instant));
    public Setting<Integer> delay = this.register(new Setting<Object>("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(250), v -> this.mode.getValue() == Mode.Instant));
    public Setting<Boolean> fastEat = this.register(new Setting<Boolean>("FastEat", false));

    public FastUse() {
        super("FastUse", Module.Category.PLAYER, "ModuleName");
    }

    @Override
    public void onUpdate() {
        if (FastUse.fullNullCheck()) {
            return;
        }
        if (this.fastEat.getValue().booleanValue() && InventoryUtil.heldItem(Items.GOLDEN_APPLE, InventoryUtil.Hand.Both)) {
            FastUse.mc.rightClickDelayTimer = 0;
        }
        if (InventoryUtil.heldItem(Items.EXPERIENCE_BOTTLE, InventoryUtil.Hand.Both) && FastUse.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            switch (this.mode.getValue()) {
                case Vanilla: {
                    FastUse.mc.rightClickDelayTimer = 0;
                    break;
                }
                case Packet: {
                    mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(this.HotbarEXP()));
                    for (int s = 0; s <= this.spoofs.getValue(); ++s) {
                        mc.getConnection().sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                    }
                    mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(FastUse.mc.player.inventory.currentItem));
                    break;
                }
                case Instant: {
                    FastUse.mc.rightClickDelayTimer = 0;
                    for (int s = 1 - this.offset.getValue(); s <= this.spoofs.getValue(); ++s) {
                        this.sendPacket();
                    }
                    break;
                }
            }
        }
    }

    private int HotbarEXP() {
        int slot = 0;
        for (int i = 0; i < 9; ++i) {
            if (FastUse.mc.player.inventory.getStackInSlot(i).getItem() != Items.EXPERIENCE_BOTTLE) continue;
            slot = i;
            break;
        }
        return slot;
    }

    private void sendPacket() {
        PacketThread packetThread = new PacketThread(this.delay.getValue());
        if (this.delay.getValue() == 0) {
            packetThread.run();
        } else {
            packetThread.start();
        }
    }

    public static class PacketThread
    extends Thread {
        private int delay;

        public PacketThread(int delayIn) {
            this.delay = delayIn;
        }

        @Override
        public void run() {
            try {
                if (this.delay != 0) {
                    TimeUnit.MILLISECONDS.sleep(this.delay);
                }
                Util.mc.addScheduledTask(() -> Util.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)));
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static enum Mode {
        Packet,
        Instant,
        Vanilla;

    }
}

