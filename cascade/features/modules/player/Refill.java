/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package cascade.features.modules.player;

import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.Timer;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Refill
extends Module {
    private final Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 0, 0, 10));
    private final Setting<Integer> gapStack = this.register(new Setting<Integer>("GapStack", 50, 1, 64));
    private final Setting<Integer> xpStackAt = this.register(new Setting<Integer>("XPStack", 50, 1, 64));
    private final Timer timer = new Timer();
    private final ArrayList<Item> Hotbar = new ArrayList();

    public Refill() {
        super("Refill", Module.Category.PLAYER, "automatically refills ur hotbar");
    }

    @Override
    public void onEnable() {
        if (Refill.fullNullCheck()) {
            return;
        }
        this.Hotbar.clear();
        for (int l_I = 0; l_I < 9; ++l_I) {
            ItemStack l_Stack = Refill.mc.player.inventory.getStackInSlot(l_I);
            if (!l_Stack.isEmpty() && !this.Hotbar.contains((Object)l_Stack.getItem())) {
                this.Hotbar.add(l_Stack.getItem());
                continue;
            }
            this.Hotbar.add(Items.AIR);
        }
    }

    @Override
    public void onUpdate() {
        if (Refill.mc.currentScreen != null) {
            return;
        }
        if (!this.timer.passedMs(this.delay.getValue() * 1000)) {
            return;
        }
        for (int l_I = 0; l_I < 9; ++l_I) {
            if (!this.RefillSlotIfNeed(l_I)) continue;
            this.timer.reset();
            return;
        }
    }

    private boolean RefillSlotIfNeed(int p_Slot) {
        ItemStack l_Stack = Refill.mc.player.inventory.getStackInSlot(p_Slot);
        if (l_Stack.isEmpty() || l_Stack.getItem() == Items.AIR) {
            return false;
        }
        if (!l_Stack.isStackable()) {
            return false;
        }
        if (l_Stack.getCount() >= l_Stack.getMaxStackSize()) {
            return false;
        }
        if (l_Stack.getItem().equals((Object)Items.GOLDEN_APPLE) && l_Stack.getCount() >= this.gapStack.getValue()) {
            return false;
        }
        if (l_Stack.getItem().equals((Object)Items.EXPERIENCE_BOTTLE) && l_Stack.getCount() > this.xpStackAt.getValue()) {
            return false;
        }
        for (int l_I = 9; l_I < 36; ++l_I) {
            ItemStack l_Item = Refill.mc.player.inventory.getStackInSlot(l_I);
            if (l_Item.isEmpty() || !this.CanItemBeMergedWith(l_Stack, l_Item)) continue;
            Refill.mc.playerController.windowClick(Refill.mc.player.inventoryContainer.windowId, l_I, 0, ClickType.QUICK_MOVE, (EntityPlayer)Refill.mc.player);
            Refill.mc.playerController.updateController();
            return true;
        }
        return false;
    }

    private boolean CanItemBeMergedWith(ItemStack p_Source, ItemStack p_Target) {
        return p_Source.getItem() == p_Target.getItem() && p_Source.getDisplayName().equals(p_Target.getDisplayName());
    }
}

