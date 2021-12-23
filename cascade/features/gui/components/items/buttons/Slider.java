/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  org.lwjgl.input.Mouse
 */
package cascade.features.gui.components.items.buttons;

import cascade.Mod;
import cascade.features.gui.CascadeGui;
import cascade.features.gui.components.Component;
import cascade.features.gui.components.items.buttons.Button;
import cascade.features.modules.core.ClickGui;
import cascade.features.setting.Setting;
import cascade.util.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import org.lwjgl.input.Mouse;

public class Slider
extends Button {
    private final Number min;
    private final Number max;
    private final int difference;
    public Setting setting;

    public Slider(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.min = (Number)setting.getMin();
        this.max = (Number)setting.getMax();
        this.difference = this.max.intValue() - this.min.intValue();
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.dragSetting(mouseX, mouseY);
        RenderUtil.drawRect(this.x, this.y, this.x + (float)this.width + 7.4f, this.y + (float)this.height - 0.5f, 0x11555555);
        RenderUtil.drawRectCol(this.x, this.y + 13.3f, ((Number)this.setting.getValue()).floatValue() <= this.min.floatValue() ? 0.0f : ((float)this.width + 7.4f) * this.partialMultiplier(), (float)this.height - 14.0f, !this.isHovering(mouseX, mouseY) ? Mod.colorManager.getColorWithAlphaColor(Mod.moduleManager.getModuleByClass(ClickGui.class).hoverA.getValue()) : Mod.colorManager.getColorWithAlphaColor(Mod.moduleManager.getModuleByClass(ClickGui.class).c.getValue().getAlpha()));
        Mod.textManager.drawStringWithShadow(this.getName() + " " + (Object)ChatFormatting.GRAY + (this.setting.getValue() instanceof Float ? this.setting.getValue() : Double.valueOf(((Number)this.setting.getValue()).doubleValue())), this.x + 2.3f, this.y - 1.3f - (float)CascadeGui.getClickGui().getTextOffset(), -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            this.setSettingFromX(mouseX);
        }
    }

    @Override
    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : CascadeGui.getClickGui().getComponents()) {
            if (!component.drag) continue;
            return false;
        }
        return (float)mouseX >= this.getX() && (float)mouseX <= this.getX() + (float)this.getWidth() + 8.0f && (float)mouseY >= this.getY() && (float)mouseY <= this.getY() + (float)this.height;
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    private void dragSetting(int mouseX, int mouseY) {
        if (this.isHovering(mouseX, mouseY) && Mouse.isButtonDown((int)0)) {
            this.setSettingFromX(mouseX);
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    private void setSettingFromX(int mouseX) {
        float percent = ((float)mouseX - this.x) / ((float)this.width + 7.4f);
        if (this.setting.getValue() instanceof Double) {
            double result = (Double)this.setting.getMin() + (double)((float)this.difference * percent);
            this.setting.setValue((double)Math.round(10.0 * result) / 10.0);
        } else if (this.setting.getValue() instanceof Float) {
            float result = ((Float)this.setting.getMin()).floatValue() + (float)this.difference * percent;
            this.setting.setValue(Float.valueOf((float)Math.round(10.0f * result) / 10.0f));
        } else if (this.setting.getValue() instanceof Integer) {
            this.setting.setValue((Integer)this.setting.getMin() + (int)((float)this.difference * percent));
        }
    }

    private float middle() {
        return this.max.floatValue() - this.min.floatValue();
    }

    private float part() {
        return ((Number)this.setting.getValue()).floatValue() - this.min.floatValue();
    }

    private float partialMultiplier() {
        return this.part() / this.middle();
    }
}

