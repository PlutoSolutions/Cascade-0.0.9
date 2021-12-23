/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockAir
 *  net.minecraft.block.BlockDeadBush
 *  net.minecraft.block.BlockFire
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.BlockSnow
 *  net.minecraft.block.BlockTallGrass
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.item.EntityXPOrb
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketHeldItemChange
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 */
package cascade.features.modules.combat;

import cascade.Mod;
import cascade.features.command.Command;
import cascade.features.modules.Module;
import cascade.features.modules.combat.AutoCrystal;
import cascade.features.setting.Setting;
import cascade.util.BlockUtil;
import cascade.util.EntityUtil;
import cascade.util.InventoryUtil;
import cascade.util.Timer;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Surround
extends Module {
    public Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 50, 0, 250));
    public Setting<Center> center = this.register(new Setting<Center>("Center", Center.None));
    public Setting<Boolean> helpingBlocks = this.register(new Setting<Boolean>("Help", true));
    public Setting<Boolean> antiPedo = this.register(new Setting<Boolean>("AlwaysHelp", false));
    public Setting<Integer> extender = this.register(new Setting<Integer>("Extend", 1, 0, 4));
    public Setting<Boolean> allowEC = this.register(new Setting<Boolean>("AllowEChests", true));
    public Setting<Boolean> attack = this.register(new Setting<Boolean>("Attack", true));
    public Setting<Integer> retry = this.register(new Setting<Integer>("Retry", 4, 1, 15));
    Map<BlockPos, Integer> retries = new HashMap<BlockPos, Integer>();
    Set<Vec3d> extendingBlocks = new HashSet<Vec3d>();
    Timer retryTimer = new Timer();
    Vec3d CenterPos = Vec3d.ZERO;
    Timer timer = new Timer();
    boolean didPlace = false;
    boolean placing = false;
    int placements = 0;
    double enablePosY;
    int obbySlot = -1;
    int extenders = 1;

    public Surround() {
        super("Surround", Module.Category.COMBAT, "Surrounds you with blocks");
    }

    @Override
    public void onEnable() {
        if (Surround.fullNullCheck()) {
            return;
        }
        this.enablePosY = Surround.mc.player.posY;
        this.retries.clear();
        this.retryTimer.reset();
        this.CenterPos = EntityUtil.getCenter(Surround.mc.player.posX, Surround.mc.player.posY, Surround.mc.player.posZ);
        if (!EntityUtil.isPlayerSafe((EntityPlayer)Surround.mc.player)) {
            switch (this.center.getValue()) {
                case Instant: {
                    Mod.movement.setMotion(0.0, 0.0, 0.0);
                    mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(this.CenterPos.x, this.CenterPos.y, this.CenterPos.z, true));
                    Surround.mc.player.setPosition(this.CenterPos.x, this.CenterPos.y, this.CenterPos.z);
                    break;
                }
                case NCP: {
                    Mod.movement.setMotion((this.CenterPos.x - Surround.mc.player.posX) / 2.0, Surround.mc.player.motionY, (this.CenterPos.z - Surround.mc.player.posZ) / 2.0);
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        boolean onEChest;
        if (this.check() || Surround.fullNullCheck()) {
            return;
        }
        boolean bl = onEChest = Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.ENDER_CHEST;
        if (Surround.mc.player.posY - (double)((int)Surround.mc.player.posY) < 0.7) {
            onEChest = false;
        }
        if (!this.isSafe((Entity)Surround.mc.player, onEChest ? 1 : 0)) {
            this.placeBlocks(Surround.mc.player.getPositionVector(), Surround.getUnsafeBlockArray((Entity)Surround.mc.player, onEChest ? 1 : 0), this.helpingBlocks.getValue(), false, false);
        } else if (!this.isSafe((Entity)Surround.mc.player, onEChest ? 0 : -1) && this.antiPedo.getValue().booleanValue()) {
            this.placeBlocks(Surround.mc.player.getPositionVector(), Surround.getUnsafeBlockArray((Entity)Surround.mc.player, onEChest ? 0 : -1), false, false, true);
        }
        this.processExtendingBlocks();
        if (this.didPlace) {
            this.timer.reset();
        }
    }

    @Override
    public String getDisplayInfo() {
        return EntityUtil.isSafe((Entity)Surround.mc.player) ? (Object)ChatFormatting.GREEN + "Safe" : (Object)ChatFormatting.RED + "Unsafe";
    }

    public boolean isSafe(Entity entity, int height) {
        return Surround.getUnsafeBlocks(entity, height).size() == 0;
    }

    @Override
    public void onDisable() {
        this.placing = false;
    }

    public static Vec3d[] getUnsafeBlockArray(Entity entity, int height) {
        List<Vec3d> list = Surround.getUnsafeBlocks(entity, height);
        Vec3d[] array = new Vec3d[list.size()];
        return list.toArray((T[])array);
    }

    public static List<Vec3d> getUnsafeBlocks(Entity entity, int height) {
        return Surround.getUnsafeBlocksFromVec3d(entity.getPositionVector(), height);
    }

    private void processExtendingBlocks() {
        if (this.extendingBlocks.size() == 2 && this.extenders < this.extender.getValue()) {
            Vec3d[] array = new Vec3d[2];
            int i = 0;
            Iterator<Vec3d> iterator = this.extendingBlocks.iterator();
            while (iterator.hasNext()) {
                Vec3d vec3d;
                array[i] = vec3d = iterator.next();
                ++i;
            }
            if (this.attack.getValue().booleanValue()) {
                BlockUtil.doBreak(new BlockPos(this.areClose((Vec3d[])array).x, this.areClose((Vec3d[])array).y, this.areClose((Vec3d[])array).z), AutoCrystal.getInstance().maxSelfDamage.getValue().floatValue());
            }
            int placementsBefore = this.placements;
            if (this.areClose(array) != null) {
                this.placeBlocks(this.areClose(array), Surround.getUnsafeBlockArrayFromVec3d(this.areClose(array), 0), this.helpingBlocks.getValue(), false, true);
            }
            if (placementsBefore < this.placements) {
                this.extendingBlocks.clear();
            }
        } else if (this.extendingBlocks.size() > 2 || this.extenders >= this.extender.getValue()) {
            this.extendingBlocks.clear();
        }
    }

    public static Vec3d[] getUnsafeBlockArrayFromVec3d(Vec3d pos, int height) {
        List<Vec3d> list = Surround.getUnsafeBlocksFromVec3d(pos, height);
        Vec3d[] array = new Vec3d[list.size()];
        return list.toArray((T[])array);
    }

    private Vec3d areClose(Vec3d[] vec3ds) {
        int matches = 0;
        for (Vec3d vec3d : vec3ds) {
            for (Vec3d pos : Surround.getUnsafeBlockArray((Entity)Surround.mc.player, 0)) {
                if (!vec3d.equals((Object)pos)) continue;
                ++matches;
            }
        }
        if (matches == 2) {
            return Surround.mc.player.getPositionVector().add(vec3ds[0].add(vec3ds[1]));
        }
        return null;
    }

    private boolean placeBlocks(Vec3d pos, Vec3d[] vec3ds, boolean hasHelpingBlocks, boolean isHelping, boolean isExtending) {
        int helpings = 0;
        int lastSlot = Surround.mc.player.inventory.currentItem;
        mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(this.obbySlot));
        block6: for (Vec3d vec3d : vec3ds) {
            boolean gotHelp = true;
            if (isHelping && ++helpings > 1) {
                return false;
            }
            BlockPos position = new BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z);
            switch (Surround.isPositionPlaceable(position)) {
                case -1: {
                    continue block6;
                }
                case 1: {
                    if (this.retries.get((Object)position) == null || this.retries.get((Object)position) < this.retry.getValue()) {
                        this.placeBlock(position);
                        this.retries.put(position, this.retries.get((Object)position) == null ? 1 : this.retries.get((Object)position) + 1);
                        this.retryTimer.reset();
                        continue block6;
                    }
                    if (this.extender.getValue() <= 0 || isExtending || this.extenders >= this.extender.getValue()) continue block6;
                    this.placeBlocks(Surround.mc.player.getPositionVector().add(vec3d), Surround.getUnsafeBlockArrayFromVec3d(Surround.mc.player.getPositionVector().add(vec3d), 0), hasHelpingBlocks, false, true);
                    this.extendingBlocks.add(vec3d);
                    ++this.extenders;
                    continue block6;
                }
                case 2: {
                    if (!hasHelpingBlocks) continue block6;
                    gotHelp = this.placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);
                }
                case 3: {
                    if (gotHelp) {
                        this.placeBlock(position);
                    }
                    if (!isHelping) continue block6;
                    return true;
                }
            }
        }
        mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(lastSlot));
        return false;
    }

    private boolean check() {
        this.placing = false;
        this.didPlace = false;
        this.extenders = 1;
        this.placements = 0;
        this.obbySlot = InventoryUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
        int echestSlot = InventoryUtil.getBlockFromHotbar(Blocks.ENDER_CHEST);
        if (this.isDisabled()) {
            return true;
        }
        if (Surround.mc.player.posY > this.enablePosY) {
            this.disable();
            return true;
        }
        if (this.retryTimer.passedMs(100L)) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        if (this.obbySlot == -1) {
            this.obbySlot = echestSlot;
            if (!this.allowEC.getValue().booleanValue() || echestSlot == -1) {
                Command.sendMessage("Out of blocks, disabling " + (Object)ChatFormatting.RED + "Surround", true, true);
                this.disable();
                return true;
            }
        }
        return !this.timer.passedMs(this.delay.getValue().intValue());
    }

    private void placeBlock(BlockPos pos) {
        if (this.placements < 20) {
            this.placing = true;
            BlockUtil.placeBlock(pos);
            this.didPlace = true;
            ++this.placements;
        }
    }

    public static int isPositionPlaceable(BlockPos pos) {
        Block block = Surround.mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow)) {
            return 0;
        }
        for (Entity entity : Surround.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
            return 1;
        }
        if (BlockUtil.isInterceptedByCrystal(pos)) {
            return 1;
        }
        for (EnumFacing side : Surround.getPossibleSides(pos)) {
            if (!Surround.canBeClicked(pos.offset(side))) continue;
            return 3;
        }
        return 2;
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        ArrayList<EnumFacing> facings = new ArrayList<EnumFacing>(6);
        for (EnumFacing side : EnumFacing.values()) {
            IBlockState blockState;
            BlockPos neighbour = pos.offset(side);
            if (!Surround.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(Surround.mc.world.getBlockState(neighbour), false) || (blockState = Surround.mc.world.getBlockState(neighbour)).getMaterial().isReplaceable()) continue;
            facings.add(side);
        }
        return facings;
    }

    public static boolean canBeClicked(BlockPos pos) {
        return Surround.mc.world.getBlockState(pos).getBlock().canCollideCheck(Surround.mc.world.getBlockState(pos), false);
    }

    public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height) {
        ArrayList<Vec3d> vec3ds = new ArrayList<Vec3d>(4);
        for (Vec3d vector : Surround.getOffsets(height)) {
            BlockPos targetPos = new BlockPos(pos).add(vector.x, vector.y, vector.z);
            Block block = Surround.mc.world.getBlockState(targetPos).getBlock();
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) continue;
            vec3ds.add(vector);
        }
        return vec3ds;
    }

    public static Vec3d[] getOffsets(int y) {
        List<Vec3d> offsets = Surround.getOffsetList(y);
        Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray((T[])array);
    }

    public static List<Vec3d> getOffsetList(int y) {
        ArrayList<Vec3d> offsets = new ArrayList<Vec3d>(4);
        offsets.add(new Vec3d(-1.0, (double)y, 0.0));
        offsets.add(new Vec3d(1.0, (double)y, 0.0));
        offsets.add(new Vec3d(0.0, (double)y, -1.0));
        offsets.add(new Vec3d(0.0, (double)y, 1.0));
        return offsets;
    }

    public static enum Center {
        None,
        Instant,
        NCP;

    }
}

