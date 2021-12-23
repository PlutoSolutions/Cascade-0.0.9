/*
 * Decompiled with CFR 0.150.
 */
package cascade.manager;

import cascade.features.Feature;

public class MovementManager
extends Feature {
    public void doStep(float height, boolean check) {
        if (check && (!MovementManager.mc.player.collidedVertically || (double)MovementManager.mc.player.fallDistance > 0.1 || MovementManager.mc.player.isOnLadder() || !MovementManager.mc.player.onGround)) {
            return;
        }
        MovementManager.mc.player.stepHeight = height;
    }

    public void setMotion(double x, double y, double z) {
        if (MovementManager.mc.player != null) {
            if (MovementManager.mc.player.isRiding()) {
                MovementManager.mc.player.ridingEntity.motionX = x;
                MovementManager.mc.player.ridingEntity.motionY = y;
                MovementManager.mc.player.ridingEntity.motionZ = x;
            } else {
                MovementManager.mc.player.motionX = x;
                MovementManager.mc.player.motionY = y;
                MovementManager.mc.player.motionZ = z;
            }
        }
    }
}

