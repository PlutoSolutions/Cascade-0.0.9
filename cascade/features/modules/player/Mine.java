/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketHeldItemChange
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.input.Keyboard
 */
package cascade.features.modules.player;

import cascade.Mod;
import cascade.event.events.BlockEvent;
import cascade.event.events.Render3DEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Bind;
import cascade.features.setting.Setting;
import cascade.util.InventoryUtil;
import cascade.util.RenderUtil;
import cascade.util.Timer;
import java.awt.Color;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class Mine
extends Module {
    private static Mine INSTANCE = new Mine();
    int delay;
    public Timer timer = new Timer();
    public Setting<Boolean> silentSwitch = this.register(new Setting<Boolean>("SilentSwitch", false));
    public Setting<SilentSwitchMode> silentSwitchMode = this.register(new Setting<SilentSwitchMode>("SilentSwitchMode", SilentSwitchMode.AUTO, v -> this.silentSwitch.getValue()));
    public Setting<Bind> switchBind = this.register(new Setting<Object>("SwitchBind", new Bind(-1), v -> this.silentSwitch.getValue() != false && this.silentSwitchMode.getValue() == SilentSwitchMode.KEYBIND));
    public Setting<Boolean> render = this.register(new Setting<Boolean>("Render", true));
    public Setting<RenderMode> renderMode = this.register(new Setting<Object>("RenderMode", (Object)RenderMode.EXPAND, v -> this.render.getValue()));
    public Setting<BoxMode> boxMode = this.register(new Setting<Object>("BoxMode", (Object)BoxMode.BOTH, v -> this.render.getValue()));
    public Setting<ColorMode> colorMode = this.register(new Setting<Object>("ColorMode", (Object)ColorMode.READYFADE, v -> this.render.getValue()));
    public Setting<Color> c = this.register(new Setting<Object>("Color", new Color(-1), v -> this.render.getValue()));
    public Setting<Color> readyc = this.register(new Setting<Object>("ReadyColor", new Color(-1), v -> this.render.getValue() != false && (this.colorMode.getValue().equals((Object)ColorMode.STATUS) || this.colorMode.getValue().equals((Object)ColorMode.READYFADE))));
    public Setting<Integer> speed = this.register(new Setting<Object>("ReadySpeed", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(5), v -> this.render.getValue() != false && (this.colorMode.getValue().equals((Object)ColorMode.STATUS) || this.colorMode.getValue().equals((Object)ColorMode.READYFADE))));
    int currentAlpha;
    int count;
    ItemStack item;
    int subVal = 40;
    AxisAlignedBB bb;
    public BlockPos currentPos;
    public Block currentBlock;
    public IBlockState currentBlockState;
    int pickSlot;
    int oldSlot;
    int red0 = this.c.getValue().getRed() - 1;
    int green0 = this.c.getValue().getGreen() - 1;
    int blue0 = this.c.getValue().getBlue() - 1;

    public Mine() {
        super("Mine", Module.Category.PLAYER, "tweaks mining shit");
        this.setInstance();
    }

    public static Mine getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Mine();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onToggle() {
        this.currentPos = null;
    }

    @Override
    public void onLogout() {
        if (this.isEnabled()) {
            this.disable();
            this.enable();
        }
    }

    @Override
    public void onTick() {
        if (Mine.fullNullCheck()) {
            return;
        }
        this.pickSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
        this.delay = this.delay > 5 ? 0 : ++this.delay;
        if (!(this.currentPos == null || Mine.mc.world.getBlockState(this.currentPos).equals((Object)this.currentBlockState) && Mine.mc.world.getBlockState(this.currentPos).getBlock() != Blocks.AIR)) {
            this.currentPos = null;
            this.currentBlockState = null;
        }
        if (this.currentAlpha < this.c.getValue().getAlpha() - 2) {
            this.currentAlpha += 3;
        }
        if (this.silentSwitch.getValue().booleanValue() && this.silentSwitchMode.getValue() == SilentSwitchMode.AUTO && this.timer.passedMs((int)(2000.0f * Mod.serverManager.getTpsFactor())) && this.getPickSlot() != -1 && this.pickSlot != -1) {
            Mine.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.getPickSlot()));
        }
        if (this.silentSwitch.getValue().booleanValue() && this.silentSwitchMode.getValue() == SilentSwitchMode.AUTO && this.timer.passedMs((int)(2200.0f * Mod.serverManager.getTpsFactor()))) {
            this.oldSlot = Mine.mc.player.inventory.currentItem;
            Mine.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.oldSlot));
        }
        if (Mine.mc.player != null && this.silentSwitch.getValue().booleanValue() && this.silentSwitchMode.getValue() == SilentSwitchMode.KEYBIND && this.switchBind.getValue().getKey() != -1 && Keyboard.isKeyDown((int)this.switchBind.getValue().getKey()) && this.pickSlot != -1) {
            Mine.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.getPickSlot()));
            if (this.delay == 5) {
                this.oldSlot = Mine.mc.player.inventory.currentItem;
                Mine.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.oldSlot));
            }
        }
        if (this.currentPos != null) {
            if (this.currentBlock == Blocks.OBSIDIAN && Mine.getBestItem(this.currentBlock) != null) {
                this.subVal = 146;
            } else if (this.currentBlock == Blocks.ENDER_CHEST && Mine.getBestItem(this.currentBlock) != null) {
                this.subVal = 66;
            }
        }
        ++this.count;
        if (this.colorMode.getValue().equals((Object)ColorMode.READYFADE)) {
            if (this.red0 != this.readyc.getValue().getRed()) {
                this.red0 = this.red0 > this.readyc.getValue().getRed() ? (this.red0 -= this.speed.getValue().intValue()) : (this.red0 += this.speed.getValue().intValue());
            }
            if (this.green0 != this.readyc.getValue().getGreen()) {
                this.green0 = this.green0 > this.readyc.getValue().getGreen() ? (this.green0 -= this.speed.getValue().intValue()) : (this.green0 += this.speed.getValue().intValue());
            }
            if (this.blue0 != this.readyc.getValue().getBlue()) {
                this.blue0 = this.blue0 > this.readyc.getValue().getBlue() ? (this.blue0 -= this.speed.getValue().intValue()) : (this.blue0 += this.speed.getValue().intValue());
            }
        }
    }

    @Override
    public void onUpdate() {
        if (Mine.fullNullCheck()) {
            return;
        }
        Mine.mc.playerController.blockHitDelay = 0;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        try {
            if (this.currentPos != null) {
                if (Mine.getMineTime(this.currentBlock, this.item, false) == -1.0) {
                    return;
                }
                this.bb = Mine.mc.world.getBlockState(this.currentPos).getSelectedBoundingBox((World)Mine.mc.world, this.currentPos);
                Color color = new Color(this.colorMode.getValue().equals((Object)ColorMode.STATIC) ? this.c.getValue().getRed() - 1 : (this.colorMode.getValue().equals((Object)ColorMode.READYFADE) ? this.red0 : (this.colorMode.getValue().equals((Object)ColorMode.STATUS) && this.timer.passedMs((int)(2000.0f * Mod.serverManager.getTpsFactor())) ? this.readyc.getValue().getRed() : this.c.getValue().getRed() - 1)), this.colorMode.getValue().equals((Object)ColorMode.STATIC) ? this.c.getValue().getGreen() - 1 : (this.colorMode.getValue().equals((Object)ColorMode.READYFADE) ? this.green0 : (this.colorMode.getValue().equals((Object)ColorMode.STATUS) && this.timer.passedMs((int)(2000.0f * Mod.serverManager.getTpsFactor())) ? this.readyc.getValue().getGreen() : this.c.getValue().getGreen() - 1)), this.colorMode.getValue().equals((Object)ColorMode.STATIC) ? this.c.getValue().getBlue() - 1 : (this.colorMode.getValue().equals((Object)ColorMode.READYFADE) ? this.blue0 : (this.colorMode.getValue().equals((Object)ColorMode.STATUS) && this.timer.passedMs((int)(2000.0f * Mod.serverManager.getTpsFactor())) ? this.readyc.getValue().getBlue() : this.c.getValue().getBlue() - 1)), this.renderMode.getValue().equals((Object)RenderMode.FADE) ? this.currentAlpha : this.c.getValue().getAlpha() - 1);
                switch (this.renderMode.getValue()) {
                    case EXPAND: {
                        this.bb = this.bb.shrink(Math.max(Math.min(this.normalize(this.count, Mine.getMineTime(this.currentBlock, this.item, false) - (double)this.subVal, 0.0), 1.0), 0.0));
                        break;
                    }
                    case EXPAND2: {
                        this.bb = this.bb.setMaxY(this.bb.minY - 0.5 + Math.max(Math.min(this.normalize(this.count * 2, Mine.getMineTime(this.currentBlock, this.item, false) - (double)this.subVal, 0.0), 1.5), 0.0));
                        break;
                    }
                }
                if (this.render.getValue().booleanValue() && this.currentPos != null) {
                    switch (this.boxMode.getValue()) {
                        case OUTLINE: {
                            RenderUtil.drawBlockOutlineBB(this.bb, color, 1.0f);
                            break;
                        }
                        case FILL: {
                            RenderUtil.drawBBBox(this.bb, color, color.getAlpha());
                            break;
                        }
                        case BOTH: {
                            RenderUtil.drawBBBox(this.bb, color, color.getAlpha());
                            RenderUtil.drawBlockOutlineBB(this.bb, color, 1.0f);
                        }
                    }
                }
            }
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
    }

    @SubscribeEvent
    public void onBlockEvent(BlockEvent event) {
        if (event.getStage() == 3 && Mine.mc.playerController.curBlockDamageMP > 0.1f) {
            Mine.mc.playerController.isHittingBlock = true;
        }
        if (event.pos != this.currentPos && this.currentPos != null) {
            this.red0 = this.c.getValue().getRed() - 1;
            this.green0 = this.c.getValue().getGreen() - 1;
            this.blue0 = this.c.getValue().getBlue() - 1;
            this.currentAlpha = 0;
            this.count = 0;
            Mine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentPos, event.facing));
            Mine.mc.playerController.isHittingBlock = false;
            Mine.mc.playerController.curBlockDamageMP = 0.0f;
            this.currentPos = event.pos;
        }
        if (event.getStage() == 4 && Mine.canBreak(event.pos)) {
            Mine.mc.playerController.isHittingBlock = false;
            if (this.currentPos == null || event.pos != this.currentPos) {
                this.currentPos = event.pos;
                this.currentBlock = Mine.mc.world.getBlockState(this.currentPos).getBlock();
                this.currentBlockState = Mine.mc.world.getBlockState(this.currentPos);
                this.timer.reset();
                this.item = Mine.getBestItem(this.currentBlock) == null ? Mine.mc.player.getHeldItem(EnumHand.MAIN_HAND) : Mine.getItemStackFromItem(Mine.getBestItem(this.currentBlock));
            }
            this.currentAlpha = 0;
            this.count = 0;
            Mine.mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.getConnection().sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
            mc.getConnection().sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
            event.setCanceled(true);
        }
    }

    private int getPickSlot() {
        for (int i = 0; i < 9; ++i) {
            if (Mine.mc.player.inventory.getStackInSlot(i).getItem() != Items.DIAMOND_PICKAXE) continue;
            return i;
        }
        return -1;
    }

    public static boolean canBreak(BlockPos pos) {
        IBlockState blockState = Mine.mc.world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, (World)Mine.mc.world, pos) != -1.0f;
    }

    public static double getMineTime(Block block, ItemStack stack, boolean raw) {
        if (Objects.requireNonNull(stack.item.equals((Object)Items.AIR)).booleanValue() || stack.item.equals(null)) {
            return -1.0;
        }
        float speedMultiplier = stack.getDestroySpeed(block.getDefaultState());
        float damage = stack.canHarvestBlock(block.getDefaultState()) ? speedMultiplier / block.blockHardness / 30.0f : speedMultiplier / block.blockHardness / 100.0f;
        if (raw) {
            return damage;
        }
        return (float)Math.ceil(1.0 / (double)damage);
    }

    private double normalize(double value, double max, double min) {
        return 0.5 * ((value - min) / (max - min)) + 0.5;
    }

    public static Item getBestItem(Block block) {
        String tool = block.getHarvestTool(block.getDefaultState());
        if (tool != null) {
            switch (tool) {
                case "axe": {
                    return Items.DIAMOND_AXE;
                }
                case "shovel": {
                    return Items.DIAMOND_SHOVEL;
                }
            }
            return Items.DIAMOND_PICKAXE;
        }
        return Items.DIAMOND_PICKAXE;
    }

    public static ItemStack getItemStackFromItem(Item item) {
        if (Mine.mc.player == null) {
            return null;
        }
        for (int slot = 0; slot <= 9; ++slot) {
            if (Mine.mc.player.inventory.getStackInSlot(slot).getItem() != item) continue;
            return Mine.mc.player.inventory.getStackInSlot(slot);
        }
        return null;
    }

    public static enum BoxMode {
        FILL,
        OUTLINE,
        BOTH;

    }

    public static enum RenderMode {
        FADE,
        EXPAND,
        EXPAND2,
        STATIC;

    }

    public static enum ColorMode {
        READYFADE,
        STATUS,
        STATIC;

    }

    public static enum SilentSwitchMode {
        AUTO,
        KEYBIND;

    }
}

