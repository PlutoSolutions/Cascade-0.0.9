/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.combat;

import cascade.Mod;
import cascade.event.events.UpdateWalkingPlayerEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.CombatUtil;
import cascade.util.EntityUtil;
import cascade.util.MathUtil;
import cascade.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Aura
extends Module {
    public static Entity target;
    private final Timer timer = new Timer();
    public Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(6.0f)));
    public Setting<Boolean> delay = this.register(new Setting<Boolean>("Delay", true));
    public Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    public Setting<Float> raytrace = this.register(new Setting<Float>("Raytrace", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(6.0f)));
    public Setting<Boolean> players = this.register(new Setting<Boolean>("Players", true));
    public Setting<Boolean> mobs = this.register(new Setting<Boolean>("Mobs", false));
    public Setting<Boolean> animals = this.register(new Setting<Boolean>("Animals", false));
    public Setting<Boolean> vehicles = this.register(new Setting<Boolean>("Entities", false));
    public Setting<Boolean> projectiles = this.register(new Setting<Boolean>("Projectiles", false));
    public Setting<Boolean> tps = this.register(new Setting<Boolean>("TpsSync", true));
    public Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", false));
    private static Aura INSTANCE;

    public Aura() {
        super("Aura", Module.Category.COMBAT, "Automatically attacks targets");
        INSTANCE = this;
    }

    public static Aura getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Aura();
        }
        return INSTANCE;
    }

    @Override
    public void onUpdate() {
        if (!this.rotate.getValue().booleanValue()) {
            this.doKillaura();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.rotate.getValue().booleanValue()) {
            this.doKillaura();
        }
    }

    private void doKillaura() {
        int wait;
        if (!CombatUtil.holdingWeapon()) {
            target = null;
            return;
        }
        int n = this.delay.getValue() == false ? 0 : (wait = (int)((float)CombatUtil.getCooldownByWeapon((EntityPlayer)Aura.mc.player) * (this.tps.getValue() != false ? Mod.serverManager.getTpsFactor() : 1.0f)));
        if (!this.timer.passedMs(wait)) {
            return;
        }
        target = this.getTarget();
        if (target == null) {
            return;
        }
        if (this.rotate.getValue().booleanValue()) {
            Mod.rotationManager.lookAtEntity(target);
        }
        CombatUtil.attackEntity(target, this.packet.getValue(), true);
        this.timer.reset();
    }

    private Entity getTarget() {
        Entity target = null;
        double distance = this.range.getValue().floatValue();
        double maxHealth = 36.0;
        for (Entity entity : Aura.mc.world.playerEntities) {
            if (!(this.players.getValue() != false && entity instanceof EntityPlayer || this.animals.getValue() != false && EntityUtil.isPassive(entity) || this.mobs.getValue() != false && EntityUtil.isMobAggressive(entity) || this.vehicles.getValue() != false && EntityUtil.isVehicle(entity)) && (!this.projectiles.getValue().booleanValue() || !EntityUtil.isProjectile(entity)) || entity instanceof EntityLivingBase && EntityUtil.isntValid(entity, distance) || !Aura.mc.player.canEntityBeSeen(entity) && !EntityUtil.canEntityFeetBeSeen(entity) && Aura.mc.player.getDistanceSq(entity) > MathUtil.square(this.raytrace.getValue().floatValue())) continue;
            if (target == null) {
                target = entity;
                distance = Aura.mc.player.getDistanceSq(entity);
                maxHealth = EntityUtil.getHealth(entity);
                continue;
            }
            if (entity instanceof EntityPlayer && CombatUtil.isArmorLow((EntityPlayer)entity, 18)) {
                target = entity;
                break;
            }
            if (Aura.mc.player.getDistanceSq(entity) < distance) {
                target = entity;
                distance = Aura.mc.player.getDistanceSq(entity);
                maxHealth = EntityUtil.getHealth(entity);
            }
            if (!((double)EntityUtil.getHealth(entity) < maxHealth)) continue;
            target = entity;
            distance = Aura.mc.player.getDistanceSq(entity);
            maxHealth = EntityUtil.getHealth(entity);
        }
        return target;
    }
}

