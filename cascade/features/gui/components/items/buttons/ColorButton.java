/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.client.audio.ISound
 *  net.minecraft.client.audio.PositionedSoundRecord
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.util.SoundEvent
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.GL11
 */
package cascade.features.gui.components.items.buttons;

import cascade.Mod;
import cascade.features.command.Command;
import cascade.features.gui.CascadeGui;
import cascade.features.gui.components.items.buttons.Button;
import cascade.features.setting.Setting;
import cascade.util.RenderUtil;
import cascade.util.Util;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ColorButton
extends Button {
    Setting setting;
    private Color finalColor;
    boolean pickingColor = false;
    boolean pickingHue = false;
    boolean pickingAlpha = false;
    public static Tessellator tessellator = Tessellator.getInstance();
    public static BufferBuilder builder = tessellator.getBuffer();

    public ColorButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.finalColor = (Color)setting.getValue();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            RenderUtil.drawRect(this.x + (float)this.width - 5.0f, this.y + 2.0f, this.x + (float)this.width + 7.0f, this.y + (float)this.height - 2.0f, this.finalColor.getRGB());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        RenderUtil.drawRect(this.x, this.y, this.x + (float)this.width + 7.4f, this.y + (float)this.height - 0.5f, !this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515);
        RenderUtil.drawOutlineRect(this.x + (float)this.width - 5.0f, this.y + 2.0f, this.x + (float)this.width + 7.0f, this.y + (float)this.height - 2.0f, Color.BLACK, 0.1f);
        Mod.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - (float)CascadeGui.getClickGui().getTextOffset(), -1);
        if (this.setting.isOpen) {
            this.drawPicker(this.setting, (int)this.x - 1, (int)this.y + 15, (int)this.x, (int)this.y + 103, (int)this.x, (int)this.y + 95, mouseX, mouseY);
            Mod.textManager.drawStringWithShadow(this.isInsideCopy(mouseX, mouseY) ? (Object)ChatFormatting.UNDERLINE + "Copy" : "Copy", this.x + 1.0f, this.y + 113.0f, -1);
            Mod.textManager.drawStringWithShadow(this.isInsidePaste(mouseX, mouseY) ? (Object)ChatFormatting.UNDERLINE + "Paste" : "Paste", this.x + 30.0f, this.y + 113.0f, -1);
            this.setting.setValue(this.finalColor);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
            Util.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord((SoundEvent)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
            boolean bl = this.setting.isOpen = !this.setting.isOpen;
        }
        if (mouseButton == 0 && this.isInsideCopy(mouseX, mouseY) && this.setting.isOpen) {
            Util.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord((SoundEvent)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
            String hex = String.format("#%06x", ((Color)this.setting.getValue()).getRGB() & 0xFFFFFF);
            StringSelection selection = new StringSelection(hex);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            Command.sendMessage("Color has been successfully copied to clipboard!", true, false);
        }
        if (mouseButton == 0 && this.isInsidePaste(mouseX, mouseY) && this.setting.isOpen) {
            try {
                if (ColorButton.readClipboard() != null) {
                    if (Objects.requireNonNull(ColorButton.readClipboard()).startsWith("#")) {
                        this.setting.setValue(Color.decode(Objects.requireNonNull(ColorButton.readClipboard())));
                    } else {
                        String[] color = ColorButton.readClipboard().split(",");
                        this.setting.setValue(new Color(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));
                    }
                }
            }
            catch (NumberFormatException e) {
                Command.sendMessage("Not a color format! Available color formats:", true, false);
                Command.sendMessage("RGB: (red),(green),(blue)", true, false);
                Command.sendMessage("HEX: #FFFFFF - must start with a hashtag '#'", true, false);
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        this.pickingAlpha = false;
        this.pickingHue = false;
        this.pickingColor = false;
    }

    public boolean isInsideCopy(int mouseX, int mouseY) {
        return ColorButton.mouseOver((int)this.x + 1, (int)this.y + 113, (int)(this.x + 1.0f) + Mod.textManager.getStringWidth("Copy"), (int)(this.y + 112.0f) + Mod.textManager.getFontHeight(), mouseX, mouseY);
    }

    public boolean isInsidePaste(int mouseX, int mouseY) {
        return ColorButton.mouseOver((int)this.x + 30, (int)this.y + 113, (int)(this.x + 30.0f) + Mod.textManager.getStringWidth("Paste"), (int)(this.y + 112.0f) + Mod.textManager.getFontHeight(), mouseX, mouseY);
    }

    public void drawPicker(Setting setting, int pickerX, int pickerY, int hueSliderX, int hueSliderY, int alphaSliderX, int alphaSliderY, int mouseX, int mouseY) {
        float restrictedX;
        float[] color = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
        try {
            color = new float[]{Color.RGBtoHSB(((Color)setting.getValue()).getRed(), ((Color)setting.getValue()).getGreen(), ((Color)setting.getValue()).getBlue(), null)[0], Color.RGBtoHSB(((Color)setting.getValue()).getRed(), ((Color)setting.getValue()).getGreen(), ((Color)setting.getValue()).getBlue(), null)[1], Color.RGBtoHSB(((Color)setting.getValue()).getRed(), ((Color)setting.getValue()).getGreen(), ((Color)setting.getValue()).getBlue(), null)[2], (float)((Color)setting.getValue()).getAlpha() / 255.0f};
        }
        catch (Exception exception) {
            // empty catch block
        }
        int pickerWidth = 115;
        int pickerHeight = 78;
        int hueSliderWidth = pickerWidth + 3;
        int hueSliderHeight = 7;
        int alphaSliderHeight = 7;
        if (!(!this.pickingColor || Mouse.isButtonDown((int)0) && ColorButton.mouseOver(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, mouseX, mouseY))) {
            this.pickingColor = false;
        }
        if (!(!this.pickingHue || Mouse.isButtonDown((int)0) && ColorButton.mouseOver(hueSliderX, hueSliderY, hueSliderX + hueSliderWidth, hueSliderY + hueSliderHeight, mouseX, mouseY))) {
            this.pickingHue = false;
        }
        if (!(!this.pickingAlpha || Mouse.isButtonDown((int)0) && ColorButton.mouseOver(alphaSliderX, alphaSliderY, alphaSliderX + pickerWidth, alphaSliderY + alphaSliderHeight, mouseX, mouseY))) {
            this.pickingAlpha = false;
        }
        if (Mouse.isButtonDown((int)0) && ColorButton.mouseOver(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, mouseX, mouseY)) {
            this.pickingColor = true;
        }
        if (Mouse.isButtonDown((int)0) && ColorButton.mouseOver(hueSliderX, hueSliderY, hueSliderX + hueSliderWidth, hueSliderY + hueSliderHeight, mouseX, mouseY)) {
            this.pickingHue = true;
        }
        if (Mouse.isButtonDown((int)0) && ColorButton.mouseOver(alphaSliderX, alphaSliderY, alphaSliderX + pickerWidth, alphaSliderY + alphaSliderHeight, mouseX, mouseY)) {
            this.pickingAlpha = true;
        }
        if (this.pickingHue) {
            restrictedX = Math.min(Math.max(hueSliderX, mouseX), hueSliderX + hueSliderWidth);
            color[0] = (restrictedX - (float)hueSliderX) / (float)hueSliderWidth;
        }
        if (this.pickingAlpha) {
            restrictedX = Math.min(Math.max(alphaSliderX, mouseX), alphaSliderX + pickerWidth);
            color[3] = 1.0f - (restrictedX - (float)alphaSliderX) / (float)pickerWidth;
        }
        if (this.pickingColor) {
            restrictedX = Math.min(Math.max(pickerX, mouseX), pickerX + pickerWidth);
            float restrictedY = Math.min(Math.max(pickerY, mouseY), pickerY + pickerHeight);
            color[1] = (restrictedX - (float)pickerX) / (float)pickerWidth;
            color[2] = 1.0f - (restrictedY - (float)pickerY) / (float)pickerHeight;
        }
        int selectedColor = Color.HSBtoRGB(color[0], 1.0f, 1.0f);
        float selectedRed = (float)(selectedColor >> 16 & 0xFF) / 255.0f;
        float selectedGreen = (float)(selectedColor >> 8 & 0xFF) / 255.0f;
        float selectedBlue = (float)(selectedColor & 0xFF) / 255.0f;
        ColorButton.drawPickerBase(pickerX, pickerY, pickerWidth, pickerHeight, selectedRed, selectedGreen, selectedBlue, color[3]);
        this.drawHueSlider(hueSliderX, hueSliderY, hueSliderWidth - 2, hueSliderHeight, color[0]);
        int cursorX = (int)((float)pickerX + color[1] * (float)pickerWidth);
        int cursorY = (int)((float)(pickerY + pickerHeight) - color[2] * (float)pickerHeight);
        RenderUtil.drawOutlineRect(cursorX - 2, cursorY - 2, cursorX + 2, cursorY + 2, Color.black, 1.0f);
        Gui.drawRect((int)(cursorX - 2), (int)(cursorY - 2), (int)(cursorX + 2), (int)(cursorY + 2), (int)-1);
        this.drawAlphaSlider(alphaSliderX, alphaSliderY, pickerWidth - 1, alphaSliderHeight, selectedRed, selectedGreen, selectedBlue, color[3]);
        this.finalColor = ColorButton.getColor(new Color(Color.HSBtoRGB(color[0], color[1], color[2])), color[3]);
    }

    public static boolean mouseOver(int minX, int minY, int maxX, int maxY, int mX, int mY) {
        return mX >= minX && mY >= minY && mX <= maxX && mY <= maxY;
    }

    public static Color getColor(Color color, float alpha) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        return new Color(red, green, blue, alpha);
    }

    public static void drawPickerBase(int pickerX, int pickerY, int pickerWidth, int pickerHeight, float red, float green, float blue, float alpha) {
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glShadeModel((int)7425);
        GL11.glBegin((int)9);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glVertex2f((float)pickerX, (float)pickerY);
        GL11.glVertex2f((float)pickerX, (float)(pickerY + pickerHeight));
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        GL11.glVertex2f((float)(pickerX + pickerWidth), (float)(pickerY + pickerHeight));
        GL11.glVertex2f((float)(pickerX + pickerWidth), (float)pickerY);
        GL11.glEnd();
        GL11.glDisable((int)3008);
        GL11.glBegin((int)9);
        GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        GL11.glVertex2f((float)pickerX, (float)pickerY);
        GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glVertex2f((float)pickerX, (float)(pickerY + pickerHeight));
        GL11.glVertex2f((float)(pickerX + pickerWidth), (float)(pickerY + pickerHeight));
        GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        GL11.glVertex2f((float)(pickerX + pickerWidth), (float)pickerY);
        GL11.glEnd();
        GL11.glEnable((int)3008);
        GL11.glShadeModel((int)7424);
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
    }

    public void drawHueSlider(int x, int y, int width, int height, float hue) {
        int step = 0;
        if (height > width) {
            RenderUtil.drawRect(x, y, x + width, y + 4, -65536);
            y += 4;
            for (int colorIndex = 0; colorIndex < 6; ++colorIndex) {
                int previousStep = Color.HSBtoRGB((float)step / 6.0f, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float)(step + 1) / 6.0f, 1.0f, 1.0f);
                ColorButton.drawGradientRect(x, (float)y + (float)step * ((float)height / 6.0f), x + width, (float)y + (float)(step + 1) * ((float)height / 6.0f), previousStep, nextStep, false);
                ++step;
            }
            int sliderMinY = (int)((float)y + (float)height * hue) - 4;
            RenderUtil.drawRect(x, sliderMinY - 1, x + width, sliderMinY + 1, -1);
            RenderUtil.drawOutlineRect(x, sliderMinY - 1, x + width, sliderMinY + 1, Color.BLACK, 1.0f);
        } else {
            for (int colorIndex = 0; colorIndex < 6; ++colorIndex) {
                int previousStep = Color.HSBtoRGB((float)step / 6.0f, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float)(step + 1) / 6.0f, 1.0f, 1.0f);
                ColorButton.gradient(x + step * (width / 6), y, x + (step + 1) * (width / 6), y + height, previousStep, nextStep, true);
                ++step;
            }
            int sliderMinX = (int)((float)x + (float)width * hue);
            RenderUtil.drawRect(sliderMinX - 1, y, sliderMinX + 1, y + height, -1);
            RenderUtil.drawOutlineRect(sliderMinX - 1, y, sliderMinX + 1, y + height, Color.BLACK, 1.0f);
        }
    }

    public void drawAlphaSlider(int x, int y, int width, int height, float red, float green, float blue, float alpha) {
        boolean left = true;
        int checkerBoardSquareSize = height / 2;
        for (int squareIndex = -checkerBoardSquareSize; squareIndex < width; squareIndex += checkerBoardSquareSize) {
            if (!left) {
                RenderUtil.drawRect(x + squareIndex, y, x + squareIndex + checkerBoardSquareSize, y + height, -1);
                RenderUtil.drawRect(x + squareIndex, y + checkerBoardSquareSize, x + squareIndex + checkerBoardSquareSize, y + height, -7303024);
                if (squareIndex < width - checkerBoardSquareSize) {
                    int minX = x + squareIndex + checkerBoardSquareSize;
                    int maxX = Math.min(x + width, x + squareIndex + checkerBoardSquareSize * 2);
                    RenderUtil.drawRect(minX, y, maxX, y + height, -7303024);
                    RenderUtil.drawRect(minX, y + checkerBoardSquareSize, maxX, y + height, -1);
                }
            }
            left = !left;
        }
        ColorButton.drawLeftGradientRect(x, y, x + width, y + height, new Color(red, green, blue, 1.0f).getRGB(), 0);
        int sliderMinX = (int)((float)(x + width) - (float)width * alpha);
        RenderUtil.drawRect(sliderMinX - 1, y, sliderMinX + 1, y + height, -1);
        RenderUtil.drawOutlineRect(sliderMinX - 1, y, sliderMinX + 1, y + height, Color.BLACK, 1.0f);
    }

    public static void drawGradientRect(double leftpos, double top, double right, double bottom, int col1, int col2) {
        float f = (float)(col1 >> 24 & 0xFF) / 255.0f;
        float f2 = (float)(col1 >> 16 & 0xFF) / 255.0f;
        float f3 = (float)(col1 >> 8 & 0xFF) / 255.0f;
        float f4 = (float)(col1 & 0xFF) / 255.0f;
        float f5 = (float)(col2 >> 24 & 0xFF) / 255.0f;
        float f6 = (float)(col2 >> 16 & 0xFF) / 255.0f;
        float f7 = (float)(col2 >> 8 & 0xFF) / 255.0f;
        float f8 = (float)(col2 & 0xFF) / 255.0f;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glShadeModel((int)7425);
        GL11.glPushMatrix();
        GL11.glBegin((int)7);
        GL11.glColor4f((float)f2, (float)f3, (float)f4, (float)f);
        GL11.glVertex2d((double)leftpos, (double)top);
        GL11.glVertex2d((double)leftpos, (double)bottom);
        GL11.glColor4f((float)f6, (float)f7, (float)f8, (float)f5);
        GL11.glVertex2d((double)right, (double)bottom);
        GL11.glVertex2d((double)right, (double)top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glDisable((int)2848);
        GL11.glShadeModel((int)7424);
    }

    public static void drawLeftGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel((int)7425);
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.pos((double)right, (double)top, 0.0).color((float)(endColor >> 24 & 0xFF) / 255.0f, (float)(endColor >> 16 & 0xFF) / 255.0f, (float)(endColor >> 8 & 0xFF) / 255.0f, (float)(endColor >> 24 & 0xFF) / 255.0f).endVertex();
        builder.pos((double)left, (double)top, 0.0).color((float)(startColor >> 16 & 0xFF) / 255.0f, (float)(startColor >> 8 & 0xFF) / 255.0f, (float)(startColor & 0xFF) / 255.0f, (float)(startColor >> 24 & 0xFF) / 255.0f).endVertex();
        builder.pos((double)left, (double)bottom, 0.0).color((float)(startColor >> 16 & 0xFF) / 255.0f, (float)(startColor >> 8 & 0xFF) / 255.0f, (float)(startColor & 0xFF) / 255.0f, (float)(startColor >> 24 & 0xFF) / 255.0f).endVertex();
        builder.pos((double)right, (double)bottom, 0.0).color((float)(endColor >> 24 & 0xFF) / 255.0f, (float)(endColor >> 16 & 0xFF) / 255.0f, (float)(endColor >> 8 & 0xFF) / 255.0f, (float)(endColor >> 24 & 0xFF) / 255.0f).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel((int)7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void gradient(int minX, int minY, int maxX, int maxY, int startColor, int endColor, boolean left) {
        if (left) {
            float startA = (float)(startColor >> 24 & 0xFF) / 255.0f;
            float startR = (float)(startColor >> 16 & 0xFF) / 255.0f;
            float startG = (float)(startColor >> 8 & 0xFF) / 255.0f;
            float startB = (float)(startColor & 0xFF) / 255.0f;
            float endA = (float)(endColor >> 24 & 0xFF) / 255.0f;
            float endR = (float)(endColor >> 16 & 0xFF) / 255.0f;
            float endG = (float)(endColor >> 8 & 0xFF) / 255.0f;
            float endB = (float)(endColor & 0xFF) / 255.0f;
            GL11.glEnable((int)3042);
            GL11.glDisable((int)3553);
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glShadeModel((int)7425);
            GL11.glBegin((int)9);
            GL11.glColor4f((float)startR, (float)startG, (float)startB, (float)startA);
            GL11.glVertex2f((float)minX, (float)minY);
            GL11.glVertex2f((float)minX, (float)maxY);
            GL11.glColor4f((float)endR, (float)endG, (float)endB, (float)endA);
            GL11.glVertex2f((float)maxX, (float)maxY);
            GL11.glVertex2f((float)maxX, (float)minY);
            GL11.glEnd();
            GL11.glShadeModel((int)7424);
            GL11.glEnable((int)3553);
            GL11.glDisable((int)3042);
        } else {
            ColorButton.drawGradientRect(minX, minY, maxX, maxY, startColor, endColor);
        }
    }

    public static int gradientColor(int color, int percentage) {
        int r = ((color & 0xFF0000) >> 16) * (100 + percentage) / 100;
        int g = ((color & 0xFF00) >> 8) * (100 + percentage) / 100;
        int b = (color & 0xFF) * (100 + percentage) / 100;
        return new Color(r, g, b).hashCode();
    }

    public static void drawGradientRect(float left, float top, float right, float bottom, int startColor, int endColor, boolean hovered) {
        if (hovered) {
            startColor = ColorButton.gradientColor(startColor, -20);
            endColor = ColorButton.gradientColor(endColor, -20);
        }
        float c = (float)(startColor >> 24 & 0xFF) / 255.0f;
        float c1 = (float)(startColor >> 16 & 0xFF) / 255.0f;
        float c2 = (float)(startColor >> 8 & 0xFF) / 255.0f;
        float c3 = (float)(startColor & 0xFF) / 255.0f;
        float c4 = (float)(endColor >> 24 & 0xFF) / 255.0f;
        float c5 = (float)(endColor >> 16 & 0xFF) / 255.0f;
        float c6 = (float)(endColor >> 8 & 0xFF) / 255.0f;
        float c7 = (float)(endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel((int)7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, 0.0).color(c1, c2, c3, c).endVertex();
        bufferbuilder.pos((double)left, (double)top, 0.0).color(c1, c2, c3, c).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, 0.0).color(c5, c6, c7, c4).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, 0.0).color(c5, c6, c7, c4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel((int)7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static String readClipboard() {
        try {
            return (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        }
        catch (UnsupportedFlavorException | IOException exception) {
            return null;
        }
    }
}

