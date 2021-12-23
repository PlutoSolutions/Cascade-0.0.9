/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockAir
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.BlockObsidian
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.item.EntityXPOrb
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.GameType
 */
package cascade.features.modules.combat;

import cascade.Mod;
import cascade.event.events.Render3DEvent;
import cascade.features.modules.Module;
import cascade.features.modules.core.ClickGui;
import cascade.features.setting.Setting;
import cascade.util.BlockUtil;
import cascade.util.ColorUtil;
import cascade.util.EntityUtil;
import cascade.util.RenderUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.GameType;

public class AutoTrap
extends Module {
    public static final List blackList = Arrays.asList(new Block[]{Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER});
    public static final List shulkerList = Arrays.asList(new Block[]{Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX});
    private final Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(4.5f), Float.valueOf(3.5f), Float.valueOf(32.0f)));
    private final Setting<Integer> blocksPerTick = this.register(new Setting<Integer>("BlocksPerTick", 2, 1, 23));
    private final Setting<Integer> tickDelay = this.register(new Setting<Integer>("TickDelay", 2, 0, 10));
    private final Setting<Cage> cage = this.register(new Setting<Cage>("Cage", Cage.TRAP));
    private final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    private final Setting<Boolean> noGlitchBlocks = this.register(new Setting<Boolean>("NoGlitchBlocks", true));
    private final Setting<Boolean> activeInFreecam = this.register(new Setting<Boolean>("ActiveInFreecam", true));
    private final Setting<Boolean> toggleoff = this.register(new Setting<Boolean>("Toggle Off", false));
    private final Setting<Boolean> esp = this.register(new Setting<Boolean>("Show esp", false));
    private final Setting<ESPMode> mode = this.register(new Setting<ESPMode>("Esp Mode", ESPMode.Full));
    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 0, 0, 255));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 255, 0, 255));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 255, 0, 255));
    private final Setting<Integer> alpha = this.register(new Setting<Integer>("Alpha", 70, 0, 255));
    private final Setting<Integer> oalpha = this.register(new Setting<Integer>("Outline Alpha", 70, 0, 255));
    int cDelay = 0;
    String caura;
    boolean isDisabling;
    boolean hasDisabled;
    private EntityPlayer closestTarget;
    private String lastTickTargetName = "";
    private int playerHotbarSlot = -1;
    private int lastHotbarSlot = -1;
    private int delayStep = 0;
    private boolean isSneaking = false;
    private int offsetStep = 0;
    private boolean firstRun;
    private int test;
    private final Set<BlockPos> placeList = new HashSet<BlockPos>();

    public AutoTrap() {
        super("AutoTrap", Module.Category.COMBAT, "Automatically traps nigger LOL!");
    }

    public static EnumFacing getPlaceableSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            IBlockState blockState;
            BlockPos neighbour = pos.offset(side);
            if (!AutoTrap.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(AutoTrap.mc.world.getBlockState(neighbour), false) || (blockState = AutoTrap.mc.world.getBlockState(neighbour)).getMaterial().isReplaceable()) continue;
            return side;
        }
        return null;
    }

    private static Block getBlock(BlockPos pos) {
        return AutoTrap.getState(pos).getBlock();
    }

    private static IBlockState getState(BlockPos pos) {
        return AutoTrap.mc.world.getBlockState(pos);
    }

    public static boolean canBeClicked(BlockPos pos) {
        return AutoTrap.getBlock(pos).canCollideCheck(AutoTrap.getState(pos), false);
    }

    @Override
    public void onEnable() {
        this.test = 0;
        if (AutoTrap.mc.player == null) {
            this.disable();
            return;
        }
        this.hasDisabled = false;
        this.firstRun = true;
        this.playerHotbarSlot = AutoTrap.mc.player.inventory.currentItem;
        this.lastHotbarSlot = -1;
    }

    @Override
    public void onDisable() {
        if (AutoTrap.mc.player == null) {
            return;
        }
        if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
            AutoTrap.mc.player.inventory.currentItem = this.playerHotbarSlot;
        }
        if (this.isSneaking) {
            mc.getConnection().sendPacket((Packet)new CPacketEntityAction((Entity)AutoTrap.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        this.playerHotbarSlot = -1;
        this.lastHotbarSlot = -1;
        this.placeList.clear();
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.esp.getValue().booleanValue()) {
            for (BlockPos pos : this.placeList) {
                if (this.mode.getValue() == ESPMode.Solid) {
                    RenderUtil.drawBoxESP(pos, ClickGui.getInstance().rainbow.getValue() != false ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), true, ClickGui.getInstance().rainbow.getValue() != false ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), 1.2f, false, true, this.alpha.getValue(), true);
                    continue;
                }
                if (this.mode.getValue() == ESPMode.Outline) {
                    RenderUtil.drawBoxESP(pos, ClickGui.getInstance().rainbow.getValue() != false ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), true, ClickGui.getInstance().rainbow.getValue() != false ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), 1.2f, true, false, this.alpha.getValue(), true);
                    continue;
                }
                if (this.mode.getValue() != ESPMode.Full) continue;
                RenderUtil.drawBoxESP(pos, ClickGui.getInstance().rainbow.getValue() != false ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), true, ClickGui.getInstance().rainbow.getValue() != false ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), 1.2f, true, true, this.alpha.getValue(), true);
            }
        }
    }

    @Override
    public void onUpdate() {
        if (AutoTrap.fullNullCheck()) {
            return;
        }
        if (this.cDelay > 0) {
            --this.cDelay;
        }
        if (this.cDelay == 0 && this.isDisabling && Mod.moduleManager.getModuleByName(this.caura) != null) {
            Mod.moduleManager.getModuleByName(this.caura).toggle();
            this.isDisabling = false;
            this.hasDisabled = true;
        }
        if (this.toggleoff.getValue().booleanValue()) {
            ++this.test;
            if (this.test == 20) {
                super.toggle();
            }
        }
        if (!this.activeInFreecam.getValue().booleanValue() && Mod.moduleManager.isModuleEnabled("Freecam")) {
            return;
        }
        if (!this.firstRun) {
            if (this.delayStep < this.tickDelay.getValue()) {
                ++this.delayStep;
                return;
            }
            this.delayStep = 0;
        }
        this.findClosestTarget();
        if (this.closestTarget == null) {
            if (this.firstRun) {
                this.firstRun = false;
            }
            return;
        }
        if (this.firstRun) {
            this.firstRun = false;
            this.lastTickTargetName = this.closestTarget.getName();
        } else if (!this.lastTickTargetName.equals(this.closestTarget.getName())) {
            this.lastTickTargetName = this.closestTarget.getName();
            this.offsetStep = 0;
        }
        ArrayList placeTargets = new ArrayList();
        if (this.cage.getValue() == Cage.TRAP) {
            Collections.addAll(placeTargets, Offsets.TRAP);
        }
        if (this.cage.getValue() == Cage.TRAPTOP) {
            Collections.addAll(placeTargets, Offsets.TRAPTOP);
        }
        if (this.cage.getValue() == Cage.TRAPFULLROOF) {
            Collections.addAll(placeTargets, Offsets.TRAPFULLROOF);
        }
        if (this.cage.getValue() == Cage.TRAPFULLROOFTOP) {
            Collections.addAll(placeTargets, Offsets.TRAPFULLROOFTOP);
        }
        if (this.cage.getValue() == Cage.CRYSTALEXA) {
            Collections.addAll(placeTargets, Offsets.CRYSTALEXA);
        }
        if (this.cage.getValue() == Cage.CRYSTAL) {
            Collections.addAll(placeTargets, Offsets.CRYSTAL);
        }
        if (this.cage.getValue() == Cage.CRYSTALFULLROOF) {
            Collections.addAll(placeTargets, Offsets.CRYSTALFULLROOF);
        }
        int blocksPlaced = 0;
        while (blocksPlaced < this.blocksPerTick.getValue()) {
            if (this.offsetStep >= placeTargets.size()) {
                this.offsetStep = 0;
                break;
            }
            BlockPos offsetPos = new BlockPos((Vec3d)placeTargets.get(this.offsetStep));
            BlockPos targetPos = new BlockPos(this.closestTarget.getPositionVector()).down().add(offsetPos.x, offsetPos.y, offsetPos.z);
            this.placeList.add(targetPos);
            if (this.placeBlockInRange(targetPos, this.range.getValue().floatValue())) {
                ++blocksPlaced;
            }
            ++this.offsetStep;
        }
        if (blocksPlaced > 0) {
            if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
                AutoTrap.mc.player.inventory.currentItem = this.playerHotbarSlot;
                this.lastHotbarSlot = this.playerHotbarSlot;
            }
            if (this.isSneaking) {
                AutoTrap.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoTrap.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                this.isSneaking = false;
            }
        }
    }

    private boolean placeBlockInRange(BlockPos pos, double range) {
        Block block = AutoTrap.mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            this.placeList.remove((Object)pos);
            return false;
        }
        for (Entity entity : AutoTrap.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
            if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
            return false;
        }
        EnumFacing side = AutoTrap.getPlaceableSide(pos);
        if (side == null) {
            return false;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        if (!AutoTrap.canBeClicked(neighbour)) {
            return false;
        }
        Vec3d hitVec = new Vec3d((Vec3i)neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = AutoTrap.mc.world.getBlockState(neighbour).getBlock();
        if (AutoTrap.mc.player.getPositionVector().distanceTo(hitVec) > range) {
            return false;
        }
        int obiSlot = this.findObiInHotbar();
        if (obiSlot == -1) {
            this.disable();
        }
        if (this.lastHotbarSlot != obiSlot) {
            AutoTrap.mc.player.inventory.currentItem = obiSlot;
            this.lastHotbarSlot = obiSlot;
        }
        if (!this.isSneaking && blackList.contains((Object)neighbourBlock) || shulkerList.contains((Object)neighbourBlock)) {
            AutoTrap.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoTrap.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        }
        if (this.rotate.getValue().booleanValue()) {
            BlockUtil.faceVectorPacketInstant(hitVec);
        }
        AutoTrap.mc.playerController.processRightClickBlock(AutoTrap.mc.player, AutoTrap.mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        AutoTrap.mc.player.swingArm(EnumHand.MAIN_HAND);
        AutoTrap.mc.rightClickDelayTimer = 4;
        if (this.noGlitchBlocks.getValue().booleanValue() && !AutoTrap.mc.playerController.getCurrentGameType().equals((Object)GameType.CREATIVE)) {
            AutoTrap.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, neighbour, opposite));
        }
        return true;
    }

    private int findObiInHotbar() {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = AutoTrap.mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock) || !((block = ((ItemBlock)stack.getItem()).getBlock()) instanceof BlockObsidian)) continue;
            slot = i;
            break;
        }
        return slot;
    }

    private void findClosestTarget() {
        List playerList = AutoTrap.mc.world.playerEntities;
        this.closestTarget = null;
        for (EntityPlayer target : playerList) {
            if (target == AutoTrap.mc.player || Mod.friendManager.isFriend(target.getName()) || !EntityUtil.isLiving((Entity)target) || target.getDistance((Entity)AutoTrap.mc.player) > 7.0f || target.getHealth() <= 0.0f) continue;
            if (this.closestTarget == null) {
                this.closestTarget = target;
                continue;
            }
            if (AutoTrap.mc.player.getDistance((Entity)target) >= AutoTrap.mc.player.getDistance((Entity)this.closestTarget)) continue;
            this.closestTarget = target;
        }
    }

    @Override
    public String getDisplayInfo() {
        if (this.closestTarget != null) {
            return this.closestTarget.getName().toUpperCase();
        }
        return null;
    }

    private static class Offsets {
        private static final Vec3d[] TRAP = new Vec3d[]{new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 0.0)};
        private static final Vec3d[] TRAPTOP = new Vec3d[]{new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 0.0), new Vec3d(0.0, 4.0, 0.0)};
        private static final Vec3d[] TRAPFULLROOF = new Vec3d[]{new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(1.0, 3.0, 0.0), new Vec3d(0.0, 3.0, 1.0), new Vec3d(-1.0, 3.0, 0.0), new Vec3d(0.0, 3.0, 0.0)};
        private static final Vec3d[] TRAPFULLROOFTOP = new Vec3d[]{new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(1.0, 3.0, 0.0), new Vec3d(0.0, 3.0, 1.0), new Vec3d(-1.0, 3.0, 0.0), new Vec3d(0.0, 3.0, 0.0), new Vec3d(0.0, 4.0, 0.0)};
        private static final Vec3d[] CRYSTALEXA = new Vec3d[]{new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(-1.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 1.0), new Vec3d(1.0, 2.0, -1.0), new Vec3d(-1.0, 2.0, 1.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 0.0)};
        private static final Vec3d[] CRYSTAL = new Vec3d[]{new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(-1.0, 0.0, 1.0), new Vec3d(1.0, 0.0, -1.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(-1.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 1.0), new Vec3d(1.0, 1.0, -1.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(-1.0, 2.0, 1.0), new Vec3d(1.0, 2.0, -1.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 0.0)};
        private static final Vec3d[] CRYSTALFULLROOF = new Vec3d[]{new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(-1.0, 0.0, 1.0), new Vec3d(1.0, 0.0, -1.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(-1.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 1.0), new Vec3d(1.0, 1.0, -1.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(-1.0, 2.0, 1.0), new Vec3d(1.0, 2.0, -1.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(1.0, 3.0, 0.0), new Vec3d(0.0, 3.0, 1.0), new Vec3d(-1.0, 3.0, 0.0), new Vec3d(0.0, 3.0, 0.0)};

        private Offsets() {
        }
    }

    private static enum Cage {
        TRAP,
        TRAPTOP,
        TRAPFULLROOF,
        TRAPFULLROOFTOP,
        CRYSTALEXA,
        CRYSTAL,
        CRYSTALFULLROOF;

    }

    private static enum ESPMode {
        Solid,
        Outline,
        Full,
        None;

    }
}

