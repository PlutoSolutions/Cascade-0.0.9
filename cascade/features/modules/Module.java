/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 */
package cascade.features.modules;

import cascade.event.events.ClientEvent;
import cascade.event.events.ModuleToggleEvent;
import cascade.event.events.Render2DEvent;
import cascade.event.events.Render3DEvent;
import cascade.features.Feature;
import cascade.features.setting.Bind;
import cascade.features.setting.Setting;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class Module
extends Feature {
    String description;
    Category category;
    public Setting<Boolean> enabled = this.register(new Setting<Boolean>("Enabled", false));
    public Setting<Boolean> drawn = this.register(new Setting<Boolean>("Drawn", true));
    public Setting<Bind> bind = this.register(new Setting<Bind>("Keybind", new Bind(-1)));
    public String name;

    public Module(String name, Category category, String description) {
        super(name);
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onToggle() {
    }

    public void onLoad() {
    }

    public void onTick() {
    }

    public void onLogin() {
    }

    public void onLogout() {
    }

    public void onUpdate() {
    }

    public void onRender2D(Render2DEvent event) {
    }

    public void onRender3D(Render3DEvent event) {
    }

    public String getDisplayInfo() {
        return null;
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            this.enable();
        } else {
            this.disable();
        }
    }

    public void enable() {
        this.enabled.setValue(true);
        this.onToggle();
        this.onEnable();
        MinecraftForge.EVENT_BUS.post((Event)new ModuleToggleEvent.Enable(this));
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    public void disable() {
        this.enabled.setValue(false);
        this.onToggle();
        this.onDisable();
        MinecraftForge.EVENT_BUS.post((Event)new ModuleToggleEvent.Disable(this));
        MinecraftForge.EVENT_BUS.unregister((Object)this);
    }

    public void toggle() {
        ClientEvent event = new ClientEvent(!this.isEnabled() ? 1 : 0, this);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (!event.isCanceled()) {
            this.setEnabled(!this.isEnabled());
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isDrawn() {
        return this.drawn.getValue();
    }

    public Category getCategory() {
        return this.category;
    }

    public String getInfo() {
        return null;
    }

    public Bind getBind() {
        return this.bind.getValue();
    }

    public void setBind(int key) {
        this.bind.setValue(new Bind(key));
    }

    public boolean isOn() {
        return this.enabled.getValue();
    }

    public String getFullArrayString() {
        return this.getName() + (Object)ChatFormatting.GRAY + (this.getDisplayInfo() != null ? " [" + (Object)ChatFormatting.WHITE + this.getDisplayInfo() + (Object)ChatFormatting.GRAY + "]" : "");
    }

    public static enum Category {
        COMBAT("Combat"),
        EXPLOIT("Exploit"),
        MISC("Misc"),
        VISUAL("Visual"),
        MOVEMENT("Movement"),
        PLAYER("Player"),
        CORE("Core");

        String name;

        private Category(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}

