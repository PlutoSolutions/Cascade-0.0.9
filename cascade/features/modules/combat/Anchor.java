/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.util.math.Vec3d
 */
package cascade.features.modules.combat;

import cascade.Mod;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;

public class Anchor
extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Instant));
    public Setting<Boolean> onlySafe = this.register(new Setting<Boolean>("OnlySafe", true));
    public Setting<Boolean> onlyOnGround = this.register(new Setting<Boolean>("OnlyOnGround", true));
    Vec3d center = Vec3d.ZERO;

    public Anchor() {
        super("Anchor", Module.Category.COMBAT, "Helps you with entering h***s");
    }

    @Override
    public void onEnable() {
        if (Anchor.fullNullCheck()) {
            this.disable();
            return;
        }
        if (Mod.moduleManager.isModuleEnabled("Strafe") || Mod.moduleManager.isModuleEnabled("Boost") || Mod.moduleManager.isModuleEnabled("YPort") || Mod.moduleManager.isModuleEnabled("Tickshift")) {
            this.disable();
            return;
        }
        if (!Anchor.mc.player.onGround && this.onlyOnGround.getValue().booleanValue() || Anchor.mc.player.isOnLadder() || Anchor.mc.player.posY < 0.0) {
            this.disable();
            return;
        }
        if (this.onlySafe.getValue().booleanValue() && !EntityUtil.isPlayerSafe((EntityPlayer)Anchor.mc.player)) {
            this.disable();
            return;
        }
        if (this.mode.getValue() == Mode.Instant) {
            this.center = EntityUtil.getCenter(Anchor.mc.player.posX, Anchor.mc.player.posY, Anchor.mc.player.posZ);
            Mod.movement.setMotion(0.0, 0.0, 0.0);
            mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(this.center.x, this.center.y, this.center.z, Anchor.mc.player.onGround));
            Anchor.mc.player.setPosition(this.center.x, this.center.y, this.center.z);
        } else {
            Mod.movement.setMotion((this.center.x - Anchor.mc.player.posX) / 2.0, (this.center.y - Anchor.mc.player.posY) / 2.0, (this.center.z - Anchor.mc.player.posZ) / 2.0);
        }
        this.disable();
    }

    public static enum Mode {
        Instant,
        NCP;

    }
}

