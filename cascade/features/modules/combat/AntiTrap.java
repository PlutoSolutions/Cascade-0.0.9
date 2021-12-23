/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.combat;

import cascade.event.events.UpdateWalkingPlayerEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.BlockUtil;
import cascade.util.CombatUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiTrap
extends Module {
    public Setting<Boolean> sortY = this.register(new Setting<Boolean>("SortY", true));
    public static Set<BlockPos> placedPos = new HashSet<BlockPos>();
    private final Vec3d[] surroundTargets = new Vec3d[]{new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(-1.0, 0.0, 1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, -1.0), new Vec3d(-1.0, 1.0, 1.0)};

    public AntiTrap() {
        super("AntiTrap", Module.Category.COMBAT, "Prevents u from getting trapped");
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (!AntiTrap.fullNullCheck() && event.getStage() == 0 && this.isEnabled()) {
            this.doAntiTrap();
        }
    }

    public void doAntiTrap() {
        ArrayList<Vec3d> targets = new ArrayList<Vec3d>();
        Collections.addAll(targets, AntiTrap.convertVec3ds(AntiTrap.mc.player.getPositionVector(), this.surroundTargets));
        EntityPlayer closestPlayer = CombatUtil.getTarget(6.0f);
        if (closestPlayer != null) {
            EntityPlayer entityPlayer = closestPlayer;
            targets.sort((vec3d, vec3d2) -> Double.compare(entityPlayer.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), entityPlayer.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
            if (this.sortY.getValue().booleanValue()) {
                targets.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
            }
        }
        for (Vec3d vec3d3 : targets) {
            BlockPos pos = new BlockPos(vec3d3);
            if (!BlockUtil.canPlaceCrystal(pos)) continue;
            this.placeCrystal(pos);
            this.disable();
            break;
        }
    }

    private void placeCrystal(BlockPos pos) {
        RayTraceResult result = AntiTrap.mc.world.rayTraceBlocks(new Vec3d(AntiTrap.mc.player.posX, AntiTrap.mc.player.posY + (double)AntiTrap.mc.player.getEyeHeight(), AntiTrap.mc.player.posZ), new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() - 0.5, (double)pos.getZ() + 0.5));
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        placedPos.add(pos);
        AntiTrap.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, facing, EnumHand.OFF_HAND, 0.0f, 0.0f, 0.0f));
        AntiTrap.mc.player.swingArm(EnumHand.MAIN_HAND);
    }

    public static Vec3d[] convertVec3ds(Vec3d vec3d, Vec3d[] input) {
        Vec3d[] output = new Vec3d[input.length];
        for (int i = 0; i < input.length; ++i) {
            output[i] = vec3d.add(input[i]);
        }
        return output;
    }
}

