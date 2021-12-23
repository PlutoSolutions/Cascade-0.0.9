/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.core;

import cascade.Mod;
import cascade.event.events.ClientEvent;
import cascade.features.command.Command;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.GraphicsEnvironment;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FontMod
extends Module {
    private static FontMod INSTANCE = new FontMod();
    public Setting<String> fontName = this.register(new Setting<String>("FontName", "Arial"));
    public Setting<Boolean> antiAlias = this.register(new Setting<Boolean>("AntiAlias", true));
    public Setting<Boolean> fractionalMetrics = this.register(new Setting<Boolean>("Metrics", true));
    public Setting<Integer> fontSize = this.register(new Setting<Integer>("Size", 18, 12, 30));
    public Setting<Integer> fontStyle = this.register(new Setting<Integer>("Style", 0, 0, 3));
    public Setting<Boolean> customAll = this.register(new Setting<Boolean>("Full", true));
    private boolean reloadFont = false;

    public FontMod() {
        super("Font", Module.Category.CORE, "Changes le font");
        this.setInstance();
    }

    public static FontMod getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FontMod();
        }
        return INSTANCE;
    }

    public static boolean checkFont(String font, boolean message) {
        String[] fonts;
        for (String s : fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (!message && s.equals(font)) {
                return true;
            }
            if (!message) continue;
            Command.sendMessage(s, true, false);
        }
        return false;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        Setting setting;
        if (event.getStage() == 2 && (setting = event.getSetting()) != null && setting.getFeature().equals(this)) {
            if (setting.getName().equals("FontName") && !FontMod.checkFont(setting.getPlannedValue().toString(), false)) {
                Command.sendMessage((Object)ChatFormatting.RED + "That font doesnt exist.", true, false);
                event.setCanceled(true);
                return;
            }
            this.reloadFont = true;
        }
    }

    @Override
    public void onTick() {
        if (this.reloadFont) {
            Mod.textManager.init(false);
            this.reloadFont = false;
        }
    }
}

