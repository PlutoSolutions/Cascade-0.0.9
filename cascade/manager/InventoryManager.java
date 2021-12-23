/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketHeldItemChange
 */
package cascade.manager;

import cascade.features.modules.combat.AutoCrystal;
import cascade.util.Util;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class InventoryManager
implements Util {
    private int recoverySlot = -1;
    public int currentPlayerItem;

    public void update() {
        if (this.recoverySlot != -1) {
            mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(this.recoverySlot == 8 ? 7 : this.recoverySlot + 1));
            mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(this.recoverySlot));
            int i = InventoryManager.mc.player.inventory.currentItem = this.recoverySlot;
            if (i != this.currentPlayerItem) {
                this.currentPlayerItem = i;
                mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(this.currentPlayerItem));
            }
            this.recoverySlot = -1;
        }
    }

    public void recoverSilent(int slot) {
        this.recoverySlot = slot;
    }

    public void performSwap(Mode mode) {
        if (AutoCrystal.getInstance().swapType.getValue() == AutoCrystal.SwapMode.Silent) {
            // empty if block
        }
    }

    public static enum Mode {
        Silent,
        Normal;

    }
}

