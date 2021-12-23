/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.client.audio.ISound
 *  net.minecraft.client.audio.PositionedSoundRecord
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.util.ChatAllowedCharacters
 *  net.minecraft.util.SoundEvent
 */
package cascade.features.gui.components.items.buttons;

import cascade.Mod;
import cascade.features.gui.CascadeGui;
import cascade.features.gui.components.items.buttons.Button;
import cascade.features.setting.Setting;
import cascade.util.RenderUtil;
import cascade.util.Util;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.SoundEvent;

public class StringButton
extends Button {
    private final Setting setting;
    public boolean isListening;
    private CurrentString currentString = new CurrentString("");

    public StringButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + (float)this.width + 7.4f, this.y + (float)this.height - 0.5f, !this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515);
        RenderUtil.drawOutlineRect(this.x, this.y + 1.0f, this.x + (float)this.width + 6.8f, this.y + (float)this.height - 0.5f, Color.BLACK, 1.0f);
        if (this.isListening) {
            Mod.textManager.drawStringWithShadow(this.currentString.getString() + Mod.textManager.getIdleSign(), this.x + 2.3f, this.y - 1.7f - (float)CascadeGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        } else {
            Mod.textManager.drawStringWithShadow((this.setting.getName().equals("Buttons") ? "Buttons " : (this.setting.getName().equals("Prefix") ? "Prefix  " + (Object)ChatFormatting.GRAY : "")) + this.setting.getValue(), this.x + 2.3f, this.y - 1.7f - (float)CascadeGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            Util.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord((SoundEvent)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (this.isListening) {
            switch (keyCode) {
                case 1: {
                    return;
                }
                case 28: {
                    this.enterString();
                }
                case 14: {
                    this.setString(StringButton.removeLastChar(this.currentString.getString()));
                }
            }
            if (ChatAllowedCharacters.isAllowedCharacter((char)typedChar)) {
                this.setString(this.currentString.getString() + typedChar);
            }
        }
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    private void enterString() {
        if (this.currentString.getString().isEmpty()) {
            this.setting.setValue(this.setting.getDefaultValue());
        } else {
            this.setting.setValue(this.currentString.getString());
        }
        this.setString("");
        this.onMouseClick();
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

    public void setString(String newString) {
        this.currentString = new CurrentString(newString);
    }

    public static class CurrentString {
        private final String string;

        public CurrentString(String string) {
            this.string = string;
        }

        public String getString() {
            return this.string;
        }
    }
}

