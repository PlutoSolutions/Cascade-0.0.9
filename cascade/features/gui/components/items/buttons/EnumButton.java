/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.util.ResourceLocation
 */
package cascade.features.gui.components.items.buttons;

import cascade.Mod;
import cascade.features.gui.CascadeGui;
import cascade.features.gui.components.items.buttons.Button;
import cascade.features.gui.components.items.buttons.ModuleButton;
import cascade.features.modules.core.ClickGui;
import cascade.features.setting.Setting;
import cascade.util.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import net.minecraft.util.ResourceLocation;

public class EnumButton
extends Button {
    public Setting setting;
    float currSize;
    ResourceLocation logo = new ResourceLocation("textures/gear.png");

    public EnumButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int john = ClickGui.getInstance().newWidth.getValue();
        if (this.setting.isOpen) {
            if (this.currSize > 0.0f) {
                this.currSize -= 0.1f;
            }
        } else {
            if (this.currSize < (float)john) {
                this.currSize += 0.1f;
            }
            if (this.currSize != (float)john && this.currSize > (float)john) {
                this.currSize -= 0.1f;
            }
        }
        RenderUtil.drawRect(this.x, this.y, this.x + (float)this.width + 7.4f, this.y + (float)this.height - 0.5f, !this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515);
        Mod.textManager.drawStringWithShadow(this.setting.getName() + " " + (Object)ChatFormatting.GRAY + (this.setting.currentEnumName().equalsIgnoreCase("ABC") ? "ABC" : this.setting.currentEnumName()), this.x + 2.3f, this.y - 1.7f - (float)CascadeGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        int y = (int)this.y;
        if (this.setting.isOpen) {
            for (Object obber : this.setting.getValue().getClass().getEnumConstants()) {
                if ((float)mouseX > this.x && (float)mouseX < this.x + (float)this.width && mouseY > (y += this.height) && mouseY < y + this.height) {
                    RenderUtil.drawRect(this.x + 2.0f, y, this.x + (float)this.width + 7.0f, y + this.height, new Color(1677721601, true).getRGB());
                }
                EnumButton.mc.fontRenderer.drawStringWithShadow((Object)(this.setting.getValueAsString().equals(obber.toString()) ? ChatFormatting.WHITE : ChatFormatting.GRAY) + obber.toString(), this.x + 4.0f, (float)y + (float)this.height / 2.0f - (float)EnumButton.mc.fontRenderer.FONT_HEIGHT / 2.0f, -1);
            }
            RenderUtil.drawOutlineRect(this.x + 3.0f, this.y + (float)this.height - 1.0f, this.x + (float)this.width + 5.0f, y + this.height - 2, new Color(1), 1.0f);
        }
        switch (ClickGui.getInstance().moduleAppender.getValue()) {
            case None: {
                break;
            }
            case PlusMinus: {
                EnumButton.mc.fontRenderer.drawStringWithShadow(this.setting.isOpen ? "-" : "+", this.x + (float)this.width - (float)EnumButton.mc.fontRenderer.getStringWidth(this.setting.isOpen ? "-" : "+") + 5.0f, this.y + (float)this.height / 2.0f - (float)EnumButton.mc.fontRenderer.FONT_HEIGHT / 2.0f + 1.0f, -1);
                break;
            }
            case Gears: {
                mc.getTextureManager().bindTexture(this.logo);
                ModuleButton.drawCompleteImage(this.x + 6.0f + (float)this.width - 7.4f, this.y - 2.2f - (float)CascadeGui.getClickGui().getTextOffset(), 8, 8);
                break;
            }
            case CustomText: {
                EnumButton.mc.fontRenderer.drawStringWithShadow(this.setting.isOpen ? ClickGui.getInstance().opened.getValue() : ClickGui.getInstance().closed.getValue(), this.x + (float)this.width - (float)EnumButton.mc.fontRenderer.getStringWidth(this.setting.isOpen ? ClickGui.getInstance().opened.getValue() : ClickGui.getInstance().closed.getValue()) + 5.0f, this.y + (float)this.height / 2.0f - (float)EnumButton.mc.fontRenderer.FONT_HEIGHT / 2.0f + 1.0f, -1);
                break;
            }
            case New: {
                this.drawAppender(this.x + (float)this.width + 1.0f, this.y + (float)this.height / 2.0f, john, john, this.currSize, this.currSize);
            }
        }
    }

    public void drawAppender(float x, float y, int leftWidth, int rightWidth, float bottomHeight, float topHeight) {
        RenderUtil.drawRect(x - (float)leftWidth, y, x, y + 1.0f, -1);
        RenderUtil.drawRect(x, y, x + 1.0f + (float)rightWidth, y + 1.0f, -1);
        RenderUtil.drawRect(x, y - topHeight, x + 1.0f, y, -1);
        RenderUtil.drawRect(x, y, x + 1.0f, y + 1.0f + bottomHeight, -1);
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int y = (int)this.y;
        if (this.setting.isOpen) {
            for (Object obber : this.setting.getValue().getClass().getEnumConstants()) {
                if (!((float)mouseX > this.x) || !((float)mouseX < this.x + (float)this.width) || mouseY <= (y += this.height) || mouseY >= y + this.height || mouseButton != 0) continue;
                this.setting.setEnumValue(String.valueOf(obber));
            }
        }
        if (this.isHovering(mouseX, mouseY) && mouseButton == 1) {
            this.setting.isOpen = !this.setting.isOpen;
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public boolean getState() {
        return true;
    }
}

