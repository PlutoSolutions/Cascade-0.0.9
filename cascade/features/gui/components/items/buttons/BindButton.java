/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
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
import cascade.features.setting.Bind;
import cascade.features.setting.Setting;
import cascade.util.ColorUtil;
import cascade.util.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

public class BindButton
extends Button {
    private final Setting setting;
    public boolean isListening;

    public BindButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int color = ColorUtil.toARGB(ClickGui.getInstance().c.getValue().getRed(), ClickGui.getInstance().c.getValue().getGreen(), ClickGui.getInstance().c.getValue().getBlue(), ClickGui.getInstance().c.getValue().getAlpha());
        RenderUtil.drawRect(this.x, this.y, this.x + (float)this.width + 7.4f, this.y + (float)this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515) : (!this.isHovering(mouseX, mouseY) ? Mod.colorManager.getColorWithAlpha(Mod.moduleManager.getModuleByClass(ClickGui.class).hoverA.getValue()) : Mod.colorManager.getColorWithAlpha(Mod.moduleManager.getModuleByClass(ClickGui.class).c.getValue().getAlpha())));
        RenderUtil.drawOutlineRect(this.x, this.y, this.x + (float)this.width + 6.8f, this.y + (float)this.height - 0.5f, Color.BLACK, 1.0f);
        if (this.isListening) {
            Mod.textManager.drawStringWithShadow("Press a Key...", this.x + 2.3f, this.y - 1.7f - (float)CascadeGui.getClickGui().getTextOffset(), -1);
        } else {
            Mod.textManager.drawStringWithShadow(this.setting.getName() + " " + (Object)ChatFormatting.GRAY + this.setting.getValue().toString().toUpperCase(), this.x + 2.3f, this.y - 1.7f - (float)CascadeGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        }
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord((SoundEvent)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (this.isListening) {
            Bind bind = new Bind(keyCode);
            if (bind.toString().equalsIgnoreCase("Escape")) {
                return;
            }
            if (bind.toString().equalsIgnoreCase("Delete")) {
                bind = new Bind(-1);
            }
            this.setting.setValue(bind);
            this.onMouseClick();
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        this.isListening = !this.isListening;
    }

    @Override
    public boolean getState() {
        return !this.isListening;
    }
}

