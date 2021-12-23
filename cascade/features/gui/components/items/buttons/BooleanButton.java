/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.audio.ISound
 *  net.minecraft.client.audio.PositionedSoundRecord
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.util.SoundEvent
 */
package cascade.features.gui.components.items.buttons;

import cascade.Mod;
import cascade.features.gui.CascadeGui;
import cascade.features.gui.components.items.buttons.Button;
import cascade.features.modules.core.ClickGui;
import cascade.features.setting.Setting;
import cascade.util.RenderUtil;
import cascade.util.Util;
import java.awt.Color;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

public class BooleanButton
extends Button {
    private final Setting setting;

    public BooleanButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + (float)this.width + 7.4f, this.y + (float)this.height - 0.5f, !this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515);
        if (this.getState()) {
            RenderUtil.drawRect(this.x + (float)this.width - 7.0f, this.y + 2.0f, this.x + (float)this.width + 4.0f, this.y + (float)this.height - 2.0f, !this.isHovering(mouseX, mouseY) ? Mod.colorManager.getColorWithAlpha(Mod.moduleManager.getModuleByClass(ClickGui.class).hoverA.getValue()) : Mod.colorManager.getColorWithAlpha(Mod.moduleManager.getModuleByClass(ClickGui.class).c.getValue().getAlpha()));
            RenderUtil.drawCheckmark(this.x + (float)this.width - 5.0f, this.y + 6.5f, Color.WHITE);
        }
        RenderUtil.drawOutlineRect(this.x + (float)this.width - 7.0f, this.y + 2.0f, this.x + (float)this.width + 4.0f, this.y + (float)this.height - 2.0f, Color.BLACK, 0.1f);
        Mod.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - (float)CascadeGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            Util.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord((SoundEvent)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        this.setting.setValue((Boolean)this.setting.getValue() == false);
    }

    @Override
    public boolean getState() {
        return (Boolean)this.setting.getValue();
    }
}

