/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package cascade.manager;

import cascade.features.Feature;
import cascade.util.BlockUtil;
import cascade.util.EntityUtil;
import cascade.util.Timer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class HoleManager
extends Feature {
    private static final BlockPos[] surroundOffset = BlockUtil.toBlockPos(EntityUtil.getOffsets(0, true));
    private List<BlockPos> holes = new ArrayList<BlockPos>();
    private final List<BlockPos> midSafety = new ArrayList<BlockPos>();
    private ScheduledExecutorService executorService;
    private int lastUpdates = 0;
    private Thread thread;
    private final AtomicBoolean shouldInterrupt = new AtomicBoolean(false);
    private final Timer holeTimer = new Timer();

    public void update() {
        if (!HoleManager.fullNullCheck()) {
            this.holes = this.calcHoles();
        }
    }

    public List<BlockPos> getHoles() {
        return this.holes;
    }

    public List<BlockPos> getMidSafety() {
        return this.midSafety;
    }

    public List<BlockPos> getSortedHoles() {
        this.holes.sort(Comparator.comparingDouble(hole -> HoleManager.mc.player.getDistanceSq(hole)));
        return this.getHoles();
    }

    public List<BlockPos> calcHoles() {
        ArrayList<BlockPos> safeSpots = new ArrayList<BlockPos>();
        this.midSafety.clear();
        List<BlockPos> positions = BlockUtil.getSphere(EntityUtil.getPlayerPos((EntityPlayer)HoleManager.mc.player), 6.0f, 6, false, true, 0);
        for (BlockPos pos : positions) {
            if (!HoleManager.mc.world.getBlockState(pos).getBlock().equals((Object)Blocks.AIR) || !HoleManager.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals((Object)Blocks.AIR) || !HoleManager.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals((Object)Blocks.AIR)) continue;
            boolean isSafe = true;
            boolean midSafe = true;
            for (BlockPos offset : surroundOffset) {
                Block block = HoleManager.mc.world.getBlockState(pos.add((Vec3i)offset)).getBlock();
                if (BlockUtil.isBlockUnSolid(block)) {
                    midSafe = false;
                }
                if (block == Blocks.BEDROCK || block == Blocks.OBSIDIAN || block == Blocks.ENDER_CHEST || block == Blocks.ANVIL) continue;
                isSafe = false;
            }
            if (isSafe) {
                safeSpots.add(pos);
            }
            if (!midSafe) continue;
            this.midSafety.add(pos);
        }
        return safeSpots;
    }

    public boolean isSafe(BlockPos pos) {
        boolean isSafe = true;
        for (BlockPos offset : surroundOffset) {
            Block block = HoleManager.mc.world.getBlockState(pos.add((Vec3i)offset)).getBlock();
            if (block == Blocks.BEDROCK) continue;
            isSafe = false;
            break;
        }
        return isSafe;
    }
}

