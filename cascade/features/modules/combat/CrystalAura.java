/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.util.EnumHand
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.combat;

import cascade.event.events.UpdateWalkingPlayerEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.CombatUtil;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CrystalAura
extends Module {
    public Setting<Page> page = this.register(new Setting<Page>("Page", Page.Place));
    public Setting<Boolean> place = this.register(new Setting<Object>("Place", Boolean.valueOf(true), v -> this.page.getValue() == Page.Place));
    public Setting<Float> generalRange = this.register(new Setting<Object>("PlaceRange", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(6.0f), v -> this.page.getValue() == Page.Place));
    public Setting<Boolean> breakBool = this.register(new Setting<Object>("Break", Boolean.valueOf(true), v -> this.page.getValue() == Page.Break));
    public Setting<Integer> breakDelay = this.register(new Setting<Object>("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(20), v -> this.page.getValue() == Page.Break));
    public Setting<Boolean> breakPredict = this.register(new Setting<Object>("Predict", Boolean.valueOf(true), v -> this.page.getValue() == Page.Break));
    public Setting<Boolean> packetBreak = this.register(new Setting<Object>("Packet", Boolean.valueOf(true), v -> this.page.getValue() == Page.Break));
    public Setting<Boolean> setDead = this.register(new Setting<Object>("SetDead", Boolean.valueOf(true), v -> this.page.getValue() == Page.Break));
    public Setting<Float> range = this.register(new Setting<Object>("Range", Float.valueOf(15.0f), Float.valueOf(0.1f), Float.valueOf(15.0f), v -> this.page.getValue() == Page.Calc));
    public Setting<Logic> logic = this.register(new Setting<Object>("Logic", (Object)Logic.BreakPlace, v -> this.page.getValue() == Page.Calc));
    public Setting<Float> wallRange = this.register(new Setting<Object>("WallRange", Float.valueOf(5.0f), Float.valueOf(0.1f), Float.valueOf(6.0f), v -> this.page.getValue() == Page.Calc));
    public Setting<Boolean> antiNaked = this.register(new Setting<Object>("AntiNaked", Boolean.valueOf(true), v -> this.page.getValue() == Page.Calc));
    public Setting<HUD> hud = this.register(new Setting<Object>("HUD", (Object)HUD.Target, v -> this.page.getValue() == Page.Visual));
    private CalculationThread calcThread;
    public static CrystalAura INSTANCE;
    EntityEnderCrystal crystal;
    EntityLivingBase target;
    int ticks;

    public CrystalAura() {
        super("CrystalAura", Module.Category.COMBAT, "autoblowjob");
        INSTANCE = this;
    }

    public static CrystalAura getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrystalAura();
        }
        return INSTANCE;
    }

    @Override
    public void onTick() {
        ++this.ticks;
    }

    @Override
    public void onEnable() {
        this.calcThread = new CalculationThread();
        this.calcThread.start();
    }

    @Override
    public void onToggle() {
        this.crystal = null;
        this.target = null;
        this.ticks = 0;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent e) {
        if (CrystalAura.fullNullCheck()) {
            return;
        }
        this.target = CombatUtil.getTarget(15.0f);
        if (this.target != null) {
            if (this.logic.getValue() == Logic.BreakPlace) {
                this.explode();
            } else {
                this.explode();
            }
        }
    }

    public void explode() {
        this.crystal = CrystalAura.mc.world.loadedEntityList.stream().filter(this::IsValidCrystal).map(p_Entity -> p_Entity).min(Comparator.comparing(p_Entity -> Float.valueOf(this.target.getDistance(p_Entity)))).orElse(null);
        if (this.crystal != null && this.breakBool.getValue().booleanValue() && (this.ticks >= this.breakDelay.getValue() || this.breakDelay.getValue() == 0)) {
            this.ticks = 0;
            CrystalAura.mc.player.swingArm(EnumHand.OFF_HAND);
            if (this.packetBreak.getValue().booleanValue()) {
                CrystalAura.mc.player.connection.sendPacket((Packet)new CPacketUseEntity((Entity)this.crystal));
            } else {
                CrystalAura.mc.playerController.attackEntity((EntityPlayer)CrystalAura.mc.player, (Entity)this.crystal);
            }
        }
    }

    private boolean IsValidCrystal(Entity e) {
        try {
            if (e == null) {
                return false;
            }
            if (!(e instanceof EntityEnderCrystal)) {
                return false;
            }
            if (this.target == null) {
                return false;
            }
            if (e.getDistance((Entity)CrystalAura.mc.player) > this.generalRange.getValue().floatValue()) {
                return false;
            }
            if (!CrystalAura.mc.player.canEntityBeSeen(e) && e.getDistance((Entity)CrystalAura.mc.player) > 5.0f) {
                return false;
            }
            if (e.isDead) {
                return false;
            }
            if (this.target.isDead || this.target.getHealth() + this.target.getAbsorptionAmount() <= 0.0f) {
                return false;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return true;
    }

    @Override
    public String getDisplayInfo() {
        switch (this.hud.getValue()) {
            case Target: {
                if (this.target == null) break;
                return (Object)this.target + "";
            }
            case Ticks: {
                if (!this.breakBool.getValue().booleanValue()) break;
                return this.ticks + "";
            }
        }
        return null;
    }

    public static class CalculationThread
    extends Thread {
        @Override
        public void run() {
            while (CrystalAura.getInstance().isEnabled()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(50L);
                }
                catch (Exception exception) {}
            }
        }
    }

    public static enum HUD {
        Target,
        Ticks;

    }

    public static enum Logic {
        BreakPlace,
        PlaceBreak;

    }

    public static enum Page {
        Place,
        Break,
        Calc,
        Visual;

    }
}

