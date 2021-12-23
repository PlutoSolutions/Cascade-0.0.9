/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.network.FMLNetworkEvent$ClientDisconnectionFromServerEvent
 */
package cascade.features.modules.misc;

import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class LogCoords
extends Module {
    public Setting<Boolean> ignorePractice = this.register(new Setting<Boolean>("IgnorePractice", true));
    public Setting<Integer> maxRadius = this.register(new Setting<Integer>("MaxRadius", 500, 100, 1000));

    public LogCoords() {
        super("LogCoords", Module.Category.MISC, "Copies ur coords when logging out");
    }

    @SubscribeEvent
    public void onPlayerLeaveEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        if (!mc.isSingleplayer() && this.isEnabled() && !LogCoords.fullNullCheck()) {
            if (this.ignorePractice.getValue().booleanValue() && (LogCoords.mc.getCurrentServerData().serverIP.equalsIgnoreCase("crystalpvp.cc") || LogCoords.mc.getCurrentServerData().serverIP.equalsIgnoreCase("us.crystalpvp.cc") || LogCoords.mc.getCurrentServerData().serverIP.equalsIgnoreCase("2b2tpvp.net") || LogCoords.mc.getCurrentServerData().serverIP.equalsIgnoreCase("strict.2b2tpvp.net"))) {
                return;
            }
            if (LogCoords.mc.player.posX > (double)this.maxRadius.getValue().intValue() || LogCoords.mc.player.posZ > (double)this.maxRadius.getValue().intValue()) {
                return;
            }
            String coords = "Logout Coords: X=" + String.format("%.1f", LogCoords.mc.player.posX) + ", Y=" + String.format("%.1f", LogCoords.mc.player.posY) + ", Z=" + String.format("%.1f", LogCoords.mc.player.posZ);
            StringSelection data = new StringSelection(coords);
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            cb.setContents(data, data);
        }
    }
}

