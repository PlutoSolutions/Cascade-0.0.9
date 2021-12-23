/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.block.BlockObsidian
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.item.EntityXPOrb
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 */
package cascade.features.modules.combat;

import cascade.Mod;
import cascade.features.command.Command;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.BlockUtil;
import cascade.util.CombatUtil;
import cascade.util.EntityUtil;
import cascade.util.InventoryUtil;
import cascade.util.Timer;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Platform
extends Module {
    public Setting<Float> scanRange = this.register(new Setting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(8.0f)));
    public Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 0, 0, 300));
    public Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    public Setting<Boolean> preserveBlocks = this.register(new Setting<Boolean>("PreserveBlocks", true));
    public Setting<Integer> blocksPerTick = this.register(new Setting<Integer>("PlaceLimit", 12, 1, 30));
    public Setting<Boolean> onMotion = this.register(new Setting<Boolean>("OnMotion", true));
    public Setting<Float> kph = this.register(new Setting<Object>("KPH", Float.valueOf(16.0f), Float.valueOf(0.1f), Float.valueOf(32.0f), v -> this.onMotion.getValue()));
    public Setting<Boolean> autopause = this.register(new Setting<Boolean>("AutoPause", false));
    public Setting<Integer> pauseLimit = this.register(new Setting<Object>("PauseLimit", Integer.valueOf(18), Integer.valueOf(1), Integer.valueOf(64), v -> this.autopause.getValue()));
    Timer timer = new Timer();
    private final Vec3d[] offsetsFull = new Vec3d[]{new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, -1.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(-1.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0)};
    private final Vec3d[] offsetsPreserve = new Vec3d[]{new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0)};
    EntityPlayer target;
    private int offsetStep = 0;
    private int oldSlot = -1;
    int obbySlot;

    public Platform() {
        super("Platform", Module.Category.COMBAT, "Forms platforms under enemys feet");
    }

    @Override
    public void onEnable() {
        this.target = null;
        this.timer.reset();
    }

    @Override
    public void onDisable() {
        this.oldSlot = -1;
        this.target = null;
        this.timer.reset();
    }

    @Override
    public void onUpdate() {
        if (Platform.fullNullCheck()) {
            return;
        }
        this.target = CombatUtil.getTarget(this.scanRange.getValue().floatValue());
        this.obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        if (this.obbySlot == -1) {
            Command.sendMessage("Out of obsidian, disabling " + (Object)ChatFormatting.RED + "Platform", true, true);
            this.disable();
            return;
        }
        if (this.target == null || EntityUtil.isInWater((Entity)this.target)) {
            return;
        }
        if (this.onMotion.getValue().booleanValue() && Mod.speedManager.getPlayerSpeed(this.target) < (double)this.kph.getValue().floatValue()) {
            return;
        }
        ArrayList placeTargets = new ArrayList();
        Collections.addAll(placeTargets, this.preserveBlocks.getValue() != false ? this.offsetsPreserve : this.offsetsFull);
        int blocksPlaced = 0;
        while (blocksPlaced < this.blocksPerTick.getValue()) {
            if (this.offsetStep >= placeTargets.size()) {
                this.offsetStep = 0;
                break;
            }
            BlockPos offsetPos = new BlockPos((Vec3d)placeTargets.get(this.offsetStep));
            BlockPos targetPos = new BlockPos(this.target.getPositionVector()).down().add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
            boolean attemptPlacing = Platform.mc.world.getBlockState(targetPos).getMaterial().isReplaceable();
            for (Entity entity : Platform.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(targetPos))) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
                attemptPlacing = false;
                break;
            }
            if (attemptPlacing && this.timer.passedMs(this.delay.getValue().intValue())) {
                this.timer.reset();
                if (this.autopause.getValue().booleanValue()) {
                    if (blocksPlaced <= this.pauseLimit.getValue()) {
                        this.place(targetPos);
                        ++blocksPlaced;
                    }
                } else {
                    this.place(targetPos);
                    ++blocksPlaced;
                }
            }
            ++this.offsetStep;
        }
    }

    private void place(BlockPos pos) {
        this.oldSlot = Platform.mc.player.inventory.currentItem;
        InventoryUtil.SilentSwitchToSlot(this.obbySlot);
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), true, Platform.mc.player.isSneaking(), true);
        Platform.mc.player.inventory.currentItem = this.oldSlot;
        Platform.mc.playerController.updateController();
    }
}

