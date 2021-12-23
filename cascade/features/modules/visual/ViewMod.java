/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.GL11
 */
package cascade.features.modules.visual;

import cascade.event.events.RenderItemEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class ViewMod
extends Module {
    public Setting<Boolean> mainhandParent = this.register(new Setting<Boolean>("Mainhand", false));
    public Setting<Boolean> mainhandTranslation = this.register(new Setting<Object>("Mainhand Translation", Boolean.valueOf(false), v -> this.mainhandParent.getValue()));
    public Setting<Float> mainhandX = this.register(new Setting<Object>("Mainhand X", Float.valueOf(0.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f), v -> this.mainhandParent.getValue() != false && this.mainhandTranslation.getValue() != false));
    public Setting<Float> mainhandY = this.register(new Setting<Object>("Mainhand Y", Float.valueOf(0.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f), v -> this.mainhandParent.getValue() != false && this.mainhandTranslation.getValue() != false));
    public Setting<Float> mainhandZ = this.register(new Setting<Object>("Mainhand Z", Float.valueOf(0.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f), v -> this.mainhandParent.getValue() != false && this.mainhandTranslation.getValue() != false));
    public Setting<Boolean> mainhandScaling = this.register(new Setting<Object>("Mainhand Scaling", Boolean.valueOf(false), v -> this.mainhandParent.getValue()));
    public Setting<Float> mainhandScaleX = this.register(new Setting<Object>("Mainhand Scale X", Float.valueOf(0.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f), v -> this.mainhandParent.getValue() != false && this.mainhandScaling.getValue() != false));
    public Setting<Float> mainhandScaleY = this.register(new Setting<Object>("Mainhand Scale Y", Float.valueOf(0.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f), v -> this.mainhandParent.getValue() != false && this.mainhandScaling.getValue() != false));
    public Setting<Float> mainhandScaleZ = this.register(new Setting<Object>("Mainhand Scale Z", Float.valueOf(0.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f), v -> this.mainhandParent.getValue() != false && this.mainhandScaling.getValue() != false));
    public Setting<Boolean> mainhandRotation = this.register(new Setting<Object>("Mainhand Rotation", Boolean.valueOf(false), v -> this.mainhandParent.getValue()));
    public Setting<Float> mainhandRotationX = this.register(new Setting<Object>("Mainhand Rotation X", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.mainhandParent.getValue() != false && this.mainhandRotation.getValue() != false));
    public Setting<Float> mainhandRotationY = this.register(new Setting<Object>("Mainhand Rotation Y", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.mainhandParent.getValue() != false && this.mainhandRotation.getValue() != false));
    public Setting<Float> mainhandRotationZ = this.register(new Setting<Object>("Mainhand Rotation Z", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.mainhandParent.getValue() != false && this.mainhandRotation.getValue() != false));
    public Setting<Boolean> offhandParent = this.register(new Setting<Boolean>("Offhand", false));
    public Setting<Boolean> offhandTranslation = this.register(new Setting<Object>("Offhand Translation", Boolean.valueOf(false), v -> this.offhandParent.getValue()));
    public Setting<Float> offhandX = this.register(new Setting<Object>("Offhand X", Float.valueOf(0.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f), v -> this.offhandParent.getValue() != false && this.offhandTranslation.getValue() != false));
    public Setting<Float> offhandY = this.register(new Setting<Object>("Offhand Y", Float.valueOf(0.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f), v -> this.offhandParent.getValue() != false && this.offhandTranslation.getValue() != false));
    public Setting<Float> offhandZ = this.register(new Setting<Object>("Offhand Z", Float.valueOf(0.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f), v -> this.offhandParent.getValue() != false && this.offhandTranslation.getValue() != false));
    public Setting<Boolean> offhandScaling = this.register(new Setting<Object>("Offhand Scaling", Boolean.valueOf(false), v -> this.offhandParent.getValue()));
    public Setting<Float> offhandScaleX = this.register(new Setting<Object>("Offhand Scale X", Float.valueOf(0.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f), v -> this.offhandParent.getValue() != false && this.offhandScaling.getValue() != false));
    public Setting<Float> offhandScaleY = this.register(new Setting<Object>("Offhand Scale Y", Float.valueOf(0.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f), v -> this.offhandParent.getValue() != false && this.offhandScaling.getValue() != false));
    public Setting<Float> offhandScaleZ = this.register(new Setting<Object>("Offhand Scale Z", Float.valueOf(0.0f), Float.valueOf(-10.0f), Float.valueOf(10.0f), v -> this.offhandParent.getValue() != false && this.offhandScaling.getValue() != false));
    public Setting<Boolean> offhandRotation = this.register(new Setting<Object>("Offhand Rotation", Boolean.valueOf(false), v -> this.offhandParent.getValue()));
    public Setting<Float> offhandRotationX = this.register(new Setting<Object>("Offhand Rotation X", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.offhandParent.getValue() != false && this.offhandRotation.getValue() != false));
    public Setting<Float> offhandRotationY = this.register(new Setting<Object>("Offhand Rotation Y", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.offhandParent.getValue() != false && this.offhandRotation.getValue() != false));
    public Setting<Float> offhandRotationZ = this.register(new Setting<Object>("Offhand Rotation Z", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.offhandParent.getValue() != false && this.offhandRotation.getValue() != false));
    public Setting<Float> a = this.register(new Setting<Float>("Item Opacity", Float.valueOf(255.0f), Float.valueOf(0.0f), Float.valueOf(255.0f)));
    static ViewMod INSTANCE = new ViewMod();

    public ViewMod() {
        super("ViewMod", Module.Category.VISUAL, "View model changer");
        this.setInstance();
    }

    public static ViewMod getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ViewMod();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRenderMainhand(RenderItemEvent.MainHand event) {
        if (this.isEnabled()) {
            GL11.glTranslated((double)(this.mainhandX.getValue().floatValue() / 40.0f), (double)(this.mainhandY.getValue().floatValue() / 40.0f), (double)(this.mainhandZ.getValue().floatValue() / 40.0f));
            GlStateManager.scale((float)(this.mainhandScaleX.getValue().floatValue() / 10.0f + 1.0f), (float)(this.mainhandScaleY.getValue().floatValue() / 10.0f + 1.0f), (float)(this.mainhandScaleZ.getValue().floatValue() / 10.0f + 1.0f));
            GlStateManager.rotate((float)(this.mainhandRotationX.getValue().floatValue() * 36.0f), (float)1.0f, (float)0.0f, (float)0.0f);
            GlStateManager.rotate((float)(this.mainhandRotationY.getValue().floatValue() * 36.0f), (float)0.0f, (float)1.0f, (float)0.0f);
            GlStateManager.rotate((float)(this.mainhandRotationZ.getValue().floatValue() * 36.0f), (float)0.0f, (float)0.0f, (float)1.0f);
        }
    }

    @SubscribeEvent
    public void onRenderOffhand(RenderItemEvent.Offhand event) {
        if (this.isEnabled()) {
            GL11.glTranslated((double)(this.offhandX.getValue().floatValue() / 40.0f), (double)(this.offhandY.getValue().floatValue() / 40.0f), (double)(this.offhandZ.getValue().floatValue() / 40.0f));
            GlStateManager.scale((float)(this.offhandScaleX.getValue().floatValue() / 10.0f + 1.0f), (float)(this.offhandScaleY.getValue().floatValue() / 10.0f + 1.0f), (float)(this.offhandScaleZ.getValue().floatValue() / 10.0f + 1.0f));
            GlStateManager.rotate((float)(this.offhandRotationX.getValue().floatValue() * 36.0f), (float)1.0f, (float)0.0f, (float)0.0f);
            GlStateManager.rotate((float)(this.offhandRotationY.getValue().floatValue() * 36.0f), (float)0.0f, (float)1.0f, (float)0.0f);
            GlStateManager.rotate((float)(this.offhandRotationZ.getValue().floatValue() * 36.0f), (float)0.0f, (float)0.0f, (float)1.0f);
        }
    }
}

