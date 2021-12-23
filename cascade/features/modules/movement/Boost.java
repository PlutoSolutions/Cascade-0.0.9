/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.util.MovementInput
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.movement;

import cascade.Mod;
import cascade.event.events.MoveEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.EntityUtil;
import net.minecraft.init.Items;
import net.minecraft.util.MovementInput;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Boost
extends Module {
    public Setting<Boolean> step = this.register(new Setting<Boolean>("Step", true));
    public Setting<Boolean> pauseLiquid = this.register(new Setting<Object>("PauseInLiquid", Boolean.valueOf(true), v -> this.step.getValue()));
    public final Setting<TimerF> timerF = this.register(new Setting<TimerF>("Timer", TimerF.Strict));

    public Boost() {
        super("Boost", Module.Category.MOVEMENT, "Boosts your speed");
    }

    @Override
    public void onToggle() {
        if (this.step.getValue().booleanValue()) {
            Boost.mc.player.stepHeight = 0.6f;
        }
        if (this.timerF.getValue() != TimerF.None) {
            Mod.timerManager.reset();
        }
    }

    @SubscribeEvent
    public void onMode(MoveEvent event) {
        if (Mod.moduleManager.isModuleEnabled("Tickshift") || Mod.moduleManager.isModuleEnabled("Strafe") || this.megaNullCheck()) {
            return;
        }
        if (!(event.getStage() != 0 || Boost.mc.player.isSneaking() || EntityUtil.isInLiquid() || Boost.mc.player.movementInput.moveForward == 0.0f && Boost.mc.player.movementInput.moveStrafe == 0.0f)) {
            if (this.timerF.getValue() != TimerF.None) {
                if (Boost.mc.player.inventory.getCurrentItem().getItem() == Items.EXPERIENCE_BOTTLE && Boost.mc.player.isHandActive()) {
                    return;
                }
                switch (this.timerF.getValue()) {
                    case Strict: {
                        Mod.timerManager.setTimer(1.088f);
                        break;
                    }
                    case Fast: {
                        Mod.timerManager.setTimer(1.15f);
                    }
                }
            }
            if (!(!this.step.getValue().booleanValue() || EntityUtil.isInLiquid() && this.pauseLiquid.getValue().booleanValue())) {
                Mod.movement.doStep(2.3f, true);
            }
            MovementInput movementInput = Boost.mc.player.movementInput;
            float moveForward = movementInput.moveForward;
            float moveStrafe = movementInput.moveStrafe;
            float rotationYaw = Boost.mc.player.rotationYaw;
            if ((double)moveForward == 0.0 && (double)moveStrafe == 0.0) {
                event.setX(0.0);
                event.setZ(0.0);
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
                event.setX((double)moveForward * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians(rotationYaw + 90.0f)) + (double)moveStrafe * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians(rotationYaw + 90.0f)));
                event.setZ((double)moveForward * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians(rotationYaw + 90.0f)) - (double)moveStrafe * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians(rotationYaw + 90.0f)));
            }
        }
    }

    public static enum TimerF {
        Strict,
        Fast,
        None;

    }
}

