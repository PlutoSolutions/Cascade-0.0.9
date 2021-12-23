/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.block.BlockWeb
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 */
package cascade.features.modules.combat;

import cascade.Mod;
import cascade.features.command.Command;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.BlockUtil;
import cascade.util.EntityUtil;
import cascade.util.InventoryUtil;
import cascade.util.Timer;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WebAura
extends Module {
    public Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 0, 0, 250));
    public Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(6.0f)));
    public Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    public Setting<Boolean> antiSelf = this.register(new Setting<Boolean>("AntiSelf", true));
    public Setting<Boolean> predict = this.register(new Setting<Boolean>("MotionPredict", true));
    public Setting<Boolean> head = this.register(new Setting<Boolean>("Head", true));
    public Setting<Boolean> feet = this.register(new Setting<Boolean>("Feet", true));
    Timer timer = new Timer();
    List<Vec3d> placeTargets;
    EntityPlayer target;

    public WebAura() {
        super("WebAura", Module.Category.COMBAT, "Traps enemies with webs");
    }

    @Override
    public void onEnable() {
        if (WebAura.fullNullCheck()) {
            return;
        }
        this.placeTargets = new ArrayList<Vec3d>();
        this.target = null;
    }

    @Override
    public void onUpdate() {
        if (WebAura.fullNullCheck()) {
            return;
        }
        int webSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
        if (webSlot != -1) {
            this.target = null;
            this.getTarget();
            if (this.timer.passedMs(this.delay.getValue().intValue())) {
                mc.addScheduledTask(() -> this.doTrap());
            }
        } else {
            Command.sendMessage("Out of webs, disabling " + (Object)ChatFormatting.RED + "WebAura", true, true);
            this.disable();
        }
    }

    private void doTrap() {
        if (this.target != null) {
            this.placeTargets.forEach(vec3d -> {
                if (this.canPlace(new BlockPos(vec3d))) {
                    int webSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
                    int ogSlot = WebAura.mc.player.inventory.currentItem;
                    InventoryUtil.SilentSwitchToSlot(webSlot);
                    BlockUtil.placeBlock(new BlockPos(vec3d), EnumHand.MAIN_HAND, this.rotate.getValue(), true, false, true);
                    if (WebAura.mc.player.inventory.currentItem != ogSlot) {
                        WebAura.mc.player.inventory.currentItem = ogSlot;
                        WebAura.mc.playerController.updateController();
                    }
                }
            });
        }
    }

    private boolean canPlace(BlockPos pos) {
        return WebAura.mc.world.getBlockState(pos).getBlock() == Blocks.AIR;
    }

    private void getTarget() {
        this.target = null;
        this.placeTargets = new ArrayList<Vec3d>();
        WebAura.mc.world.playerEntities.forEach(e -> {
            if (e != null && e.getHealth() > 0.0f && e != WebAura.mc.player && WebAura.mc.player.getDistance((Entity)e) <= this.range.getValue().floatValue() && !Mod.friendManager.isFriend(e.getName())) {
                if (EntityUtil.getRoundedBlockPos((Entity)WebAura.mc.player) == EntityUtil.getRoundedBlockPos((Entity)e) && this.antiSelf.getValue().booleanValue()) {
                    return;
                }
                this.target = e;
            }
        });
        if (this.target != null) {
            this.placeTargets = this.getPlacements();
        }
    }

    @Override
    public String getDisplayInfo() {
        if (this.target != null) {
            return this.target.getName();
        }
        return null;
    }

    private List<Vec3d> getPlacements() {
        ArrayList<Vec3d> list = new ArrayList<Vec3d>();
        Vec3d baseVec = this.target.getPositionVector();
        if (this.feet.getValue().booleanValue()) {
            if (this.predict.getValue().booleanValue()) {
                list.add(baseVec.add(0.0 + this.target.motionX, 0.0, 0.0 + this.target.motionZ));
            }
            list.add(baseVec.add(0.0, 0.0, 0.0));
        }
        if (this.head.getValue().booleanValue()) {
            if (this.predict.getValue().booleanValue()) {
                list.add(baseVec.add(0.0 + this.target.motionX, 1.0, 0.0 + this.target.motionZ));
            }
            list.add(baseVec.add(0.0, 1.0, 0.0));
        }
        return list;
    }
}

