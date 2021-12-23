/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 */
package cascade.features.modules.player;

import cascade.features.modules.Module;
import cascade.util.BlockUtil;
import cascade.util.EntityUtil;
import cascade.util.InventoryUtil;
import cascade.util.RotationUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class SelfAnvil
extends Module {
    int blockSlot;
    BlockPos pos;
    BlockPos upperBlockPos = null;
    BlockPos baseBlockPos = null;
    BlockPos anvilBlockPos = null;

    public SelfAnvil() {
        super("SelfAnvil", Module.Category.PLAYER, "Places an anvil over your head");
    }

    @Override
    public void onDisable() {
        this.upperBlockPos = null;
        this.baseBlockPos = null;
        this.anvilBlockPos = null;
    }

    @Override
    public void onUpdate() {
        if (SelfAnvil.fullNullCheck()) {
            return;
        }
        this.doAnvil();
    }

    public void doAnvil() {
        BlockPos pos;
        this.pos = pos = EntityUtil.getPlayerPos((EntityPlayer)SelfAnvil.mc.player);
        int anvilSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock((Block)Blocks.ANVIL));
        if (!EntityUtil.isPlayerSafe((EntityPlayer)SelfAnvil.mc.player)) {
            return;
        }
        if (!SelfAnvil.mc.player.onGround) {
            return;
        }
        if (anvilSlot == -1) {
            return;
        }
        this.blockSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock((Block)Blocks.ANVIL));
        if (this.blockSlot == -1) {
            return;
        }
        if (!this.hasBaseBlocks()) {
            this.placeBaseBlocks(this.blockSlot, false, true, true, EnumHand.MAIN_HAND);
        }
        this.placeUpperBaseBlocks(this.blockSlot, false, true, true, EnumHand.MAIN_HAND);
        if (!(SelfAnvil.mc.world.getBlockState(pos.north().up().up()).getBlock().equals((Object)Blocks.AIR) && SelfAnvil.mc.world.getBlockState(pos.east().up().up()).getBlock().equals((Object)Blocks.AIR) && SelfAnvil.mc.world.getBlockState(pos.south().up().up()).getBlock().equals((Object)Blocks.AIR) && SelfAnvil.mc.world.getBlockState(pos.west().up().up()).getBlock().equals((Object)Blocks.AIR))) {
            this.placeAnvil(anvilSlot, false, true, true, EnumHand.MAIN_HAND);
            this.disable();
        }
    }

    boolean hasBaseBlocks() {
        BlockPos pos = EntityUtil.getPlayerPos((EntityPlayer)SelfAnvil.mc.player);
        if (!SelfAnvil.mc.world.getBlockState(pos.north().up()).getBlock().equals((Object)Blocks.AIR)) {
            return true;
        }
        if (!SelfAnvil.mc.world.getBlockState(pos.east().up()).getBlock().equals((Object)Blocks.AIR)) {
            return true;
        }
        if (!SelfAnvil.mc.world.getBlockState(pos.south().up()).getBlock().equals((Object)Blocks.AIR)) {
            return true;
        }
        return !SelfAnvil.mc.world.getBlockState(pos.west().up()).getBlock().equals((Object)Blocks.AIR);
    }

    void placeAnvil(int slot, boolean rotate, boolean packet, boolean swing, EnumHand hand) {
        BlockPos pos = EntityUtil.getPlayerPos((EntityPlayer)SelfAnvil.mc.player);
        int oldSlot = SelfAnvil.mc.player.inventory.currentItem;
        this.anvilBlockPos = pos.up().up();
        InventoryUtil.SilentSwitchToSlot(slot);
        SelfAnvil.placeBlock(pos.up().up(), EnumHand.MAIN_HAND, rotate, packet, false, swing, hand);
        SelfAnvil.mc.player.inventory.currentItem = oldSlot;
        SelfAnvil.mc.playerController.updateController();
    }

    void placeBaseBlocks(int slot, boolean rotate, boolean packet, boolean swing, EnumHand hand) {
        BlockPos pos = EntityUtil.getPlayerPos((EntityPlayer)SelfAnvil.mc.player);
        int oldSlot = SelfAnvil.mc.player.inventory.currentItem;
        if (this.getSide() == 1) {
            this.baseBlockPos = pos.up().north();
            InventoryUtil.SilentSwitchToSlot(slot);
            SelfAnvil.placeBlock(pos.up().north(), EnumHand.MAIN_HAND, rotate, packet, false, swing, hand);
            SelfAnvil.mc.player.inventory.currentItem = oldSlot;
            SelfAnvil.mc.playerController.updateController();
        } else if (this.getSide() == 2) {
            this.baseBlockPos = pos.up().east();
            InventoryUtil.SilentSwitchToSlot(slot);
            SelfAnvil.placeBlock(pos.up().east(), EnumHand.MAIN_HAND, rotate, packet, false, swing, hand);
            SelfAnvil.mc.player.inventory.currentItem = oldSlot;
            SelfAnvil.mc.playerController.updateController();
        } else if (this.getSide() == 3) {
            this.baseBlockPos = pos.up().south();
            InventoryUtil.SilentSwitchToSlot(slot);
            SelfAnvil.placeBlock(pos.up().south(), EnumHand.MAIN_HAND, rotate, packet, false, swing, hand);
            SelfAnvil.mc.player.inventory.currentItem = oldSlot;
            SelfAnvil.mc.playerController.updateController();
        } else if (this.getSide() == 4) {
            this.baseBlockPos = pos.up().west();
            InventoryUtil.SilentSwitchToSlot(slot);
            SelfAnvil.placeBlock(pos.up().west(), EnumHand.MAIN_HAND, rotate, packet, false, swing, hand);
            SelfAnvil.mc.player.inventory.currentItem = oldSlot;
            SelfAnvil.mc.playerController.updateController();
        }
    }

    void placeUpperBaseBlocks(int slot, boolean rotate, boolean packet, boolean swing, EnumHand hand) {
        BlockPos pos = EntityUtil.getPlayerPos((EntityPlayer)SelfAnvil.mc.player);
        int oldSlot = SelfAnvil.mc.player.inventory.currentItem;
        if (this.getUpperSide() == 1) {
            if (!SelfAnvil.mc.world.getBlockState(pos.up().north()).getBlock().equals((Object)Blocks.AIR)) {
                this.upperBlockPos = pos.up().up().north();
                InventoryUtil.SilentSwitchToSlot(slot);
                SelfAnvil.placeBlock(pos.up().up().north(), EnumHand.MAIN_HAND, rotate, packet, false, swing, hand);
                SelfAnvil.mc.player.inventory.currentItem = oldSlot;
                SelfAnvil.mc.playerController.updateController();
            }
        } else if (this.getUpperSide() == 2) {
            if (!SelfAnvil.mc.world.getBlockState(pos.up().east()).getBlock().equals((Object)Blocks.AIR)) {
                this.upperBlockPos = pos.up().up().east();
                InventoryUtil.SilentSwitchToSlot(slot);
                SelfAnvil.placeBlock(pos.up().up().east(), EnumHand.MAIN_HAND, rotate, packet, false, swing, hand);
                SelfAnvil.mc.player.inventory.currentItem = oldSlot;
                SelfAnvil.mc.playerController.updateController();
            }
        } else if (this.getUpperSide() == 3) {
            if (!SelfAnvil.mc.world.getBlockState(pos.up().south()).getBlock().equals((Object)Blocks.AIR)) {
                this.upperBlockPos = pos.up().up().south();
                InventoryUtil.SilentSwitchToSlot(slot);
                SelfAnvil.placeBlock(pos.up().up().south(), EnumHand.MAIN_HAND, rotate, packet, false, swing, hand);
                SelfAnvil.mc.player.inventory.currentItem = oldSlot;
                SelfAnvil.mc.playerController.updateController();
            }
        } else if (this.getUpperSide() == 4 && !SelfAnvil.mc.world.getBlockState(pos.up().west()).getBlock().equals((Object)Blocks.AIR)) {
            this.upperBlockPos = pos.up().up().west();
            InventoryUtil.SilentSwitchToSlot(slot);
            SelfAnvil.placeBlock(pos.up().up().west(), EnumHand.MAIN_HAND, rotate, packet, false, swing, hand);
            SelfAnvil.mc.player.inventory.currentItem = oldSlot;
            SelfAnvil.mc.playerController.updateController();
        }
    }

    int getSide() {
        BlockPos pos = EntityUtil.getPlayerPos((EntityPlayer)SelfAnvil.mc.player);
        boolean placedNorth = false;
        boolean placedEast = false;
        boolean placedSouth = false;
        boolean placedWest = false;
        if (!SelfAnvil.mc.world.getBlockState(pos.north().up()).getBlock().equals((Object)Blocks.AIR) && !SelfAnvil.mc.world.getBlockState(pos.north().up().up()).getBlock().equals((Object)Blocks.AIR)) {
            placedNorth = true;
        }
        if (!SelfAnvil.mc.world.getBlockState(pos.east().up()).getBlock().equals((Object)Blocks.AIR) && !SelfAnvil.mc.world.getBlockState(pos.east().up().up()).getBlock().equals((Object)Blocks.AIR)) {
            placedEast = true;
        }
        if (!SelfAnvil.mc.world.getBlockState(pos.south().up()).getBlock().equals((Object)Blocks.AIR) && !SelfAnvil.mc.world.getBlockState(pos.south().up().up()).getBlock().equals((Object)Blocks.AIR)) {
            placedSouth = true;
        }
        if (!SelfAnvil.mc.world.getBlockState(pos.west().up()).getBlock().equals((Object)Blocks.AIR) && !SelfAnvil.mc.world.getBlockState(pos.west().up().up()).getBlock().equals((Object)Blocks.AIR)) {
            placedWest = true;
        }
        if (SelfAnvil.mc.world.getBlockState(pos.north().up()).getBlock().equals((Object)Blocks.AIR) && SelfAnvil.mc.world.getBlockState(pos.north().up().up()).getBlock().equals((Object)Blocks.AIR) && !placedNorth && !placedEast && !placedSouth && !placedWest) {
            return 1;
        }
        if (SelfAnvil.mc.world.getBlockState(pos.east().up()).getBlock().equals((Object)Blocks.AIR) && SelfAnvil.mc.world.getBlockState(pos.east().up().up()).getBlock().equals((Object)Blocks.AIR) && !placedNorth && !placedEast && !placedSouth && !placedWest) {
            return 2;
        }
        if (SelfAnvil.mc.world.getBlockState(pos.south().up()).getBlock().equals((Object)Blocks.AIR) && SelfAnvil.mc.world.getBlockState(pos.south().up().up()).getBlock().equals((Object)Blocks.AIR) && !placedNorth && !placedEast && !placedSouth && !placedWest) {
            return 3;
        }
        if (SelfAnvil.mc.world.getBlockState(pos.west().up()).getBlock().equals((Object)Blocks.AIR) && SelfAnvil.mc.world.getBlockState(pos.west().up().up()).getBlock().equals((Object)Blocks.AIR) && !placedNorth && !placedEast && !placedSouth && !placedWest) {
            return 4;
        }
        return 0;
    }

    int getUpperSide() {
        BlockPos pos = EntityUtil.getPlayerPos((EntityPlayer)SelfAnvil.mc.player);
        boolean placedNorth = false;
        boolean placedEast = false;
        boolean placedSouth = false;
        boolean placedWest = false;
        if (!SelfAnvil.mc.world.getBlockState(pos.north().up()).getBlock().equals((Object)Blocks.AIR) && !SelfAnvil.mc.world.getBlockState(pos.north().up().up()).getBlock().equals((Object)Blocks.AIR)) {
            placedNorth = true;
        }
        if (!SelfAnvil.mc.world.getBlockState(pos.east().up()).getBlock().equals((Object)Blocks.AIR) && !SelfAnvil.mc.world.getBlockState(pos.east().up().up()).getBlock().equals((Object)Blocks.AIR)) {
            placedEast = true;
        }
        if (!SelfAnvil.mc.world.getBlockState(pos.south().up()).getBlock().equals((Object)Blocks.AIR) && !SelfAnvil.mc.world.getBlockState(pos.south().up().up()).getBlock().equals((Object)Blocks.AIR)) {
            placedSouth = true;
        }
        if (!SelfAnvil.mc.world.getBlockState(pos.west().up()).getBlock().equals((Object)Blocks.AIR) && !SelfAnvil.mc.world.getBlockState(pos.west().up().up()).getBlock().equals((Object)Blocks.AIR)) {
            placedWest = true;
        }
        if (!(!SelfAnvil.mc.world.getBlockState(pos.north().up().up()).getBlock().equals((Object)Blocks.AIR) || SelfAnvil.mc.world.getBlockState(pos.north().up()).getBlock().equals((Object)Blocks.AIR) || placedNorth || placedEast || placedSouth || placedWest)) {
            return 1;
        }
        if (!(!SelfAnvil.mc.world.getBlockState(pos.east().up().up()).getBlock().equals((Object)Blocks.AIR) || SelfAnvil.mc.world.getBlockState(pos.east().up()).getBlock().equals((Object)Blocks.AIR) || placedNorth || placedEast || placedSouth || placedWest)) {
            return 2;
        }
        if (!(!SelfAnvil.mc.world.getBlockState(pos.south().up().up()).getBlock().equals((Object)Blocks.AIR) || SelfAnvil.mc.world.getBlockState(pos.south().up()).getBlock().equals((Object)Blocks.AIR) || placedNorth || placedEast || placedSouth || placedWest)) {
            return 3;
        }
        if (!(!SelfAnvil.mc.world.getBlockState(pos.west().up().up()).getBlock().equals((Object)Blocks.AIR) || SelfAnvil.mc.world.getBlockState(pos.west().up()).getBlock().equals((Object)Blocks.AIR) || placedNorth || placedEast || placedSouth || placedWest)) {
            return 4;
        }
        return 0;
    }

    public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking, boolean swing, EnumHand renderHand) {
        boolean sneaking = false;
        EnumFacing side = BlockUtil.getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d((Vec3i)neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = SelfAnvil.mc.world.getBlockState(neighbour).getBlock();
        if (!SelfAnvil.mc.player.isSneaking() && (BlockUtil.blackList.contains((Object)neighbourBlock) || BlockUtil.shulkerList.contains((Object)neighbourBlock))) {
            SelfAnvil.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)SelfAnvil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            SelfAnvil.mc.player.setSneaking(true);
            sneaking = true;
        }
        if (rotate) {
            RotationUtil.faceVector(hitVec, true);
        }
        BlockUtil.rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        if (swing) {
            SelfAnvil.mc.player.swingArm(renderHand);
        }
        SelfAnvil.mc.rightClickDelayTimer = 4;
        return sneaking || isSneaking;
    }
}

