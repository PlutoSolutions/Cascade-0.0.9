/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.item.EntityBoat
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.player;

import cascade.event.events.CollisionBoxEvent;
import cascade.event.events.UpdateWalkingPlayerEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.MathUtil;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BoatClip
extends Module {
    private Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Sand));
    private Setting<Boolean> noVoid = this.register(new Setting<Boolean>("NoVoid", true));
    private Setting<Boolean> autoClip = this.register(new Setting<Boolean>("AutoClip", false));
    private Setting<Double> distance = this.register(new Setting<Double>("Distance", 6.0, 0.1, 10.0));

    public BoatClip() {
        super("BoatClip", Module.Category.PLAYER, "Phase for boats");
    }

    @SubscribeEvent
    public void onAddCollisionBoxToList(CollisionBoxEvent event) {
        if (BoatClip.mc.player == null || BoatClip.mc.world == null) {
            return;
        }
        if (this.mode.getValue() == Mode.Sand) {
            if (BoatClip.mc.player.getRidingEntity() != null && event.getEntity() == BoatClip.mc.player.getRidingEntity()) {
                if (BoatClip.mc.gameSettings.keyBindSprint.isKeyDown() && this.noVoid.getValue().booleanValue()) {
                    event.setCanceled(true);
                } else {
                    if (BoatClip.mc.gameSettings.keyBindJump.isKeyDown() && (double)event.getPos().getY() >= BoatClip.mc.player.getRidingEntity().posY) {
                        event.setCanceled(true);
                    }
                    if ((double)event.getPos().getY() >= BoatClip.mc.player.getRidingEntity().posY) {
                        event.setCanceled(true);
                    }
                }
            }
        } else if (event.getEntity() == BoatClip.mc.player || BoatClip.mc.player.getRidingEntity() != null && event.getEntity() == BoatClip.mc.player.getRidingEntity()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (this.mode.getValue() == Mode.NoClip) {
            BoatClip.mc.player.setVelocity(0.0, 0.0, 0.0);
            if (BoatClip.mc.gameSettings.keyBindForward.isKeyDown() || BoatClip.mc.gameSettings.keyBindBack.isKeyDown() || BoatClip.mc.gameSettings.keyBindLeft.isKeyDown() || BoatClip.mc.gameSettings.keyBindRight.isKeyDown()) {
                double[] speed = MathUtil.directionSpeed(0.06f);
                BoatClip.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BoatClip.mc.player.posX + speed[0], BoatClip.mc.player.posY, BoatClip.mc.player.posZ + speed[1], BoatClip.mc.player.onGround));
                BoatClip.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BoatClip.mc.player.posX, 0.0, BoatClip.mc.player.posZ, BoatClip.mc.player.onGround));
            }
            if (BoatClip.mc.gameSettings.keyBindSneak.isKeyDown()) {
                BoatClip.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BoatClip.mc.player.posX, BoatClip.mc.player.posY - this.distance.getValue() / 1000.0, BoatClip.mc.player.posZ, BoatClip.mc.player.onGround));
                BoatClip.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BoatClip.mc.player.posX, 0.0, BoatClip.mc.player.posZ, BoatClip.mc.player.onGround));
            }
            if (BoatClip.mc.gameSettings.keyBindJump.isKeyDown()) {
                BoatClip.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BoatClip.mc.player.posX, BoatClip.mc.player.posY + this.distance.getValue() / 1000.0, BoatClip.mc.player.posZ, BoatClip.mc.player.onGround));
                BoatClip.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BoatClip.mc.player.posX, 0.0, BoatClip.mc.player.posZ, BoatClip.mc.player.onGround));
            }
        }
    }

    @Override
    public void onUpdate() {
        if (this.mode.getValue() == Mode.Sand && BoatClip.mc.gameSettings.keyBindJump.isKeyDown() && BoatClip.mc.player.getRidingEntity() != null && BoatClip.mc.player.getRidingEntity() instanceof EntityBoat) {
            EntityBoat boat = (EntityBoat)BoatClip.mc.player.getRidingEntity();
            if (boat.onGround) {
                boat.motionY = 0.42f;
            }
        }
    }

    @Override
    public void onEnable() {
        if (this.autoClip.getValue().booleanValue() && BoatClip.mc.player != null && BoatClip.mc.world != null) {
            double cos = Math.cos(Math.toRadians(BoatClip.mc.player.rotationYaw + 90.0f));
            double sin = Math.sin(Math.toRadians(BoatClip.mc.player.rotationYaw + 90.0f));
            BoatClip.mc.player.setPosition(BoatClip.mc.player.posX + (1.0 * (this.distance.getValue() / 1000.0) * cos + 0.0 * (this.distance.getValue() / 1000.0) * sin), BoatClip.mc.player.posY, BoatClip.mc.player.posZ + (1.0 * (this.distance.getValue() / 1000.0) * sin - 0.0 * (this.distance.getValue() / 1000.0) * cos));
        }
    }

    @Override
    public void onDisable() {
        BoatClip.mc.player.noClip = false;
    }

    private static enum Mode {
        Sand,
        NoClip;

    }
}

