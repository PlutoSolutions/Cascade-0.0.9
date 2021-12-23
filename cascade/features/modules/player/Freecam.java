/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityOtherPlayerMP
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketChatMessage
 *  net.minecraft.network.play.client.CPacketConfirmTeleport
 *  net.minecraft.network.play.client.CPacketKeepAlive
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItem
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.network.play.client.CPacketVehicleMove
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraft.network.play.server.SPacketSetPassengers
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.player;

import cascade.event.events.PacketEvent;
import cascade.event.events.PushEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.MathUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Freecam
extends Module {
    public Setting<Double> speed = this.register(new Setting<Double>("Speed", 0.5, 0.1, 5.0));
    public Setting<Boolean> view = this.register(new Setting<Boolean>("3D", false));
    public Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", true));
    public Setting<Boolean> legit = this.register(new Setting<Boolean>("Legit", false));
    private static Freecam INSTANCE;
    private AxisAlignedBB oldBoundingBox;
    private EntityOtherPlayerMP entity;
    private Vec3d position;
    private Entity riding;
    private float yaw;
    private float pitch;

    public Freecam() {
        super("Freecam", Module.Category.PLAYER, "Lets u look around freely");
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static Freecam getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Freecam();
        }
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        if (!Freecam.fullNullCheck()) {
            this.oldBoundingBox = Freecam.mc.player.getEntityBoundingBox();
            Freecam.mc.player.setEntityBoundingBox(new AxisAlignedBB(Freecam.mc.player.posX, Freecam.mc.player.posY, Freecam.mc.player.posZ, Freecam.mc.player.posX, Freecam.mc.player.posY, Freecam.mc.player.posZ));
            if (Freecam.mc.player.getRidingEntity() != null) {
                this.riding = Freecam.mc.player.getRidingEntity();
                Freecam.mc.player.dismountRidingEntity();
            }
            this.entity = new EntityOtherPlayerMP((World)Freecam.mc.world, Freecam.mc.session.getProfile());
            this.entity.copyLocationAndAnglesFrom((Entity)Freecam.mc.player);
            this.entity.rotationYaw = Freecam.mc.player.rotationYaw;
            this.entity.rotationYawHead = Freecam.mc.player.rotationYawHead;
            this.entity.inventory.copyInventory(Freecam.mc.player.inventory);
            Freecam.mc.world.addEntityToWorld(726804364, (Entity)this.entity);
            this.position = Freecam.mc.player.getPositionVector();
            this.yaw = Freecam.mc.player.rotationYaw;
            this.pitch = Freecam.mc.player.rotationPitch;
            Freecam.mc.player.noClip = true;
        }
    }

    @Override
    public void onDisable() {
        if (!Freecam.fullNullCheck()) {
            Freecam.mc.player.setEntityBoundingBox(this.oldBoundingBox);
            if (this.riding != null) {
                Freecam.mc.player.startRiding(this.riding, true);
            }
            if (this.entity != null) {
                Freecam.mc.world.removeEntity((Entity)this.entity);
            }
            if (this.position != null) {
                Freecam.mc.player.setPosition(this.position.x, this.position.y, this.position.z);
            }
            Freecam.mc.player.rotationYaw = this.yaw;
            Freecam.mc.player.rotationPitch = this.pitch;
            Freecam.mc.player.noClip = false;
        }
    }

    @Override
    public void onUpdate() {
        if (Freecam.fullNullCheck()) {
            return;
        }
        Freecam.mc.player.noClip = true;
        Freecam.mc.player.setVelocity(0.0, 0.0, 0.0);
        Freecam.mc.player.jumpMovementFactor = this.speed.getValue().floatValue();
        double[] dir = MathUtil.directionSpeed(this.speed.getValue());
        if (Freecam.mc.player.movementInput.moveStrafe != 0.0f || Freecam.mc.player.movementInput.moveForward != 0.0f) {
            Freecam.mc.player.motionX = dir[0];
            Freecam.mc.player.motionZ = dir[1];
        } else {
            Freecam.mc.player.motionX = 0.0;
            Freecam.mc.player.motionZ = 0.0;
        }
        Freecam.mc.player.setSprinting(false);
        if (this.view.getValue().booleanValue() && !Freecam.mc.gameSettings.keyBindSneak.isKeyDown() && !Freecam.mc.gameSettings.keyBindJump.isKeyDown()) {
            Freecam.mc.player.motionY = this.speed.getValue() * -MathUtil.degToRad(Freecam.mc.player.rotationPitch) * (double)Freecam.mc.player.movementInput.moveForward;
        }
        if (Freecam.mc.gameSettings.keyBindJump.isKeyDown()) {
            EntityPlayerSP player = Freecam.mc.player;
            player.motionY += this.speed.getValue().doubleValue();
        }
        if (Freecam.mc.gameSettings.keyBindSneak.isKeyDown()) {
            EntityPlayerSP player2 = Freecam.mc.player;
            player2.motionY -= this.speed.getValue().doubleValue();
        }
    }

    @Override
    public void onLogout() {
        if (this.isEnabled()) {
            this.disable();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (this.isDisabled()) {
            return;
        }
        if (this.legit.getValue().booleanValue() && this.entity != null && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packetPlayer = (CPacketPlayer)event.getPacket();
            packetPlayer.x = this.entity.posX;
            packetPlayer.y = this.entity.posY;
            packetPlayer.z = this.entity.posZ;
            return;
        }
        if (this.packet.getValue().booleanValue()) {
            if (event.getPacket() instanceof CPacketPlayer) {
                event.setCanceled(true);
            }
        } else if (!(event.getPacket() instanceof CPacketUseEntity || event.getPacket() instanceof CPacketPlayerTryUseItem || event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock || event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketVehicleMove || event.getPacket() instanceof CPacketChatMessage || event.getPacket() instanceof CPacketKeepAlive)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        SPacketSetPassengers packet;
        Entity riding;
        if (this.isDisabled()) {
            return;
        }
        if (event.getPacket() instanceof SPacketSetPassengers && (riding = Freecam.mc.world.getEntityByID((packet = (SPacketSetPassengers)event.getPacket()).getEntityId())) != null && riding == this.riding) {
            this.riding = null;
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet2 = (SPacketPlayerPosLook)event.getPacket();
            if (this.packet.getValue().booleanValue()) {
                if (this.entity != null) {
                    this.entity.setPositionAndRotation(packet2.getX(), packet2.getY(), packet2.getZ(), packet2.getYaw(), packet2.getPitch());
                }
                this.position = new Vec3d(packet2.getX(), packet2.getY(), packet2.getZ());
                Freecam.mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(packet2.getTeleportId()));
                event.setCanceled(true);
            } else {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPush(PushEvent event) {
        if (event.getStage() == 1) {
            event.setCanceled(true);
        }
    }
}

