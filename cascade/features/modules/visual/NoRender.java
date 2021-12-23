/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.gui.BossInfoClient
 *  net.minecraft.client.gui.GuiBossOverlay
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.network.play.server.SPacketMaps
 *  net.minecraft.world.BossInfo
 *  net.minecraftforge.client.event.EntityViewRenderEvent$FogDensity
 *  net.minecraftforge.client.event.RenderBlockOverlayEvent
 *  net.minecraftforge.client.event.RenderBlockOverlayEvent$OverlayType
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$ElementType
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Post
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Pre
 *  net.minecraftforge.fml.common.eventhandler.EventPriority
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.GL11
 */
package cascade.features.modules.visual;

import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.world.BossInfo;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class NoRender
extends Module {
    public Setting<Boolean> noMaps = this.register(new Setting<Boolean>("Maps", true));
    public Setting<Boolean> items = this.register(new Setting<Boolean>("Items", true));
    public Setting<Boolean> raytrace = this.register(new Setting<Object>("Raytrace", Boolean.valueOf(false), v -> this.items.getValue()));
    public Setting<Boolean> posY = this.register(new Setting<Object>("PosY", Boolean.valueOf(true), v -> this.items.getValue()));
    public Setting<Integer> y = this.register(new Setting<Object>("Y", Integer.valueOf(200), Integer.valueOf(1), Integer.valueOf(255), v -> this.items.getValue() != false && this.posY.getValue() != false));
    public Setting<Boolean> noHurt = this.register(new Setting<Boolean>("Hurt", true));
    public Setting<Boolean> noOverlay = this.register(new Setting<Boolean>("Overlay", true));
    public Setting<Boolean> totemPops = this.register(new Setting<Boolean>("TotemPop", false));
    public Setting<Boolean> noArmor = this.register(new Setting<Boolean>("Armor", true));
    public Setting<Boolean> noAdvancements = this.register(new Setting<Boolean>("Advancement", true));
    public Setting<Boss> boss = this.register(new Setting<Boss>("BossBars", Boss.None));
    public Setting<Float> scale = this.register(new Setting<Object>("Scale", Float.valueOf(0.5f), Float.valueOf(0.0f), Float.valueOf(1.0f), v -> this.boss.getValue() == Boss.Minimize || this.boss.getValue() != Boss.Stack));
    private static NoRender INSTANCE;

    public NoRender() {
        super("NoRender", Module.Category.VISUAL, "stops certain things from rendering");
        INSTANCE = this;
    }

    public static NoRender getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoRender();
        }
        return INSTANCE;
    }

    @Override
    public void onUpdate() {
        if (this.items.getValue().booleanValue()) {
            for (Entity e : NoRender.mc.world.loadedEntityList) {
                if (!(e instanceof EntityItem)) {
                    return;
                }
                if (!NoRender.mc.player.canEntityBeSeen(e) && this.raytrace.getValue().booleanValue()) continue;
                if (this.posY.getValue().booleanValue() && NoRender.mc.player.posY < (double)this.y.getValue().intValue()) {
                    return;
                }
                e.setDead();
            }
        }
    }

    @SubscribeEvent
    public void onRenderPre(RenderGameOverlayEvent.Pre e) {
        if (e.getType() == RenderGameOverlayEvent.ElementType.BOSSINFO && this.boss.getValue() != Boss.None) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderPost(RenderGameOverlayEvent.Post e) {
        block7: {
            block8: {
                if (e.getType() != RenderGameOverlayEvent.ElementType.BOSSINFO || this.boss.getValue() == Boss.None) break block7;
                if (this.boss.getValue() != Boss.Minimize) break block8;
                Map map = NoRender.mc.ingameGUI.getBossOverlay().mapBossInfos;
                if (map == null) {
                    return;
                }
                ScaledResolution scaledresolution = new ScaledResolution(mc);
                int i = scaledresolution.getScaledWidth();
                int j = 12;
                for (Map.Entry entry : map.entrySet()) {
                    BossInfoClient info = (BossInfoClient)entry.getValue();
                    String text = info.getName().getFormattedText();
                    int k = (int)((float)i / this.scale.getValue().floatValue() / 2.0f - 91.0f);
                    GL11.glScaled((double)this.scale.getValue().floatValue(), (double)this.scale.getValue().floatValue(), (double)1.0);
                    if (!e.isCanceled()) {
                        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                        mc.getTextureManager().bindTexture(GuiBossOverlay.GUI_BARS_TEXTURES);
                        NoRender.mc.ingameGUI.getBossOverlay().render(k, j, (BossInfo)info);
                        NoRender.mc.fontRenderer.drawStringWithShadow(text, (float)i / this.scale.getValue().floatValue() / 2.0f - (float)(NoRender.mc.fontRenderer.getStringWidth(text) / 2), (float)(j - 9), 0xFFFFFF);
                    }
                    GL11.glScaled((double)(1.0 / (double)this.scale.getValue().floatValue()), (double)(1.0 / (double)this.scale.getValue().floatValue()), (double)1.0);
                    j += 10 + NoRender.mc.fontRenderer.FONT_HEIGHT;
                }
                break block7;
            }
            if (this.boss.getValue() != Boss.Stack) break block7;
            Map map = NoRender.mc.ingameGUI.getBossOverlay().mapBossInfos;
            HashMap to = new HashMap();
            for (Map.Entry entry2 : map.entrySet()) {
                Pair p;
                String s = ((BossInfoClient)entry2.getValue()).getName().getFormattedText();
                if (to.containsKey(s)) {
                    p = (Pair)to.get(s);
                    p = new Pair(p.getKey(), p.getValue() + 1);
                    to.put(s, p);
                    continue;
                }
                p = new Pair(entry2.getValue(), 1);
                to.put(s, p);
            }
            ScaledResolution scaledresolution2 = new ScaledResolution(mc);
            int l = scaledresolution2.getScaledWidth();
            int m = 12;
            for (Map.Entry entry3 : to.entrySet()) {
                String text = (String)entry3.getKey();
                BossInfoClient info2 = (BossInfoClient)((Pair)entry3.getValue()).getKey();
                int a = (Integer)((Pair)entry3.getValue()).getValue();
                text = text + " x" + a;
                int k2 = (int)((float)l / this.scale.getValue().floatValue() / 2.0f - 91.0f);
                GL11.glScaled((double)this.scale.getValue().floatValue(), (double)this.scale.getValue().floatValue(), (double)1.0);
                if (!e.isCanceled()) {
                    GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                    mc.getTextureManager().bindTexture(GuiBossOverlay.GUI_BARS_TEXTURES);
                    NoRender.mc.ingameGUI.getBossOverlay().render(k2, m, (BossInfo)info2);
                    NoRender.mc.fontRenderer.drawStringWithShadow(text, (float)l / this.scale.getValue().floatValue() / 2.0f - (float)(NoRender.mc.fontRenderer.getStringWidth(text) / 2), (float)(m - 9), 0xFFFFFF);
                }
                GL11.glScaled((double)(1.0 / (double)this.scale.getValue().floatValue()), (double)(1.0 / (double)this.scale.getValue().floatValue()), (double)1.0);
                m += 10 + NoRender.mc.fontRenderer.FONT_HEIGHT;
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST, receiveCanceled=true)
    public void onPacketReceive(PacketEvent.Receive e) {
        if (e.getPacket() instanceof SPacketMaps && this.noMaps.getValue().booleanValue() && this.isEnabled()) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderBlockOverlay(RenderBlockOverlayEvent e) {
        if (!NoRender.fullNullCheck() && this.isEnabled() && (e.getOverlayType() == RenderBlockOverlayEvent.OverlayType.WATER || e.getOverlayType() == RenderBlockOverlayEvent.OverlayType.BLOCK) && this.noOverlay.getValue().booleanValue()) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void something(EntityViewRenderEvent.FogDensity e) {
        if (this.noOverlay.getValue().booleanValue() && this.isEnabled() && (e.getState().getMaterial().equals((Object)Material.WATER) || e.getState().getMaterial().equals((Object)Material.LAVA))) {
            e.setDensity(0.0f);
            e.setCanceled(true);
        }
    }

    public static class Pair<T, S> {
        private T key;
        private S value;

        public Pair(T key, S value) {
            this.key = key;
            this.value = value;
        }

        public T getKey() {
            return this.key;
        }

        public S getValue() {
            return this.value;
        }

        public void setKey(T key) {
            this.key = key;
        }

        public void setValue(S value) {
            this.value = value;
        }
    }

    public static enum Boss {
        None,
        Remove,
        Stack,
        Minimize;

    }
}

