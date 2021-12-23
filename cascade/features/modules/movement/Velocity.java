/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.EntityFishHook
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.play.server.SPacketEntityStatus
 *  net.minecraft.network.play.server.SPacketEntityVelocity
 *  net.minecraft.network.play.server.SPacketExplosion
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.movement;

import cascade.event.events.PacketEvent;
import cascade.event.events.PushEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Velocity
extends Module {
    public Setting<Boolean> knockBack = this.register(new Setting<Boolean>("KnockBack", true));
    public Setting<Boolean> noPush = this.register(new Setting<Boolean>("NoPush", true));
    public Setting<Float> horizontal = this.register(new Setting<Float>("Horizontal", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(100.0f)));
    public Setting<Float> vertical = this.register(new Setting<Float>("Vertical", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(100.0f)));
    public Setting<Boolean> explosions = this.register(new Setting<Boolean>("Explosions", true));
    public Setting<Boolean> bobbers = this.register(new Setting<Boolean>("Bobbers", true));
    public Setting<Boolean> water = this.register(new Setting<Boolean>("Water", false));
    public Setting<Boolean> blocks = this.register(new Setting<Boolean>("Blocks", false));
    public Setting<Boolean> ice = this.register(new Setting<Boolean>("Ice", false));
    private static Velocity INSTANCE;

    public Velocity() {
        super("Velocity", Module.Category.MOVEMENT, "Tweaks velocity in various ways");
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static Velocity getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new Velocity();
        }
        return INSTANCE;
    }

    @Override
    public void onUpdate() {
        if (this.ice.getValue().booleanValue()) {
            Blocks.ICE.slipperiness = 0.6f;
            Blocks.PACKED_ICE.slipperiness = 0.6f;
            Blocks.FROSTED_ICE.slipperiness = 0.6f;
        }
    }

    @Override
    public void onDisable() {
        Blocks.ICE.slipperiness = 0.98f;
        Blocks.PACKED_ICE.slipperiness = 0.98f;
        Blocks.FROSTED_ICE.slipperiness = 0.98f;
    }

    @SubscribeEvent
    public void onPacketReceived(PacketEvent.Receive e) {
        if (this.megaNullCheck()) {
            return;
        }
        if (e.getStage() == 0 && Velocity.mc.player != null) {
            Entity entity;
            SPacketEntityStatus packet;
            SPacketEntityVelocity velocity;
            if (this.knockBack.getValue().booleanValue() && e.getPacket() instanceof SPacketEntityVelocity && (velocity = (SPacketEntityVelocity)e.getPacket()).getEntityID() == Velocity.mc.player.entityId) {
                if (this.horizontal.getValue().floatValue() == 0.0f && this.vertical.getValue().floatValue() == 0.0f) {
                    e.setCanceled(true);
                    return;
                }
                SPacketEntityVelocity sPacketEntityVelocity = velocity;
                sPacketEntityVelocity.motionX *= ((Integer)((Object)this.horizontal.getValue())).intValue();
                SPacketEntityVelocity sPacketEntityVelocity2 = velocity;
                sPacketEntityVelocity2.motionY *= ((Integer)((Object)this.vertical.getValue())).intValue();
                SPacketEntityVelocity sPacketEntityVelocity3 = velocity;
                sPacketEntityVelocity3.motionZ *= ((Integer)((Object)this.horizontal.getValue())).intValue();
            }
            if (e.getPacket() instanceof SPacketEntityStatus && this.bobbers.getValue().booleanValue() && (packet = (SPacketEntityStatus)e.getPacket()).getOpCode() == 31 && (entity = packet.getEntity((World)Velocity.mc.world)) instanceof EntityFishHook) {
                EntityFishHook fishHook = (EntityFishHook)entity;
                if (fishHook.caughtEntity == Velocity.mc.player) {
                    e.setCanceled(true);
                }
            }
            if (this.explosions.getValue().booleanValue() && e.getPacket() instanceof SPacketExplosion) {
                SPacketExplosion sPacketExplosion;
                SPacketExplosion velocity2 = sPacketExplosion = (SPacketExplosion)e.getPacket();
                sPacketExplosion.motionX *= this.horizontal.getValue().floatValue();
                SPacketExplosion sPacketExplosion2 = velocity2;
                sPacketExplosion2.motionY *= this.vertical.getValue().floatValue();
                SPacketExplosion sPacketExplosion3 = velocity2;
                sPacketExplosion3.motionZ *= this.horizontal.getValue().floatValue();
            }
        }
    }

    @SubscribeEvent
    public void onPush(PushEvent e) {
        if (this.megaNullCheck()) {
            return;
        }
        if (e.getStage() == 0 && this.noPush.getValue().booleanValue() && e.entity.equals((Object)Velocity.mc.player)) {
            if (this.horizontal.getValue().floatValue() == 0.0f && this.vertical.getValue().floatValue() == 0.0f) {
                e.setCanceled(true);
                return;
            }
            e.x = -e.x * (double)this.horizontal.getValue().floatValue();
            e.y = -e.y * (double)this.vertical.getValue().floatValue();
            e.z = -e.z * (double)this.horizontal.getValue().floatValue();
        } else if (e.getStage() == 1 && this.blocks.getValue().booleanValue()) {
            e.setCanceled(true);
        } else if (e.getStage() == 2 && this.water.getValue().booleanValue() && Velocity.mc.player != null && Velocity.mc.player.equals((Object)e.entity)) {
            e.setCanceled(true);
        }
    }
}

