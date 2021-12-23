/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.network.NetworkPlayerInfo
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.culling.ICamera
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.item.ItemTool
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.util.math.MathHelper
 *  org.lwjgl.opengl.GL11
 */
package cascade.features.modules.visual;

import cascade.Mod;
import cascade.event.events.Render3DEvent;
import cascade.features.gui.font.CustomFont;
import cascade.features.modules.Module;
import cascade.features.modules.core.FontMod;
import cascade.features.setting.Setting;
import cascade.util.EntityUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class Nametags
extends Module {
    private static Nametags INSTANCE = new Nametags();
    private final Setting<Boolean> outline;
    private final Setting<Boolean> inside;
    private final Setting<Color> oc;
    private final Setting<Float> Owidth;
    private final Setting<EnchantMode> enchantMode;
    private final Setting<Boolean> reversed;
    private final Setting<Boolean> reversedHand;
    private final Setting<Boolean> health;
    private final Setting<Boolean> gameMode;
    private final Setting<Boolean> ping;
    private final Setting<Boolean> pingColor;
    private final Setting<Boolean> armor;
    private final Setting<Boolean> durability;
    private final Setting<Boolean> item;
    private final Setting<Boolean> invisibles;
    private final Setting<Float> scale;
    private final Setting<Float> height;
    private final Setting<FriendMode> friendMode;
    private final Setting<Boolean> friends;
    private final Setting<Color> c;
    private final CustomFont customFont = new CustomFont(new Font("Verdana", 0, 17), true, true);
    public Setting<OutLineMode> outlineMode;
    private ICamera camera;
    boolean shownItem;
    private Map<Integer, Boolean> glCapMap;

    public Nametags() {
        super("Nametags", Module.Category.VISUAL, "omg omg omg omg");
        this.setInstance();
        this.glCapMap = new HashMap<Integer, Boolean>();
        this.camera = new Frustum();
        this.outline = this.register(new Setting<Boolean>("Outline", true));
        this.inside = this.register(new Setting<Boolean>("Background", true));
        this.outlineMode = this.register(new Setting<OutLineMode>("Outline Mode", OutLineMode.DEPEND));
        this.oc = this.register(new Setting<Object>("OutlineColor", new Color(-1), v -> this.outline.getValue()));
        this.Owidth = this.register(new Setting<Object>("Outline Width", Float.valueOf(1.5f), Float.valueOf(0.0f), Float.valueOf(3.0f), v -> this.outline.getValue()));
        this.reversed = this.register(new Setting<Boolean>("Reversed", false));
        this.reversedHand = this.register(new Setting<Boolean>("Reversed Hand", false));
        this.enchantMode = this.register(new Setting<EnchantMode>("Enchant Mode", EnchantMode.MAX));
        this.health = this.register(new Setting<Boolean>("Health", true));
        this.gameMode = this.register(new Setting<Boolean>("GameMode", true));
        this.ping = this.register(new Setting<Boolean>("Ping", true));
        this.pingColor = this.register(new Setting<Boolean>("Ping Color", true));
        this.armor = this.register(new Setting<Boolean>("Armor", true));
        this.durability = this.register(new Setting<Boolean>("Durability", true));
        this.item = this.register(new Setting<Boolean>("Item Name", true));
        this.invisibles = this.register(new Setting<Boolean>("Invisibles", false));
        this.scale = this.register(new Setting<Float>("Scale", Float.valueOf(5.0f), Float.valueOf(1.0f), Float.valueOf(9.0f)));
        this.height = this.register(new Setting<Float>("Height", Float.valueOf(2.5f), Float.valueOf(0.5f), Float.valueOf(5.0f)));
        this.friends = this.register(new Setting<Boolean>("Friends", true));
        this.friendMode = this.register(new Setting<Object>("Friend Mode", (Object)FriendMode.TEXT, v -> this.friends.getValue()));
        this.c = this.register(new Setting<Object>("FriendColor", new Color(-1), v -> this.friends.getValue() != false && this.friendMode.getValue() == FriendMode.BOX));
    }

    public void drawOutlineLine(double left, double top, double right, double bottom, float width, int color) {
        float b4;
        float g4;
        float r4;
        float a4;
        int rainbow2;
        int rainbow;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glLineWidth((float)width);
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float a1 = 0.0f;
        float r1 = 0.0f;
        float g1 = 0.0f;
        float b1 = 0.0f;
        float a2 = 0.0f;
        float r2 = 0.0f;
        float g2 = 0.0f;
        float b2 = 0.0f;
        float a3 = 0.0f;
        float r3 = 0.0f;
        float g3 = 0.0f;
        float b3 = 0.0f;
        if (this.outlineMode.getValue() == OutLineMode.RAINBOW) {
            rainbow = Nametags.rainbow(1).getRGB();
            rainbow2 = Nametags.rainbow(1000).getRGB();
            int rainbow3 = Nametags.rainbow(500).getRGB();
            int rainbow4 = Nametags.rainbow(1).getRGB();
            a4 = (float)(rainbow >> 24 & 0xFF) / 255.0f;
            r4 = (float)(rainbow >> 16 & 0xFF) / 255.0f;
            g4 = (float)(rainbow >> 8 & 0xFF) / 255.0f;
            b4 = (float)(rainbow & 0xFF) / 255.0f;
            a1 = (float)(rainbow2 >> 24 & 0xFF) / 255.0f;
            r1 = (float)(rainbow2 >> 16 & 0xFF) / 255.0f;
            g1 = (float)(rainbow2 >> 8 & 0xFF) / 255.0f;
            b1 = (float)(rainbow2 & 0xFF) / 255.0f;
            a2 = (float)(rainbow3 >> 24 & 0xFF) / 255.0f;
            r2 = (float)(rainbow3 >> 16 & 0xFF) / 255.0f;
            g2 = (float)(rainbow3 >> 8 & 0xFF) / 255.0f;
            b2 = (float)(rainbow3 & 0xFF) / 255.0f;
            a3 = (float)(rainbow4 >> 24 & 0xFF) / 255.0f;
            r3 = (float)(rainbow4 >> 16 & 0xFF) / 255.0f;
            g3 = (float)(rainbow4 >> 8 & 0xFF) / 255.0f;
            b3 = (float)(rainbow4 & 0xFF) / 255.0f;
        } else if (this.outlineMode.getValue() == OutLineMode.RAINBOW2) {
            rainbow = Nametags.rainbow(1).getRGB();
            rainbow2 = Nametags.rainbow(1000).getRGB();
            a4 = (float)(rainbow >> 24 & 0xFF) / 255.0f;
            r4 = (float)(rainbow >> 16 & 0xFF) / 255.0f;
            g4 = (float)(rainbow >> 8 & 0xFF) / 255.0f;
            b4 = (float)(rainbow & 0xFF) / 255.0f;
            a1 = (float)(rainbow2 >> 24 & 0xFF) / 255.0f;
            r1 = (float)(rainbow2 >> 16 & 0xFF) / 255.0f;
            g1 = (float)(rainbow2 >> 8 & 0xFF) / 255.0f;
            b1 = (float)(rainbow2 & 0xFF) / 255.0f;
        } else {
            a4 = (float)(color >> 24 & 0xFF) / 255.0f;
            r4 = (float)(color >> 16 & 0xFF) / 255.0f;
            g4 = (float)(color >> 8 & 0xFF) / 255.0f;
            b4 = (float)(color & 0xFF) / 255.0f;
        }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        if (this.outlineMode.getValue() == OutLineMode.RAINBOW) {
            bufferbuilder.pos(left, bottom, 0.0).color(r4, g4, b4, a4).endVertex();
            bufferbuilder.pos(right, bottom, 0.0).color(r1, g1, b1, a1).endVertex();
            bufferbuilder.pos(right, top, 0.0).color(r1, g1, b1, a1).endVertex();
            bufferbuilder.pos(left, top, 0.0).color(r2, g2, b2, a2).endVertex();
            bufferbuilder.pos(left, bottom, 0.0).color(r3, g3, b3, a3).endVertex();
        } else if (this.outlineMode.getValue() == OutLineMode.DEPEND) {
            bufferbuilder.pos(left, bottom, 0.0).color(r4, g4, b4, a4).endVertex();
            bufferbuilder.pos(right, bottom, 0.0).color(r4, g4, b4, a4).endVertex();
            bufferbuilder.pos(right, top, 0.0).color(r4, g4, b4, a4).endVertex();
            bufferbuilder.pos(left, top, 0.0).color(r4, g4, b4, a4).endVertex();
            bufferbuilder.pos(left, bottom, 0.0).color(r4, g4, b4, a4).endVertex();
        } else if (this.outlineMode.getValue() == OutLineMode.NORMAL) {
            bufferbuilder.pos(left, bottom, 0.0).color(r4, g4, b4, a4).endVertex();
            bufferbuilder.pos(right, bottom, 0.0).color(r4, g4, b4, a4).endVertex();
            bufferbuilder.pos(right, top, 0.0).color(r4, g4, b4, a4).endVertex();
            bufferbuilder.pos(left, top, 0.0).color(r4, g4, b4, a4).endVertex();
            bufferbuilder.pos(left, bottom, 0.0).color(r4, g4, b4, a4).endVertex();
        } else if (this.outlineMode.getValue() == OutLineMode.RAINBOW2) {
            bufferbuilder.pos(left, bottom, 0.0).color(r4, g4, b4, a4).endVertex();
            bufferbuilder.pos(right, bottom, 0.0).color(r1, g1, b1, a1).endVertex();
            bufferbuilder.pos(right, top, 0.0).color(r4, g4, b4, a4).endVertex();
            bufferbuilder.pos(left, top, 0.0).color(r1, g1, b1, a1).endVertex();
            bufferbuilder.pos(left, bottom, 0.0).color(r4, g4, b4, a4).endVertex();
        }
        tessellator.draw();
        GL11.glDisable((int)2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static Color rainbow(int delay) {
        double rainbowState = Math.ceil((double)(System.currentTimeMillis() + (long)delay) / 20.0);
        return Color.getHSBColor((float)((rainbowState %= 360.0) / 360.0), 1.0f, 1.0f);
    }

    public static Nametags getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Nametags();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onRender3D(Render3DEvent e) {
        if (Nametags.fullNullCheck() || this.isDisabled()) {
            return;
        }
        EntityPlayerSP entityPlayerSP = mc.getRenderViewEntity() == null ? Nametags.mc.player : mc.getRenderViewEntity();
        double d3 = entityPlayerSP.lastTickPosX + (entityPlayerSP.posX - entityPlayerSP.lastTickPosX) * (double)e.getPartialTicks();
        double d4 = entityPlayerSP.lastTickPosY + (entityPlayerSP.posY - entityPlayerSP.lastTickPosY) * (double)e.getPartialTicks();
        double d5 = entityPlayerSP.lastTickPosZ + (entityPlayerSP.posZ - entityPlayerSP.lastTickPosZ) * (double)e.getPartialTicks();
        this.camera.setPosition(d3, d4, d5);
        ArrayList<Object> players = new ArrayList<Object>(Nametags.mc.world.playerEntities);
        players.sort(Comparator.comparing(entityPlayer -> Float.valueOf(entityPlayerSP.getDistance((Entity)entityPlayer))).reversed());
        for (EntityPlayer entityPlayer2 : players) {
            NetworkPlayerInfo npi = mc.getConnection().getPlayerInfo(entityPlayer2.getGameProfile().getId());
            if (!this.camera.isBoundingBoxInFrustum(entityPlayer2.getEntityBoundingBox()) && !this.camera.isBoundingBoxInFrustum(entityPlayer2.getEntityBoundingBox().offset(0.0, 2.0, 0.0)) && !Nametags.fullNullCheck() || entityPlayer2 == mc.getRenderViewEntity() || !entityPlayer2.isEntityAlive()) continue;
            double pX = entityPlayer2.lastTickPosX + (entityPlayer2.posX - entityPlayer2.lastTickPosX) * (double)Nametags.mc.timer.renderPartialTicks - Nametags.mc.renderManager.renderPosX;
            double pY = entityPlayer2.lastTickPosY + (entityPlayer2.posY - entityPlayer2.lastTickPosY) * (double)Nametags.mc.timer.renderPartialTicks - Nametags.mc.renderManager.renderPosY;
            double pZ = entityPlayer2.lastTickPosZ + (entityPlayer2.posZ - entityPlayer2.lastTickPosZ) * (double)Nametags.mc.timer.renderPartialTicks - Nametags.mc.renderManager.renderPosZ;
            if (npi != null && this.getShortName(npi.getGameType().getName()).equalsIgnoreCase("SP") && !this.invisibles.getValue().booleanValue() || entityPlayer2.getName().startsWith("Body #")) continue;
            this.renderNametag(entityPlayer2, pX, pY, pZ);
        }
    }

    public String getShortName(String gameType) {
        if (gameType.equalsIgnoreCase("survival")) {
            return "S";
        }
        if (gameType.equalsIgnoreCase("creative")) {
            return "C";
        }
        if (gameType.equalsIgnoreCase("adventure")) {
            return "A";
        }
        if (gameType.equalsIgnoreCase("spectator")) {
            return "SP";
        }
        return "NONE";
    }

    public String getHealth(float health) {
        if (health > 18.0f) {
            return (Object)ChatFormatting.GREEN + "";
        }
        if (health > 16.0f) {
            return (Object)ChatFormatting.DARK_GREEN + "";
        }
        if (health > 12.0f) {
            return (Object)ChatFormatting.YELLOW + "";
        }
        if (health > 8.0f) {
            return (Object)ChatFormatting.GOLD + "";
        }
        if (health > 5.0f) {
            return (Object)ChatFormatting.RED + "";
        }
        return (Object)ChatFormatting.DARK_RED + "";
    }

    public String getPing(float ping) {
        if (ping > 200.0f) {
            return (Object)ChatFormatting.RED + "";
        }
        if (ping > 100.0f) {
            return (Object)ChatFormatting.YELLOW + "";
        }
        return (Object)ChatFormatting.GREEN + "";
    }

    private String getName(EntityPlayer player) {
        return player.getName();
    }

    public void renderNametag(EntityPlayer player, double x, double y, double z) {
        this.shownItem = false;
        GlStateManager.pushMatrix();
        NetworkPlayerInfo npi = Nametags.mc.player.connection.getPlayerInfo(player.getGameProfile().getId());
        boolean isFriend = Mod.friendManager.isFriend(player.getName()) && this.friends.getValue() != false;
        StringBuilder append = new StringBuilder().append(isFriend && this.friendMode.getValue() == FriendMode.TEXT ? (isFriend ? ChatFormatting.BLUE : ChatFormatting.RED) : (player.isSneaking() ? ChatFormatting.GRAY : "")).append(this.getName(player)).append(this.gameMode.getValue() != false ? " [" + this.getShortName(npi.getGameType().getName()) + "]" : "").append(this.ping.getValue() != false ? " " + (this.pingColor.getValue() != false ? this.getPing(npi.getResponseTime()) : "") + npi.getResponseTime() + "ms" : "").append(this.health.getValue() != false ? " " + this.getHealth(player.getHealth() + player.getAbsorptionAmount()) + MathHelper.ceil((float)(player.getHealth() + player.getAbsorptionAmount())) : "");
        String string = "";
        String name = append.append(string).toString();
        name = name.replace(".0", "");
        EntityPlayerSP entityPlayerSP = mc.getRenderViewEntity() == null ? Nametags.mc.player : mc.getRenderViewEntity();
        float distance = entityPlayerSP.getDistance((Entity)player);
        float var14 = (distance / 5.0f <= 2.0f ? 2.0f : distance / 5.0f * (this.scale.getValue().floatValue() / 100.0f * 10.0f + 1.0f)) * 2.5f * (this.scale.getValue().floatValue() / 100.0f / 10.0f);
        if ((double)distance <= 8.0) {
            var14 = 0.0245f;
        }
        GL11.glTranslated((double)((float)x), (double)((double)((float)y + this.height.getValue().floatValue()) - (player.isSneaking() ? 0.4 : 0.0) + (distance / 5.0f > 2.0f ? (double)(distance / 12.0f) - 0.7 : 0.0)), (double)((float)z));
        GL11.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)(-Nametags.mc.getRenderManager().playerViewY), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)Nametags.mc.getRenderManager().playerViewX, (float)(Nametags.mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f), (float)0.0f, (float)0.0f);
        GL11.glScalef((float)(-var14), (float)(-var14), (float)var14);
        this.disableGlCap(2896, 2929);
        this.enableGlCap(3042);
        GL11.glBlendFunc((int)770, (int)771);
        int width = FontMod.getInstance().isEnabled() ? this.customFont.getStringWidth(name) / 2 + 1 : Nametags.mc.fontRenderer.getStringWidth(name) / 2 + 1;
        int color = isFriend && this.friendMode.getValue() == FriendMode.BOX ? new Color(this.c.getValue().getRed(), this.c.getValue().getGreen(), this.c.getValue().getBlue()).getRGB() : 0;
        int outlineColor = new Color(this.oc.getValue().getRed(), this.oc.getValue().getGreen(), this.oc.getValue().getBlue(), this.oc.getValue().getAlpha()).getRGB();
        if (this.outlineMode.getValue() == OutLineMode.DEPEND) {
            outlineColor = Mod.friendManager.isFriend(player.getName()) ? new Color(0, 191, 230, this.oc.getValue().getAlpha()).getRGB() : (EntityUtil.isBurrow((Entity)player) ? new Color(177, 27, 196, this.oc.getValue().getAlpha()).getRGB() : (EntityUtil.isSafe((Entity)player) ? new Color(0, 255, 0, this.oc.getValue().getAlpha()).getRGB() : new Color(255, 0, 0, this.oc.getValue().getAlpha()).getRGB()));
        }
        if (this.inside.getValue().booleanValue()) {
            Gui.drawRect((int)(-width - 1), (int)8, (int)(width + 1), (int)19, (int)Nametags.changeAlpha(color, 120));
        }
        if (this.outline.getValue().booleanValue()) {
            this.drawOutlineLine(-width - 1, 8.0, width + 1, 19.0, this.Owidth.getValue().floatValue(), outlineColor);
        }
        if (FontMod.getInstance().isEnabled()) {
            this.customFont.drawStringWithShadow(name, -width, 8.65f, -1);
        } else {
            Nametags.mc.fontRenderer.drawStringWithShadow(name, (float)(-width), 9.2f, -1);
        }
        if (this.armor.getValue().booleanValue()) {
            int index;
            int xOffset = -8;
            Item mainhand = player.getHeldItemMainhand().getItem();
            Item offhand = player.getHeldItemOffhand().getItem();
            if (mainhand != Items.AIR && offhand == Items.AIR) {
                xOffset = -16;
            } else if (mainhand == Items.AIR && offhand != Items.AIR) {
                xOffset = 0;
            }
            int count = 0;
            for (ItemStack armourStack : player.inventory.armorInventory) {
                if (armourStack == null) continue;
                xOffset -= 8;
                if (armourStack.getItem() == Items.AIR) continue;
                ++count;
            }
            if (player.getHeldItemOffhand().getItem() != Items.AIR) {
                ++count;
            }
            int cacheX = xOffset - 8;
            xOffset += 8 * (5 - count) - (count == 0 ? 4 : 0);
            if (!(this.reversedHand.getValue() != false ? player.getHeldItemOffhand().getItem() == Items.AIR : player.getHeldItemMainhand().getItem() == Items.AIR)) {
                ItemStack renderStack;
                xOffset -= 10;
                if (this.reversedHand.getValue().booleanValue()) {
                    renderStack = player.getHeldItemOffhand().copy();
                    this.renderItem(player, renderStack, xOffset, -8, cacheX, false);
                } else {
                    renderStack = player.getHeldItemMainhand().copy();
                    this.renderItem(player, renderStack, xOffset, -8, cacheX, true);
                }
                xOffset += 18;
            } else if (!this.reversedHand.getValue().booleanValue()) {
                this.shownItem = true;
            }
            if (this.reversed.getValue().booleanValue()) {
                for (index = 0; index <= 3; ++index) {
                    ItemStack armourStack2 = (ItemStack)player.inventory.armorInventory.get(index);
                    if (armourStack2 == null || armourStack2.getItem() == Items.AIR) continue;
                    ItemStack renderStack2 = armourStack2.copy();
                    this.renderItem(player, renderStack2, xOffset, -8, cacheX, false);
                    xOffset += 16;
                }
            } else {
                for (index = 3; index >= 0; --index) {
                    ItemStack armourStack2 = (ItemStack)player.inventory.armorInventory.get(index);
                    if (armourStack2 == null || armourStack2.getItem() == Items.AIR) continue;
                    ItemStack renderStack2 = armourStack2.copy();
                    this.renderItem(player, renderStack2, xOffset, -8, cacheX, false);
                    xOffset += 16;
                }
            }
            if (!(this.reversedHand.getValue() != false ? player.getHeldItemMainhand().getItem() == Items.AIR : player.getHeldItemOffhand().getItem() == Items.AIR)) {
                xOffset += 0;
                if (this.reversedHand.getValue().booleanValue()) {
                    ItemStack renderOffhand = player.getHeldItemMainhand().copy();
                    this.renderItem(player, renderOffhand, xOffset, -8, cacheX, true);
                } else {
                    ItemStack renderOffhand = player.getHeldItemOffhand().copy();
                    this.renderItem(player, renderOffhand, xOffset, -8, cacheX, false);
                }
            }
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask((boolean)false);
            GL11.glEnable((int)2848);
            GL11.glHint((int)3154, (int)4354);
        } else if (this.durability.getValue().booleanValue()) {
            int xOffset = -6;
            int count2 = 0;
            for (ItemStack armourStack3 : player.inventory.armorInventory) {
                if (armourStack3 == null) continue;
                xOffset -= 8;
                if (armourStack3.getItem() == Items.AIR) continue;
                ++count2;
            }
            if (player.getHeldItemOffhand().getItem() != Items.AIR) {
                ++count2;
            }
            int cacheX2 = xOffset - 8;
            xOffset += 8 * (5 - count2) - (count2 == 0 ? 4 : 0);
            if (this.reversed.getValue().booleanValue()) {
                for (int index2 = 0; index2 <= 3; ++index2) {
                    ItemStack armourStack4 = (ItemStack)player.inventory.armorInventory.get(index2);
                    if (armourStack4 == null || armourStack4.getItem() == Items.AIR) continue;
                    ItemStack renderStack3 = armourStack4.copy();
                    this.renderDurabilityText(player, renderStack3, xOffset, -8);
                    xOffset += 16;
                }
            } else {
                for (int index2 = 3; index2 >= 0; --index2) {
                    ItemStack armourStack4 = (ItemStack)player.inventory.armorInventory.get(index2);
                    if (armourStack4 == null || armourStack4.getItem() == Items.AIR) continue;
                    ItemStack renderStack3 = armourStack4.copy();
                    this.renderDurabilityText(player, renderStack3, xOffset, -8);
                    xOffset += 16;
                }
            }
            GL11.glDisable((int)2848);
            GlStateManager.depthMask((boolean)true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
        this.resetCaps();
        GlStateManager.resetColor();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glPopMatrix();
    }

    public float getNametagSize(EntityLivingBase player) {
        ScaledResolution scaledRes = new ScaledResolution(mc);
        double twoDscale = (double)scaledRes.getScaleFactor() / Math.pow(scaledRes.getScaleFactor(), 2.0);
        EntityPlayerSP entityPlayerSP = mc.getRenderViewEntity() == null ? Nametags.mc.player : mc.getRenderViewEntity();
        return (float)twoDscale + entityPlayerSP.getDistance((Entity)player) / 7.0f;
    }

    public void drawBorderRect(float left, float top, float right, float bottom, int bcolor, int icolor, float f) {
        Nametags.drawGuiRect(left + f, top + f, right - f, bottom - f, icolor);
        Nametags.drawGuiRect(left, top, left + f, bottom, bcolor);
        Nametags.drawGuiRect(left + f, top, right, top + f, bcolor);
        Nametags.drawGuiRect(left + f, bottom - f, right, bottom, bcolor);
        Nametags.drawGuiRect(right - f, top + f, right, bottom - f, bcolor);
    }

    public static void drawGuiRect(double x1, double y1, double x2, double y2, int color) {
        float red = (float)(color >> 24 & 0xFF) / 255.0f;
        float green = (float)(color >> 16 & 0xFF) / 255.0f;
        float blue = (float)(color >> 8 & 0xFF) / 255.0f;
        float alpha = (float)(color & 0xFF) / 255.0f;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glPushMatrix();
        GL11.glColor4f((float)green, (float)blue, (float)alpha, (float)red);
        GL11.glBegin((int)7);
        GL11.glVertex2d((double)x2, (double)y1);
        GL11.glVertex2d((double)x1, (double)y1);
        GL11.glVertex2d((double)x1, (double)y2);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glDisable((int)2848);
    }

    public static void fakeGuiRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f4 = (float)(color >> 16 & 0xFF) / 255.0f;
        float f5 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f6 = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.color((float)f4, (float)f5, (float)f6, (float)f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, top, 0.0).endVertex();
        bufferbuilder.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawBorderedRect(double x, double y, double x1, double y1, double width, int internalColor, int borderColor) {
        GlStateManager.pushMatrix();
        Nametags.enableGL2D();
        Nametags.fakeGuiRect(x + width, y + width, x1 - width, y1 - width, internalColor);
        Nametags.fakeGuiRect(x + width, y, x1 - width, y + width, borderColor);
        Nametags.fakeGuiRect(x, y, x + width, y1, borderColor);
        Nametags.fakeGuiRect(x1 - width, y, x1, y1, borderColor);
        Nametags.fakeGuiRect(x + width, y1 - width, x1 - width, y1, borderColor);
        Nametags.disableGL2D();
        GlStateManager.popMatrix();
    }

    public void renderItem(EntityPlayer player, ItemStack stack, int x, int y, int nameX, boolean showHeldItemText) {
        GL11.glPushMatrix();
        GL11.glDepthMask((boolean)true);
        GlStateManager.clear((int)256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        Nametags.mc.getRenderItem().zLevel = -100.0f;
        GlStateManager.scale((float)1.0f, (float)1.0f, (float)0.01f);
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y / 2 - 12);
        if (this.durability.getValue().booleanValue()) {
            mc.getRenderItem().renderItemOverlays(Nametags.mc.fontRenderer, stack, x, y / 2 - 12);
        }
        Nametags.mc.getRenderItem().zLevel = 0.0f;
        GlStateManager.scale((float)1.0f, (float)1.0f, (float)1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale((double)0.5, (double)0.5, (double)0.5);
        GlStateManager.disableDepth();
        this.renderEnchantText(player, stack, x, y - 18);
        if (!this.shownItem && this.item.getValue().booleanValue() && showHeldItemText) {
            if (FontMod.getInstance().isEnabled()) {
                this.customFont.drawString(stack.getDisplayName().equalsIgnoreCase("Air") ? "" : stack.getDisplayName(), nameX * 2 + 95 - this.customFont.getStringWidth(stack.getDisplayName()) / 2, y - 37, -1, true);
            } else {
                Nametags.mc.fontRenderer.drawStringWithShadow(stack.getDisplayName().equalsIgnoreCase("Air") ? "" : stack.getDisplayName(), (float)(nameX * 2 + 95 - Nametags.mc.fontRenderer.getStringWidth(stack.getDisplayName()) / 2), (float)(y - 37), -1);
            }
            this.shownItem = true;
        }
        GlStateManager.enableDepth();
        GlStateManager.scale((float)2.0f, (float)2.0f, (float)2.0f);
        GL11.glPopMatrix();
    }

    public boolean isMaxEnchants(ItemStack stack) {
        int maxnum;
        NBTTagList enchants = stack.getEnchantmentTagList();
        ArrayList<String> enchantments = new ArrayList<String>();
        int count = 0;
        if (enchants == null) {
            return false;
        }
        for (int index = 0; index < enchants.tagCount(); ++index) {
            short id = enchants.getCompoundTagAt(index).getShort("id");
            short level = enchants.getCompoundTagAt(index).getShort("lvl");
            Enchantment enc = Enchantment.getEnchantmentByID((int)id);
            if (enc == null) continue;
            enchantments.add(enc.getTranslatedName((int)level));
        }
        if (stack.getItem() == Items.DIAMOND_HELMET) {
            maxnum = 5;
            for (String s : enchantments) {
                if (s.equalsIgnoreCase("Protection IV")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Respiration III")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Aqua Affinity")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Unbreaking III")) {
                    ++count;
                }
                if (!s.equalsIgnoreCase("Mending")) continue;
                ++count;
            }
            return count >= 5;
        }
        if (stack.getItem() == Items.DIAMOND_CHESTPLATE) {
            maxnum = 3;
            for (String s : enchantments) {
                if (s.equalsIgnoreCase("Protection IV")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Unbreaking III")) {
                    ++count;
                }
                if (!s.equalsIgnoreCase("Mending")) continue;
                ++count;
            }
            return count >= 3;
        }
        if (stack.getItem() == Items.DIAMOND_LEGGINGS) {
            maxnum = 3;
            for (String s : enchantments) {
                if (s.equalsIgnoreCase("Blast Protection IV")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Unbreaking III")) {
                    ++count;
                }
                if (!s.equalsIgnoreCase("Mending")) continue;
                ++count;
            }
            return count >= 3;
        }
        if (stack.getItem() == Items.DIAMOND_BOOTS) {
            maxnum = 5;
            for (String s : enchantments) {
                if (s.equalsIgnoreCase("Protection IV")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Feather Falling IV")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Depth Strider III")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Unbreaking III")) {
                    ++count;
                }
                if (!s.equalsIgnoreCase("Mending")) continue;
                ++count;
            }
            return count >= 5;
        }
        return false;
    }

    private void renderDurabilityText(EntityPlayer player, ItemStack stack, int x, int y) {
        GL11.glPushMatrix();
        GL11.glDepthMask((boolean)true);
        GlStateManager.clear((int)256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.scale((float)1.0f, (float)1.0f, (float)0.01f);
        GlStateManager.scale((float)1.0f, (float)1.0f, (float)1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale((double)0.5, (double)0.5, (double)0.5);
        GlStateManager.disableDepth();
        if (stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemTool) {
            float green = ((float)stack.getMaxDamage() - (float)stack.getItemDamage()) / (float)stack.getMaxDamage();
            float red = 1.0f - green;
            int dmg = 100 - (int)(red * 100.0f);
            if (FontMod.getInstance().isEnabled()) {
                this.customFont.drawStringWithShadow(dmg + "%", x * 2 + 4, y - 10, ColourHolder.toHex((int)(red * 255.0f), (int)(green * 255.0f), 0));
            } else {
                Nametags.mc.fontRenderer.drawStringWithShadow(dmg + "%", (float)(x * 2 + 4), (float)(y - 10), ColourHolder.toHex((int)(red * 255.0f), (int)(green * 255.0f), 0));
            }
        }
        GlStateManager.enableDepth();
        GlStateManager.scale((float)2.0f, (float)2.0f, (float)2.0f);
        GL11.glPopMatrix();
    }

    public void renderEnchantText(EntityPlayer player, ItemStack stack, int x, int y) {
        short id;
        int encY = y;
        int yCount = y;
        if ((stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemTool) && this.durability.getValue().booleanValue()) {
            float green = ((float)stack.getMaxDamage() - (float)stack.getItemDamage()) / (float)stack.getMaxDamage();
            float red = 1.0f - green;
            int dmg = 100 - (int)(red * 100.0f);
            if (FontMod.getInstance().isEnabled()) {
                this.customFont.drawStringWithShadow(dmg + "%", x * 2 + 4, y - 10, ColourHolder.toHex((int)(red * 255.0f), (int)(green * 255.0f), 0));
            } else {
                Nametags.mc.fontRenderer.drawStringWithShadow(dmg + "%", (float)(x * 2 + 4), (float)(y - 10), ColourHolder.toHex((int)(red * 255.0f), (int)(green * 255.0f), 0));
            }
        }
        if (this.enchantMode.getValue() == EnchantMode.NONE) {
            return;
        }
        if (this.enchantMode.getValue() == EnchantMode.MAX && this.isMaxEnchants(stack)) {
            GL11.glPushMatrix();
            GL11.glScalef((float)1.0f, (float)1.0f, (float)0.0f);
            if (FontMod.getInstance().isEnabled()) {
                this.customFont.drawStringWithShadow("Max", x * 2 + 7, yCount + 24, 0xFF0000);
            } else {
                Nametags.mc.fontRenderer.drawStringWithShadow("Max", (float)(x * 2 + 7), (float)(yCount + 24), 0xFF0000);
            }
            GL11.glScalef((float)1.0f, (float)1.0f, (float)1.0f);
            GL11.glPopMatrix();
            return;
        }
        if (this.enchantMode.getValue() == EnchantMode.PROT) {
            NBTTagList enchants = stack.getEnchantmentTagList();
            if (enchants != null) {
                for (int index = 0; index < enchants.tagCount(); ++index) {
                    id = enchants.getCompoundTagAt(index).getShort("id");
                    short level = enchants.getCompoundTagAt(index).getShort("lvl");
                    Enchantment enc = Enchantment.getEnchantmentByID((int)id);
                    if (enc == null || enc.isCurse()) continue;
                    String encName = level == 1 ? enc.getTranslatedName((int)level).substring(0, 3).toLowerCase() : enc.getTranslatedName((int)level).substring(0, 2).toLowerCase() + level;
                    if (!(encName = encName.substring(0, 1).toUpperCase() + encName.substring(1)).contains("Pr") && !encName.contains("Bl")) continue;
                    GL11.glPushMatrix();
                    GL11.glScalef((float)1.0f, (float)1.0f, (float)0.0f);
                    if (FontMod.getInstance().isEnabled()) {
                        this.customFont.drawStringWithShadow(encName, x * 2 + 3, yCount, -1);
                    } else {
                        Nametags.mc.fontRenderer.drawStringWithShadow(encName, (float)(x * 2 + 3), (float)yCount, -1);
                    }
                    GL11.glScalef((float)1.0f, (float)1.0f, (float)1.0f);
                    GL11.glPopMatrix();
                    encY += 8;
                    yCount += 8;
                }
            }
            return;
        }
        NBTTagList enchants = stack.getEnchantmentTagList();
        if (enchants != null) {
            for (int index = 0; index < enchants.tagCount(); ++index) {
                id = enchants.getCompoundTagAt(index).getShort("id");
                short level = enchants.getCompoundTagAt(index).getShort("lvl");
                Enchantment enc = Enchantment.getEnchantmentByID((int)id);
                if (enc == null || enc.isCurse()) continue;
                String encName = level == 1 ? enc.getTranslatedName((int)level).substring(0, 3).toLowerCase() : enc.getTranslatedName((int)level).substring(0, 2).toLowerCase() + level;
                encName = encName.substring(0, 1).toUpperCase() + encName.substring(1);
                GL11.glPushMatrix();
                GL11.glScalef((float)1.0f, (float)1.0f, (float)0.0f);
                if (FontMod.getInstance().isEnabled()) {
                    this.customFont.drawStringWithShadow(encName, x * 2 + 3, yCount, -1);
                } else {
                    Nametags.mc.fontRenderer.drawStringWithShadow(encName, (float)(x * 2 + 3), (float)yCount, -1);
                }
                GL11.glScalef((float)1.0f, (float)1.0f, (float)1.0f);
                GL11.glPopMatrix();
                encY += 8;
                yCount += 8;
            }
        }
    }

    public static final int changeAlpha(int origColor, int userInputedAlpha) {
        return userInputedAlpha << 24 | (origColor &= 0xFFFFFF);
    }

    public static void enableGL2D() {
        GL11.glDisable((int)2929);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glHint((int)3155, (int)4354);
    }

    public static void disableGL2D() {
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glDisable((int)2848);
        GL11.glHint((int)3154, (int)4352);
        GL11.glHint((int)3155, (int)4352);
    }

    public void glColor(int red, int green, int blue, int alpha) {
        GlStateManager.color((float)((float)red / 255.0f), (float)((float)green / 255.0f), (float)((float)blue / 255.0f), (float)((float)alpha / 255.0f));
    }

    public void glColor(Color color) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        float alpha = (float)color.getAlpha() / 255.0f;
        GlStateManager.color((float)red, (float)green, (float)blue, (float)alpha);
    }

    private void glColor(int hex) {
        float alpha = (float)(hex >> 24 & 0xFF) / 255.0f;
        float red = (float)(hex >> 16 & 0xFF) / 255.0f;
        float green = (float)(hex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(hex & 0xFF) / 255.0f;
        GlStateManager.color((float)red, (float)green, (float)blue, (float)alpha);
    }

    public void resetCaps() {
        this.glCapMap.forEach((arg_0, arg_1) -> this.setGlState(arg_0, arg_1));
    }

    public void enableGlCap(int cap) {
        this.setGlCap(cap, true);
    }

    public void enableGlCap(int ... caps) {
        for (int cap : caps) {
            this.setGlCap(cap, true);
        }
    }

    public void disableGlCap(int cap) {
        this.setGlCap(cap, false);
    }

    public void disableGlCap(int ... caps) {
        for (int cap : caps) {
            this.setGlCap(cap, false);
        }
    }

    public void setGlCap(int cap, boolean state) {
        this.glCapMap.put(cap, GL11.glGetBoolean((int)cap));
        this.setGlState(cap, state);
    }

    public void setGlState(int cap, boolean state) {
        if (state) {
            GL11.glEnable((int)cap);
        } else {
            GL11.glDisable((int)cap);
        }
    }

    public static class ColourHolder {
        int r;
        int g;
        int b;
        int a;

        public ColourHolder(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = 255;
        }

        public ColourHolder(int r, int g, int b, int a) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }

        public ColourHolder brighter() {
            return new ColourHolder(Math.min(this.r + 10, 255), Math.min(this.g + 10, 255), Math.min(this.b + 10, 255), this.getA());
        }

        public ColourHolder darker() {
            return new ColourHolder(Math.max(this.r - 10, 0), Math.max(this.g - 10, 0), Math.max(this.b - 10, 0), this.getA());
        }

        public void setGLColour() {
            this.setGLColour(-1, -1, -1, -1);
        }

        public void setGLColour(int dr, int dg, int db, int da) {
            GL11.glColor4f((float)((float)(dr == -1 ? this.r : dr) / 255.0f), (float)((float)(dg == -1 ? this.g : dg) / 255.0f), (float)((float)(db == -1 ? this.b : db) / 255.0f), (float)((float)(da == -1 ? this.a : da) / 255.0f));
        }

        public void becomeGLColour() {
        }

        public void becomeHex(int hex) {
            this.setR((hex & 0xFF0000) >> 16);
            this.setG((hex & 0xFF00) >> 8);
            this.setB(hex & 0xFF);
            this.setA(255);
        }

        public static ColourHolder fromHex(int hex) {
            ColourHolder n = new ColourHolder(0, 0, 0);
            n.becomeHex(hex);
            return n;
        }

        public static int toHex(int r, int g, int b) {
            return 0xFF000000 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
        }

        public int toHex() {
            return ColourHolder.toHex(this.r, this.g, this.b);
        }

        public int getB() {
            return this.b;
        }

        public int getG() {
            return this.g;
        }

        public int getR() {
            return this.r;
        }

        public int getA() {
            return this.a;
        }

        public ColourHolder setR(int r) {
            this.r = r;
            return this;
        }

        public ColourHolder setB(int b) {
            this.b = b;
            return this;
        }

        public ColourHolder setG(int g) {
            this.g = g;
            return this;
        }

        public ColourHolder setA(int a) {
            this.a = a;
            return this;
        }

        public ColourHolder clone() {
            return new ColourHolder(this.r, this.g, this.b, this.a);
        }

        public Color toJavaColour() {
            return new Color(this.r, this.g, this.b, this.a);
        }
    }

    public static enum OutLineMode {
        NORMAL,
        DEPEND,
        RAINBOW,
        RAINBOW2;

    }

    public static enum EnchantMode {
        PROT,
        LIST,
        MAX,
        NONE;

    }

    public static enum FriendMode {
        TEXT,
        BOX;

    }
}

