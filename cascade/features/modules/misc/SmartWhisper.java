/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketChatMessage
 *  net.minecraft.network.play.server.SPacketChat
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.misc;

import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.mixin.mixins.accessor.ICPacketChatMessage;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SmartWhisper
extends Module {
    private String whisperSender;
    private boolean whispered;
    private boolean newWhisperWhileChatOpen;

    public SmartWhisper() {
        super("SmartWhisper", Module.Category.MISC, "Smart reply");
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Receive e) {
        String s;
        if (SmartWhisper.fullNullCheck()) {
            return;
        }
        if (e.getPacket() instanceof SPacketChat && this.isEnabled() && (s = ((SPacketChat)e.getPacket()).getChatComponent().getUnformattedText()).contains("whispers: ")) {
            if (SmartWhisper.mc.ingameGUI.getChatGUI().getChatOpen() && this.whispered) {
                this.newWhisperWhileChatOpen = true;
            } else {
                this.whisperSender = s.split(" ")[0];
                this.whispered = true;
            }
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Send e) {
        if (SmartWhisper.fullNullCheck()) {
            return;
        }
        if (e.getPacket() instanceof CPacketChatMessage && this.isEnabled()) {
            CPacketChatMessage packet = (CPacketChatMessage)e.getPacket();
            String s = packet.getMessage();
            if (this.whisperSender != null && this.whispered && s.split(" ")[0].equalsIgnoreCase("/r") && this.newWhisperWhileChatOpen) {
                ((ICPacketChatMessage)packet).setMessage("/msg " + this.whisperSender + " " + s.substring(3));
            }
        }
    }
}

