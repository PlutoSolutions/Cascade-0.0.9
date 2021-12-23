/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.core;

import cascade.Mod;
import cascade.event.events.ClientEvent;
import cascade.features.gui.CascadeGui;
import cascade.features.modules.Module;
import cascade.features.setting.ParentSetting;
import cascade.features.setting.Setting;
import cascade.util.Util;
import java.awt.Color;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClickGui
extends Module {
    public Setting<Boolean> descriptions = this.register(new Setting<Boolean>("Descriptions", true));
    public Setting<Boolean> rect = this.register(new Setting<Object>("Rectangle", Boolean.valueOf(true), v -> this.descriptions.getValue()));
    public Setting<Boolean> outline = this.register(new Setting<Object>("Outline", Boolean.valueOf(true), v -> this.descriptions.getValue()));
    public ParentSetting backgroundParent = this.registerParent(new ParentSetting("Backgrounds"));
    public Setting<Boolean> guiBlur = this.register(new Setting<Boolean>("Blur", true).setParent(this.backgroundParent));
    public Setting<BlurTypes> blurType = this.register(new Setting<BlurTypes>("BlurType", BlurTypes.blur).setParent(this.backgroundParent));
    public Setting<ModuleAppender> moduleAppender = this.register(new Setting<ModuleAppender>("Module Appender", ModuleAppender.PlusMinus));
    public Setting<Integer> newWidth = this.register(new Setting<Integer>("New Width", Integer.valueOf(3), Integer.valueOf(0), Integer.valueOf(7), v -> this.moduleAppender.getValue().equals((Object)ModuleAppender.New)));
    public Setting<String> closed = this.register(new Setting<String>("Closed", "+", v -> this.moduleAppender.getValue().equals((Object)ModuleAppender.CustomText)));
    public Setting<String> opened = this.register(new Setting<String>("Opened", "-", v -> this.moduleAppender.getValue().equals((Object)ModuleAppender.CustomText)));
    public Setting<Boolean> ifSettingsOnly = this.register(new Setting<Boolean>("If Settings Only", Boolean.valueOf(false), v -> !this.moduleAppender.getValue().equals((Object)ModuleAppender.None)));
    public Setting<Boolean> moduleOutline = this.register(new Setting<Boolean>("ModuleOutline", true));
    public Setting<Color> c = this.register(new Setting<Color>("Color", new Color(-8912641)));
    public Setting<Color> topc = this.register(new Setting<Color>("TopColor", new Color(-8912641)));
    public Setting<Integer> hoverA = this.register(new Setting<Integer>("Alpha", 255, 0, 255));
    public Setting<Boolean> rainbow = this.register(new Setting<Boolean>("Rainbow", false));
    public Setting<rainbowMode> rainbowModeHud = this.register(new Setting<Object>("HUD Mode", (Object)rainbowMode.Static, v -> this.rainbow.getValue()));
    public Setting<rainbowModeArray> rainbowModeA = this.register(new Setting<Object>("ArrayList Mode", (Object)rainbowModeArray.Static, v -> this.rainbow.getValue()));
    public Setting<Integer> rainbowHue = this.register(new Setting<Object>("Delay", Integer.valueOf(240), Integer.valueOf(0), Integer.valueOf(600), v -> this.rainbow.getValue()));
    public Setting<Integer> rainbowBrightness = this.register(new Setting<Object>("Brightness ", Integer.valueOf(150), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue()));
    public Setting<Integer> rainbowSaturation = this.register(new Setting<Object>("Saturation", Integer.valueOf(150), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue()));
    private static ClickGui INSTANCE = new ClickGui();

    public ClickGui() {
        super("ClickGui", Module.Category.CORE, "Client's Click GUI");
        this.setInstance();
        this.setBind(24);
    }

    public static ClickGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGui();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            Mod.colorManager.setColor(this.c.getPlannedValue().getRed(), this.c.getPlannedValue().getGreen(), this.c.getPlannedValue().getBlue(), this.c.getPlannedValue().getAlpha());
        }
    }

    @Override
    public void onEnable() {
        Util.mc.displayGuiScreen((GuiScreen)CascadeGui.getClickGui());
    }

    @Override
    public void onLoad() {
        Mod.colorManager.setColor(this.c.getValue().getRed(), this.c.getValue().getGreen(), this.c.getValue().getBlue(), this.c.getValue().getAlpha());
    }

    @Override
    public void onTick() {
        if (!(ClickGui.mc.currentScreen instanceof CascadeGui)) {
            this.disable();
        }
    }

    public static enum rainbowModeArray {
        Static,
        Up;

    }

    public static enum rainbowMode {
        Static,
        Sideway;

    }

    public static enum ModuleAppender {
        PlusMinus,
        Gears,
        CustomText,
        New,
        None;

    }

    public static enum BlurTypes {
        antialias,
        art,
        bits,
        blobs,
        blobs2,
        blur,
        bumpy,
        color_convolve,
        creeper,
        deconverge,
        desaturate,
        entity_outline,
        flip,
        fxaa,
        green,
        invert,
        notch,
        ntsc,
        outline,
        pencil,
        phosphor,
        scan_pincushion,
        sobel,
        spider,
        wobble;

    }
}

