/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package cascade.features.modules.combat;

import cascade.features.modules.Module;
import cascade.features.modules.combat.AutoCrystalRewrite;
import cascade.features.setting.Setting;
import cascade.util.BlockUtil;
import cascade.util.EntityUtil;
import cascade.util.MathUtil;
import cascade.util.Timer;
import java.util.ArrayList;
import java.util.TreeMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AK47
extends Module {
    public Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 50, 0, 500));
    public Setting<Integer> delay2 = this.register(new Setting<Integer>("Delay2", 50, 0, 500));
    AutoCrystalRewrite autoCrystalRewrite = AutoCrystalRewrite.getInstance();
    public PlacePosition placePosition = new PlacePosition(null, 0.0f);
    public EntityPlayer target;
    public ArrayList<Integer> entityIdList = new ArrayList();
    public Timer timer = new Timer();
    int id;
    boolean hasDoneDelay = true;

    public AK47() {
        super("AK47", Module.Category.COMBAT, "lel ghosty ghost ghosty");
    }

    @Override
    public void onUpdate() {
        this.setup();
        if (this.target == null) {
            return;
        }
        if (this.placePosition != null) {
            if (this.timer.passedMs(this.delay.getValue().intValue()) && this.hasDoneDelay) {
                this.placeCrystal();
                this.hasDoneDelay = false;
            }
            if (this.timer.passedMs(this.delay.getValue() + 50 + this.delay2.getValue())) {
                this.explodeCrystal();
                this.timer.reset();
                this.hasDoneDelay = true;
            }
        }
    }

    public void setup() {
        if (AK47.fullNullCheck()) {
            return;
        }
        this.target = EntityUtil.getTarget(this.autoCrystalRewrite.targetRange.getValue().floatValue());
        if (this.target == null) {
            return;
        }
        this.placePosition = this.searchPosition();
    }

    public void explodeCrystal() {
        for (int j = 0; j < this.entityIdList.size(); ++j) {
            Integer i = this.entityIdList.get(j);
            AK47.mc.world.removeEntityFromWorld(i.intValue());
            this.entityIdList.remove(i);
        }
    }

    public void placeCrystal() {
        --this.id;
        EntityEnderCrystal crystal = new EntityEnderCrystal((World)AK47.mc.world, (double)this.placePosition.getBlockPos().getX() + 0.5, (double)this.placePosition.getBlockPos().getY() + 1.0, (double)this.placePosition.getBlockPos().getZ() + 0.5);
        AK47.mc.world.addEntityToWorld(this.id, (Entity)crystal);
        this.entityIdList.add(this.id);
    }

    public PlacePosition searchPosition() {
        TreeMap<Float, PlacePosition> posList = new TreeMap<Float, PlacePosition>();
        for (BlockPos pos : BlockUtil.getSphereAutoCrystal(this.autoCrystalRewrite.placeRange.getValue().floatValue(), true)) {
            if (!BlockUtil.isPosValidForCrystal(pos, false)) continue;
            float targetDamage = EntityUtil.calculatePosDamage(pos, this.target);
            float selfDamage = EntityUtil.calculatePosDamage(pos, (EntityPlayer)AK47.mc.player);
            float selfHealth = AK47.mc.player.getHealth() + AK47.mc.player.getAbsorptionAmount();
            float targetHealth = this.target.getHealth() + this.target.getAbsorptionAmount();
            float minimumDamageValue = this.autoCrystalRewrite.placeMinimumDamage.getValue().floatValue();
            if (AK47.mc.player.getDistanceSq((double)((float)pos.getX() + 0.5f), (double)pos.getY(), (double)((float)pos.getZ() + 0.5f)) > MathUtil.square(this.autoCrystalRewrite.placeRange.getValue().floatValue()) || BlockUtil.rayTraceCheckPos(new BlockPos(pos.getX(), pos.getY(), pos.getZ())) && AK47.mc.player.getDistance((double)((float)pos.getX() + 0.5f), (double)(pos.getY() + 1), (double)((float)pos.getZ() + 0.5f)) > (double)this.autoCrystalRewrite.placeWallRange.getValue().floatValue()) continue;
            if (BlockUtil.isPlayerSafe(this.target) && targetHealth < this.autoCrystalRewrite.facePlaceHp.getValue().floatValue()) {
                minimumDamageValue = 2.0f;
            }
            if (targetDamage < minimumDamageValue || selfDamage > this.autoCrystalRewrite.placeMaximumSelfDamage.getValue().floatValue() || this.autoCrystalRewrite.placeAntiSuicide.getValue().booleanValue() && selfDamage > selfHealth) continue;
            posList.put(Float.valueOf(targetDamage), new PlacePosition(pos, targetDamage));
        }
        if (!posList.isEmpty()) {
            return (PlacePosition)posList.lastEntry().getValue();
        }
        return null;
    }

    static class PlacePosition {
        BlockPos blockPos;
        float targetDamage;

        public PlacePosition(BlockPos blockPos, float targetDamage) {
            this.blockPos = blockPos;
            this.targetDamage = targetDamage;
        }

        public BlockPos getBlockPos() {
            return this.blockPos;
        }
    }
}

