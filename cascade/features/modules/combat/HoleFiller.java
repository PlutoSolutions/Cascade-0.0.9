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

public class HoleFiller
extends Module {
    private static HoleFiller INSTANCE;
    public Setting<Integer> range = this.register(new Setting<Integer>("PlaceRange", 6, 0, 6));
    public Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 0, 0, 250));
    public Setting<Integer> blocksPerTick = this.register(new Setting<Integer>("BlocksPerTick", 30, 8, 30));
    private final Map<BlockPos, Integer> retries = new HashMap<BlockPos, Integer>();
    private ArrayList<BlockPos> holes = new ArrayList();
    private final Timer retryTimer = new Timer();
    private final Timer offTimer = new Timer();
    private final Timer timer = new Timer();
    private int blocksThisTick;
    EntityLivingBase target;

    public HoleFiller() {
        super("HoleFiller", Module.Category.COMBAT, "Fills safe spots around you");
        this.setInstance();
    }

    public static HoleFiller getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HoleFiller();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        this.offTimer.reset();
        this.blocksThisTick = 0;
        this.target = null;
    }

    @Override
    public void onTick() {
        if (HoleFiller.fullNullCheck()) {
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
        this.blocksThisTick = 0;
        this.target = null;
    }

    private void doHoleFill() {
        if (this.check()) {
            return;
        }
        this.holes = new ArrayList();
        Iterable blocks = BlockPos.getAllInBox((BlockPos)HoleFiller.mc.player.getPosition().add(-this.range.getValue().intValue(), -this.range.getValue().intValue(), -this.range.getValue().intValue()), (BlockPos)HoleFiller.mc.player.getPosition().add(this.range.getValue().intValue(), this.range.getValue().intValue(), this.range.getValue().intValue()));
        for (BlockPos pos : blocks) {
            boolean solidNeighbours;
            if (HoleFiller.mc.world.getBlockState(pos).getMaterial().blocksMovement() || HoleFiller.mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial().blocksMovement()) continue;
            boolean bl = HoleFiller.mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK | HoleFiller.mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN && HoleFiller.mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK | HoleFiller.mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN && HoleFiller.mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK | HoleFiller.mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN && HoleFiller.mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK | HoleFiller.mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN && HoleFiller.mc.world.getBlockState(pos.add(0, 0, 0)).getMaterial() == Material.AIR && HoleFiller.mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial() == Material.AIR && HoleFiller.mc.world.getBlockState(pos.add(0, 2, 0)).getMaterial() == Material.AIR ? true : (solidNeighbours = false);
            if (!solidNeighbours) continue;
            this.holes.add(pos);
        }
        this.holes.forEach(this::placeBlock);
        this.disable();
    }

    private void placeBlock(BlockPos pos) {
        for (Entity entity : HoleFiller.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityLivingBase)) continue;
            return;
        }
        if (this.blocksThisTick < this.blocksPerTick.getValue()) {
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int ecSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            if (obbySlot == -1 && ecSlot == -1) {
                Command.sendMessage("Out of blocks, disabling " + (Object)ChatFormatting.RED + "HoleFiller", true, true);
                this.disable();
            }
            int originalSlot = HoleFiller.mc.player.inventory.currentItem;
            mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(obbySlot == -1 ? ecSlot : obbySlot));
            InventoryUtil.SilentSwitchToSlot(obbySlot == -1 ? ecSlot : obbySlot);
            HoleFiller.mc.playerController.updateController();
            BlockUtil.placeBlock(pos);
            mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(originalSlot));
            if (HoleFiller.mc.player.inventory.currentItem != originalSlot) {
                HoleFiller.mc.player.inventory.currentItem = originalSlot;
                HoleFiller.mc.playerController.updateController();
            }
            this.timer.reset();
            ++this.blocksThisTick;
        }
    }

    private boolean check() {
        if (HoleFiller.fullNullCheck()) {
            this.disable();
            return true;
        }
        this.blocksThisTick = 0;
        if (this.retryTimer.passedMs(2000L)) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        return !this.timer.passedMs(this.delay.getValue().intValue());
    }
}

