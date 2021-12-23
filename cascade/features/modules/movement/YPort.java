/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 */
package cascade.features.modules.movement;

import cascade.Mod;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class YPort
extends Module {
    public Setting<Double> speed = this.register(new Setting<Double>("YPortSpeed", 0.1, 0.0, 1.0));
    public Setting<Float> fallSpeed = this.register(new Setting<Float>("FallSpeed", Float.valueOf(0.8f), Float.valueOf(0.1f), Float.valueOf(9.0f)));
    public Setting<Integer> yMotion = this.register(new Setting<Integer>("YMotion", 390, 350, 420));
    public Setting<Boolean> noShake = this.register(new Setting<Boolean>("NoShake", true));
    private static YPort INSTANCE;
    public double startY;

    public YPort() {
        super("YPort", Module.Category.MOVEMENT, "speeeed");
        INSTANCE = this;
    }

    public static YPort getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new YPort();
        }
        return INSTANCE;
    }

    @Override
    public void onToggle() {
        if (YPort.fullNullCheck()) {
            return;
        }
        YPort.mc.player.stepHeight = 0.6f;
    }

    @Override
    public void onUpdate() {
        if (YPort.fullNullCheck()) {
            return;
        }
        if (YPort.mc.player.isSneaking() || EntityUtil.isInLiquid()) {
            return;
        }
        Mod.movement.doStep(2.3f, false);
        if (YPort.mc.player.onGround) {
            this.startY = YPort.mc.player.posY;
            EntityUtil.setSpeed((EntityLivingBase)YPort.mc.player, EntityUtil.getMaxSpeed() + this.speed.getValue());
            YPort.mc.player.motionY = (float)this.yMotion.getValue().intValue() / 1000.0f;
        } else {
            for (double y = 0.0; y < 3.0; y += 0.01) {
                if (YPort.mc.world.getCollisionBoxes((Entity)YPort.mc.player, YPort.mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) continue;
                YPort.mc.player.motionY = -this.fallSpeed.getValue().floatValue();
                break;
            }
        }
    }
}

