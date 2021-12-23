/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
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
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TrollBow
extends Module {
    public Setting<Integer> packets = this.register(new Setting<Integer>("Packets", 30, 1, 30));
    public Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 1000, 0, 5000));
    public Setting<Boolean> ongroudonly = this.register(new Setting<Boolean>("OnlyOnGround", true));
    public Setting<Boolean> negative = this.register(new Setting<Boolean>("Negative", false));
    public Setting<Boolean> bounds = this.register(new Setting<Boolean>("Bounds+", false));
    public Setting<Boolean> boundsN = this.register(new Setting<Boolean>("Bounds-", false));
    public Setting<Integer> hPower = this.register(new Setting<Integer>("HPower", 0, 0, 100));
    public Setting<Integer> vPower = this.register(new Setting<Integer>("VPower", 2, 0, 100));
    private static TrollBow INSTANCE;
    Timer timer = new Timer();

    public TrollBow() {
        super("TrollBow", Module.Category.COMBAT, "FastProjectile");
        INSTANCE = this;
    }

    public static TrollBow getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TrollBow();
        }
        return INSTANCE;
    }

    @Override
    public void onUpdate() {
        if (TrollBow.fullNullCheck()) {
            return;
        }
        if (!TrollBow.mc.player.isHandActive() && this.timer.passedMs(this.delay.getValue().intValue()) && !InventoryUtil.heldItem((Item)Items.BOW, InventoryUtil.Hand.Main)) {
            this.timer.reset();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (e.getPacket() instanceof CPacketPlayerDigging && ((CPacketPlayerDigging)e.getPacket()).getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM && this.isEnabled() && !TrollBow.fullNullCheck() && InventoryUtil.heldItem((Item)Items.BOW, InventoryUtil.Hand.Main) && this.timer.passedMs(this.delay.getValue().intValue())) {
            if (this.ongroudonly.getValue().booleanValue() && !TrollBow.mc.player.onGround) {
                return;
            }
            mc.getConnection().sendPacket((Packet)new CPacketEntityAction((Entity)TrollBow.mc.player, CPacketEntityAction.Action.START_SPRINTING));
            for (int p = 0; p < this.packets.getValue(); ++p) {
                double sin = -Math.sin(Math.toRadians(TrollBow.mc.player.rotationYaw));
                double cos = Math.cos(Math.toRadians(TrollBow.mc.player.rotationYaw));
                if (!this.negative.getValue().booleanValue()) {
                    mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(TrollBow.mc.player.posX + sin * (double)this.hPower.getValue().intValue(), TrollBow.mc.player.posY + 1.0E-5 * (double)this.vPower.getValue().intValue(), TrollBow.mc.player.posZ + cos * (double)this.hPower.getValue().intValue(), false));
                    mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(TrollBow.mc.player.posX - sin * (double)this.hPower.getValue().intValue(), TrollBow.mc.player.posY - 1.0E-5 * (double)this.vPower.getValue().intValue(), TrollBow.mc.player.posZ - cos * (double)this.hPower.getValue().intValue(), true));
                } else {
                    mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(TrollBow.mc.player.posX - sin * (double)this.hPower.getValue().intValue(), TrollBow.mc.player.posY - 1.0E-5 * (double)this.vPower.getValue().intValue(), TrollBow.mc.player.posZ - cos * (double)this.hPower.getValue().intValue(), true));
                    mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(TrollBow.mc.player.posX + sin * (double)this.hPower.getValue().intValue(), TrollBow.mc.player.posY + 1.0E-5 * (double)this.vPower.getValue().intValue(), TrollBow.mc.player.posZ + cos * (double)this.hPower.getValue().intValue(), false));
                }
                if (this.bounds.getValue().booleanValue()) {
                    mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(TrollBow.mc.player.posX, TrollBow.mc.player.posY + 1339.2, TrollBow.mc.player.posZ, false));
                }
                if (!this.boundsN.getValue().booleanValue()) continue;
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(TrollBow.mc.player.posX, TrollBow.mc.player.posY - 1339.2, TrollBow.mc.player.posZ, false));
            }
            this.timer.reset();
        }
    }

    @Override
    public String getDisplayInfo() {
        if (TrollBow.mc.player.isHandActive() && InventoryUtil.heldItem((Item)Items.BOW, InventoryUtil.Hand.Main)) {
            if (this.timer.passedMs(this.delay.getValue().intValue())) {
                return (Object)ChatFormatting.GREEN + "Charged";
            }
            return (Object)ChatFormatting.YELLOW + "Charging";
        }
        return null;
    }
}

