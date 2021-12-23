/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketChat
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.misc;

import cascade.Mod;
import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.MathUtil;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CoordsReply
extends Module {
    public Setting<Boolean> friendOnly = this.register(new Setting<Boolean>("FriendsOnly", false));
    public Setting<Integer> range = this.register(new Setting<Integer>("SpawnRange", 100, 0, 2000));

    public CoordsReply() {
        super("CoordsReply", Module.Category.MISC, "Replies to friends asking for coords.");
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        block4: {
            String username;
            block6: {
                block5: {
                    if (CoordsReply.fullNullCheck() || !this.isEnabled() || !(event.getPacket() instanceof SPacketChat)) {
                        return;
                    }
                    String chatMessage = ((SPacketChat)event.getPacket()).getChatComponent().getUnformattedText();
                    String chatMessageLowercase = chatMessage.toLowerCase();
                    if (!chatMessage.contains("says: ")) break block4;
                    username = chatMessage.split(" ")[0];
                    if (!chatMessageLowercase.contains("coords") || username.equals(CoordsReply.mc.player.getName()) || !Mod.friendManager.isFriend(username) && this.friendOnly.getValue().booleanValue()) break block5;
                    BlockPos blockPos = new BlockPos(0.0, CoordsReply.mc.player.posY, 0.0);
                    if (!(CoordsReply.mc.player.getDistanceSq(blockPos) > MathUtil.square(this.range.getValue().intValue()))) break block6;
                }
                return;
            }
            CoordsReply.mc.player.sendChatMessage("/msg " + username + " Im currently at: X: " + MathUtil.round(CoordsReply.mc.player.posX, 0) + " Y: " + MathUtil.round(CoordsReply.mc.player.posY, 0) + " Z: " + MathUtil.round(CoordsReply.mc.player.posZ, 0) + ".");
        }
    }
}

