/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.item.Item
 */
package cascade.features.modules.combat;

import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.CombatUtil;
import cascade.util.EntityUtil;
import cascade.util.InventoryUtil;
import cascade.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;

public class Offhand
extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Crystal));
    public Setting<Float> range = this.register(new Setting<Float>("PlayerRange", Float.valueOf(25.0f), Float.valueOf(0.1f), Float.valueOf(50.0f)));
    public Setting<Integer> delay = this.register(new Setting<Object>("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(200), v -> this.mode.getValue() != Mode.Totem));
    public Setting<Integer> minDistance = this.register(new Setting<Integer>("FallDistance", 86, 1, 120));
    public Setting<Boolean> gapSwap = this.register(new Setting<Boolean>("GapSwap", true));
    public Setting<Float> crystalHealth = this.register(new Setting<Object>("CrystalHealth", Float.valueOf(18.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.mode.getValue() == Mode.Crystal));
    public Setting<Float> crystalHoleHealth = this.register(new Setting<Object>("CrystalHoleHealth", Float.valueOf(8.5f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.mode.getValue() == Mode.Crystal));
    Timer timer = new Timer();

    public Offhand() {
        super("Offhand", Module.Category.COMBAT, "hoe");
    }

    @Override
    public void onUpdate() {
        if (Offhand.fullNullCheck() || Offhand.mc.currentScreen != null) {
            return;
        }
        int offhandSlot = InventoryUtil.getItemSlot(this.getOffhandItem());
        if (Offhand.mc.player.getHeldItemOffhand().getItem() != this.getOffhandItem() && offhandSlot != -1 && this.timer.passedMs(this.delay.getValue().intValue())) {
            this.switchItem(offhandSlot < 9 ? offhandSlot + 36 : offhandSlot);
            Offhand.mc.playerController.updateController();
            this.timer.reset();
        }
    }

    public void switchItem(int slot) {
        try {
            Offhand.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)Offhand.mc.player);
            Offhand.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)Offhand.mc.player);
            Offhand.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)Offhand.mc.player);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Item getOffhandItem() {
        switch (this.mode.getValue()) {
            case Crystal: {
                if (!(EntityUtil.isPlayerSafe((EntityPlayer)Offhand.mc.player) || !(EntityUtil.getHealth((Entity)Offhand.mc.player) > this.crystalHealth.getValue().floatValue()) || InventoryUtil.heldItem(Items.DIAMOND_SWORD, InventoryUtil.Hand.Main) && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown() && this.gapSwap.getValue().booleanValue())) {
                    return Items.END_CRYSTAL;
                }
                if (!(!EntityUtil.isPlayerSafe((EntityPlayer)Offhand.mc.player) || !(EntityUtil.getHealth((Entity)Offhand.mc.player) > this.crystalHoleHealth.getValue().floatValue()) || InventoryUtil.heldItem(Items.DIAMOND_SWORD, InventoryUtil.Hand.Main) && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown() && this.gapSwap.getValue().booleanValue())) {
                    return Items.END_CRYSTAL;
                }
                if (InventoryUtil.heldItem(Items.DIAMOND_SWORD, InventoryUtil.Hand.Main) && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown() && this.gapSwap.getValue().booleanValue()) {
                    return Items.GOLDEN_APPLE;
                }
                EntityPlayer target = CombatUtil.getTarget(200.0f);
                if (target != null && target.getDistance((Entity)Offhand.mc.player) > this.range.getValue().floatValue()) {
                    return Items.TOTEM_OF_UNDYING;
                }
                if (Offhand.mc.player.fallDistance > (float)this.minDistance.getValue().intValue()) {
                    return Items.TOTEM_OF_UNDYING;
                }
                return Items.TOTEM_OF_UNDYING;
            }
            case Totem: {
                if (InventoryUtil.getStackCount(Items.TOTEM_OF_UNDYING) == 0) break;
                return Items.TOTEM_OF_UNDYING;
            }
        }
        return Items.TOTEM_OF_UNDYING;
    }

    public static enum Mode {
        Crystal,
        Totem;

    }
}

