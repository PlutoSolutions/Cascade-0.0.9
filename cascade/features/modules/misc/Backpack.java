/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.network.play.client.CPacketCloseWindow
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.misc;

import cascade.event.events.PacketEvent;
import cascade.features.gui.CascadeGui;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Backpack
extends Module {
    public Setting<Boolean> open = this.register(new Setting<Boolean>("Open", false));
    public Setting<Boolean> close = this.register(new Setting<Boolean>("Close", false));
    private GuiScreen cancelledGui = null;

    public Backpack() {
        super("Backpack", Module.Category.MISC, "Manipulates container packets");
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (this.megaNullCheck()) {
            return;
        }
        if (e.getPacket() instanceof CPacketCloseWindow && this.close.getValue().booleanValue()) {
            e.setCanceled(true);
        }
    }

    @Override
    public void onUpdate() {
        if (!this.isEnabled()) {
            return;
        }
        if (Backpack.mc.currentScreen instanceof GuiContainer && this.open.getValue().booleanValue()) {
            if (Backpack.mc.currentScreen instanceof CascadeGui) {
                return;
            }
            this.cancelledGui = Backpack.mc.currentScreen;
            Backpack.mc.currentScreen = null;
        }
    }

    @Override
    public void onDisable() {
        if (Backpack.fullNullCheck() && this.cancelledGui != null && this.open.getValue().booleanValue()) {
            mc.displayGuiScreen(this.cancelledGui);
        }
        this.cancelledGui = null;
    }
}

