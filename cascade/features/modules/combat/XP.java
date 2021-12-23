/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Items
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.combat;

import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.CombatUtil;
import cascade.util.EntityUtil;
import cascade.util.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class XP
extends Module {
    public Setting<Boolean> safeOnly = this.register(new Setting<Boolean>("SafeOnly", true));
    public Setting<Float> enemyRange = this.register(new Setting<Float>("EnemyRange", Float.valueOf(2.0f), Float.valueOf(0.1f), Float.valueOf(8.0f)));

    public XP() {
        super("XPDown", Module.Category.COMBAT, "Looks down when ur using xp");
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (e.getPacket() instanceof CPacketPlayer && this.isEnabled() && !XP.fullNullCheck()) {
            if (this.safeOnly.getValue().booleanValue() && !EntityUtil.isSafe((Entity)XP.mc.player)) {
                return;
            }
            if (!InventoryUtil.heldItem(Items.EXPERIENCE_BOTTLE, InventoryUtil.Hand.Both)) {
                return;
            }
            if (!XP.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                return;
            }
            try {
                if (XP.mc.player.getDistance((Entity)CombatUtil.getTarget(12.0f)) > this.enemyRange.getValue().floatValue()) {
                    return;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            ((CPacketPlayer)e.getPacket()).pitch = 90.0f;
        }
    }
}

