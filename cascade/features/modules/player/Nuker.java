/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.player;

import cascade.Mod;
import cascade.event.events.BlockEvent;
import cascade.event.events.UpdateWalkingPlayerEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.BlockUtil;
import cascade.util.InventoryUtil;
import cascade.util.MathUtil;
import cascade.util.Timer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Nuker
extends Module {
    public Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    public Setting<Float> distance = this.register(new Setting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(6.0f)));
    public Setting<Integer> blockPerTick = this.register(new Setting<Integer>("Blocks/Attack", 50, 1, 100));
    public Setting<Integer> delay = this.register(new Setting<Integer>("Delay/Attack", 50, 1, 1000));
    public Setting<Boolean> nuke = this.register(new Setting<Boolean>("Nuke", false));
    public Setting<Mode> mode = this.register(new Setting<Object>("Mode", (Object)Mode.Nuke, v -> this.nuke.getValue()));
    public Setting<Boolean> shulkers = this.register(new Setting<Boolean>("Shulkers", false));
    public Setting<Boolean> echests = this.register(new Setting<Boolean>("EChests", false));
    public Setting<Boolean> hoppers = this.register(new Setting<Boolean>("Hoppers", false));
    public Setting<Boolean> anvils = this.register(new Setting<Boolean>("Anvils", true));
    public Setting<Boolean> silentSwitch = this.register(new Setting<Boolean>("SilentSwitch", false));
    int oldSlot;
    boolean isMining;
    Timer timer = new Timer();
    Block selected;

    public Nuker() {
        super("Nuker", Module.Category.PLAYER, "Destroys blocks");
    }

    @Override
    public void onToggle() {
        this.selected = null;
        this.oldSlot = -1;
        this.timer.reset();
    }

    @SubscribeEvent
    public void onClickBlock(BlockEvent event) {
        Block block;
        if (event.getStage() == 3 && this.mode.getValue() != Mode.All && (block = Nuker.mc.world.getBlockState(event.pos).getBlock()) != null && block != this.selected) {
            this.selected = block;
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerPre(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.isEnabled()) {
            ArrayList<Object> blocklist;
            if (this.nuke.getValue().booleanValue()) {
                BlockPos pos = null;
                switch (this.mode.getValue()) {
                    case Selection: 
                    case Nuke: {
                        pos = this.getClosestBlockSelection();
                        break;
                    }
                    case All: {
                        pos = this.getClosestBlockAll();
                    }
                }
                if (pos != null) {
                    if (this.mode.getValue() != Mode.Nuke) {
                        if (this.rotate.getValue().booleanValue()) {
                            float[] angle = MathUtil.calcAngle(Nuker.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((double)((float)pos.getX() + 0.5f), (double)((float)pos.getY() + 0.5f), (double)((float)pos.getZ() + 0.5f)));
                            Mod.rotationManager.setPlayerRotations(angle[0], angle[1]);
                        }
                        if (this.canBreak(pos)) {
                            Nuker.mc.playerController.onPlayerDamageBlock(pos, Nuker.mc.player.getHorizontalFacing());
                            Nuker.mc.player.swingArm(EnumHand.MAIN_HAND);
                        }
                    } else {
                        for (int i = 0; i < this.blockPerTick.getValue(); ++i) {
                            pos = this.getClosestBlockSelection();
                            if (pos == null) continue;
                            if (this.rotate.getValue().booleanValue()) {
                                float[] angle2 = MathUtil.calcAngle(Nuker.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((double)((float)pos.getX() + 0.5f), (double)((float)pos.getY() + 0.5f), (double)((float)pos.getZ() + 0.5f)));
                                Mod.rotationManager.setPlayerRotations(angle2[0], angle2[1]);
                            }
                            if (!this.timer.passedMs(this.delay.getValue().intValue())) continue;
                            Nuker.mc.playerController.onPlayerDamageBlock(pos, Nuker.mc.player.getHorizontalFacing());
                            Nuker.mc.player.swingArm(EnumHand.MAIN_HAND);
                            this.timer.reset();
                        }
                    }
                }
            }
            if (this.shulkers.getValue().booleanValue()) {
                this.breakBlocks(BlockUtil.shulkerList);
            }
            if (this.echests.getValue().booleanValue()) {
                blocklist = new ArrayList<Block>();
                blocklist.add((Object)Blocks.ENDER_CHEST);
                this.breakBlocks(blocklist);
            }
            if (this.hoppers.getValue().booleanValue()) {
                blocklist = new ArrayList();
                blocklist.add((Object)((Block)Blocks.HOPPER));
                this.breakBlocks(blocklist);
            }
            if (this.anvils.getValue().booleanValue()) {
                blocklist = new ArrayList();
                blocklist.add((Object)Blocks.ANVIL);
                this.breakBlocks(blocklist);
            }
        }
    }

    public void breakBlocks(List<Block> blocks) {
        BlockPos pos = this.getNearestBlock(blocks);
        if (pos != null) {
            if (!this.isMining) {
                this.oldSlot = Nuker.mc.player.inventory.currentItem;
                this.isMining = true;
            }
            if (this.rotate.getValue().booleanValue()) {
                float[] angle = MathUtil.calcAngle(Nuker.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((double)((float)pos.getX() + 0.5f), (double)((float)pos.getY() + 0.5f), (double)((float)pos.getZ() + 0.5f)));
                Mod.rotationManager.setPlayerRotations(angle[0], angle[1]);
            }
            if (this.canBreak(pos)) {
                int pickSlot;
                if (this.silentSwitch.getValue().booleanValue() && (pickSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE)) != -1) {
                    InventoryUtil.SilentSwitchToSlot(pickSlot);
                }
                Nuker.mc.playerController.onPlayerDamageBlock(pos, Nuker.mc.player.getHorizontalFacing());
                Nuker.mc.player.swingArm(EnumHand.MAIN_HAND);
                if (this.silentSwitch.getValue().booleanValue() && this.oldSlot != -1) {
                    Nuker.mc.player.inventory.currentItem = this.oldSlot;
                    Nuker.mc.playerController.updateController();
                    this.oldSlot = -1;
                    this.isMining = false;
                }
            }
        }
    }

    private boolean canBreak(BlockPos pos) {
        IBlockState blockState = Nuker.mc.world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, (World)Nuker.mc.world, pos) != -1.0f;
    }

    private BlockPos getNearestBlock(List<Block> blocks) {
        double maxDist = MathUtil.square(this.distance.getValue().floatValue());
        BlockPos ret = null;
        for (double x = maxDist; x >= -maxDist; x -= 1.0) {
            for (double y = maxDist; y >= -maxDist; y -= 1.0) {
                for (double z = maxDist; z >= -maxDist; z -= 1.0) {
                    BlockPos pos = new BlockPos(Nuker.mc.player.posX + x, Nuker.mc.player.posY + y, Nuker.mc.player.posZ + z);
                    double dist = Nuker.mc.player.getDistanceSq((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
                    if (!(dist <= maxDist) || !blocks.contains((Object)Nuker.mc.world.getBlockState(pos).getBlock()) || !this.canBreak(pos)) continue;
                    maxDist = dist;
                    ret = pos;
                }
            }
        }
        return ret;
    }

    private BlockPos getClosestBlockAll() {
        float maxDist = this.distance.getValue().floatValue();
        BlockPos ret = null;
        for (float x = maxDist; x >= -maxDist; x -= 1.0f) {
            for (float y = maxDist; y >= -maxDist; y -= 1.0f) {
                for (float z = maxDist; z >= -maxDist; z -= 1.0f) {
                    BlockPos pos = new BlockPos(Nuker.mc.player.posX + (double)x, Nuker.mc.player.posY + (double)y, Nuker.mc.player.posZ + (double)z);
                    double dist = Nuker.mc.player.getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
                    if (!(dist <= (double)maxDist) || Nuker.mc.world.getBlockState(pos).getBlock() == Blocks.AIR || Nuker.mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid || !this.canBreak(pos) || !((double)pos.getY() >= Nuker.mc.player.posY)) continue;
                    maxDist = (float)dist;
                    ret = pos;
                }
            }
        }
        return ret;
    }

    private BlockPos getClosestBlockSelection() {
        float maxDist = this.distance.getValue().floatValue();
        BlockPos ret = null;
        for (float x = maxDist; x >= -maxDist; x -= 1.0f) {
            for (float y = maxDist; y >= -maxDist; y -= 1.0f) {
                for (float z = maxDist; z >= -maxDist; z -= 1.0f) {
                    BlockPos pos = new BlockPos(Nuker.mc.player.posX + (double)x, Nuker.mc.player.posY + (double)y, Nuker.mc.player.posZ + (double)z);
                    double dist = Nuker.mc.player.getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
                    if (!(dist <= (double)maxDist) || Nuker.mc.world.getBlockState(pos).getBlock() == Blocks.AIR || Nuker.mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid || Nuker.mc.world.getBlockState(pos).getBlock() != this.selected || !this.canBreak(pos) || !((double)pos.getY() >= Nuker.mc.player.posY)) continue;
                    maxDist = (float)dist;
                    ret = pos;
                }
            }
        }
        return ret;
    }

    public static enum Mode {
        Selection,
        All,
        Nuke;

    }
}

