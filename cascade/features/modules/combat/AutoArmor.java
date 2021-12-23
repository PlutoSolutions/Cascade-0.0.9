/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.gui.inventory.GuiInventory
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemExpBottle
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.InputEvent$KeyInputEvent
 *  org.lwjgl.input.Keyboard
 */
package cascade.features.modules.combat;

import cascade.Mod;
import cascade.features.gui.CascadeGui;
import cascade.features.modules.Module;
import cascade.features.modules.player.XCarry;
import cascade.features.setting.Bind;
import cascade.features.setting.Setting;
import cascade.util.CombatUtil;
import cascade.util.EntityUtil;
import cascade.util.InventoryUtil;
import cascade.util.MathUtil;
import cascade.util.Timer;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class AutoArmor
extends Module {
    private final Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 50, 0, 500));
    private final Setting<Boolean> mendingTakeOff = this.register(new Setting<Boolean>("AutoMend", false));
    private final Setting<Integer> closestEnemy = this.register(new Setting<Object>("Enemy", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(20), v -> this.mendingTakeOff.getValue()));
    private Setting<Integer> mendTo;
    private Setting<Integer> minPercentage = this.register(new Setting<Object>("Min%", Integer.valueOf(80), Integer.valueOf(1), Integer.valueOf(100), v -> this.mendingTakeOff.getValue()));
    private final Setting<Boolean> curse;
    private final Setting<Integer> actions;
    private final Setting<Bind> elytraBind;
    private final Setting<Boolean> tps;
    private final Setting<Boolean> updateController;
    private final Setting<Boolean> shiftClick;
    private final Timer timer;
    private final Timer elytraTimer;
    private final Queue<InventoryUtil.Task> taskList;
    private final List<Integer> doneSlots;
    private boolean elytraOn = false;
    EntityPlayer closest;

    public AutoArmor() {
        super("AutoArmor", Module.Category.COMBAT, "Puts armor on u");
        this.mendTo = this.register(new Setting<Object>("MendTo%", Integer.valueOf(98), Integer.valueOf(1), Integer.valueOf(100), v -> this.mendingTakeOff.getValue()));
        this.curse = this.register(new Setting<Boolean>("CurseOfBinding", false));
        this.actions = this.register(new Setting<Integer>("Actions", 3, 1, 12));
        this.elytraBind = this.register(new Setting<Bind>("Elytra", new Bind(-1)));
        this.tps = this.register(new Setting<Boolean>("TpsSync", true));
        this.updateController = this.register(new Setting<Boolean>("Update", true));
        this.shiftClick = this.register(new Setting<Boolean>("ShiftClick", false));
        this.timer = new Timer();
        this.elytraTimer = new Timer();
        this.taskList = new ConcurrentLinkedQueue<InventoryUtil.Task>();
        this.doneSlots = new ArrayList<Integer>();
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState() && !(AutoArmor.mc.currentScreen instanceof CascadeGui) && this.elytraBind.getValue().getKey() == Keyboard.getEventKey()) {
            this.elytraOn = !this.elytraOn;
        }
    }

    @Override
    public void onLogin() {
        this.timer.reset();
        this.elytraTimer.reset();
    }

    @Override
    public void onDisable() {
        this.taskList.clear();
        this.doneSlots.clear();
        this.elytraOn = false;
    }

    @Override
    public void onLogout() {
        this.taskList.clear();
        this.doneSlots.clear();
    }

    @Override
    public void onTick() {
        if (AutoArmor.fullNullCheck() || AutoArmor.mc.currentScreen instanceof GuiContainer && !(AutoArmor.mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        if (this.taskList.isEmpty()) {
            int slot4;
            ItemStack feet;
            int slot3;
            ItemStack legging;
            int slot2;
            ItemStack chest;
            int slot;
            if (this.mendingTakeOff.getValue().booleanValue() && InventoryUtil.holdingItem(ItemExpBottle.class) && AutoArmor.mc.gameSettings.keyBindUseItem.isKeyDown() && (this.isSafe() || EntityUtil.isSafe((Entity)AutoArmor.mc.player, 1.0, false))) {
                int bootDamage;
                int leggingsDamage;
                int chestDamage;
                int helmetDamage;
                ItemStack helmet = AutoArmor.mc.player.inventoryContainer.getSlot(5).getStack();
                if (!helmet.isEmpty && (helmetDamage = CombatUtil.getRoundedDamage(helmet)) >= this.mendTo.getValue() && helmetDamage <= this.minPercentage.getValue()) {
                    this.takeOffSlot(5);
                }
                ItemStack chest2 = AutoArmor.mc.player.inventoryContainer.getSlot(6).getStack();
                if (!chest2.isEmpty && (chestDamage = CombatUtil.getRoundedDamage(chest2)) >= this.mendTo.getValue() && chestDamage <= this.minPercentage.getValue()) {
                    this.takeOffSlot(6);
                }
                ItemStack leggings = AutoArmor.mc.player.inventoryContainer.getSlot(7).getStack();
                if (!leggings.isEmpty && (leggingsDamage = CombatUtil.getRoundedDamage(leggings)) >= this.mendTo.getValue() && leggingsDamage <= this.minPercentage.getValue()) {
                    this.takeOffSlot(7);
                }
                ItemStack feet2 = AutoArmor.mc.player.inventoryContainer.getSlot(8).getStack();
                if (!feet2.isEmpty && (bootDamage = CombatUtil.getRoundedDamage(feet2)) >= this.mendTo.getValue() && bootDamage <= this.minPercentage.getValue()) {
                    this.takeOffSlot(8);
                }
                return;
            }
            ItemStack helm = AutoArmor.mc.player.inventoryContainer.getSlot(5).getStack();
            if (helm.getItem() == Items.AIR && (slot = InventoryUtil.findArmorSlot(EntityEquipmentSlot.HEAD, this.curse.getValue(), XCarry.getInstance().isEnabled())) != -1) {
                this.getSlotOn(5, slot);
            }
            if ((chest = AutoArmor.mc.player.inventoryContainer.getSlot(6).getStack()).getItem() == Items.AIR) {
                if (this.taskList.isEmpty()) {
                    if (this.elytraOn && this.elytraTimer.passedMs(500L)) {
                        int elytraSlot = InventoryUtil.findItemInventorySlot(Items.ELYTRA, false, XCarry.getInstance().isEnabled());
                        if (elytraSlot != -1) {
                            if (elytraSlot < 5 && elytraSlot > 1 || !this.shiftClick.getValue().booleanValue()) {
                                this.taskList.add(new InventoryUtil.Task(elytraSlot));
                                this.taskList.add(new InventoryUtil.Task(6));
                            } else {
                                this.taskList.add(new InventoryUtil.Task(elytraSlot, true));
                            }
                            if (this.updateController.getValue().booleanValue()) {
                                this.taskList.add(new InventoryUtil.Task());
                            }
                            this.elytraTimer.reset();
                        }
                    } else if (!this.elytraOn && (slot2 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.CHEST, this.curse.getValue(), XCarry.getInstance().isEnabled())) != -1) {
                        this.getSlotOn(6, slot2);
                    }
                }
            } else if (this.elytraOn && chest.getItem() != Items.ELYTRA && this.elytraTimer.passedMs(500L)) {
                if (this.taskList.isEmpty()) {
                    slot2 = InventoryUtil.findItemInventorySlot(Items.ELYTRA, false, XCarry.getInstance().isEnabled());
                    if (slot2 != -1) {
                        this.taskList.add(new InventoryUtil.Task(slot2));
                        this.taskList.add(new InventoryUtil.Task(6));
                        this.taskList.add(new InventoryUtil.Task(slot2));
                        if (this.updateController.getValue().booleanValue()) {
                            this.taskList.add(new InventoryUtil.Task());
                        }
                    }
                    this.elytraTimer.reset();
                }
            } else if (!this.elytraOn && chest.getItem() == Items.ELYTRA && this.elytraTimer.passedMs(500L) && this.taskList.isEmpty()) {
                slot2 = InventoryUtil.findItemInventorySlot((Item)Items.DIAMOND_CHESTPLATE, false, XCarry.getInstance().isEnabled());
                if (slot2 == -1 && (slot2 = InventoryUtil.findItemInventorySlot((Item)Items.IRON_CHESTPLATE, false, XCarry.getInstance().isEnabled())) == -1 && (slot2 = InventoryUtil.findItemInventorySlot((Item)Items.GOLDEN_CHESTPLATE, false, XCarry.getInstance().isEnabled())) == -1 && (slot2 = InventoryUtil.findItemInventorySlot((Item)Items.CHAINMAIL_CHESTPLATE, false, XCarry.getInstance().isEnabled())) == -1) {
                    slot2 = InventoryUtil.findItemInventorySlot((Item)Items.LEATHER_CHESTPLATE, false, XCarry.getInstance().isEnabled());
                }
                if (slot2 != -1) {
                    this.taskList.add(new InventoryUtil.Task(slot2));
                    this.taskList.add(new InventoryUtil.Task(6));
                    this.taskList.add(new InventoryUtil.Task(slot2));
                    if (this.updateController.getValue().booleanValue()) {
                        this.taskList.add(new InventoryUtil.Task());
                    }
                }
                this.elytraTimer.reset();
            }
            if ((legging = AutoArmor.mc.player.inventoryContainer.getSlot(7).getStack()).getItem() == Items.AIR && (slot3 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.LEGS, this.curse.getValue(), XCarry.getInstance().isEnabled())) != -1) {
                this.getSlotOn(7, slot3);
            }
            if ((feet = AutoArmor.mc.player.inventoryContainer.getSlot(8).getStack()).getItem() == Items.AIR && (slot4 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.FEET, this.curse.getValue(), XCarry.getInstance().isEnabled())) != -1) {
                this.getSlotOn(8, slot4);
            }
        }
        if (this.timer.passedMs((int)((float)this.delay.getValue().intValue() * (this.tps.getValue() != false ? Mod.serverManager.getTpsFactor() : 1.0f)))) {
            if (!this.taskList.isEmpty()) {
                for (int i = 0; i < this.actions.getValue(); ++i) {
                    InventoryUtil.Task task = this.taskList.poll();
                    if (task == null) continue;
                    task.run();
                }
            }
            this.timer.reset();
        }
    }

    private void takeOffSlot(int slot) {
        if (this.taskList.isEmpty()) {
            int target = -1;
            for (int i : InventoryUtil.findEmptySlots(XCarry.getInstance().isEnabled())) {
                if (this.doneSlots.contains(target)) continue;
                target = i;
                this.doneSlots.add(i);
            }
            if (target != -1) {
                if (target < 5 && target > 0 || !this.shiftClick.getValue().booleanValue()) {
                    this.taskList.add(new InventoryUtil.Task(slot));
                    this.taskList.add(new InventoryUtil.Task(target));
                } else {
                    this.taskList.add(new InventoryUtil.Task(slot, true));
                }
                if (this.updateController.getValue().booleanValue()) {
                    this.taskList.add(new InventoryUtil.Task());
                }
            }
        }
    }

    private void getSlotOn(int slot, int target) {
        if (this.taskList.isEmpty()) {
            this.doneSlots.remove((Object)target);
            if (target < 5 && target > 0 || !this.shiftClick.getValue().booleanValue()) {
                this.taskList.add(new InventoryUtil.Task(target));
                this.taskList.add(new InventoryUtil.Task(slot));
            } else {
                this.taskList.add(new InventoryUtil.Task(target, true));
            }
            if (this.updateController.getValue().booleanValue()) {
                this.taskList.add(new InventoryUtil.Task());
            }
        }
    }

    private boolean isSafe() {
        this.closest = CombatUtil.getTarget(this.closestEnemy.getValue().intValue());
        return this.closest == null || AutoArmor.mc.player.getDistanceSq((Entity)this.closest) >= MathUtil.square(this.closestEnemy.getValue().intValue());
    }
}

