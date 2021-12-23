/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ChunkRenderContainer
 *  net.minecraft.client.renderer.RenderGlobal
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.RayTraceResult
 */
package cascade.mixin.mixins;

import cascade.Mod;
import cascade.features.modules.movement.YPort;
import cascade.util.Util;
import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderGlobal.class})
public class MixinRenderGlobal {
    @Inject(method={"drawSelectionBox"}, at={@At(value="HEAD")}, cancellable=true)
    public void drawSelectionBox(EntityPlayer player, RayTraceResult movingObjectPositionIn, int execute, float partialTicks, CallbackInfo callbackInfo) {
        if (Mod.moduleManager.isModuleEnabled("BlockHighlight")) {
            callbackInfo.cancel();
        }
    }

    @Redirect(method={"setupTerrain"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/ChunkRenderContainer;initialize(DDD)V"))
    public void initializeHook(ChunkRenderContainer chunkRenderContainer, double viewEntityXIn, double viewEntityYIn, double viewEntityZIn) {
        double y = viewEntityYIn;
        if (YPort.getInstance().isEnabled() && YPort.getInstance().noShake.getValue().booleanValue() && !Util.mc.player.isRiding()) {
            y = YPort.getInstance().startY;
        }
        chunkRenderContainer.initialize(viewEntityXIn, y, viewEntityZIn);
    }

    @Redirect(method={"renderEntities"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/entity/RenderManager;setRenderPosition(DDD)V"))
    public void setRenderPositionHook(RenderManager renderManager, double renderPosXIn, double renderPosYIn, double renderPosZIn) {
        double y = renderPosYIn;
        if (YPort.getInstance().isEnabled() && YPort.getInstance().noShake.getValue().booleanValue() && !Util.mc.player.isRiding()) {
            y = YPort.getInstance().startY;
        }
        TileEntityRendererDispatcher.staticPlayerY = y;
        renderManager.setRenderPosition(renderPosXIn, TileEntityRendererDispatcher.staticPlayerY, renderPosZIn);
    }

    @Redirect(method={"drawSelectionBox"}, at=@At(value="INVOKE", target="Lnet/minecraft/util/math/AxisAlignedBB;offset(DDD)Lnet/minecraft/util/math/AxisAlignedBB;"))
    public AxisAlignedBB offsetHook(AxisAlignedBB axisAlignedBB, double x, double y, double z) {
        double yIn = y;
        if (YPort.getInstance().isEnabled() && YPort.getInstance().noShake.getValue().booleanValue() && !Util.mc.player.isRiding()) {
            yIn = YPort.getInstance().startY;
        }
        return axisAlignedBB.offset(x, y, z);
    }
}

