/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.GL11
 */
package cascade.features.modules.visual;

import cascade.event.events.CrystalTextureEvent;
import cascade.event.events.RenderCrystalEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import java.awt.Color;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class CrystalChams
extends Module {
    public Setting<Integer> rotations = this.register(new Setting<Integer>("Rotations", 30, 0, 200));
    public Setting<Boolean> glint = this.register(new Setting<Boolean>("Glint", false));
    public Setting<Double> scaleX = this.register(new Setting<Double>("ScaleX", 1.0, 0.0, 2.0));
    public Setting<Double> scaleY = this.register(new Setting<Double>("ScaleY", 1.0, 0.0, 2.0));
    public Setting<Double> scaleZ = this.register(new Setting<Double>("ScaleZ", 1.0, 0.0, 2.0));
    public Setting<Float> lineWidth = this.register(new Setting<Float>("Line Width", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(3.0f)));
    public Setting<Color> c = this.register(new Setting<Color>("Color", new Color(-1)));
    static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    public CrystalChams() {
        super("CrystalChams", Module.Category.VISUAL, "");
    }

    @SubscribeEvent
    public void renderCrystalTexture(CrystalTextureEvent event) {
        if (this.isEnabled()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderCrystalPre(RenderCrystalEvent.RenderCrystalPreEvent event) {
        if (this.isEnabled()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderCrystalPost(RenderCrystalEvent.RenderCrystalPostEvent event) {
        if (CrystalChams.fullNullCheck() || !this.isEnabled()) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glPushAttrib((int)1048575);
        float rotation = (float)event.getEntityEnderCrystal().innerRotation + event.getPartialTicks();
        float rotationMoved = MathHelper.sin((float)(rotation * 0.2f)) / 2.0f + 0.5f;
        rotationMoved = (float)((double)rotationMoved + Math.pow(rotationMoved, 2.0));
        GL11.glTranslated((double)event.getX(), (double)event.getY(), (double)event.getZ());
        GL11.glScaled((double)this.scaleX.getValue(), (double)this.scaleY.getValue(), (double)this.scaleZ.getValue());
        GL11.glEnable((int)3042);
        GL11.glDepthMask((boolean)false);
        GL11.glEnable((int)3553);
        GL11.glDisable((int)2929);
        if (this.glint.getValue().booleanValue()) {
            mc.getTextureManager().bindTexture(RES_ITEM_GLINT);
            GL11.glTexCoord3d((double)1.0, (double)1.0, (double)1.0);
            GL11.glEnable((int)3553);
            GL11.glBlendFunc((int)768, (int)771);
            GL11.glBlendFunc((int)770, (int)32772);
        }
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glLineWidth((float)this.lineWidth.getValue().floatValue());
        GL11.glColor4f((float)((float)this.c.getValue().getRed() / 255.0f), (float)((float)this.c.getValue().getGreen() / 255.0f), (float)((float)this.c.getValue().getBlue() / 255.0f), (float)((float)this.c.getValue().getAlpha() / 255.0f));
        GL11.glPolygonMode((int)1032, (int)2880);
        event.getModelNoBase().render((Entity)event.getEntityEnderCrystal(), 0.0f, rotation * ((float)this.rotations.getValue().intValue() / 10.0f), rotationMoved * 0.2f, 0.0f, 0.0f, 0.0625f);
        GL11.glPolygonMode((int)1032, (int)6913);
        event.getModelNoBase().render((Entity)event.getEntityEnderCrystal(), 0.0f, rotation * ((float)this.rotations.getValue().intValue() / 10.0f), rotationMoved * 0.2f, 0.0f, 0.0f, 0.0625f);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)3553);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glScaled((double)1.0, (double)1.0, (double)1.0);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}

