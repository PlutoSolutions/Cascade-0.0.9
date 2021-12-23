/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraftforge.client.event.RenderGameOverlayEvent
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$ElementType
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.visual;

import cascade.event.events.Render2DEvent;
import cascade.event.events.UpdateWalkingPlayerEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import java.awt.Color;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Crosshair
extends Module {
    private final Setting<Boolean> dot = this.register(new Setting<Boolean>("Dot", false));
    private final Setting<Float> crosshairGap = this.register(new Setting<Float>("Gap", Float.valueOf(2.0f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    private final Setting<Float> motionGap = this.register(new Setting<Float>("MotionGap", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(5.0f)));
    private final Setting<Float> crosshairWidth = this.register(new Setting<Float>("Width", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
    private final Setting<Float> motionWidth = this.register(new Setting<Float>("MotionWidth", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(2.5f)));
    private final Setting<Float> crosshairSize = this.register(new Setting<Float>("Size", Float.valueOf(2.0f), Float.valueOf(0.1f), Float.valueOf(40.0f)));
    private final Setting<Float> motionSize = this.register(new Setting<Float>("MotionSize", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    private final Setting<Color> c = this.register(new Setting<Color>("Color", new Color(8325375)));
    float currentMotion = 0.0f;
    long lastUpdate = -1L;
    float prevMotion = 0.0f;

    public Crosshair() {
        super("Crosshair", Module.Category.VISUAL, "Draws a custom crosshair");
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (this.megaNullCheck()) {
            return;
        }
        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent e) {
        this.prevMotion = this.currentMotion;
        double dX = Crosshair.mc.player.posX - Crosshair.mc.player.prevPosX;
        double dZ = Crosshair.mc.player.posZ - Crosshair.mc.player.prevPosZ;
        this.currentMotion = (float)Math.sqrt(dX * dX + dZ * dZ);
        this.lastUpdate = System.currentTimeMillis();
    }

    @Override
    public void onRender2D(Render2DEvent e) {
        ScaledResolution sr = new ScaledResolution(mc);
        float cX = (float)(sr.getScaledWidth_double() / 2.0 + 0.5);
        float cY = (float)(sr.getScaledHeight_double() / 2.0 + 0.5);
        float gap = this.crosshairGap.getValue().floatValue();
        float width = Math.max(this.crosshairWidth.getValue().floatValue(), 0.5f);
        float size = this.crosshairSize.getValue().floatValue();
        float tickLength = Crosshair.mc.timer.tickLength;
        Crosshair.drawRect(cX - (gap += Crosshair.lerp(this.prevMotion, this.currentMotion, Math.min((float)(System.currentTimeMillis() - this.lastUpdate) / tickLength, 1.0f)) * this.motionGap.getValue().floatValue()) - (size += Crosshair.lerp(this.prevMotion, this.currentMotion, Math.min((float)(System.currentTimeMillis() - this.lastUpdate) / tickLength, 1.0f)) * this.motionSize.getValue().floatValue()), cY - (width += Crosshair.lerp(this.prevMotion, this.currentMotion, Math.min((float)(System.currentTimeMillis() - this.lastUpdate) / tickLength, 1.0f)) * this.motionWidth.getValue().floatValue()) / 2.0f, cX - gap, cY + width / 2.0f, this.c.getValue().getRGB());
        Crosshair.drawRect(cX + gap + size, cY - width / 2.0f, cX + gap, cY + width / 2.0f, this.c.getValue().getRGB());
        Crosshair.drawRect(cX - width / 2.0f, cY + gap + size, cX + width / 2.0f, cY + gap, this.c.getValue().getRGB());
        Crosshair.drawRect(cX - width / 2.0f, cY - gap - size, cX + width / 2.0f, cY - gap, this.c.getValue().getRGB());
        if (this.dot.getValue().booleanValue()) {
            Crosshair.drawRect(cX - width / 2.0f, cY - width / 2.0f, cX + width / 2.0f, cY + width / 2.0f, this.c.getValue().getRGB());
        }
    }

    public static void drawRect(float left, float top, float right, float bottom, int color) {
        if (left < right) {
            float i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            float j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.color((float)f, (float)f1, (float)f2, (float)f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos((double)left, (double)bottom, 0.0).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, 0.0).endVertex();
        bufferbuilder.pos((double)right, (double)top, 0.0).endVertex();
        bufferbuilder.pos((double)left, (double)top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static float lerp(float a, float b, float partial) {
        return a * (1.0f - partial) + b * partial;
    }
}

