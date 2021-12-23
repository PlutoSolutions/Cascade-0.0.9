/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 */
package cascade.util;

import cascade.util.MathUtil;
import cascade.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class RotationUtil
implements Util {
    public static Vec3d getEyesPos() {
        return new Vec3d(RotationUtil.mc.player.posX, RotationUtil.mc.player.posY + (double)RotationUtil.mc.player.getEyeHeight(), RotationUtil.mc.player.posZ);
    }

    public static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = RotationUtil.getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{RotationUtil.mc.player.rotationYaw + MathHelper.wrapDegrees((float)(yaw - RotationUtil.mc.player.rotationYaw)), RotationUtil.mc.player.rotationPitch + MathHelper.wrapDegrees((float)(pitch - RotationUtil.mc.player.rotationPitch))};
    }

    public static void faceYawAndPitch(float yaw, float pitch) {
        RotationUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(yaw, pitch, RotationUtil.mc.player.onGround));
    }

    public static void faceVector(Vec3d vec, boolean normalizeAngle) {
        float[] rotations = RotationUtil.getLegitRotations(vec);
        RotationUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(rotations[0], normalizeAngle ? (float)MathHelper.normalizeAngle((int)((int)rotations[1]), (int)360) : rotations[1], RotationUtil.mc.player.onGround));
    }

    public static void faceEntity(Entity entity) {
        float[] angle = MathUtil.calcAngle(RotationUtil.mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionEyes(mc.getRenderPartialTicks()));
        RotationUtil.faceYawAndPitch(angle[0], angle[1]);
    }

    public static float[] getAngle(Entity entity) {
        return MathUtil.calcAngle(RotationUtil.mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionEyes(mc.getRenderPartialTicks()));
    }

    public static int getDirection4D() {
        return MathHelper.floor((double)((double)(RotationUtil.mc.player.rotationYaw * 4.0f / 360.0f) + 0.5)) & 3;
    }

    public static String getDirection4D(boolean northRed) {
        int dirnumber = RotationUtil.getDirection4D();
        if (dirnumber == 0) {
            return "South (+Z)";
        }
        if (dirnumber == 1) {
            return "West (-X)";
        }
        if (dirnumber == 2) {
            return (northRed ? "\u00c2\u00a7c" : "") + "North (-Z)";
        }
        if (dirnumber == 3) {
            return "East (+X)";
        }
        return "Loading...";
    }

    public static boolean isInFov(BlockPos pos) {
        return pos != null && (RotationUtil.mc.player.getDistanceSq(pos) < 4.0 || RotationUtil.yawDist(pos) < (double)(RotationUtil.getHalvedfov() + 2.0f));
    }

    public static double yawDist(BlockPos pos) {
        if (pos != null) {
            Vec3d difference = new Vec3d((Vec3i)pos).subtract(RotationUtil.mc.player.getPositionEyes(mc.getRenderPartialTicks()));
            double d = Math.abs((double)RotationUtil.mc.player.rotationYaw - (Math.toDegrees(Math.atan2(difference.z, difference.x)) - 90.0)) % 360.0;
            return d > 180.0 ? 360.0 - d : d;
        }
        return 0.0;
    }

    public static float getHalvedfov() {
        return RotationUtil.getFov() / 2.0f;
    }

    public static float getFov() {
        return RotationUtil.mc.gameSettings.fovSetting;
    }
}

