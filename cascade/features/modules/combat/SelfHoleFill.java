/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.block.BlockEnderChest
 *  net.minecraft.block.BlockObsidian
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketHeldItemChange
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 */
package cascade.features.modules.combat;

import cascade.features.command.Command;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.BlockUtil;
import cascade.util.CombatUtil;
import cascade.util.InventoryUtil;
import cascade.util.Timer;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class SelfHoleFill
extends Module {
    public Setting<Double> holeRange = this.register(new Setting<Double>("HoleRange", 1.2, 0.1, 2.0));
    public Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 0, 0, 250));
    private final Map<BlockPos, Integer> retries = new HashMap<BlockPos, Integer>();
    private ArrayList<BlockPos> holes = new ArrayList();
    private final Timer retryTimer = new Timer();
    private final Timer offTimer = new Timer();
    private final Timer timer = new Timer();
    EntityLivingBase target;

    public SelfHoleFill() {
        super("SelfHoleFill", Module.Category.COMBAT, "Fills a hole u r in");
    }

    @Override
    public void onEnable() {
        this.offTimer.reset();
        this.target = null;
    }

    @Override
    public void onTick() {
        if (SelfHoleFill.fullNullCheck()) {
            return;
        }
        this.target = CombatUtil.getTarget(6.0f);
        if (this.isEnabled() && this.target != null) {
            mc.addScheduledTask(() -> this.doHoleFill());
        }
    }

    @Override
    public void onDisable() {
        this.retries.clear();
        this.target = null;
    }

    private void doHoleFill() {
        if (this.check()) {
            return;
        }
        this.holes = new ArrayList();
        Iterable blocks = BlockPos.getAllInBox((BlockPos)SelfHoleFill.mc.player.getPosition().add(-this.holeRange.getValue().doubleValue(), -this.holeRange.getValue().doubleValue(), -this.holeRange.getValue().doubleValue()), (BlockPos)SelfHoleFill.mc.player.getPosition().add(this.holeRange.getValue().doubleValue(), this.holeRange.getValue().doubleValue(), this.holeRange.getValue().doubleValue()));
        for (BlockPos pos : blocks) {
            boolean solidNeighbours;
            if (SelfHoleFill.mc.world.getBlockState(pos).getMaterial().blocksMovement() || SelfHoleFill.mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial().blocksMovement()) continue;
            boolean bl = SelfHoleFill.mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK | SelfHoleFill.mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN && SelfHoleFill.mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK | SelfHoleFill.mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN && SelfHoleFill.mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK | SelfHoleFill.mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN && SelfHoleFill.mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK | SelfHoleFill.mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN && SelfHoleFill.mc.world.getBlockState(pos.add(0, 0, 0)).getMaterial() == Material.AIR && SelfHoleFill.mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial() == Material.AIR && SelfHoleFill.mc.world.getBlockState(pos.add(0, 2, 0)).getMaterial() == Material.AIR ? true : (solidNeighbours = false);
            if (!solidNeighbours) continue;
            this.holes.add(pos);
        }
        this.holes.forEach(this::placeBlock);
    }

    private void placeBlock(BlockPos pos) {
        for (Entity entity : SelfHoleFill.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityLivingBase)) continue;
            return;
        }
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int ecSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (obbySlot == -1 && ecSlot == -1) {
            Command.sendMessage("Out of blocks, disabling " + (Object)ChatFormatting.RED + "SelfHoleFill", true, true);
            this.disable();
        }
        int originalSlot = SelfHoleFill.mc.player.inventory.currentItem;
        mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(obbySlot == -1 ? ecSlot : obbySlot));
        InventoryUtil.SilentSwitchToSlot(obbySlot == -1 ? ecSlot : obbySlot);
        SelfHoleFill.mc.playerController.updateController();
        BlockUtil.placeBlock(pos);
        mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(originalSlot));
        if (SelfHoleFill.mc.player.inventory.currentItem != originalSlot) {
            SelfHoleFill.mc.player.inventory.currentItem = originalSlot;
            SelfHoleFill.mc.playerController.updateController();
        }
        this.timer.reset();
    }

    private boolean check() {
        if (SelfHoleFill.fullNullCheck()) {
            this.disable();
            return true;
        }
        if (this.retryTimer.passedMs(2000L)) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        return !this.timer.passedMs(this.delay.getValue().intValue());
    }
}

