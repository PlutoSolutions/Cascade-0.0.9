/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.AbstractClientPlayer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.ItemRenderer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumHandSide
 *  org.lwjgl.opengl.GL11
 */
package cascade.mixin.mixins;

import cascade.features.modules.core.ClickGui;
import cascade.features.modules.visual.HandChams;
import cascade.features.modules.visual.NoRender;
import cascade.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemRenderer.class})
public abstract class MixinItemRenderer {
    @Shadow
    @Final
    public Minecraft mc;
    private boolean injection = true;

    @Inject(method={"renderFireInFirstPerson"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderFireInFirstPersonHook(CallbackInfo info) {
        if (NoRender.getInstance().isEnabled() && NoRender.getInstance().noOverlay.getValue().booleanValue()) {
            info.cancel();
        }
    }

    @Inject(method={"renderSuffocationOverlay"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderSuffocationOverlay(CallbackInfo ci) {
        if (NoRender.getInstance().isEnabled() && NoRender.getInstance().noOverlay.getValue().booleanValue()) {
            ci.cancel();
        }
    }

    @Shadow
    public abstract void renderItemInFirstPerson(AbstractClientPlayer var1, float var2, float var3, EnumHand var4, float var5, ItemStack var6, float var7);

    @Shadow
    protected abstract void renderArmFirstPerson(float var1, float var2, EnumHandSide var3);

    @Inject(method={"renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderItemInFirstPersonHook(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_, CallbackInfo info) {
        if (this.injection) {
            info.cancel();
            float xOffset = 0.0f;
            float yOffset = 0.0f;
            this.injection = false;
            if (HandChams.getINSTANCE().isEnabled() && hand == EnumHand.MAIN_HAND && stack.isEmpty()) {
                if (HandChams.getINSTANCE().mode.getValue().equals((Object)HandChams.RenderMode.Wireframe)) {
                    this.renderItemInFirstPerson(player, p_187457_2_, p_187457_3_, hand, p_187457_5_ + xOffset, stack, p_187457_7_ + yOffset);
                }
                GlStateManager.pushMatrix();
                if (HandChams.getINSTANCE().mode.getValue().equals((Object)HandChams.RenderMode.Wireframe)) {
                    GL11.glPushAttrib((int)1048575);
                } else {
                    GlStateManager.pushAttrib();
                }
                if (HandChams.getINSTANCE().mode.getValue().equals((Object)HandChams.RenderMode.Wireframe)) {
                    GL11.glPolygonMode((int)1032, (int)6913);
                }
                GL11.glDisable((int)3553);
                GL11.glDisable((int)2896);
                if (HandChams.getINSTANCE().mode.getValue().equals((Object)HandChams.RenderMode.Wireframe)) {
                    GL11.glEnable((int)2848);
                    GL11.glEnable((int)3042);
                }
                GL11.glColor4f((float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRed() / 255.0f : (float)HandChams.getINSTANCE().c.getValue().getRed() / 255.0f), (float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getGreen() / 255.0f : (float)HandChams.getINSTANCE().c.getValue().getGreen() / 255.0f), (float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getBlue() / 255.0f : (float)HandChams.getINSTANCE().c.getValue().getBlue() / 255.0f), (float)((float)HandChams.getINSTANCE().c.getValue().getAlpha() / 255.0f));
                this.renderItemInFirstPerson(player, p_187457_2_, p_187457_3_, hand, p_187457_5_ + xOffset, stack, p_187457_7_ + yOffset);
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }
            if (!stack.isEmpty || HandChams.getINSTANCE().isDisabled()) {
                this.renderItemInFirstPerson(player, p_187457_2_, p_187457_3_, hand, p_187457_5_ + xOffset, stack, p_187457_7_ + yOffset);
            } else if (!stack.isEmpty || HandChams.getINSTANCE().isDisabled()) {
                this.renderItemInFirstPerson(player, p_187457_2_, p_187457_3_, hand, p_187457_5_, stack, p_187457_7_);
            }
            this.injection = true;
        }
    }
}

