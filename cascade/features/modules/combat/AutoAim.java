/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.util.math.Vec3d
 */
package cascade.features.modules.combat;

import cascade.Mod;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.CombatUtil;
import cascade.util.EntityUtil;
import cascade.util.InventoryUtil;
import cascade.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;

public class AutoAim
extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Instant));
    public Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(25.0f)));
    EntityPlayer target = null;

    public AutoAim() {
        super("AutoAim", Module.Category.COMBAT, "Aimbot");
    }

    @Override
    public void onUpdate() {
        if (AutoAim.fullNullCheck()) {
            return;
        }
        if (!InventoryUtil.heldItem((Item)Items.BOW, InventoryUtil.Hand.Both) && !AutoAim.mc.player.isHandActive() && AutoAim.mc.player.getItemInUseMaxCount() < 3) {
            return;
        }
        this.target = CombatUtil.getTarget(this.range.getValue().floatValue());
        if (this.target != null && !Mod.friendManager.isFriend(this.target.getName())) {
            Vec3d pos = this.target.getPositionVector();
            double xPos = pos.x;
            double yPos = pos.y;
            double zPos = pos.z;
            if (AutoAim.mc.player.canEntityBeSeen((Entity)this.target)) {
                yPos += (double)this.target.eyeHeight;
            } else if (EntityUtil.canEntityFeetBeSeen((Entity)this.target)) {
                yPos += 0.1;
            }
            if (this.mode.getValue() == Mode.Legit) {
                Mod.rotationManager.lookAtVec3d(xPos, yPos, zPos);
            }
            Vec3d vec = new Vec3d(this.target.posX, this.target.posY + 1.0, this.target.posZ);
            if (this.mode.getValue() == Mode.Instant) {
                AutoAim.faceVector(vec);
            }
        }
    }

    public static void faceVector(Vec3d vec) {
        float[] rotations = RotationUtil.getLegitRotations(vec);
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Rotation(rotations[0], rotations[1], AutoAim.mc.player.onGround));
    }

    @Override
    public String getDisplayInfo() {
        if (this.target != null) {
            return this.target.getName();
        }
        return null;
    }

    public static enum Mode {
        Legit,
        Instant;

    }
}

