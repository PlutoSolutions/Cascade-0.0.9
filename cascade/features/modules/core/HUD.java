/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.gui.GuiChat
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraftforge.event.entity.player.AttackEntityEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.core;

import cascade.Mod;
import cascade.event.events.ClientEvent;
import cascade.event.events.ModuleToggleEvent;
import cascade.event.events.Render2DEvent;
import cascade.features.Feature;
import cascade.features.modules.Module;
import cascade.features.modules.core.ClickGui;
import cascade.features.setting.ParentSetting;
import cascade.features.setting.Setting;
import cascade.util.ColorUtil;
import cascade.util.EntityUtil;
import cascade.util.MathUtil;
import cascade.util.RenderUtil;
import cascade.util.TextUtil;
import cascade.util.Util;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HUD
extends Module {
    private static final ResourceLocation box = new ResourceLocation("textures/gui/container/shulker_box.png");
    private static final ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
    private static HUD INSTANCE = new HUD();
    private final Setting<Boolean> grayNess = this.register(new Setting<Boolean>("Gray", true));
    private final Setting<Boolean> renderingUp = this.register(new Setting<Boolean>("RenderingUp", false));
    private final Setting<Boolean> watermark = this.register(new Setting<Boolean>("Watermark", false));
    public Setting<String> watermarkText = this.register(new Setting<Object>("Text", "Future v2.11.1+12.62e4bf76be", v -> this.watermark.getValue()));
    public Setting<Integer> waterMarkY = this.register(new Setting<Object>("OffsetY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(20), v -> this.watermark.getValue()));
    private final Setting<Boolean> arrayList = this.register(new Setting<Boolean>("ArrayList", false));
    private final Setting<Boolean> alphaStepArrayList = this.register(new Setting<Object>("AlphaStepArrayList", Boolean.valueOf(false), v -> this.arrayList.getValue()));
    public Setting<Integer> index = this.register(new Setting<Object>("Index", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(100), v -> this.arrayList.getValue() != false && this.alphaStepArrayList.getValue() != false));
    public Setting<Integer> countt = this.register(new Setting<Object>("Count", Integer.valueOf(25), Integer.valueOf(0), Integer.valueOf(30), v -> this.arrayList.getValue() != false && this.alphaStepArrayList.getValue() != false));
    public Setting<RenderingMode> renderingMode = this.register(new Setting<Object>("Ordering", (Object)RenderingMode.ABC, v -> this.arrayList.getValue()));
    private final Setting<Boolean> coords = this.register(new Setting<Boolean>("Coords", false));
    private final Setting<Boolean> direction = this.register(new Setting<Boolean>("Direction", false));
    public ParentSetting armorParent = this.registerParent(new ParentSetting("ArmorHud"));
    private final Setting<Boolean> armor = this.register(new Setting<Boolean>("Armor", false).setParent(this.armorParent));
    public Setting<Boolean> customPercentageColor = this.register(new Setting<Boolean>("CustomPercentageColor", Boolean.valueOf(false), v -> this.armor.getValue()).setParent(this.armorParent));
    public Setting<Color> percentageColor = this.register(new Setting<Color>("PercentageColor", new Color(0xFFFFFF), v -> this.armor.getValue() != false && this.customPercentageColor.getValue() != false).setParent(this.armorParent));
    private final Setting<Boolean> totems = this.register(new Setting<Boolean>("Totems", false));
    private final Setting<Welcomer> greeter = this.register(new Setting<Welcomer>("Welcomer", Welcomer.None));
    public Setting<String> message = this.register(new Setting<Object>("Message", "UID:1", v -> this.greeter.getValue() == Welcomer.Custom));
    private final Setting<Boolean> speed = this.register(new Setting<Boolean>("Speed", false));
    public Setting<Integer> speedTicks = this.register(new Setting<Object>("Ticks", Integer.valueOf(20), Integer.valueOf(5), Integer.valueOf(100), v -> this.speed.getValue()));
    public Setting<Boolean> potions = this.register(new Setting<Boolean>("Potions", false));
    private final Setting<Boolean> ping = this.register(new Setting<Boolean>("Ping", false));
    private final Setting<Boolean> tps = this.register(new Setting<Boolean>("TPS", false));
    private final Setting<Boolean> fps = this.register(new Setting<Boolean>("FPS", false));
    public Setting<Boolean> time = this.register(new Setting<Boolean>("Time", false));
    private final Setting<Boolean> lag = this.register(new Setting<Boolean>("LagNotifier", false));
    public Setting<Integer> lagTime = this.register(new Setting<Object>("LagTime", Integer.valueOf(1000), Integer.valueOf(0), Integer.valueOf(2000), v -> this.lag.getValue()));
    private final Map<String, Integer> players = new HashMap<String, Integer>();
    public Setting<String> command = this.register(new Setting<String>("Command", "Trollhack"));
    public Setting<TextUtil.Color> bracketColor = this.register(new Setting<TextUtil.Color>("BracketColor", TextUtil.Color.BLUE));
    public Setting<TextUtil.Color> commandColor = this.register(new Setting<TextUtil.Color>("NameColor", TextUtil.Color.BLUE));
    public Setting<String> commandBracket = this.register(new Setting<String>("LBracket", "["));
    public Setting<String> commandBracket2 = this.register(new Setting<String>("RBracket", "]"));
    public Setting<Boolean> notifyToggles = this.register(new Setting<Boolean>("ChatNotify", false));
    private int color;
    private boolean shouldIncrement;
    private final ArrayDeque<Double> speedDeque = new ArrayDeque();
    private int hitMarkerTimer;

    public HUD() {
        super("HUD", Module.Category.CORE, "Client's HUD");
        this.setInstance();
    }

    public static HUD getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HUD();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.shouldIncrement) {
            ++this.hitMarkerTimer;
        }
        if (this.hitMarkerTimer == 10) {
            this.hitMarkerTimer = 0;
            this.shouldIncrement = false;
        }
    }

    @SubscribeEvent
    public void onModuleEnable(ModuleToggleEvent.Enable event) {
        if (this.megaNullCheck() || !this.notifyToggles.getValue().booleanValue() || !this.isEnabled()) {
            return;
        }
        TextComponentString text = new TextComponentString(Mod.commandManager.getClientMessage() + " " + (Object)ChatFormatting.GREEN + event.getModule().getName() + " toggled on.");
        HUD.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)text, 1);
    }

    @SubscribeEvent
    public void onModuleDisable(ModuleToggleEvent.Disable event) {
        if (this.megaNullCheck() || !this.notifyToggles.getValue().booleanValue() || !this.isEnabled()) {
            return;
        }
        TextComponentString text = new TextComponentString(Mod.commandManager.getClientMessage() + " " + (Object)ChatFormatting.RED + event.getModule().getName() + " toggled off.");
        Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)text, 1);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        String str1;
        String fpsText;
        String str;
        ArrayList effects;
        int i;
        String grayString;
        int j;
        if (Feature.fullNullCheck()) {
            return;
        }
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        this.color = ColorUtil.toRGBA(ClickGui.getInstance().c.getValue().getRed(), ClickGui.getInstance().c.getValue().getGreen(), ClickGui.getInstance().c.getValue().getBlue());
        if (this.watermark.getValue().booleanValue()) {
            String string = this.watermarkText.getPlannedValue();
            if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(string, 2.0f, this.waterMarkY.getValue().intValue(), ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                } else {
                    int[] arrayOfInt = new int[]{1};
                    char[] stringToCharArray = string.toCharArray();
                    float f = 0.0f;
                    for (char c : stringToCharArray) {
                        this.renderer.drawString(String.valueOf(c), 2.0f + f, this.waterMarkY.getValue().intValue(), ColorUtil.rainbow(arrayOfInt[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                        f += (float)this.renderer.getStringWidth(String.valueOf(c));
                        arrayOfInt[0] = arrayOfInt[0] + 1;
                    }
                }
            } else {
                this.renderer.drawString(string, 2.0f, this.waterMarkY.getValue().intValue(), this.color, true);
            }
        }
        int[] counter1 = new int[]{1};
        int n = j = Util.mc.currentScreen instanceof GuiChat && this.renderingUp.getValue() == false ? 14 : 0;
        if (this.arrayList.getValue().booleanValue()) {
            String str2;
            if (this.renderingUp.getValue().booleanValue()) {
                if (this.renderingMode.getValue() == RenderingMode.ABC) {
                    for (int k = 0; k < Mod.moduleManager.sortedModulesABC.size(); ++k) {
                        String str3 = Mod.moduleManager.sortedModulesABC.get(k);
                        this.renderer.drawString(str3, width - 2 - this.renderer.getStringWidth(str3), 2 + j * 10, this.alphaStepArrayList.getValue().booleanValue() ? ColorUtil.alphaStep(new Color(this.color), this.index.getValue(), counter1[0] + this.countt.getValue()).getRGB() : (ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color), true);
                        ++j;
                        counter1[0] = counter1[0] + 1;
                    }
                } else {
                    for (int k = 0; k < Mod.moduleManager.sortedModules.size(); ++k) {
                        Module module = Mod.moduleManager.sortedModules.get(k);
                        str2 = module.getName() + (Object)ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + (Object)ChatFormatting.WHITE + module.getDisplayInfo() + (Object)ChatFormatting.GRAY + "]" : "");
                        this.renderer.drawString(str2, width - 2 - this.renderer.getStringWidth(str2), 2 + j * 10, this.alphaStepArrayList.getValue().booleanValue() ? ColorUtil.alphaStep(new Color(this.color), this.index.getValue(), counter1[0] + this.countt.getValue()).getRGB() : (ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color), true);
                        ++j;
                        counter1[0] = counter1[0] + 1;
                    }
                }
            } else if (this.renderingMode.getValue() == RenderingMode.ABC) {
                for (int k = 0; k < Mod.moduleManager.sortedModulesABC.size(); ++k) {
                    String str4 = Mod.moduleManager.sortedModulesABC.get(k);
                    this.renderer.drawString(str4, width - 2 - this.renderer.getStringWidth(str4), height - (j += 10), this.alphaStepArrayList.getValue().booleanValue() ? ColorUtil.alphaStep(new Color(this.color), this.index.getValue(), counter1[0] + this.countt.getValue()).getRGB() : (ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color), true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                for (int k = 0; k < Mod.moduleManager.sortedModules.size(); ++k) {
                    Module module = Mod.moduleManager.sortedModules.get(k);
                    str2 = module.getName() + (Object)ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + (Object)ChatFormatting.WHITE + module.getDisplayInfo() + (Object)ChatFormatting.GRAY + "]" : "");
                    this.renderer.drawString(str2, width - 2 - this.renderer.getStringWidth(str2), height - (j += 10), this.alphaStepArrayList.getValue().booleanValue() ? ColorUtil.alphaStep(new Color(this.color), this.index.getValue(), counter1[0] + this.countt.getValue()).getRGB() : (ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color), true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        }
        String string = grayString = this.grayNess.getValue() != false ? String.valueOf((Object)ChatFormatting.GRAY) : "";
        int n2 = Util.mc.currentScreen instanceof GuiChat && this.renderingUp.getValue() != false ? 13 : (i = this.renderingUp.getValue() != false ? -2 : 0);
        if (this.renderingUp.getValue().booleanValue()) {
            if (this.potions.getValue().booleanValue()) {
                effects = new ArrayList(Util.mc.player.getActivePotionEffects());
                for (PotionEffect potionEffect : effects) {
                    String str5 = Mod.potionManager.getColoredPotionString(potionEffect);
                    this.renderer.drawString(str5, width - this.renderer.getStringWidth(str5) - 2, height - 2 - (i += 10), potionEffect.getPotion().getLiquidColor(), true);
                }
            }
            if (this.speed.getValue().booleanValue()) {
                double speed;
                double displaySpeed = speed = this.calcSpeed(Util.mc.player);
                if (speed > 0.0 || Util.mc.player.ticksExisted % 4 == 0) {
                    this.speedDeque.add(speed);
                } else {
                    this.speedDeque.pollFirst();
                }
                while (!this.speedDeque.isEmpty() && this.speedDeque.size() > this.speedTicks.getValue()) {
                    this.speedDeque.poll();
                }
                displaySpeed = this.average(this.speedDeque);
                str = grayString + "Speed " + (Object)ChatFormatting.WHITE + String.format("%.1f", displaySpeed) + " km/h";
                this.renderer.drawString(str, width - this.renderer.getStringWidth(str) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.time.getValue().booleanValue()) {
                String str6 = grayString + "Time " + (Object)ChatFormatting.WHITE + new SimpleDateFormat("h:mm a").format(new Date());
                this.renderer.drawString(str6, width - this.renderer.getStringWidth(str6) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.tps.getValue().booleanValue()) {
                String str7 = grayString + "TPS " + (Object)ChatFormatting.WHITE + Mod.serverManager.getTPS();
                this.renderer.drawString(str7, width - this.renderer.getStringWidth(str7) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            fpsText = grayString + "FPS " + (Object)ChatFormatting.WHITE + Minecraft.debugFPS;
            str1 = grayString + "Ping " + (Object)ChatFormatting.WHITE + Mod.serverManager.getPing();
            if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, width - this.renderer.getStringWidth(str1) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - this.renderer.getStringWidth(fpsText) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - this.renderer.getStringWidth(fpsText) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, width - this.renderer.getStringWidth(str1) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        } else {
            if (this.potions.getValue().booleanValue()) {
                effects = new ArrayList(Util.mc.player.getActivePotionEffects());
                for (PotionEffect potionEffect : effects) {
                    String str8 = Mod.potionManager.getColoredPotionString(potionEffect);
                    this.renderer.drawString(str8, width - this.renderer.getStringWidth(str8) - 2, 2 + i++ * 10, potionEffect.getPotion().getLiquidColor(), true);
                }
            }
            if (this.speed.getValue().booleanValue()) {
                double speed;
                double displaySpeed = speed = this.calcSpeed(Util.mc.player);
                if (speed > 0.0 || Util.mc.player.ticksExisted % 4 == 0) {
                    this.speedDeque.add(speed);
                } else {
                    this.speedDeque.pollFirst();
                }
                while (!this.speedDeque.isEmpty() && this.speedDeque.size() > this.speedTicks.getValue()) {
                    this.speedDeque.poll();
                }
                displaySpeed = this.average(this.speedDeque);
                str = grayString + "Speed " + (Object)ChatFormatting.WHITE + String.format("%.1f", displaySpeed) + " km/h";
                this.renderer.drawString(str, width - this.renderer.getStringWidth(str) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.time.getValue().booleanValue()) {
                String str9 = grayString + "Time " + (Object)ChatFormatting.WHITE + new SimpleDateFormat("h:mm a").format(new Date());
                this.renderer.drawString(str9, width - this.renderer.getStringWidth(str9) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.tps.getValue().booleanValue()) {
                String str10 = grayString + "TPS " + (Object)ChatFormatting.WHITE + Mod.serverManager.getTPS();
                this.renderer.drawString(str10, width - this.renderer.getStringWidth(str10) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            fpsText = grayString + "FPS " + (Object)ChatFormatting.WHITE + Minecraft.debugFPS;
            str1 = grayString + "Ping " + (Object)ChatFormatting.WHITE + Mod.serverManager.getPing();
            if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, width - this.renderer.getStringWidth(str1) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - this.renderer.getStringWidth(fpsText) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - this.renderer.getStringWidth(fpsText) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, width - this.renderer.getStringWidth(str1) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        }
        boolean inHell = Util.mc.world.getBiome(Util.mc.player.getPosition()).getBiomeName().equals("Hell");
        int posX = (int)Util.mc.player.posX;
        int posY = (int)Util.mc.player.posY;
        int posZ = (int)Util.mc.player.posZ;
        float nether = !inHell ? 0.125f : 8.0f;
        int hposX = (int)(Util.mc.player.posX * (double)nether);
        int hposZ = (int)(Util.mc.player.posZ * (double)nether);
        i = Util.mc.currentScreen instanceof GuiChat ? 14 : 0;
        String coordinates = (Object)ChatFormatting.WHITE + (inHell ? posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]" : posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]");
        String direction = this.direction.getValue() != false ? (Object)ChatFormatting.WHITE + Mod.rotationManager.getDirection4D(false) : "";
        String coords = this.coords.getValue() != false ? coordinates : "";
        i += 10;
        if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
            String rainbowCoords;
            String string2 = this.coords.getValue() != false ? "XYZ " + (inHell ? posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]" : posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]") : (rainbowCoords = "");
            if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                this.renderer.drawString(direction, 2.0f, height - i - 11, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                this.renderer.drawString(rainbowCoords, 2.0f, height - i, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
            } else {
                int[] counter2 = new int[]{1};
                char[] stringToCharArray = direction.toCharArray();
                float s = 0.0f;
                for (char c : stringToCharArray) {
                    this.renderer.drawString(String.valueOf(c), 2.0f + s, height - i - 11, ColorUtil.rainbow(counter2[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    s += (float)this.renderer.getStringWidth(String.valueOf(c));
                    counter2[0] = counter2[0] + 1;
                }
                int[] counter3 = new int[]{1};
                char[] stringToCharArray2 = rainbowCoords.toCharArray();
                float u = 0.0f;
                for (char c : stringToCharArray2) {
                    this.renderer.drawString(String.valueOf(c), 2.0f + u, height - i, ColorUtil.rainbow(counter3[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    u += (float)this.renderer.getStringWidth(String.valueOf(c));
                    counter3[0] = counter3[0] + 1;
                }
            }
        } else {
            this.renderer.drawString(direction, 2.0f, height - i - 11, this.color, true);
            this.renderer.drawString(coords, 2.0f, height - i, this.color, true);
        }
        if (this.armor.getValue().booleanValue()) {
            this.renderArmorHUD(true);
        }
        if (this.totems.getValue().booleanValue()) {
            this.renderTotemHUD();
        }
        if (this.greeter.getValue() != Welcomer.None) {
            this.renderGreeter();
        }
        if (this.lag.getValue().booleanValue()) {
            this.renderLag();
        }
    }

    public Map<String, Integer> getTextRadarPlayers() {
        return EntityUtil.getTextRadarPlayers();
    }

    public void renderGreeter() {
        int width = this.renderer.scaledWidth;
        String text = "";
        switch (this.greeter.getValue()) {
            case None: {
                text = "";
            }
            case Custom: {
                text = this.message.getPlannedValue();
                break;
            }
            case Calendar: {
                text = MathUtil.getTimeOfDay() + HUD.mc.player.getDisplayNameString();
            }
        }
        if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
            if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                this.renderer.drawString(text, (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f, 2.0f, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
            } else {
                int[] counter1 = new int[]{1};
                char[] stringToCharArray = text.toCharArray();
                float i = 0.0f;
                for (char c : stringToCharArray) {
                    this.renderer.drawString(String.valueOf(c), (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f + i, 2.0f, ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    i += (float)this.renderer.getStringWidth(String.valueOf(c));
                    counter1[0] = counter1[0] + 1;
                }
            }
        } else {
            this.renderer.drawString(text, (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f, 2.0f, this.color, true);
        }
    }

    public void renderLag() {
        int width = this.renderer.scaledWidth;
        if (Mod.serverManager.isServerNotResponding()) {
            String text = (Object)ChatFormatting.RED + "Server not responding " + MathUtil.round((float)Mod.serverManager.serverRespondingTime() / 1000.0f, 1) + "s.";
            this.renderer.drawString(text, (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f, 20.0f, this.color, true);
        }
    }

    @Override
    public void onDisable() {
        this.speedDeque.clear();
    }

    public void renderTotemHUD() {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        int totems = Util.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::func_190916_E).sum();
        if (Util.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            totems += Util.mc.player.getHeldItemOffhand().getCount();
        }
        if (totems > 0) {
            GlStateManager.enableTexture2D();
            int i = width / 2;
            boolean iteration = false;
            int y = height - 55 - (Util.mc.player.isInWater() && Util.mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
            int x = i - 189 + 180 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(totem, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(Util.mc.fontRenderer, totem, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            this.renderer.drawStringWithShadow(totems + "", x + 19 - 2 - this.renderer.getStringWidth(totems + ""), y + 9, 0xFFFFFF);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }

    public void renderArmorHUD(boolean percent) {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        GlStateManager.enableTexture2D();
        int i = width / 2;
        int iteration = 0;
        int y = height - 55 - (HUD.mc.player.isInWater() && HUD.mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
        for (ItemStack is : HUD.mc.player.inventory.armorInventory) {
            ++iteration;
            if (is.isEmpty()) continue;
            int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, is, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            String s = is.getCount() > 1 ? is.getCount() + "" : "";
            this.renderer.drawStringWithShadow(s, x + 19 - 2 - this.renderer.getStringWidth(s), y + 9, 0xFFFFFF);
            if (!percent) continue;
            float green = ((float)is.getMaxDamage() - (float)is.getItemDamage()) / (float)is.getMaxDamage();
            float red = 1.0f - green;
            int dmg = 100 - (int)(red * 100.0f);
            this.renderer.drawStringWithShadow(dmg + "", x + 8 - this.renderer.getStringWidth(dmg + "") / 2, y - 11, this.customPercentageColor.getValue() != false ? this.percentageColor.getValue().getRGB() : ColorUtil.toRGBA((int)(red * 255.0f), (int)(green * 255.0f), 0));
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(AttackEntityEvent event) {
        this.shouldIncrement = true;
    }

    @Override
    public void onLoad() {
        Mod.commandManager.setClientMessage(this.getCommandMessage());
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && this.equals(event.getSetting().getFeature())) {
            Mod.commandManager.setClientMessage(this.getCommandMessage());
        }
    }

    public String getCommandMessage() {
        return TextUtil.coloredString(this.commandBracket.getPlannedValue(), this.bracketColor.getPlannedValue()) + TextUtil.coloredString(this.command.getPlannedValue(), this.commandColor.getPlannedValue()) + TextUtil.coloredString(this.commandBracket2.getPlannedValue(), this.bracketColor.getPlannedValue());
    }

    public void drawTextRadar(int yOffset) {
        if (!this.players.isEmpty()) {
            int y = this.renderer.getFontHeight() + 7 + yOffset;
            for (Map.Entry<String, Integer> player : this.players.entrySet()) {
                String text = player.getKey() + " ";
                int textheight = this.renderer.getFontHeight() + 1;
                this.renderer.drawString(text, 2.0f, y, this.color, true);
                y += textheight;
            }
        }
    }

    private double calcSpeed(EntityPlayerSP player) {
        double tps = 1000.0 / (double)Util.mc.timer.tickLength;
        double xDiff = player.posX - player.prevPosX;
        double zDiff = player.posZ - player.prevPosZ;
        double speed = Math.hypot(xDiff, zDiff) * tps;
        return speed *= 3.6;
    }

    private double average(Collection<Double> collection) {
        if (collection.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        int size = 0;
        for (double element : collection) {
            sum += element;
            ++size;
        }
        return sum / (double)size;
    }

    public static enum RenderingMode {
        Length,
        ABC;

    }

    public static enum Welcomer {
        None,
        Custom,
        Calendar;

    }
}

