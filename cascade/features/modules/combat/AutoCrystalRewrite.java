/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.network.play.client.CPacketUseEntity$Action
 *  net.minecraft.network.play.server.SPacketExplosion
 *  net.minecraft.network.play.server.SPacketSoundEffect
 *  net.minecraft.network.play.server.SPacketSpawnObject
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.EventPriority
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.combat;

import cascade.event.events.PacketEvent;
import cascade.event.events.Render3DEvent;
import cascade.features.modules.Module;
import cascade.features.setting.ParentSetting;
import cascade.features.setting.Setting;
import cascade.util.BlockUtil;
import cascade.util.EntityUtil;
import cascade.util.MathUtil;
import cascade.util.RenderUtil;
import cascade.util.Timer;
import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoCrystalRewrite
extends Module {
    public ParentSetting targetParent = this.registerParent(new ParentSetting("Targets"));
    public Setting<Float> targetRange = this.register(new Setting<Float>("Target Range", Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(15.0f)).setParent(this.targetParent));
    public ParentSetting placingParent = this.registerParent(new ParentSetting("Placing"));
    public Setting<Float> placeRange = this.register(new Setting<Float>("Place Range", Float.valueOf(5.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)).setParent(this.placingParent));
    public Setting<Float> placeWallRange = this.register(new Setting<Float>("Place Wall Range", Float.valueOf(5.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)).setParent(this.placingParent));
    public Setting<Integer> placeDelay = this.register(new Setting<Integer>("Place Delay", 10, 0, 500).setParent(this.placingParent));
    public Setting<Float> placeMinimumDamage = this.register(new Setting<Float>("Place Minimum Damage", Float.valueOf(8.0f), Float.valueOf(0.0f), Float.valueOf(36.0f)).setParent(this.placingParent));
    public Setting<Float> placeMaximumSelfDamage = this.register(new Setting<Float>("Place Maximum Self Damage", Float.valueOf(8.0f), Float.valueOf(0.0f), Float.valueOf(36.0f)).setParent(this.placingParent));
    public Setting<Boolean> placeAntiSuicide = this.register(new Setting<Boolean>("Place Anti Suicide", false).setParent(this.placingParent));
    public Setting<Boolean> placePacket = this.register(new Setting<Boolean>("Place Packet", false).setParent(this.placingParent));
    public Setting<Boolean> placeSwing = this.register(new Setting<Boolean>("Place Swing", false).setParent(this.placingParent));
    public Setting<EntityUtil.SwingType> placeSwingHand = this.register(new Setting<EntityUtil.SwingType>("Place Swing Offhand", EntityUtil.SwingType.MainHand, v -> this.placeSwing.getValue()).setParent(this.placingParent));
    public ParentSetting explodingParent = this.registerParent(new ParentSetting("Exploding"));
    public Setting<Float> explodeRange = this.register(new Setting<Float>("Explode Range", Float.valueOf(5.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)).setParent(this.explodingParent));
    public Setting<Float> explodeWallRange = this.register(new Setting<Float>("Explode Wall Range", Float.valueOf(5.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)).setParent(this.explodingParent));
    public Setting<Integer> breakDelay = this.register(new Setting<Integer>("Break Delay", 60, 0, 500).setParent(this.explodingParent));
    public Setting<Float> explodeMinimumDamage = this.register(new Setting<Float>("Explode Minimum Damage", Float.valueOf(8.0f), Float.valueOf(0.0f), Float.valueOf(36.0f)).setParent(this.explodingParent));
    public Setting<Float> explodeMaximumSelfDamage = this.register(new Setting<Float>("Explode Maximum Self Damage", Float.valueOf(8.0f), Float.valueOf(0.0f), Float.valueOf(36.0f)).setParent(this.explodingParent));
    public Setting<Boolean> explodeAntiSuicide = this.register(new Setting<Boolean>("Explode Anti Suicide", false).setParent(this.explodingParent));
    public Setting<Boolean> explodePacket = this.register(new Setting<Boolean>("Explode Packet", false).setParent(this.explodingParent));
    public Setting<Boolean> explodeSwing = this.register(new Setting<Boolean>("Explode Swing", false).setParent(this.explodingParent));
    public Setting<EntityUtil.SwingType> explodeSwingHand = this.register(new Setting<EntityUtil.SwingType>("Explode Swing Offhand", EntityUtil.SwingType.MainHand, v -> this.explodeSwing.getValue()).setParent(this.explodingParent));
    public ParentSetting facePlacingParent = this.registerParent(new ParentSetting("FacePlacing"));
    public Setting<Float> facePlaceHp = this.register(new Setting<Float>("Face Place HP", Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(36.0f)).setParent(this.facePlacingParent));
    public ParentSetting predictingParent = this.registerParent(new ParentSetting("Predicting"));
    public Setting<Boolean> predict = this.register(new Setting<Boolean>("Predict", false).setParent(this.predictingParent));
    public Setting<Integer> predictDelay = this.register(new Setting<Integer>("Predict Delay", Integer.valueOf(60), Integer.valueOf(0), Integer.valueOf(500), v -> this.predict.getValue()).setParent(this.predictingParent));
    public Setting<Boolean> predictSetDead = this.register(new Setting<Boolean>("Predict Set Dead", Boolean.valueOf(false), v -> this.predict.getValue()).setParent(this.predictingParent));
    public ParentSetting rendering = this.registerParent(new ParentSetting("Rendering"));
    public Setting<RenderType> renderType = this.register(new Setting<RenderType>("Render Type", RenderType.Place).setParent(this.rendering));
    public Setting<Boolean> placeBox = this.register(new Setting<Boolean>("Place Box", Boolean.valueOf(false), v -> this.renderType.getValue().equals((Object)RenderType.Place) || this.renderType.getValue().equals((Object)RenderType.Both)).setParent(this.rendering));
    public Setting<Color> placeBoxColor = this.register(new Setting<Color>("Place Box Color", new Color(0xFFFFFF), v -> this.renderType.getValue().equals((Object)RenderType.Place) || this.renderType.getValue().equals((Object)RenderType.Both) && this.placeBox.getValue() != false).setParent(this.rendering));
    public Setting<Boolean> placeOutline = this.register(new Setting<Boolean>("Place Outline", Boolean.valueOf(false), v -> this.renderType.getValue().equals((Object)RenderType.Place) || this.renderType.getValue().equals((Object)RenderType.Both)).setParent(this.rendering));
    public Setting<Color> placeOutlineColor = this.register(new Setting<Color>("Place Outline Color", new Color(0xFFFFFF), v -> this.renderType.getValue().equals((Object)RenderType.Place) || this.renderType.getValue().equals((Object)RenderType.Both) && this.placeOutline.getValue() != false).setParent(this.rendering));
    public Setting<Float> placeLineWidth = this.register(new Setting<Float>("Place Line Width", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(5.0f), v -> this.placeOutline.getValue()).setParent(this.rendering));
    public Setting<Boolean> placeText = this.register(new Setting<Boolean>("Place Text", Boolean.valueOf(false), v -> this.renderType.getValue().equals((Object)RenderType.Place) || this.renderType.getValue().equals((Object)RenderType.Both)).setParent(this.rendering));
    public Setting<Boolean> explodeBox = this.register(new Setting<Boolean>("Explode Box", Boolean.valueOf(false), v -> this.renderType.getValue().equals((Object)RenderType.Explode) || this.renderType.getValue().equals((Object)RenderType.Both)).setParent(this.rendering));
    public Setting<Color> explodeBoxColor = this.register(new Setting<Color>("Explode Box Color", new Color(0xFFFFFF), v -> this.renderType.getValue().equals((Object)RenderType.Explode) || this.renderType.getValue().equals((Object)RenderType.Both) && this.explodeBox.getValue() != false).setParent(this.rendering));
    public Setting<Boolean> explodeOutline = this.register(new Setting<Boolean>("Explode Outline", Boolean.valueOf(false), v -> this.renderType.getValue().equals((Object)RenderType.Explode) || this.renderType.getValue().equals((Object)RenderType.Both)).setParent(this.rendering));
    public Setting<Color> explodeOutlineColor = this.register(new Setting<Color>("Explode Outline Color", new Color(0xFFFFFF), v -> this.renderType.getValue().equals((Object)RenderType.Explode) || this.renderType.getValue().equals((Object)RenderType.Both) && this.explodeOutline.getValue() != false).setParent(this.rendering));
    public Setting<Float> explodeLineWidth = this.register(new Setting<Float>("Explode Line Width", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(5.0f), v -> this.explodeOutline.getValue()).setParent(this.rendering));
    public Setting<Boolean> explodeText = this.register(new Setting<Boolean>("Explode Text", Boolean.valueOf(false), v -> this.renderType.getValue().equals((Object)RenderType.Explode) || this.renderType.getValue().equals((Object)RenderType.Both)).setParent(this.rendering));
    public PlacePosition placePosition = new PlacePosition(null, 0.0f);
    public ExplodePosition explodePosition = new ExplodePosition(null, 0.0f);
    public EntityPlayer target;
    public Timer placeTimer = new Timer();
    public Timer explodeTimer = new Timer();
    public Timer predictTimer = new Timer();
    public static AutoCrystalRewrite INSTANCE = new AutoCrystalRewrite();
    public CurrentThread currentThread;

    public AutoCrystalRewrite() {
        super("AutoCrystalRewrite", Module.Category.COMBAT, "rewrite by le god zprestige_");
        this.setInstance();
    }

    public static AutoCrystalRewrite getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutoCrystalRewrite();
        }
        return INSTANCE;
    }

    void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        this.setup();
        if (this.target == null) {
            return;
        }
        if (this.placePosition != null && this.placeTimer.passedMs(this.placeDelay.getValue().intValue())) {
            this.placeCrystal();
        }
        if (this.explodePosition != null && this.explodeTimer.passedMs(this.breakDelay.getValue().intValue())) {
            this.explodeCrystal();
        }
    }

    public void setup() {
        if (AutoCrystalRewrite.fullNullCheck()) {
            return;
        }
        this.target = EntityUtil.getTarget(this.targetRange.getValue().floatValue());
        if (this.target == null) {
            return;
        }
        this.placePosition = this.searchPosition();
        this.explodePosition = this.searchCrystal();
    }

    public void explodeCrystal() {
        if (this.explodePacket.getValue().booleanValue()) {
            Objects.requireNonNull(mc.getConnection()).sendPacket((Packet)new CPacketUseEntity(this.explodePosition.getEntity()));
        } else {
            AutoCrystalRewrite.mc.playerController.attackEntity((EntityPlayer)AutoCrystalRewrite.mc.player, this.explodePosition.getEntity());
        }
        if (this.explodeSwing.getValue().booleanValue()) {
            EntityUtil.swingArm(this.explodeSwingHand.getValue());
        }
        this.explodeTimer.reset();
        this.currentThread = CurrentThread.Exploding;
    }

    public void placeCrystal() {
        if (this.placePacket.getValue().booleanValue()) {
            Objects.requireNonNull(mc.getConnection()).sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(this.placePosition.getBlockPos(), EnumFacing.UP, AutoCrystalRewrite.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
        } else {
            AutoCrystalRewrite.mc.playerController.processRightClickBlock(AutoCrystalRewrite.mc.player, AutoCrystalRewrite.mc.world, this.placePosition.getBlockPos(), EnumFacing.UP, new Vec3d(AutoCrystalRewrite.mc.player.posX, -AutoCrystalRewrite.mc.player.posY, -AutoCrystalRewrite.mc.player.posZ), AutoCrystalRewrite.mc.player.getHeldItemOffhand().getItem().equals((Object)Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        }
        if (this.placeSwing.getValue().booleanValue()) {
            EntityUtil.swingArm(this.placeSwingHand.getValue());
        }
        this.placeTimer.reset();
        this.currentThread = CurrentThread.Placing;
    }

    public PlacePosition searchPosition() {
        TreeMap<Float, PlacePosition> posList = new TreeMap<Float, PlacePosition>();
        for (BlockPos pos : BlockUtil.getSphereAutoCrystal(this.placeRange.getValue().floatValue(), true)) {
            if (!BlockUtil.isPosValidForCrystal(pos, false)) continue;
            float targetDamage = EntityUtil.calculatePosDamage(pos, this.target);
            float selfDamage = EntityUtil.calculatePosDamage(pos, (EntityPlayer)AutoCrystalRewrite.mc.player);
            float selfHealth = AutoCrystalRewrite.mc.player.getHealth() + AutoCrystalRewrite.mc.player.getAbsorptionAmount();
            float targetHealth = this.target.getHealth() + this.target.getAbsorptionAmount();
            float minimumDamageValue = this.placeMinimumDamage.getValue().floatValue();
            if (AutoCrystalRewrite.mc.player.getDistanceSq((double)((float)pos.getX() + 0.5f), (double)pos.getY(), (double)((float)pos.getZ() + 0.5f)) > MathUtil.square(this.placeRange.getValue().floatValue()) || BlockUtil.rayTraceCheckPos(new BlockPos(pos.getX(), pos.getY(), pos.getZ())) && AutoCrystalRewrite.mc.player.getDistance((double)((float)pos.getX() + 0.5f), (double)(pos.getY() + 1), (double)((float)pos.getZ() + 0.5f)) > (double)this.placeWallRange.getValue().floatValue()) continue;
            if (BlockUtil.isPlayerSafe(this.target) && targetHealth < this.facePlaceHp.getValue().floatValue()) {
                minimumDamageValue = 2.0f;
            }
            if (targetDamage < minimumDamageValue || selfDamage > this.placeMaximumSelfDamage.getValue().floatValue() || this.placeAntiSuicide.getValue().booleanValue() && selfDamage > selfHealth) continue;
            posList.put(Float.valueOf(targetDamage), new PlacePosition(pos, targetDamage));
            this.currentThread = CurrentThread.Calculating;
        }
        if (!posList.isEmpty()) {
            return (PlacePosition)posList.lastEntry().getValue();
        }
        return null;
    }

    public ExplodePosition searchCrystal() {
        TreeMap<Float, ExplodePosition> crystalList = new TreeMap<Float, ExplodePosition>();
        for (Entity entity : AutoCrystalRewrite.mc.world.loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal)) continue;
            float selfHealth = AutoCrystalRewrite.mc.player.getHealth() + AutoCrystalRewrite.mc.player.getAbsorptionAmount();
            float selfDamage = EntityUtil.calculateEntityDamage((EntityEnderCrystal)entity, (EntityPlayer)AutoCrystalRewrite.mc.player);
            float targetDamage = EntityUtil.calculateEntityDamage((EntityEnderCrystal)entity, this.target);
            float targetHealth = this.target.getHealth() + this.target.getAbsorptionAmount();
            float minimumDamageValue = this.explodeMinimumDamage.getValue().floatValue();
            if (entity.getDistanceSq(EntityUtil.getPlayerPos((EntityPlayer)AutoCrystalRewrite.mc.player)) > MathUtil.square(this.explodeRange.getValue().floatValue()) || BlockUtil.rayTraceCheckPos(new BlockPos(Math.floor(entity.posX), Math.floor(entity.posY), Math.floor(entity.posZ))) && AutoCrystalRewrite.mc.player.getDistanceSq(new BlockPos(Math.floor(entity.posX), Math.floor(entity.posY), Math.floor(entity.posZ))) > (double)this.explodeWallRange.getValue().floatValue()) continue;
            if (BlockUtil.isPlayerSafe(this.target) && targetHealth < this.facePlaceHp.getValue().floatValue()) {
                minimumDamageValue = 2.0f;
            }
            if (targetDamage < minimumDamageValue || selfDamage > this.explodeMaximumSelfDamage.getValue().floatValue() || this.explodeAntiSuicide.getValue().booleanValue() && selfDamage > selfHealth) continue;
            crystalList.put(Float.valueOf(targetDamage), new ExplodePosition(entity, targetDamage));
            this.currentThread = CurrentThread.Calculating;
        }
        if (!crystalList.isEmpty()) {
            return (ExplodePosition)crystalList.lastEntry().getValue();
        }
        return null;
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onPacketReceive(PacketEvent.Receive event) {
        SPacketSoundEffect packet;
        if (this.megaNullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketSpawnObject && ((SPacketSpawnObject)event.getPacket()).getType() == 51 && this.predict.getValue().booleanValue() && this.target != null) {
            CPacketUseEntity predict = new CPacketUseEntity();
            predict.entityId = ((SPacketSpawnObject)event.getPacket()).getEntityID();
            predict.action = CPacketUseEntity.Action.ATTACK;
            AutoCrystalRewrite.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
            AutoCrystalRewrite.mc.player.connection.sendPacket((Packet)predict);
        }
        if (event.getPacket() instanceof SPacketSoundEffect && this.predict.getValue().booleanValue() && this.predictSetDead.getValue().booleanValue()) {
            packet = (SPacketSoundEffect)event.getPacket();
            try {
                if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    List loadedEntityList = AutoCrystalRewrite.mc.world.loadedEntityList;
                    loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal && entity.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) < MathUtil.square(this.explodeRange.getValue().floatValue())).forEach(entity -> {
                        Objects.requireNonNull(AutoCrystalRewrite.mc.world.getEntityByID(entity.getEntityId())).setDead();
                        AutoCrystalRewrite.mc.world.removeEntityFromWorld(entity.entityId);
                    });
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (event.getPacket() instanceof SPacketExplosion && this.predict.getValue().booleanValue() && this.predictSetDead.getValue().booleanValue()) {
            try {
                packet = (SPacketExplosion)event.getPacket();
                AutoCrystalRewrite.mc.world.loadedEntityList.stream().filter(arg_0 -> this.lambda$onPacketReceive$18((SPacketExplosion)packet, arg_0)).forEach(entity -> {
                    Objects.requireNonNull(AutoCrystalRewrite.mc.world.getEntityByID(entity.getEntityId())).setDead();
                    AutoCrystalRewrite.mc.world.removeEntityFromWorld(entity.entityId);
                });
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onPacketSend(PacketEvent.Send event) {
        CPacketUseEntity packet;
        if (this.megaNullCheck()) {
            return;
        }
        if (event.getPacket() instanceof CPacketUseEntity && this.predict.getValue().booleanValue() && this.predictTimer.passedMs(this.predictDelay.getValue().intValue()) && (packet = (CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld((World)AutoCrystalRewrite.mc.world) instanceof EntityEnderCrystal) {
            if (this.predictSetDead.getValue().booleanValue()) {
                Objects.requireNonNull(packet.getEntityFromWorld((World)AutoCrystalRewrite.mc.world)).setDead();
                AutoCrystalRewrite.mc.world.removeEntityFromWorld(packet.entityId);
            }
            if (this.placePosition != null) {
                this.placeCrystal();
                this.currentThread = CurrentThread.Placing;
            }
            this.predictTimer.reset();
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        double damage;
        if (this.target == null) {
            return;
        }
        if (this.placePosition != null && (this.renderType.getValue().equals((Object)RenderType.Place) || this.renderType.getValue().equals((Object)RenderType.Both))) {
            RenderUtil.drawBoxESP(this.placePosition.getBlockPos(), this.placeBoxColor.getValue(), true, this.placeOutlineColor.getValue(), this.placeLineWidth.getValue().floatValue(), this.placeOutline.getValue(), this.placeBox.getValue(), this.placeBoxColor.getValue().getAlpha(), true);
            damage = EntityUtil.calculatePosDamage((double)this.placePosition.getBlockPos().getX() + 0.5, (double)this.placePosition.getBlockPos().getY() + 1.0, (double)this.placePosition.getBlockPos().getZ() + 0.5, (Entity)this.target);
            if (this.placeText.getValue().booleanValue()) {
                RenderUtil.drawText(this.placePosition.getBlockPos(), (Math.floor(damage) == damage ? Integer.valueOf((int)damage) : String.format("%.1f", damage)) + "");
            }
        }
        if (this.explodePosition != null && (this.renderType.getValue().equals((Object)RenderType.Explode) || this.renderType.getValue().equals((Object)RenderType.Both))) {
            RenderUtil.drawBoxESP(this.explodePosition.getEntity().getPosition(), this.explodeBoxColor.getValue(), true, this.explodeOutlineColor.getValue(), this.explodeLineWidth.getValue().floatValue(), this.explodeOutline.getValue(), this.explodeBox.getValue(), this.explodeBoxColor.getValue().getAlpha(), true);
            damage = EntityUtil.calculatePosDamage(Math.floor(this.explodePosition.getEntity().getPosition().getX()), Math.floor(this.explodePosition.getEntity().getPosition().getY()), Math.floor(this.explodePosition.getEntity().getPosition().getZ()), (Entity)this.target);
            if (this.explodeText.getValue().booleanValue()) {
                RenderUtil.drawText(this.explodePosition.getEntity().getPosition(), (Math.floor(damage) == damage ? Integer.valueOf((int)damage) : String.format("%.1f", damage)) + "");
            }
        }
    }

    @Override
    public String getDisplayInfo() {
        if (this.currentThread == null) {
            return "";
        }
        return this.currentThread.toString();
    }

    private /* synthetic */ boolean lambda$onPacketReceive$18(SPacketExplosion packet, Entity entity) {
        return entity instanceof EntityEnderCrystal && entity.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) < MathUtil.square(this.explodeRange.getValue().floatValue());
    }

    public static class PlacePosition {
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

    public static class ExplodePosition {
        Entity entity;
        float targetDamage;

        public ExplodePosition(Entity entity, float targetDamage) {
            this.entity = entity;
            this.targetDamage = targetDamage;
        }

        public Entity getEntity() {
            return this.entity;
        }
    }

    public static enum CurrentThread {
        Placing,
        Exploding,
        Calculating;

    }

    public static enum RenderType {
        Place,
        Explode,
        Both;

    }
}

