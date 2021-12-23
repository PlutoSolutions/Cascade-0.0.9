/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 */
package cascade.features.modules.combat;

import cascade.Mod;
import cascade.features.command.Command;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.BlockUtil;
import cascade.util.EntityUtil;
import cascade.util.InventoryUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class SelfFill
extends Module {
    public Setting<Block> prefer = this.register(new Setting<Block>("Prefer", Block.EChest));
    public Setting<Float> offset = this.register(new Setting<Float>("Offset", Float.valueOf(1.0f), Float.valueOf(1.0f), Float.valueOf(5.0f)));
    public Setting<LagMode> lagBack = this.register(new Setting<LagMode>("LagBack", LagMode.Teleport));
    public Setting<Boolean> spam = this.register(new Setting<Object>("Spam", Boolean.valueOf(true), v -> this.lagBack.getValue() == LagMode.VTimer || this.lagBack.getValue() == LagMode.HTimer));
    public Setting<Boolean> confirm = this.register(new Setting<Object>("Confirm", Boolean.valueOf(true), v -> this.lagBack.getValue() == LagMode.VTimer || this.lagBack.getValue() == LagMode.HTimer));
    public Setting<Boolean> packetJump = this.register(new Setting<Object>("PacketJump", Boolean.valueOf(true), v -> this.lagBack.getValue() == LagMode.DoubleJump));
    public BlockPos startPos = null;

    public SelfFill() {
        super("SelfFill", Module.Category.COMBAT, "Phases you into a block");
    }

    @Override
    public void onEnable() {
        if (SelfFill.fullNullCheck()) {
            this.disable();
            return;
        }
        this.startPos = new BlockPos(SelfFill.mc.player.getPositionVector());
    }

    @Override
    public void onDisable() {
        if (this.lagBack.getValue() == LagMode.VTimer || this.lagBack.getValue() == LagMode.HTimer) {
            Mod.timerManager.reset();
        }
    }

    @Override
    public void onUpdate() {
        if (SelfFill.fullNullCheck()) {
            this.disable();
            return;
        }
        int originalSlot = SelfFill.mc.player.inventory.currentItem;
        int ecSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock((net.minecraft.block.Block)Blocks.ENDER_CHEST));
        int obbySlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock((net.minecraft.block.Block)Blocks.OBSIDIAN));
        if (ecSlot == -1 && obbySlot == -1) {
            Command.sendMessage("Out of blocks, disabling " + (Object)ChatFormatting.RED + "SelfFill", true, true);
            this.disable();
            return;
        }
        if (this.prefer.getValue() == Block.EChest && ecSlot != -1) {
            InventoryUtil.SilentSwitchToSlot(ecSlot);
            if (ecSlot == -1 && obbySlot != -1) {
                InventoryUtil.SilentSwitchToSlot(obbySlot);
            }
        }
        if (this.prefer.getValue() == Block.Obsidian) {
            InventoryUtil.SilentSwitchToSlot(obbySlot);
            if (obbySlot == -1 && ecSlot != -1) {
                InventoryUtil.SilentSwitchToSlot(ecSlot);
            }
        }
        EntityUtil.packetJump(SelfFill.mc.player.onGround);
        InventoryUtil.SilentSwitchToSlot(obbySlot != -1 ? obbySlot : (ecSlot != -1 ? ecSlot : originalSlot));
        BlockUtil.placeBlock(this.startPos, EnumHand.MAIN_HAND, false, true, false, true);
        SelfFill.mc.player.inventory.currentItem = originalSlot;
        SelfFill.mc.playerController.updateController();
        switch (this.lagBack.getValue()) {
            case Packet: {
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(SelfFill.mc.player.posX, SelfFill.mc.player.posY + (double)this.offset.getValue().floatValue(), SelfFill.mc.player.posZ, SelfFill.mc.player.onGround));
                break;
            }
            case YMotion: {
                SelfFill.mc.player.motionY = this.offset.getValue().floatValue();
                break;
            }
            case Teleport: {
                SelfFill.mc.player.setPositionAndUpdate(SelfFill.mc.player.posX, SelfFill.mc.player.posY + (double)this.offset.getValue().floatValue(), SelfFill.mc.player.posZ);
                break;
            }
            case LagFall: {
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.PositionRotation(SelfFill.mc.player.posX, SelfFill.mc.player.posY + (double)this.offset.getValue().floatValue(), SelfFill.mc.player.posZ, SelfFill.mc.player.rotationYaw, SelfFill.mc.player.rotationPitch, SelfFill.mc.player.onGround));
                mc.getConnection().sendPacket((Packet)new CPacketEntityAction((Entity)SelfFill.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                break;
            }
            case DoubleJump: {
                if (this.packetJump.getValue().booleanValue()) {
                    EntityUtil.packetJump(SelfFill.mc.player.onGround);
                    break;
                }
                SelfFill.mc.player.jump();
                break;
            }
            case VTimer: {
                Mod.timerManager.setTimer(12.8f);
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(SelfFill.mc.player.posX, SelfFill.mc.player.posY + (double)this.offset.getValue().floatValue(), SelfFill.mc.player.posZ, SelfFill.mc.player.onGround));
                if (this.spam.getValue().booleanValue()) {
                    mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(SelfFill.mc.player.posX, SelfFill.mc.player.posY + (double)(this.offset.getValue().floatValue() * 2.0f), SelfFill.mc.player.posZ, SelfFill.mc.player.onGround));
                    mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(SelfFill.mc.player.posX, SelfFill.mc.player.posY + (double)(this.offset.getValue().floatValue() * 3.0f), SelfFill.mc.player.posZ, SelfFill.mc.player.onGround));
                }
                if (!this.confirm.getValue().booleanValue()) break;
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(SelfFill.mc.player.posX, SelfFill.mc.player.posY, SelfFill.mc.player.posZ, false));
                SelfFill.mc.player.setLocationAndAngles(SelfFill.mc.player.posX, SelfFill.mc.player.posY, SelfFill.mc.player.posZ, SelfFill.mc.player.rotationYaw, SelfFill.mc.player.rotationPitch);
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(SelfFill.mc.player.posX, SelfFill.mc.player.posY - 1339.2, SelfFill.mc.player.posZ, true));
                break;
            }
            case HTimer: {
                Mod.timerManager.setTimer(12.8f);
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(SelfFill.mc.player.posX + (double)this.offset.getValue().floatValue(), SelfFill.mc.player.posY, SelfFill.mc.player.posZ + (double)this.offset.getValue().floatValue(), SelfFill.mc.player.onGround));
                if (this.spam.getValue().booleanValue()) {
                    mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(SelfFill.mc.player.posX + (double)(this.offset.getValue().floatValue() * 2.0f), SelfFill.mc.player.posY, SelfFill.mc.player.posZ + (double)(this.offset.getValue().floatValue() * 2.0f), SelfFill.mc.player.onGround));
                    mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(SelfFill.mc.player.posX + (double)(this.offset.getValue().floatValue() * 3.0f), SelfFill.mc.player.posY, SelfFill.mc.player.posZ + (double)(this.offset.getValue().floatValue() * 3.0f), SelfFill.mc.player.onGround));
                }
                if (!this.confirm.getValue().booleanValue()) break;
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(SelfFill.mc.player.posX, SelfFill.mc.player.posY, SelfFill.mc.player.posZ, false));
                SelfFill.mc.player.setLocationAndAngles(SelfFill.mc.player.posX, SelfFill.mc.player.posY, SelfFill.mc.player.posZ, SelfFill.mc.player.rotationYaw, SelfFill.mc.player.rotationPitch);
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(SelfFill.mc.player.posX, SelfFill.mc.player.posY - 1339.2, SelfFill.mc.player.posZ, true));
            }
        }
        EntityUtil.startSneaking();
        EntityUtil.stopSneaking(false);
        if (originalSlot != -1) {
            SelfFill.mc.player.inventory.currentItem = originalSlot;
            SelfFill.mc.playerController.updateController();
        }
        this.disable();
    }

    public static enum LagMode {
        Packet,
        YMotion,
        Teleport,
        LagFall,
        DoubleJump,
        VTimer,
        HTimer;

    }

    public static enum Block {
        EChest,
        Obsidian;

    }
}

