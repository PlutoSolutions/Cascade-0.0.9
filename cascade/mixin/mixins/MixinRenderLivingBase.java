/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.entity.RenderLivingBase
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.client.event.RenderLivingEvent$Post
 *  net.minecraftforge.client.event.RenderLivingEvent$Pre
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  org.lwjgl.opengl.GL11
 */
package cascade.mixin.mixins;

import cascade.Mod;
import cascade.features.modules.core.ClickGui;
import cascade.features.modules.visual.Chams;
import cascade.features.modules.visual.Wireframe;
import cascade.util.ColorUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderLivingBase.class})
public abstract class MixinRenderLivingBase<T extends EntityLivingBase>
extends Render<T> {
    private static final ResourceLocation glint = new ResourceLocation("textures/shinechams.png");
    @Shadow
    protected ModelBase mainModel;
    @Shadow
    protected boolean renderMarker;
    float red;
    float green;
    float blue;

    public MixinRenderLivingBase(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn);
    }

    protected MixinRenderLivingBase(RenderManager renderManager) {
        super(renderManager);
        this.red = 0.0f;
        this.green = 0.0f;
        this.blue = 0.0f;
    }

    @Redirect(method={"renderModel"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    private void renderModelHook(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        boolean cancel = false;
        if (Chams.getInstance().isEnabled() && entityIn instanceof EntityPlayer && Chams.getInstance().colored.getValue().booleanValue() && !Chams.getInstance().textured.getValue().booleanValue()) {
            if (!Chams.getInstance().textured.getValue().booleanValue()) {
                GL11.glPushAttrib((int)1048575);
                GL11.glDisable((int)3008);
                GL11.glDisable((int)3553);
                GL11.glDisable((int)2896);
                GL11.glEnable((int)3042);
                GL11.glBlendFunc((int)770, (int)771);
                GL11.glLineWidth((float)1.5f);
                GL11.glEnable((int)2960);
                if (Chams.getInstance().rainbow.getValue().booleanValue()) {
                    Color rainbowColor1 = ColorUtil.rainbow(Chams.getInstance().rainbowhue.getValue());
                    Color rainbowColor2 = ColorUtil.getColor(entityIn, rainbowColor1.getRed(), rainbowColor1.getGreen(), rainbowColor1.getBlue(), Chams.getInstance().alpha.getValue(), true);
                    GL11.glDisable((int)2929);
                    GL11.glDepthMask((boolean)false);
                    GL11.glEnable((int)10754);
                    GL11.glColor4f((float)((float)rainbowColor2.getRed() / 255.0f), (float)((float)rainbowColor2.getGreen() / 255.0f), (float)((float)rainbowColor2.getBlue() / 255.0f), (float)((float)Chams.getInstance().alpha.getValue().intValue() / 255.0f));
                    modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                    GL11.glEnable((int)2929);
                    GL11.glDepthMask((boolean)true);
                } else if (Chams.getInstance().xqz.getValue().booleanValue()) {
                    Color hiddenColor = ColorUtil.getColor(entityIn, Chams.getInstance().hiddenRed.getValue(), Chams.getInstance().hiddenGreen.getValue(), Chams.getInstance().hiddenBlue.getValue(), Chams.getInstance().hiddenAlpha.getValue(), true);
                    Color visibleColor = ColorUtil.getColor(entityIn, Chams.getInstance().red.getValue(), Chams.getInstance().green.getValue(), Chams.getInstance().blue.getValue(), Chams.getInstance().alpha.getValue(), true);
                    GL11.glDisable((int)2929);
                    GL11.glDepthMask((boolean)false);
                    GL11.glEnable((int)10754);
                    GL11.glColor4f((float)((float)hiddenColor.getRed() / 255.0f), (float)((float)hiddenColor.getGreen() / 255.0f), (float)((float)hiddenColor.getBlue() / 255.0f), (float)((float)Chams.getInstance().alpha.getValue().intValue() / 255.0f));
                    modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                    GL11.glEnable((int)2929);
                    GL11.glDepthMask((boolean)true);
                    GL11.glColor4f((float)((float)visibleColor.getRed() / 255.0f), (float)((float)visibleColor.getGreen() / 255.0f), (float)((float)visibleColor.getBlue() / 255.0f), (float)((float)Chams.getInstance().alpha.getValue().intValue() / 255.0f));
                    modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                } else {
                    Color visibleColor2 = ColorUtil.getColor(entityIn, Chams.getInstance().red.getValue(), Chams.getInstance().green.getValue(), Chams.getInstance().blue.getValue(), Chams.getInstance().alpha.getValue(), true);
                    GL11.glDisable((int)2929);
                    GL11.glDepthMask((boolean)false);
                    GL11.glEnable((int)10754);
                    GL11.glColor4f((float)((float)visibleColor2.getRed() / 255.0f), (float)((float)visibleColor2.getGreen() / 255.0f), (float)((float)visibleColor2.getBlue() / 255.0f), (float)((float)Chams.getInstance().alpha.getValue().intValue() / 255.0f));
                    modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                    GL11.glEnable((int)2929);
                    GL11.glDepthMask((boolean)true);
                }
                GL11.glEnable((int)3042);
                GL11.glEnable((int)2896);
                GL11.glEnable((int)3553);
                GL11.glEnable((int)3008);
                GL11.glPopAttrib();
            }
        } else if (Chams.getInstance().textured.getValue().booleanValue()) {
            GL11.glDisable((int)2929);
            GL11.glDepthMask((boolean)false);
            Color visibleColor2 = ColorUtil.getColor(entityIn, Chams.getInstance().red.getValue(), Chams.getInstance().green.getValue(), Chams.getInstance().blue.getValue(), Chams.getInstance().alpha.getValue(), true);
            GL11.glColor4f((float)((float)visibleColor2.getRed() / 255.0f), (float)((float)visibleColor2.getGreen() / 255.0f), (float)((float)visibleColor2.getBlue() / 255.0f), (float)((float)Chams.getInstance().alpha.getValue().intValue() / 255.0f));
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GL11.glEnable((int)2929);
            GL11.glDepthMask((boolean)true);
        } else if (!cancel) {
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Inject(method={"doRender"}, at={@At(value="HEAD")})
    public void doRenderPre(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        if (Chams.getInstance().isEnabled() && !Chams.getInstance().colored.getValue().booleanValue() && entity != null) {
            GL11.glEnable((int)32823);
            GL11.glPolygonOffset((float)1.0f, (float)-1100000.0f);
        }
    }

    @Inject(method={"doRender"}, at={@At(value="RETURN")})
    public void doRenderPost(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        if (Chams.getInstance().isEnabled() && !Chams.getInstance().colored.getValue().booleanValue() && entity != null) {
            GL11.glPolygonOffset((float)1.0f, (float)1000000.0f);
            GL11.glDisable((int)32823);
        }
    }

    @Overwrite
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!MinecraftForge.EVENT_BUS.post((Event)new RenderLivingEvent.Pre(entity, (RenderLivingBase)RenderLivingBase.class.cast((Object)this), partialTicks, x, y, z))) {
            boolean shouldSit;
            GlStateManager.pushMatrix();
            GlStateManager.disableCull();
            this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
            this.mainModel.isRiding = shouldSit = entity.isRiding() && entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit();
            this.mainModel.isChild = entity.isChild();
            try {
                float f = this.interpolateRotation(((EntityLivingBase)entity).prevRenderYawOffset, ((EntityLivingBase)entity).renderYawOffset, partialTicks);
                float f2 = this.interpolateRotation(((EntityLivingBase)entity).prevRotationYawHead, ((EntityLivingBase)entity).rotationYawHead, partialTicks);
                float f3 = f2 - f;
                if (shouldSit && entity.getRidingEntity() instanceof EntityLivingBase) {
                    EntityLivingBase entitylivingbase = (EntityLivingBase)entity.getRidingEntity();
                    f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
                    f3 = f2 - f;
                    float f4 = MathHelper.wrapDegrees((float)f3);
                    if (f4 < -85.0f) {
                        f4 = -85.0f;
                    }
                    if (f4 >= 85.0f) {
                        f4 = 85.0f;
                    }
                    f = f2 - f4;
                    if (f4 * f4 > 2500.0f) {
                        f += f4 * 0.2f;
                    }
                    f3 = f2 - f;
                }
                float f5 = ((EntityLivingBase)entity).prevRotationPitch + (((EntityLivingBase)entity).rotationPitch - ((EntityLivingBase)entity).prevRotationPitch) * partialTicks;
                this.renderLivingAt(entity, x, y, z);
                float f6 = this.handleRotationFloat(entity, partialTicks);
                this.applyRotations(entity, f6, f, partialTicks);
                float f7 = this.prepareScale(entity, partialTicks);
                float f8 = 0.0f;
                float f9 = 0.0f;
                if (!entity.isRiding()) {
                    f8 = ((EntityLivingBase)entity).prevLimbSwingAmount + (((EntityLivingBase)entity).limbSwingAmount - ((EntityLivingBase)entity).prevLimbSwingAmount) * partialTicks;
                    f9 = ((EntityLivingBase)entity).limbSwing - ((EntityLivingBase)entity).limbSwingAmount * (1.0f - partialTicks);
                    if (entity.isChild()) {
                        f9 *= 3.0f;
                    }
                    if (f8 > 1.0f) {
                        f8 = 1.0f;
                    }
                    f3 = f2 - f;
                }
                GlStateManager.enableAlpha();
                this.mainModel.setLivingAnimations(entity, f9, f8, partialTicks);
                this.mainModel.setRotationAngles(f9, f8, f6, f3, f5, f7, entity);
                if (this.renderOutlines) {
                    boolean flag1 = this.setScoreTeamColor(entity);
                    GlStateManager.enableColorMaterial();
                    GlStateManager.enableOutlineMode((int)this.getTeamColor((Entity)entity));
                    if (!this.renderMarker) {
                        this.renderModel(entity, f9, f8, f6, f3, f5, f7);
                    }
                    if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator()) {
                        this.renderLayers(entity, f9, f8, partialTicks, f6, f3, f5, f7);
                    }
                    GlStateManager.disableOutlineMode();
                    GlStateManager.disableColorMaterial();
                    if (flag1) {
                        this.unsetScoreTeamColor();
                    }
                } else {
                    if (Wireframe.getINSTANCE().isEnabled() && entity instanceof EntityPlayer && Wireframe.getINSTANCE().pmode.getValue().equals((Object)Wireframe.RenderMode.Solid)) {
                        this.red = (float)Wireframe.getINSTANCE().c.getValue().getRed() / 255.0f;
                        this.green = (float)Wireframe.getINSTANCE().c.getValue().getGreen() / 255.0f;
                        this.blue = (float)Wireframe.getINSTANCE().c.getValue().getBlue() / 255.0f;
                        GlStateManager.pushMatrix();
                        GlStateManager.disableLighting();
                        GL11.glPushAttrib((int)1048575);
                        GL11.glDisable((int)3553);
                        GL11.glDisable((int)2896);
                        GL11.glEnable((int)2848);
                        GL11.glEnable((int)3042);
                        GL11.glBlendFunc((int)770, (int)771);
                        GL11.glDisable((int)2929);
                        GL11.glDepthMask((boolean)false);
                        if (Mod.friendManager.isFriend(entity.getName()) || entity == Minecraft.getMinecraft().player) {
                            GL11.glColor4f((float)0.0f, (float)191.0f, (float)255.0f, (float)((float)Wireframe.getINSTANCE().c.getValue().getAlpha() / 255.0f));
                        } else {
                            GL11.glColor4f((float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRed() / 255.0f : this.red), (float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getGreen() / 255.0f : this.green), (float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getBlue() / 255.0f : this.blue), (float)((float)Wireframe.getINSTANCE().c.getValue().getAlpha() / 255.0f));
                        }
                        this.renderModel(entity, f9, f8, f6, f3, f5, f7);
                        GL11.glDisable((int)2896);
                        GL11.glEnable((int)2929);
                        GL11.glDepthMask((boolean)true);
                        if (Mod.friendManager.isFriend(entity.getName()) || entity == Minecraft.getMinecraft().player) {
                            GL11.glColor4f((float)0.0f, (float)191.0f, (float)255.0f, (float)((float)Wireframe.getINSTANCE().c.getValue().getAlpha() / 255.0f));
                        } else {
                            GL11.glColor4f((float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRed() / 255.0f : this.red), (float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getGreen() / 255.0f : this.green), (float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getBlue() / 255.0f : this.blue), (float)((float)Wireframe.getINSTANCE().c.getValue().getAlpha() / 255.0f));
                        }
                        this.renderModel(entity, f9, f8, f6, f3, f5, f7);
                        GL11.glEnable((int)2896);
                        GlStateManager.popAttrib();
                        GlStateManager.enableLighting();
                        GlStateManager.popMatrix();
                    }
                    boolean flag1 = this.setDoRenderBrightness(entity, partialTicks);
                    if (!(entity instanceof EntityPlayer) || Wireframe.getINSTANCE().isEnabled() && Wireframe.getINSTANCE().pmode.getValue().equals((Object)Wireframe.RenderMode.Wireframe) && Wireframe.getINSTANCE().pmod.getValue().booleanValue() || Wireframe.getINSTANCE().isDisabled()) {
                        this.renderModel(entity, f9, f8, f6, f3, f5, f7);
                    }
                    if (flag1) {
                        this.unsetBrightness();
                    }
                    GlStateManager.depthMask((boolean)true);
                    if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator()) {
                        this.renderLayers(entity, f9, f8, partialTicks, f6, f3, f5, f7);
                    }
                    if (Wireframe.getINSTANCE().isEnabled() && entity instanceof EntityPlayer && Wireframe.getINSTANCE().pmode.getValue().equals((Object)Wireframe.RenderMode.Wireframe)) {
                        this.red = (float)Wireframe.getINSTANCE().c.getValue().getRed() / 255.0f;
                        this.green = (float)Wireframe.getINSTANCE().c.getValue().getGreen() / 255.0f;
                        this.blue = (float)Wireframe.getINSTANCE().c.getValue().getBlue() / 255.0f;
                        GlStateManager.pushMatrix();
                        GL11.glPushAttrib((int)1048575);
                        GL11.glPolygonMode((int)1032, (int)6913);
                        GL11.glDisable((int)3553);
                        GL11.glDisable((int)2896);
                        GL11.glDisable((int)2929);
                        GL11.glEnable((int)2848);
                        GL11.glEnable((int)3042);
                        GL11.glBlendFunc((int)770, (int)771);
                        if (Mod.friendManager.isFriend(entity.getName()) || entity == Minecraft.getMinecraft().player) {
                            GL11.glColor4f((float)0.0f, (float)191.0f, (float)255.0f, (float)((float)Wireframe.getINSTANCE().c.getValue().getAlpha() / 255.0f));
                        } else {
                            GL11.glColor4f((float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRed() / 255.0f : this.red), (float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getGreen() / 255.0f : this.green), (float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getBlue() / 255.0f : this.blue), (float)((float)Wireframe.getINSTANCE().c.getValue().getAlpha() / 255.0f));
                        }
                        GL11.glLineWidth((float)Wireframe.getINSTANCE().plw.getValue().floatValue());
                        this.renderModel(entity, f9, f8, f6, f3, f5, f7);
                        GL11.glEnable((int)2896);
                        GlStateManager.popAttrib();
                        GlStateManager.popMatrix();
                    }
                }
                GlStateManager.disableRescaleNormal();
            }
            catch (Exception exception) {
                // empty catch block
            }
            GlStateManager.setActiveTexture((int)OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.setActiveTexture((int)OpenGlHelper.defaultTexUnit);
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
            MinecraftForge.EVENT_BUS.post((Event)new RenderLivingEvent.Post(entity, (RenderLivingBase)RenderLivingBase.class.cast((Object)this), partialTicks, x, y, z));
        }
    }

    @Shadow
    protected abstract boolean isVisible(EntityLivingBase var1);

    @Shadow
    protected abstract float getSwingProgress(T var1, float var2);

    @Shadow
    protected abstract float interpolateRotation(float var1, float var2, float var3);

    @Shadow
    protected abstract float handleRotationFloat(T var1, float var2);

    @Shadow
    protected abstract void applyRotations(T var1, float var2, float var3, float var4);

    @Shadow
    public abstract float prepareScale(T var1, float var2);

    @Shadow
    protected abstract void unsetScoreTeamColor();

    @Shadow
    protected abstract boolean setScoreTeamColor(T var1);

    @Shadow
    protected abstract void renderLivingAt(T var1, double var2, double var4, double var6);

    @Shadow
    protected abstract void unsetBrightness();

    @Shadow
    protected abstract void renderModel(T var1, float var2, float var3, float var4, float var5, float var6, float var7);

    @Shadow
    protected abstract void renderLayers(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8);

    @Shadow
    protected abstract boolean setDoRenderBrightness(T var1, float var2);
}

