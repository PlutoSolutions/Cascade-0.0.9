/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.audio.ISound
 *  net.minecraft.client.audio.PositionedSoundRecord
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundEvent
 *  org.lwjgl.opengl.GL11
 */
package cascade.features.gui.components.items.buttons;

import cascade.Mod;
import cascade.features.gui.CascadeGui;
import cascade.features.gui.components.Component;
import cascade.features.gui.components.items.Item;
import cascade.features.gui.components.items.buttons.BindButton;
import cascade.features.gui.components.items.buttons.BooleanButton;
import cascade.features.gui.components.items.buttons.Button;
import cascade.features.gui.components.items.buttons.ColorButton;
import cascade.features.gui.components.items.buttons.EnumButton;
import cascade.features.gui.components.items.buttons.ParentButton;
import cascade.features.gui.components.items.buttons.Slider;
import cascade.features.gui.components.items.buttons.StringButton;
import cascade.features.modules.Module;
import cascade.features.modules.core.ClickGui;
import cascade.features.setting.Bind;
import cascade.features.setting.ParentSetting;
import cascade.features.setting.Setting;
import cascade.util.ColorUtil;
import cascade.util.RenderUtil;
import cascade.util.Util;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import org.lwjgl.opengl.GL11;

public class ModuleButton
extends Button {
    private final Module module;
    private final ResourceLocation logo = new ResourceLocation("textures/gear.png");
    private List<Item> items = new ArrayList<Item>();
    private boolean subOpen;
    private float _y;
    float currSize;
    float henk;

    public ModuleButton(Module module) {
        super(module.getName());
        this.module = module;
        this.initSettings();
    }

    public static void drawCompleteImage(float posX, float posY, int width, int height) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)posX, (float)posY, (float)0.0f);
        GL11.glBegin((int)7);
        GL11.glTexCoord2f((float)0.0f, (float)0.0f);
        GL11.glVertex3f((float)0.0f, (float)0.0f, (float)0.0f);
        GL11.glTexCoord2f((float)0.0f, (float)1.0f);
        GL11.glVertex3f((float)0.0f, (float)height, (float)0.0f);
        GL11.glTexCoord2f((float)1.0f, (float)1.0f);
        GL11.glVertex3f((float)width, (float)height, (float)0.0f);
        GL11.glTexCoord2f((float)1.0f, (float)0.0f);
        GL11.glVertex3f((float)width, (float)0.0f, (float)0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void initSettings() {
        ArrayList<Item> newItems = new ArrayList<Item>();
        if (!this.module.getSettings().isEmpty()) {
            this.module.getSettings().forEach(setting -> {
                if (setting instanceof ParentSetting) {
                    newItems.add(new ParentButton((ParentSetting)setting));
                    return;
                }
                if (setting.getValue() instanceof Boolean && !setting.getName().equals("Enabled")) {
                    newItems.add(new BooleanButton((Setting)setting));
                }
                if (setting.getValue() instanceof Bind && !setting.getName().equalsIgnoreCase("Keybind") && !this.module.getName().equalsIgnoreCase("Hud")) {
                    newItems.add(new BindButton((Setting)setting));
                }
                if ((setting.getValue() instanceof String || setting.getValue() instanceof Character) && !setting.getName().equalsIgnoreCase("displayName")) {
                    newItems.add(new StringButton((Setting)setting));
                }
                if (setting.getValue() instanceof Color) {
                    newItems.add(new ColorButton((Setting)setting));
                }
                if (setting.isNumberSetting() && setting.hasRestriction()) {
                    newItems.add(new Slider((Setting)setting));
                    return;
                }
                if (!setting.isEnumSetting()) {
                    return;
                }
                newItems.add(new EnumButton((Setting)setting));
            });
        }
        newItems.add(new BindButton(this.module.getSettingByName("Keybind")));
        this.items = newItems;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        int john = ClickGui.getInstance().newWidth.getValue();
        if (this.subOpen) {
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
        if (!this.items.isEmpty()) {
            switch (ClickGui.getInstance().moduleAppender.getValue()) {
                case None: {
                    break;
                }
                case PlusMinus: {
                    if (ClickGui.getInstance().ifSettingsOnly.getValue().booleanValue() && this.items.size() <= 2) break;
                    ModuleButton.mc.fontRenderer.drawStringWithShadow(this.subOpen ? "-" : "+", this.x + (float)this.width - (float)ModuleButton.mc.fontRenderer.getStringWidth(this.subOpen ? "-" : "+") - 2.0f, this.y + (float)this.height / 2.0f - (float)ModuleButton.mc.fontRenderer.FONT_HEIGHT / 2.0f + 1.0f, -1);
                    break;
                }
                case Gears: {
                    if (ClickGui.getInstance().ifSettingsOnly.getValue().booleanValue() && this.items.size() <= 2) break;
                    mc.getTextureManager().bindTexture(this.logo);
                    ModuleButton.drawCompleteImage(this.x - 1.5f + (float)this.width - 7.4f, this.y - 2.2f - (float)CascadeGui.getClickGui().getTextOffset(), 8, 8);
                    break;
                }
                case CustomText: {
                    if (ClickGui.getInstance().ifSettingsOnly.getValue().booleanValue() && this.items.size() <= 2) break;
                    ModuleButton.mc.fontRenderer.drawStringWithShadow(this.subOpen ? ClickGui.getInstance().opened.getValue() : ClickGui.getInstance().closed.getValue(), this.x + (float)this.width - (float)ModuleButton.mc.fontRenderer.getStringWidth(this.subOpen ? ClickGui.getInstance().opened.getValue() : ClickGui.getInstance().closed.getValue()) - 2.0f, this.y + (float)this.height / 2.0f - (float)ModuleButton.mc.fontRenderer.FONT_HEIGHT / 2.0f + 1.0f, -1);
                    break;
                }
                case New: {
                    ModuleButton.drawAppender(this.x + (float)this.width - 7.0f, this.y + (float)this.height / 2.0f, john, john, this.currSize, this.currSize);
                }
            }
            if (ClickGui.getInstance().moduleOutline.getValue().booleanValue()) {
                RenderUtil.drawOutlineRect(this.x, this.y + (float)this.height, this.x + (float)this.width, this.y, Color.BLACK, 1.0f);
            }
            if (this.subOpen || this.henk > 0.0f) {
                if (this.subOpen && this.henk < 15.0f) {
                    this.henk += 0.5f;
                }
                float height = 1.0f;
                for (Item item : this.items) {
                    Component.counter1[0] = Component.counter1[0] + 1;
                    if (!item.isHidden()) {
                        item.setLocation(this.x + 1.0f, this.y + (height += this.henk));
                        item.setHeight((int)this.henk);
                        item.setWidth(this.width - 9);
                        item.drawScreen(mouseX, mouseY, partialTicks);
                        this._y = height;
                        if (item instanceof ColorButton && ((ColorButton)item).setting.isOpen) {
                            height += 110.0f;
                        }
                        if (item instanceof EnumButton && ((EnumButton)item).setting.isOpen) {
                            height += (float)(((EnumButton)item).setting.getValue().getClass().getEnumConstants().length * 15);
                        }
                    }
                    item.update();
                }
                if (this.module.isEnabled()) {
                    RenderUtil.drawOutlineRect(this.x, this.y + 1.0f, this.x + (float)this.width, this.y + (this._y + 16.0f), this.getState() ? Mod.colorManager.getColorWithAlphaColor(Mod.moduleManager.getModuleByClass(ClickGui.class).c.getValue().getAlpha()) : Color.BLACK, 2.0f);
                } else {
                    RenderUtil.drawOutlineRect(this.x, this.y, this.x + (float)this.width, this.y + (this._y + 16.0f), Color.BLACK, 1.0f);
                }
            }
            if (!this.subOpen && this.henk > 0.0f) {
                this.henk -= 0.5f;
            }
        }
        if (this.isHovering(mouseX, mouseY) && ClickGui.getInstance().isEnabled() && ClickGui.getInstance().descriptions.getValue().booleanValue()) {
            if (ClickGui.getInstance().rect.getValue().booleanValue()) {
                RenderUtil.drawRect(mouseX + 10, mouseY, mouseX + 10 + this.renderer.getStringWidth(this.module.getDescription()), mouseY + 10, ColorUtil.toRGBA(0, 0, 0, 100));
            }
            if (ClickGui.getInstance().outline.getValue().booleanValue()) {
                RenderUtil.drawOutlineRect(mouseX + 10, mouseY, mouseX + 10 + this.renderer.getStringWidth(this.module.getDescription()), mouseY + 10, new Color(ColorUtil.toRGBA(ClickGui.getInstance().c.getValue().getRed(), ClickGui.getInstance().c.getValue().getGreen(), ClickGui.getInstance().c.getValue().getBlue(), 255)), 1.0f);
            }
            this.renderer.drawStringWithShadow(this.module.getDescription(), mouseX + 10, mouseY, -1);
        }
    }

    public static void drawAppender(float x, float y, int leftWidth, int rightWidth, float bottomHeight, float topHeight) {
        RenderUtil.drawRect(x - (float)leftWidth, y, x, y + 1.0f, -1);
        RenderUtil.drawRect(x, y, x + 1.0f + (float)rightWidth, y + 1.0f, -1);
        RenderUtil.drawRect(x, y - topHeight, x + 1.0f, y, -1);
        RenderUtil.drawRect(x, y, x + 1.0f, y + 1.0f + bottomHeight, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.items.isEmpty()) {
            if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
                this.subOpen = !this.subOpen;
                Util.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord((SoundEvent)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
            }
            if (this.subOpen) {
                for (Item item : this.items) {
                    if (item.isHidden()) continue;
                    item.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        super.onKeyTyped(typedChar, keyCode);
        if (!this.items.isEmpty() && this.subOpen) {
            for (Item item : this.items) {
                if (item.isHidden()) continue;
                item.onKeyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public int getHeight() {
        if (this.subOpen || this.henk > 0.0f) {
            int height = (int)this.henk - 1;
            for (Item item : this.items) {
                if (item.isHidden()) continue;
                height = item instanceof ColorButton && ((ColorButton)item).setting.isOpen ? (int)((float)height + (this.henk + 110.0f)) : (int)((float)height + this.henk);
                if (!(item instanceof EnumButton) || !((EnumButton)item).setting.isOpen) continue;
                height = (int)((float)height + (float)((EnumButton)item).setting.getValue().getClass().getEnumConstants().length * this.henk);
            }
            return height;
        }
        return (int)(15.0f - this.henk) - 1;
    }

    public Module getModule() {
        return this.module;
    }

    @Override
    public void toggle() {
        this.module.toggle();
    }

    @Override
    public boolean getState() {
        return this.module.isEnabled();
    }
}

