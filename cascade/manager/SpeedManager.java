/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.MathHelper
 */
package cascade.manager;

import cascade.features.Feature;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class SpeedManager
extends Feature {
    public static boolean didJumpThisTick = false;
    public static boolean isJumping = false;
    private final int distancer = 20;
    public double firstJumpSpeed = 0.0;
    public double lastJumpSpeed = 0.0;
    public double percentJumpSpeedChanged = 0.0;
    public double jumpSpeedChanged = 0.0;
    public boolean didJumpLastTick = false;
    public long jumpInfoStartTime = 0L;
    public boolean wasFirstJump = true;
    public double speedometerCurrentSpeed = 0.0;
    public HashMap<EntityPlayer, Double> playerSpeeds = new HashMap();

    public void updateValues() {
        double distTraveledLastTickX = SpeedManager.mc.player.posX - SpeedManager.mc.player.prevPosX;
        double distTraveledLastTickZ = SpeedManager.mc.player.posZ - SpeedManager.mc.player.prevPosZ;
        this.speedometerCurrentSpeed = distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;
        if (didJumpThisTick && (!SpeedManager.mc.player.onGround || isJumping)) {
            if (didJumpThisTick && !this.didJumpLastTick) {
                this.wasFirstJump = this.lastJumpSpeed == 0.0;
                this.percentJumpSpeedChanged = this.speedometerCurrentSpeed != 0.0 ? this.speedometerCurrentSpeed / this.lastJumpSpeed - 1.0 : -1.0;
                this.jumpSpeedChanged = this.speedometerCurrentSpeed - this.lastJumpSpeed;
                this.jumpInfoStartTime = Minecraft.getSystemTime();
                this.lastJumpSpeed = this.speedometerCurrentSpeed;
                this.firstJumpSpeed = this.wasFirstJump ? this.lastJumpSpeed : 0.0;
            }
            this.didJumpLastTick = didJumpThisTick;
        } else {
            this.didJumpLastTick = false;
            this.lastJumpSpeed = 0.0;
        }
        this.updatePlayers();
    }

    public void updatePlayers() {
        for (EntityPlayer player : SpeedManager.mc.world.playerEntities) {
            double d = SpeedManager.mc.player.getDistanceSq((Entity)player);
            this.getClass();
            this.getClass();
            if (!(d < (double)(20 * 20))) continue;
            double distTraveledLastTickX = player.posX - player.prevPosX;
            double distTraveledLastTickZ = player.posZ - player.prevPosZ;
            double playerSpeed = distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;
            this.playerSpeeds.put(player, playerSpeed);
        }
    }

    public double getPlayerSpeed(EntityPlayer player) {
        if (this.playerSpeeds.get((Object)player) == null) {
            return 0.0;
        }
        return this.turnIntoKpH(this.playerSpeeds.get((Object)player));
    }

    public double turnIntoKpH(double input) {
        return (double)MathHelper.sqrt((double)input) * 71.2729367892;
    }

    public double getSpeedKpH() {
        double speedometerkphdouble = this.turnIntoKpH(this.speedometerCurrentSpeed);
        speedometerkphdouble = (double)Math.round(10.0 * speedometerkphdouble) / 10.0;
        return speedometerkphdouble;
    }
}

