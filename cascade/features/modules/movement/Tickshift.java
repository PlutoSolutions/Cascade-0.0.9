/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.init.Items
 *  net.minecraft.util.MovementInput
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.movement;

import cascade.Mod;
import cascade.event.events.MoveEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.EntityUtil;
import cascade.util.InventoryUtil;
import cascade.util.MathUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.init.Items;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Tickshift
extends Module {
    public Setting<Boolean> step = this.register(new Setting<Boolean>("Step", true));
    public Setting<Boolean> charge = this.register(new Setting<Boolean>("Charge", true));
    public Setting<Float> chargeTimer = this.register(new Setting<Object>("ChargeTimer", Float.valueOf(9.8f), Float.valueOf(0.1f), Float.valueOf(100.0f), v -> this.charge.getValue()));
    Setting<Integer> chargeTicks = this.register(new Setting<Object>("ChargeTicks", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(100), v -> this.charge.getValue()));
    public Setting<Float> timer = this.register(new Setting<Float>("Timer", Float.valueOf(12.8f), Float.valueOf(0.1f), Float.valueOf(30.0f)));
    public Setting<Integer> ticksVal = this.register(new Setting<Integer>("Ticks", 13, 1, 100));
    public Setting<Double> distance = this.register(new Setting<Double>("Distance", 15.0, 0.1, 15.0));
    BlockPos startPos = null;
    int ticks;

    public Tickshift() {
        super("Tickshift", Module.Category.MOVEMENT, "wtf 0x22 disliek dar");
    }

    @Override
    public void onUpdate() {
        if (Tickshift.fullNullCheck()) {
            this.disable();
            return;
        }
        ++this.ticks;
        if (this.ticks >= this.ticksVal.getValue()) {
            this.disable();
        } else if (this.startPos != null && Tickshift.mc.player.getDistanceSq(this.startPos) >= MathUtil.square(this.distance.getValue())) {
            this.disable();
        }
    }

    @Override
    public void onEnable() {
        if (Tickshift.fullNullCheck()) {
            this.disable();
            return;
        }
        Mod.timerManager.reset();
        this.ticks = 0;
        Mod.movement.setMotion(0.0, 0.0, 0.0);
        this.startPos = Tickshift.mc.player.getPosition();
        if (this.step.getValue().booleanValue()) {
            Tickshift.mc.player.stepHeight = 0.6f;
        }
    }

    @Override
    public void onDisable() {
        if (Tickshift.fullNullCheck()) {
            this.disable();
            return;
        }
        Mod.timerManager.reset();
        this.ticks = 0;
        this.startPos = null;
        if (this.step.getValue().booleanValue()) {
            Tickshift.mc.player.stepHeight = 0.6f;
        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent e) {
        if (this.megaNullCheck()) {
            return;
        }
        if (!(e.getStage() != 0 || Tickshift.mc.player.isSneaking() || EntityUtil.isInLiquid() || Tickshift.mc.player.movementInput.moveForward == 0.0f && Tickshift.mc.player.movementInput.moveStrafe == 0.0f || Tickshift.mc.player.isOnLadder())) {
            if (InventoryUtil.heldItem(Items.EXPERIENCE_BOTTLE, InventoryUtil.Hand.Both) && Tickshift.mc.player.isHandActive()) {
                return;
            }
            if (this.charge.getValue().booleanValue()) {
                Mod.timerManager.setTimer(this.chargeTimer.getValue().floatValue() / 100.0f);
                if (this.ticks >= this.chargeTicks.getValue()) {
                    Mod.timerManager.setTimer(this.timer.getValue().floatValue());
                }
            } else {
                Mod.timerManager.setTimer(this.timer.getValue().floatValue());
            }
            if (this.step.getValue().booleanValue()) {
                Mod.movement.doStep(2.3f, false);
            }
            MovementInput movementInput = Tickshift.mc.player.movementInput;
            float moveForward = movementInput.moveForward;
            float moveStrafe = movementInput.moveStrafe;
            float rotationYaw = Tickshift.mc.player.rotationYaw;
            if ((double)moveForward == 0.0 && (double)moveStrafe == 0.0) {
                e.setX(0.0);
                e.setZ(0.0);
            } else {
                if ((double)moveForward != 0.0) {
                    if ((double)moveStrafe > 0.0) {
                        rotationYaw += (float)((double)moveForward > 0.0 ? -45 : 45);
                    } else if ((double)moveStrafe < 0.0) {
                        rotationYaw += (float)((double)moveForward > 0.0 ? 45 : -45);
                    }
                    moveStrafe = 0.0f;
                    float f = moveForward == 0.0f ? moveForward : (moveForward = (double)moveForward > 0.0 ? 1.0f : -1.0f);
                }
                moveStrafe = moveStrafe == 0.0f ? moveStrafe : ((double)moveStrafe > 0.0 ? 1.0f : -1.0f);
                e.setX((double)moveForward * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians(rotationYaw + 90.0f)) + (double)moveStrafe * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians(rotationYaw + 90.0f)));
                e.setZ((double)moveForward * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians(rotationYaw + 90.0f)) - (double)moveStrafe * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians(rotationYaw + 90.0f)));
            }
        }
    }

    @Override
    public String getDisplayInfo() {
        if (this.charge.getValue().booleanValue()) {
            if (this.ticks < this.chargeTicks.getValue()) {
                return (Object)ChatFormatting.RED + "Switching";
            }
            if (this.ticks >= this.chargeTicks.getValue()) {
                return (Object)ChatFormatting.GREEN + "Switched";
            }
        } else {
            return this.ticks + "";
        }
        return null;
    }
}

