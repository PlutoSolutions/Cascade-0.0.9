/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.movement;

import cascade.Mod;
import cascade.event.events.MoveEvent;
import cascade.event.events.UpdateWalkingPlayerEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.EntityUtil;
import cascade.util.InventoryUtil;
import cascade.util.Timer;
import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Strafe
extends Module {
    private static Strafe INSTANCE;
    Setting<Boolean> limiter = this.register(new Setting<Boolean>("SetGround", true));
    Setting<Boolean> dogshit = this.register(new Setting<Boolean>("LagCalc", false));
    Setting<Boolean> liquidReturn = this.register(new Setting<Boolean>("PauseInLiquid", true));
    Setting<Boolean> bhop2 = this.register(new Setting<Boolean>("Hop", true));
    Setting<Float> speedLimit = this.register(new Setting<Float>("KPHLimit", Float.valueOf(35.5f), Float.valueOf(20.0f), Float.valueOf(60.0f)));
    Setting<Integer> yOffset = this.register(new Setting<Integer>("YOffset", 401, 350, 420));
    Setting<Integer> startStage = this.register(new Setting<Integer>("StartFactor", 2, 0, 4));
    Setting<Boolean> step = this.register(new Setting<Boolean>("Step", false));
    Setting<Boolean> burrowCheck = this.register(new Setting<Object>("BurrowCheck", Boolean.valueOf(true), v -> this.step.getValue()));
    Setting<Float> height = this.register(new Setting<Object>("Height", Float.valueOf(1.3f), Float.valueOf(0.5f), Float.valueOf(2.0f), v -> this.step.getValue()));
    Setting<Boolean> onShift = this.register(new Setting<Object>("OnShift", Boolean.valueOf(true), v -> this.step.getValue()));
    Setting<Float> heightShift = this.register(new Setting<Object>("ShiftHeight", Float.valueOf(2.0f), Float.valueOf(0.1f), Float.valueOf(2.0f), v -> this.step.getValue()));
    Setting<TimerF> timerF = this.register(new Setting<TimerF>("Timer", TimerF.Strict));
    int stage = 1;
    double moveSpeed;
    double lastDist;
    Timer timer = new Timer();

    public Strafe() {
        super("Strafe", Module.Category.MOVEMENT, "Its obvious");
        INSTANCE = this;
    }

    public static Strafe getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Strafe();
        }
        return INSTANCE;
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.272;
        if (Strafe.mc.player.isPotionActive(MobEffects.SPEED)) {
            int amplifier = Objects.requireNonNull(Strafe.mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (double)amplifier;
        }
        return baseSpeed;
    }

    @Override
    public void onEnable() {
        if (Strafe.fullNullCheck()) {
            this.disable();
            return;
        }
        this.timer.reset();
        if (this.step.getValue().booleanValue()) {
            Strafe.mc.player.stepHeight = 0.6f;
        }
        this.moveSpeed = Strafe.getBaseMoveSpeed();
        Mod.timerManager.reset();
    }

    @Override
    public void onDisable() {
        this.moveSpeed = 0.0;
        this.stage = this.startStage.getValue();
        this.timer.reset();
        if (this.step.getValue().booleanValue()) {
            Strafe.mc.player.stepHeight = 0.6f;
        }
        Mod.timerManager.reset();
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent e) {
        if (this.megaNullCheck()) {
            this.disable();
            return;
        }
        if (e.getStage() == 0 && this.dogshit.getValue().booleanValue()) {
            this.lastDist = Math.sqrt((Strafe.mc.player.posX - Strafe.mc.player.prevPosX) * (Strafe.mc.player.posX - Strafe.mc.player.prevPosX) + (Strafe.mc.player.posZ - Strafe.mc.player.prevPosZ) * (Strafe.mc.player.posZ - Strafe.mc.player.prevPosZ));
        }
        if (this.step.getValue().booleanValue()) {
            if (this.burrowCheck.getValue().booleanValue()) {
                BlockPos pos = new BlockPos(Strafe.mc.player.posX, Strafe.mc.player.posY + 0.2, Strafe.mc.player.posZ);
                if (Strafe.mc.world.getBlockState(pos).getBlock() == Blocks.AIR) {
                    Mod.movement.doStep(this.height.getValue().floatValue(), true);
                    if (this.onShift.getValue().booleanValue() && Strafe.mc.gameSettings.keyBindSneak.isKeyDown()) {
                        Mod.movement.doStep(this.heightShift.getValue().floatValue(), true);
                    }
                }
            } else {
                Mod.movement.doStep(this.height.getValue().floatValue(), true);
                if (this.onShift.getValue().booleanValue() && Strafe.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    Mod.movement.doStep(this.heightShift.getValue().floatValue(), true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent e) {
        if (this.megaNullCheck()) {
            this.disable();
            return;
        }
        if (Mod.moduleManager.isModuleEnabled("Tickshift") || Mod.moduleManager.isModuleEnabled("PacketFly") || Mod.moduleManager.isModuleEnabled("Phase") || e.getStage() != 0) {
            return;
        }
        if (EntityUtil.isInLiquid() && this.liquidReturn.getValue().booleanValue()) {
            return;
        }
        this.doNCP(e);
    }

    private void doNCP(MoveEvent e) {
        if (this.megaNullCheck()) {
            this.disable();
            return;
        }
        if (Strafe.mc.gameSettings.keyBindSneak.isKeyDown()) {
            Strafe.mc.player.setSneaking(false);
        }
        if (this.timerF.getValue() != TimerF.None) {
            if (InventoryUtil.heldItem(Items.EXPERIENCE_BOTTLE, InventoryUtil.Hand.Both) && Strafe.mc.player.isHandActive()) {
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
        if (!this.limiter.getValue().booleanValue() && Strafe.mc.player.onGround) {
            this.stage = 2;
        }
        switch (this.stage) {
            case 0: {
                ++this.stage;
                this.lastDist = 0.0;
                break;
            }
            case 2: {
                double motionY;
                double d = motionY = this.yOffset.getValue() == 401 ? 0.40123128 : (double)((float)this.yOffset.getValue().intValue() / 1000.0f);
                if (Strafe.mc.player.moveForward == 0.0f && Strafe.mc.player.moveStrafing == 0.0f || !Strafe.mc.player.onGround) break;
                if (Strafe.mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                    motionY += (double)((float)(Strafe.mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f);
                }
                if (!EntityUtil.isInLiquid()) {
                    Strafe.mc.player.motionY = motionY;
                    e.setY(Strafe.mc.player.motionY);
                }
                this.moveSpeed *= 2.149;
                break;
            }
            case 3: {
                this.moveSpeed = this.lastDist - 0.76 * (this.lastDist - Strafe.getBaseMoveSpeed());
                break;
            }
            default: {
                if (Strafe.mc.world.getCollisionBoxes((Entity)Strafe.mc.player, Strafe.mc.player.getEntityBoundingBox().offset(0.0, Strafe.mc.player.motionY, 0.0)).size() > 0 || Strafe.mc.player.collidedVertically && this.stage > 0) {
                    this.stage = this.bhop2.getValue() != false && Mod.speedManager.getSpeedKpH() >= (double)this.speedLimit.getValue().floatValue() ? 0 : (Strafe.mc.player.moveForward != 0.0f || Strafe.mc.player.moveStrafing != 0.0f ? 1 : 0);
                }
                this.moveSpeed = this.lastDist - this.lastDist / 159.0;
            }
        }
        this.moveSpeed = Math.max(this.moveSpeed, Strafe.getBaseMoveSpeed());
        double forward = Strafe.mc.player.movementInput.moveForward;
        double strafe = Strafe.mc.player.movementInput.moveStrafe;
        double yaw = Strafe.mc.player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            e.setX(0.0);
            e.setZ(0.0);
        } else if (forward != 0.0 && strafe != 0.0) {
            forward *= Math.sin(0.7853981633974483);
            strafe *= Math.cos(0.7853981633974483);
        }
        e.setX((forward * this.moveSpeed * -Math.sin(Math.toRadians(yaw)) + strafe * this.moveSpeed * Math.cos(Math.toRadians(yaw))) * 0.99);
        e.setZ((forward * this.moveSpeed * Math.cos(Math.toRadians(yaw)) - strafe * this.moveSpeed * -Math.sin(Math.toRadians(yaw))) * 0.99);
        ++this.stage;
    }

    static enum TimerF {
        Strict,
        Fast,
        None;

    }
}

