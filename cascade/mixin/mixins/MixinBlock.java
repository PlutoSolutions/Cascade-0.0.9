/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 */
package cascade.mixin.mixins;

import cascade.features.modules.visual.Wallhack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Block.class})
public class MixinBlock {
    @Inject(method={"shouldSideBeRendered"}, at={@At(value="HEAD")}, cancellable=true)
    public void shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> info) {
        if (Wallhack.INSTANCE.isEnabled()) {
            info.setReturnValue(true);
        }
    }

    @Inject(method={"getRenderLayer"}, at={@At(value="HEAD")}, cancellable=true)
    public void getRenderLayer(CallbackInfoReturnable<BlockRenderLayer> info) {
        if (Wallhack.INSTANCE.isEnabled() && !Wallhack.blocks.contains(this)) {
            info.setReturnValue(BlockRenderLayer.TRANSLUCENT);
        }
    }

    @Inject(method={"getLightValue"}, at={@At(value="HEAD")}, cancellable=true)
    public void getLightValue(CallbackInfoReturnable<Integer> info) {
        if (Wallhack.INSTANCE.isEnabled()) {
            info.setReturnValue(Wallhack.INSTANCE.light.getValue());
        }
    }
}

