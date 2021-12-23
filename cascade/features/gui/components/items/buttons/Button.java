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
import cascade.features.gui.components.Component;
import cascade.features.gui.components.items.Item;
import cascade.features.modules.core.ClickGui;
import cascade.util.RenderUtil;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

public class Button
extends Item {
    boolean state;
    float currWidth;

    public Button(String name) {
        super(name);
        this.height = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.getState()) {
            if (this.currWidth < (float)(this.width - 3)) {
                this.currWidth += 4.0f;
            }
        } else if (this.currWidth > 0.0f) {
            this.currWidth -= 4.0f;
        }
        RenderUtil.drawRect(this.x + this.currWidth, this.y, this.x + (float)this.width, this.y + (float)this.height, this.isHovering(mouseX, mouseY) ? -2007673515 : 0x11555555);
        RenderUtil.drawRect(this.x, this.y, this.x + this.currWidth, this.y + (float)this.height, this.isHovering(mouseX, mouseY) ? Mod.colorManager.getColorWithAlpha(Mod.moduleManager.getModuleByClass(ClickGui.class).c.getValue().getAlpha()) : Mod.colorManager.getColorWithAlpha(Mod.moduleManager.getModuleByClass(ClickGui.class).hoverA.getValue()));
        Mod.textManager.drawStringWithShadow(this.getName(), this.x + (this.isHovering(mouseX, mouseY) ? 3.3f : 2.3f), this.y - 2.0f - (float)CascadeGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.onMouseClick();
        }
    }

    public void onMouseClick() {
        this.state = !this.state;
        this.toggle();
        mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord((SoundEvent)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
    }

    public void toggle() {
    }

    public boolean getState() {
        return this.state;
    }

    @Override
    public int getHeight() {
        return 14;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : CascadeGui.getClickGui().getComponents()) {
            if (!component.drag) continue;
            return false;
        }
        return (float)mouseX >= this.getX() && (float)mouseX <= this.getX() + (float)this.getWidth() && (float)mouseY >= this.getY() && (float)mouseY <= this.getY() + (float)this.height;
    }
}

