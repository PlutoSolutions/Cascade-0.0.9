/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiIngame
 *  net.minecraft.network.play.client.CPacketChatMessage
 *  net.minecraftforge.fml.common.ObfuscationReflectionHelper
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.misc;

import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatModifications
extends Module {
    static ChatModifications INSTANCE = new ChatModifications();
    public Setting<Boolean> suffix = this.register(new Setting<Boolean>("Suffix", true));
    public Setting<String> suffixString = this.register(new Setting<String>("ChatSuffix", "i am russian retard", v -> this.suffix.getValue()));
    public Setting<Type> animationType = this.register(new Setting<Type>("AnimationType", Type.Horizontal));
    public Setting<Double> chatX = this.register(new Setting<Double>("ChatX", 0.0, 0.0, 600.0));
    public Setting<Double> chatY = this.register(new Setting<Double>("ChatY", 0.0, 0.0, 30.0));
    public Setting<Double> animationSpeed = this.register(new Setting<Double>("AnimationSpeed", Double.valueOf(30.0), Double.valueOf(1.0), Double.valueOf(100.0), v -> !this.animationType.getValue().equals((Object)Type.Vertical)));
    public Setting<Double> startOffset = this.register(new Setting<Double>("StartOffset", Double.valueOf(10.0), Double.valueOf(5.0), Double.valueOf(100.0), v -> !this.animationType.getValue().equals((Object)Type.Vertical)));
    public Setting<Double> smoothness = this.register(new Setting<Double>("Smoothness", Double.valueOf(1.0), Double.valueOf(1.0), Double.valueOf(5.0), v -> !this.animationType.getValue().equals((Object)Type.Vertical)));

    public ChatModifications() {
        super("ChatModifications", Module.Category.MISC, "Clean chat");
        this.setInstance();
    }

    public static ChatModifications getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChatModifications();
        }
        return INSTANCE;
    }

    void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketChatMessage && this.suffix.getValue().booleanValue()) {
            CPacketChatMessage packet = (CPacketChatMessage)event.getPacket();
            String message = packet.getMessage();
            if (message.startsWith("/")) {
                return;
            }
            if ((message = message + " " + this.suffixString.getValue()).length() >= 256) {
                message = message.substring(0, 256);
            }
            packet.message = message;
        }
    }

    @Override
    public void onToggle() {
        ObfuscationReflectionHelper.setPrivateValue(GuiIngame.class, (Object)ChatModifications.mc.ingameGUI, (Object)((Object)new GuiChat(mc)), (String[])new String[]{"field_73840_e"});
    }

    @Override
    public void onLogin() {
        if (!this.isEnabled()) {
            return;
        }
        this.disable();
        this.enable();
    }

    public static enum Type {
        Horizontal,
        Vertical;

    }
}

