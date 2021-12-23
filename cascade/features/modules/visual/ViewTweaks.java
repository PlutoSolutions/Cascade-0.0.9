/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.settings.GameSettings$Options
 *  net.minecraft.inventory.ItemStackHelper
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.event.EntityViewRenderEvent$FOVModifier
 *  net.minecraftforge.client.event.EntityViewRenderEvent$FogColors
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.visual;

import cascade.event.events.PerspectiveEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.ColorUtil;
import cascade.util.RenderUtil;
import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ViewTweaks
extends Module {
    public Setting<Boolean> fullBright = this.register(new Setting<Boolean>("FullBright", true));
    public Setting<Boolean> shulkerViewer = this.register(new Setting<Boolean>("ShulkerViewer", true));
    public Setting<Color> textc = this.register(new Setting<Object>("TextColor", new Color(-1), v -> this.shulkerViewer.getValue()));
    public Setting<Swing> swing = this.register(new Setting<Swing>("Swing", Swing.Mainhand));
    public Setting<Boolean> fovChanger = this.register(new Setting<Boolean>("FovChanger", false));
    public Setting<Boolean> stay = this.register(new Setting<Object>("Stay", Boolean.valueOf(false), v -> this.fovChanger.getValue()));
    public Setting<Integer> fov = this.register(new Setting<Object>("Fov", Integer.valueOf(137), Integer.valueOf(-180), Integer.valueOf(180), v -> this.fovChanger.getValue()));
    public Setting<Boolean> skyChanger = this.register(new Setting<Boolean>("SkyChanger", false));
    public Setting<Color> c = this.register(new Setting<Object>("Color", new Color(-1), v -> this.skyChanger.getValue()));
    public Setting<Boolean> aspect = this.register(new Setting<Boolean>("Aspect", false));
    public Setting<Float> aspectValue = this.register(new Setting<Object>("Value", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(3.0f), v -> this.aspect.getValue()));
    private static ViewTweaks INSTANCE = new ViewTweaks();
    float originalBrightness;
    private static final ResourceLocation SHULKER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");

    public ViewTweaks() {
        super("ViewTweaks", Module.Category.VISUAL, "Tweaks for visual stuff");
        this.setInstance();
    }

    public static ViewTweaks getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ViewTweaks();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.fovChanger.getValue().booleanValue() && !this.stay.getValue().booleanValue()) {
            ViewTweaks.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, (float)this.fov.getValue().intValue());
        }
        if (this.fullBright.getValue().booleanValue() && ViewTweaks.mc.gameSettings.gammaSetting != 42069.0f) {
            ViewTweaks.mc.gameSettings.gammaSetting = 42069.0f;
        }
    }

    @Override
    public void onEnable() {
        this.originalBrightness = ViewTweaks.mc.gameSettings.gammaSetting;
        if (this.fullBright.getValue().booleanValue()) {
            ViewTweaks.mc.gameSettings.gammaSetting = 42069.0f;
        }
    }

    @Override
    public void onDisable() {
        ViewTweaks.mc.gameSettings.gammaSetting = this.originalBrightness;
    }

    @SubscribeEvent
    public void onFogColor(EntityViewRenderEvent.FogColors event) {
        if (this.skyChanger.getValue().booleanValue() && this.isEnabled() && !ViewTweaks.fullNullCheck()) {
            event.setRed((float)this.c.getValue().getRed() / 255.0f);
            event.setGreen((float)this.c.getValue().getGreen() / 255.0f);
            event.setBlue((float)this.c.getValue().getBlue() / 255.0f);
        }
    }

    @SubscribeEvent
    public void onFovChange(EntityViewRenderEvent.FOVModifier event) {
        if (this.fovChanger.getValue().booleanValue() && this.stay.getValue().booleanValue() && this.isEnabled()) {
            event.setFOV((float)this.fov.getValue().intValue());
        }
    }

    @SubscribeEvent
    public void onPerspectiveEvent(PerspectiveEvent event) {
        if (this.aspect.getValue().booleanValue() && this.isEnabled()) {
            event.setAspect(this.aspectValue.getValue().floatValue());
        }
    }

    public void renderShulkerToolTip(ItemStack stack, int x, int y, String name) {
        NBTTagCompound blockEntityTag;
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10) && (blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag")).hasKey("Items", 9)) {
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            mc.getTextureManager().bindTexture(SHULKER_GUI_TEXTURE);
            RenderUtil.drawTexturedRect(x, y, 0, 0, 176, 16, 500);
            RenderUtil.drawTexturedRect(x, y + 16, 0, 16, 176, 54, 500);
            RenderUtil.drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
            GlStateManager.disableDepth();
            Color color = new Color(0, 0, 0, 255);
            color = new Color(this.textc.getValue().getRed(), this.textc.getValue().getGreen(), this.textc.getValue().getBlue(), this.textc.getValue().getAlpha());
            this.renderer.drawStringWithShadow(name == null ? stack.getDisplayName() : name, x + 8, y + 6, ColorUtil.toRGBA(color));
            GlStateManager.enableDepth();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            NonNullList nonnulllist = NonNullList.withSize((int)27, (Object)ItemStack.EMPTY);
            ItemStackHelper.loadAllItems((NBTTagCompound)blockEntityTag, (NonNullList)nonnulllist);
            for (int i = 0; i < nonnulllist.size(); ++i) {
                int iX = x + i % 9 * 18 + 8;
                int iY = y + i / 9 * 18 + 18;
                ItemStack itemStack = (ItemStack)nonnulllist.get(i);
                ViewTweaks.mc.getRenderItem().zLevel = 501.0f;
                RenderUtil.itemRender.renderItemAndEffectIntoGUI(itemStack, iX, iY);
                RenderUtil.itemRender.renderItemOverlayIntoGUI(ViewTweaks.mc.fontRenderer, itemStack, iX, iY, (String)null);
                ViewTweaks.mc.getRenderItem().zLevel = 0.0f;
            }
            GlStateManager.disableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
    }

    public static enum Swing {
        Mainhand,
        Offhand,
        Packet;

    }
}

