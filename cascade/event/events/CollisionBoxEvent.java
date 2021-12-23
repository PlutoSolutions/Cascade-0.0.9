/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package cascade.event.events;

import cascade.event.EventStage;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CollisionBoxEvent
extends EventStage {
    private static CollisionBoxEvent INSTANCE = new CollisionBoxEvent();
    private Block block;
    private IBlockState state;
    private World world;
    private BlockPos pos;
    private AxisAlignedBB entityBox;
    private List<AxisAlignedBB> collidingBoxes;
    private Entity entity;
    private boolean isActualState;

    public static CollisionBoxEvent get(Block block, IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
        INSTANCE.setCanceled(false);
        CollisionBoxEvent.INSTANCE.block = block;
        CollisionBoxEvent.INSTANCE.state = state;
        CollisionBoxEvent.INSTANCE.world = worldIn;
        CollisionBoxEvent.INSTANCE.pos = pos;
        CollisionBoxEvent.INSTANCE.entityBox = entityBox;
        CollisionBoxEvent.INSTANCE.collidingBoxes = collidingBoxes;
        CollisionBoxEvent.INSTANCE.entity = entityIn;
        CollisionBoxEvent.INSTANCE.isActualState = isActualState;
        return INSTANCE;
    }

    public Block getBlock() {
        return this.block;
    }

    public IBlockState getState() {
        return this.state;
    }

    public World getWorld() {
        return this.world;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public AxisAlignedBB getEntityBox() {
        return this.entityBox;
    }

    public void setEntityBox(AxisAlignedBB entityBox) {
        this.entityBox = entityBox;
    }

    public List<AxisAlignedBB> getCollidingBoxes() {
        return this.collidingBoxes;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public boolean isActualState() {
        return this.isActualState;
    }
}

