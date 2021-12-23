/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.audio.ISound
 *  net.minecraft.client.audio.PositionedSoundRecord
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundEvent
 */
package cascade.features.gui.components.items.buttons;

import cascade.Mod;
import cascade.features.gui.CascadeGui;
import cascade.features.gui.components.items.buttons.Button;
import cascade.features.gui.components.items.buttons.ModuleButton;
import cascade.features.modules.core.ClickGui;
import cascade.features.setting.ParentSetting;
import cascade.features.setting.Setting;
import cascade.util.RenderUtil;
import java.awt.Color;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class ParentButton
extends Button {
    public ParentSetting parentSetting;
    public ResourceLocation logo = new ResourceLocation("textures/gear.png");
    float currSize;

    public ParentButton(ParentSetting parentSetting) {
        super(parentSetting.getName());
        this.parentSetting = parentSetting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int john = ClickGui.getInstance().newWidth.getValue();
        RenderUtil.drawRect(this.x + 1.0f, this.y, this.x + (float)this.width + 6.0f, this.y + (float)this.height, this.isHovering(mouseX, mouseY) ? Mod.colorManager.getColorWithAlpha(Mod.moduleManager.getModuleByClass(ClickGui.class).c.getValue().getAlpha()) : Mod.colorManager.getColorWithAlpha(Mod.moduleManager.getModuleByClass(ClickGui.class).hoverA.getValue()));
        RenderUtil.drawOutlineRect(this.x + 1.0f, this.y, this.x + (float)this.width + 6.0f, this.y + (float)this.height, new Color(1), 1.0f);
        ParentButton.mc.fontRenderer.drawStringWithShadow(this.parentSetting.getName(), this.x + 3.0f, this.y + (float)this.height / 2.0f - (float)ParentButton.mc.fontRenderer.FONT_HEIGHT / 2.0f + 1.0f, -1);
        if (this.parentSetting.isOpened()) {
            if (this.currSize > 0.0f) {
                this.currSize -= 0.1f;
            }
            int i = 0;
            for (Setting setting : this.parentSetting.getChildren()) {
                if (setting.isVisible()) {
                    i += 15;
                }
                if (setting.getValue() instanceof Color && setting.isOpen) {
                    i += 110;
                }
                if (!(setting.getValue() instanceof Enum) || !setting.isOpen) continue;
                i += setting.getValue().getClass().getEnumConstants().length * 15;
            }
            RenderUtil.drawOutlineRect(this.x + 1.0f, this.y + 1.0f, this.x + (float)this.width + 6.0f, this.y + (float)this.height + (float)i, new Color(Mod.colorManager.getColorWithAlpha(Mod.moduleManager.getModuleByClass(ClickGui.class).hoverA.getValue())), 2.0f);
        } else {
            if (this.currSize < (float)john) {
                this.currSize += 0.1f;
            }
            if (this.currSize != (float)john && this.currSize > (float)john) {
                this.currSize -= 0.1f;
            }
        }
        switch (ClickGui.getInstance().moduleAppender.getValue()) {
            case None: {
                break;
            }
            case PlusMinus: {
                ParentButton.mc.fontRenderer.drawStringWithShadow(this.parentSetting.isOpened() ? "-" : "+", this.x + (float)this.width - (float)ParentButton.mc.fontRenderer.getStringWidth(this.parentSetting.isOpened() ? "-" : "+") + 5.0f, this.y + (float)this.height / 2.0f - (float)ParentButton.mc.fontRenderer.FONT_HEIGHT / 2.0f + 1.0f, -1);
                break;
            }
            case Gears: {
                mc.getTextureManager().bindTexture(this.logo);
                ModuleButton.drawCompleteImage(this.x + 4.0f + (float)this.width - 7.4f, this.y - 2.2f - (float)CascadeGui.getClickGui().getTextOffset(), 8, 8);
                break;
            }
            case CustomText: {
                ParentButton.mc.fontRenderer.drawStringWithShadow(this.parentSetting.isOpened() ? ClickGui.getInstance().opened.getValue() : ClickGui.getInstance().closed.getValue(), this.x + (float)this.width - (float)ParentButton.mc.fontRenderer.getStringWidth(this.parentSetting.isOpened() ? ClickGui.getInstance().opened.getValue() : ClickGui.getInstance().closed.getValue()) + 5.0f, this.y + (float)this.height / 2.0f - (float)ParentButton.mc.fontRenderer.FONT_HEIGHT / 2.0f + 1.0f, -1);
                break;
            }
            case New: {
                ModuleButton.drawAppender(this.x + (float)this.width + 1.0f, this.y + (float)this.height / 2.0f, john, john, this.currSize, this.currSize);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.isHovering(mouseX, mouseY) && mouseButton == 1) {
            this.parentSetting.setOpened((Boolean)this.parentSetting.getValue() == false);
            mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord((SoundEvent)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
        }
    }
}

