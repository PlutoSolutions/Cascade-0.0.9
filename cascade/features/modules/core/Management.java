/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.core;

import cascade.Mod;
import cascade.event.events.ClientEvent;
import cascade.features.command.Command;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Management
extends Module {
    public Setting<Page> page = this.register(new Setting<Page>("Page", Page.General));
    public Setting<String> prefix = this.register(new Setting<Object>("Prefix", ".", v -> this.page.getValue() == Page.General));
    public Setting<Boolean> noPacketKick = this.register(new Setting<Object>("NoPacketKick", Boolean.valueOf(true), v -> this.page.getValue() == Page.General));
    public Setting<Boolean> unfocusedCPU = this.register(new Setting<Object>("UnfocusedCPU", Boolean.valueOf(true), v -> this.page.getValue() == Page.General));
    public Setting<Integer> cpuFPS = this.register(new Setting<Object>("FPS", Integer.valueOf(60), Integer.valueOf(1), Integer.valueOf(144), v -> this.page.getValue() == Page.General && this.unfocusedCPU.getValue() != false));
    private static Management INSTANCE = new Management();

    public Management() {
        super("Management", Module.Category.CORE, "Manages the client");
        this.setInstance();
    }

    public static Management getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Management();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this) && event.getSetting().equals(this.prefix)) {
            Mod.commandManager.setPrefix(this.prefix.getPlannedValue());
            Command.sendMessage("Prefix set to " + Mod.commandManager.getPrefix(), true, false);
        }
    }

    @Override
    public void onLoad() {
        Mod.commandManager.setPrefix(this.prefix.getValue());
    }

    public static enum Page {
        General;

    }
}

