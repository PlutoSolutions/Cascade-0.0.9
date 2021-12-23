/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItem
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityBeacon
 *  net.minecraft.tileentity.TileEntityChest
 *  net.minecraft.tileentity.TileEntityEnderChest
 *  net.minecraft.tileentity.TileEntityFurnace
 *  net.minecraft.tileentity.TileEntityHopper
 *  net.minecraft.util.EnumHand
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.misc;

import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.util.InventoryUtil;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoInteract
extends Module {
    public NoInteract() {
        super("NoInteract", Module.Category.MISC, "Prevents u from interacting with blocks");
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (NoInteract.fullNullCheck() || this.isDisabled()) {
            return;
        }
        try {
            if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && (InventoryUtil.heldItem(Items.EXPERIENCE_BOTTLE, InventoryUtil.Hand.Both) || InventoryUtil.heldItem(Items.GOLDEN_APPLE, InventoryUtil.Hand.Both) || InventoryUtil.heldItem(Items.CHORUS_FRUIT, InventoryUtil.Hand.Both) || InventoryUtil.heldItem((Item)Items.BOW, InventoryUtil.Hand.Both) || InventoryUtil.heldItem(Items.WRITABLE_BOOK, InventoryUtil.Hand.Both) || InventoryUtil.heldItem(Items.WRITTEN_BOOK, InventoryUtil.Hand.Both))) {
                for (TileEntity e : NoInteract.mc.world.loadedTileEntityList) {
                    if (!(e instanceof TileEntityEnderChest) && !(e instanceof TileEntityBeacon) && !(e instanceof TileEntityFurnace) && !(e instanceof TileEntityHopper) && !(e instanceof TileEntityChest) || !NoInteract.mc.objectMouseOver.getBlockPos().equals((Object)e.getPos()) || !NoInteract.mc.gameSettings.keyBindUseItem.isKeyDown()) continue;
                    event.setCanceled(true);
                    mc.getConnection().sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                }
                if (NoInteract.mc.world.getBlockState(NoInteract.mc.objectMouseOver.getBlockPos()).getBlock() == Blocks.ANVIL && NoInteract.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                    event.setCanceled(true);
                    mc.getConnection().sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

