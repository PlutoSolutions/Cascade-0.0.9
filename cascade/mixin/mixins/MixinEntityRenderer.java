/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.client.renderer.EntityRenderer
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemPickaxe
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  org.lwjgl.util.glu.Project
 */
package cascade.mixin.mixins;

import cascade.event.events.PerspectiveEvent;
import cascade.features.modules.misc.EntityTrace;
import cascade.features.modules.visual.CameraClip;
import cascade.features.modules.visual.NoRender;
import cascade.util.Util;
import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderer.class})
public abstract class MixinEntityRenderer {
    @Shadow
    private ItemStack itemActivationItem;

    @Redirect(method={"setupCameraTransform"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onSetupCameraTransform(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float)Util.mc.displayWidth / (float)Util.mc.displayHeight);
        MinecraftForge.EVENT_BUS.post((Event)event);
        Project.gluPerspective((float)fovy, (float)event.getAspect(), (float)zNear, (float)zFar);
    }

    @Redirect(method={"renderWorldPass"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderWorldPass(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float)Util.mc.displayWidth / (float)Util.mc.displayHeight);
        MinecraftForge.EVENT_BUS.post((Event)event);
        Project.gluPerspective((float)fovy, (float)event.getAspect(), (float)zNear, (float)zFar);
    }

    @Redirect(method={"renderCloudsCheck"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderCloudsCheck(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float)Util.mc.displayWidth / (float)Util.mc.displayHeight);
        MinecraftForge.EVENT_BUS.post((Event)event);
        Project.gluPerspective((float)fovy, (float)event.getAspect(), (float)zNear, (float)zFar);
    }

    @ModifyVariable(method={"orientCamera"}, ordinal=3, at=@At(value="STORE", ordinal=0), require=1)
    public double changeCameraDistanceHook(double range) {
        return CameraClip.getInstance().isEnabled() && CameraClip.getInstance().extend.getValue() != false ? CameraClip.getInstance().distance.getValue() : range;
    }

    @ModifyVariable(method={"orientCamera"}, ordinal=7, at=@At(value="STORE", ordinal=0), require=1)
    public double orientCameraHook(double range) {
        return CameraClip.getInstance().isEnabled() && CameraClip.getInstance().extend.getValue() != false ? CameraClip.getInstance().distance.getValue() : (CameraClip.getInstance().isEnabled() && CameraClip.getInstance().extend.getValue() == false ? 4.0 : range);
    }

    @Inject(method={"hurtCameraEffect"}, at={@At(value="HEAD")}, cancellable=true)
    public void hurtCameraEffectHook(float ticks, CallbackInfo info) {
        if (NoRender.getInstance().isEnabled() && NoRender.getInstance().noHurt.getValue().booleanValue()) {
            info.cancel();
        }
    }

    @Redirect(method={"setupCameraTransform"}, at=@At(value="FIELD", target="Lnet/minecraft/client/entity/EntityPlayerSP;prevTimeInPortal:F"))
    public float prevTimeInPortalHook(EntityPlayerSP entityPlayerSP) {
        if (NoRender.getInstance().isEnabled() && NoRender.getInstance().noOverlay.getValue().booleanValue()) {
            return -3.4028235E38f;
        }
        return entityPlayerSP.prevTimeInPortal;
    }

    @Inject(method={"renderItemActivation"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderItemActivationHook(CallbackInfo info) {
        if (this.itemActivationItem != null && NoRender.getInstance().isEnabled() && NoRender.getInstance().totemPops.getValue().booleanValue() && this.itemActivationItem.getItem() == Items.TOTEM_OF_UNDYING) {
            info.cancel();
        }
    }

    @Redirect(method={"getMouseOver"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate) {
        if (EntityTrace.getINSTANCE().isEnabled() && (Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemPickaxe && EntityTrace.getINSTANCE().pickaxe.getValue() != false || Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && EntityTrace.getINSTANCE().crystal.getValue() != false || Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE && EntityTrace.getINSTANCE().gapple.getValue().booleanValue())) {
            return new ArrayList<Entity>();
        }
        return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
    }
}

