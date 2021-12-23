/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.common.ForgeModContainer
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.visual;

import cascade.event.events.ClientEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Wallhack
extends Module {
    public static Wallhack INSTANCE;
    public Setting<Integer> opacity = this.register(new Setting<Integer>("Opacity", 120, 0, 255));
    public Setting<Integer> light = this.register(new Setting<Integer>("Light", 100, 0, 100));
    public Setting<Reload> reload = this.register(new Setting<Reload>("Reload", Reload.Soft));
    private boolean needsReload = false;
    public static ArrayList<Block> blocks;

    public Wallhack() {
        super("Wallhack", Module.Category.VISUAL, "Sets opacity for blocks");
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        ForgeModContainer.forgeLightPipelineEnabled = false;
        if (Wallhack.fullNullCheck()) {
            this.reload();
        } else {
            this.needsReload = true;
        }
    }

    @Override
    public void onDisable() {
        this.needsReload = false;
        this.reload();
        Wallhack.mc.renderChunksMany = false;
        ForgeModContainer.forgeLightPipelineEnabled = true;
    }

    @Override
    public void onUpdate() {
        if (this.needsReload) {
            this.needsReload = false;
            this.reload();
        }
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this) && this.isEnabled()) {
            this.reload();
        }
    }

    private void reload() {
        Wallhack.mc.renderChunksMany = true;
        if (this.reload.getValue() == Reload.All) {
            Wallhack.mc.renderGlobal.loadRenderers();
        }
        if (this.reload.getValue() == Reload.Soft) {
            Vec3d pos = Wallhack.mc.player.getPositionVector();
            int dist = Wallhack.mc.gameSettings.renderDistanceChunks * 16;
            Wallhack.mc.renderGlobal.markBlockRangeForRenderUpdate((int)pos.x - dist, (int)pos.y - dist, (int)pos.z - dist, (int)pos.x + dist, (int)pos.y + dist, (int)pos.z + dist);
        }
    }

    static {
        blocks = Lists.newArrayList((Object[])new Block[]{Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.GOLD_ORE, Blocks.LAPIS_ORE, Blocks.REDSTONE_ORE, Blocks.DIAMOND_ORE, Blocks.COAL_BLOCK, Blocks.IRON_BLOCK, Blocks.GOLD_BLOCK, Blocks.LAPIS_BLOCK, Blocks.REDSTONE_BLOCK, Blocks.DIAMOND_BLOCK, Blocks.IRON_BARS, Blocks.REDSTONE_LAMP, Blocks.LIT_REDSTONE_LAMP, Blocks.FURNACE, Blocks.LIT_FURNACE, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST});
    }

    public static enum Reload {
        Soft,
        All;

    }
}

