/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraftforge.fml.client.config.GuiUtils
 */
package cascade.features.modules.core;

import cascade.event.events.Render2DEvent;
import cascade.features.gui.CascadeGui;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.Util;
import java.awt.Color;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.client.config.GuiUtils;

public class Gradient
extends Module {
    public Setting<Boolean> cgui = this.register(new Setting<Boolean>("ClickGui", true));
    public Setting<Boolean> gui = this.register(new Setting<Boolean>("AllGuis", true));
    public Setting<Boolean> always = this.register(new Setting<Boolean>("Always", false));
    public Setting<Page> page = this.register(new Setting<Page>("Page", Page.Top));
    public Setting<Integer> rtop = this.register(new Setting<Object>("TopRed", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.page.getValue() == Page.Top));
    public Setting<Integer> gtop = this.register(new Setting<Object>("TopGreen", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.page.getValue() == Page.Top));
    public Setting<Integer> btop = this.register(new Setting<Object>("TopBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.page.getValue() == Page.Top));
    public Setting<Integer> atop = this.register(new Setting<Object>("TopAlpha", Integer.valueOf(80), Integer.valueOf(0), Integer.valueOf(255), v -> this.page.getValue() == Page.Top));
    public Setting<Integer> rbottom = this.register(new Setting<Object>("BottomRed", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.page.getValue() == Page.Bottom));
    public Setting<Integer> gbottom = this.register(new Setting<Object>("BottomGreen", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.page.getValue() == Page.Bottom));
    public Setting<Integer> bbottom = this.register(new Setting<Object>("BottomBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.page.getValue() == Page.Bottom));
    public Setting<Integer> abottom = this.register(new Setting<Object>("BottomAlpha", Integer.valueOf(80), Integer.valueOf(0), Integer.valueOf(255), v -> this.page.getValue() == Page.Bottom));
    public Setting<Integer> rleft = this.register(new Setting<Object>("LeftRed", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.page.getValue() == Page.Left));
    public Setting<Integer> gleft = this.register(new Setting<Object>("LeftGreen", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.page.getValue() == Page.Left));
    public Setting<Integer> bleft = this.register(new Setting<Object>("LeftBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.page.getValue() == Page.Left));
    public Setting<Integer> aleft = this.register(new Setting<Object>("LeftAlpha", Integer.valueOf(80), Integer.valueOf(0), Integer.valueOf(255), v -> this.page.getValue() == Page.Left));
    public Setting<Integer> rright = this.register(new Setting<Object>("RightRed", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.page.getValue() == Page.Right));
    public Setting<Integer> gright = this.register(new Setting<Object>("RightGreen", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.page.getValue() == Page.Right));
    public Setting<Integer> bright = this.register(new Setting<Object>("RightBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.page.getValue() == Page.Right));
    public Setting<Integer> aright = this.register(new Setting<Object>("RightAlpha", Integer.valueOf(80), Integer.valueOf(0), Integer.valueOf(255), v -> this.page.getValue() == Page.Right));

    public Gradient() {
        super("Gradient", Module.Category.CORE, "Draws gradient");
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        ScaledResolution resolution = new ScaledResolution(Util.mc);
        if (Util.mc.currentScreen instanceof CascadeGui && this.cgui.getValue().booleanValue() || this.always.getValue().booleanValue() || this.gui.getValue().booleanValue() && Util.mc.currentScreen instanceof CascadeGui) {
            this.drawGradient(0.0, 0.0, resolution.getScaledWidth(), resolution.getScaledHeight(), new Color(0, 0, 0, 0).getRGB(), new Color(this.rbottom.getValue(), this.gbottom.getValue(), this.bbottom.getValue(), this.abottom.getValue()).getRGB());
            this.drawGradient(0.0, 0.0, resolution.getScaledWidth(), resolution.getScaledHeight(), new Color(this.rtop.getValue(), this.gtop.getValue(), this.btop.getValue(), this.atop.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB());
            this.drawGradient(0.0, resolution.getScaledHeight(), resolution.getScaledWidth(), 0.0, new Color(this.rleft.getValue(), this.gleft.getValue(), this.bleft.getValue(), this.aleft.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB());
            this.drawGradient(resolution.getScaledHeight(), 0.0, resolution.getScaledHeight(), 0.0, new Color(0, 0, 0, 0).getRGB(), new Color(this.rright.getValue(), this.gright.getValue(), this.bright.getValue(), this.aright.getValue()).getRGB());
        }
    }

    public void drawGradient(double left, double top, double right, double bottom, int startColor, int endColor) {
        GuiUtils.drawGradientRect((int)0, (int)((int)left), (int)((int)top), (int)((int)right), (int)((int)bottom), (int)startColor, (int)endColor);
    }

    public static enum Page {
        Top,
        Bottom,
        Left,
        Right;

    }
}

