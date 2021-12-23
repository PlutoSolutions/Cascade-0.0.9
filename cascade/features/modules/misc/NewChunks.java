/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.RenderGlobal
 *  net.minecraft.network.play.server.SPacketChunkData
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.Vec2f
 *  net.minecraftforge.fml.common.eventhandler.EventPriority
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.GL11
 */
package cascade.features.modules.misc;

import cascade.event.events.PacketEvent;
import cascade.event.events.Render3DEvent;
import cascade.features.modules.Module;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class NewChunks
extends Module {
    private List<Vec2f> chunkDataList = new ArrayList<Vec2f>();

    public NewChunks() {
        super("NewChunks", Module.Category.MISC, "Shows newly generated chunks");
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST, receiveCanceled=true)
    public void onPacketReceive(PacketEvent.Receive e) {
        Vec2f chunk;
        SPacketChunkData packet2;
        if (e.getPacket() instanceof SPacketChunkData && !(packet2 = (SPacketChunkData)e.getPacket()).isFullChunk() && !this.chunkDataList.contains((Object)(chunk = new Vec2f((float)(packet2.getChunkX() * 16), (float)(packet2.getChunkZ() * 16))))) {
            this.chunkDataList.add(chunk);
        }
    }

    @Override
    public void onRender3D(Render3DEvent e) {
        try {
            ArrayList<Vec2f> found = new ArrayList<Vec2f>();
            for (Vec2f chunkData : this.chunkDataList) {
                if (chunkData == null) continue;
                if (NewChunks.mc.player.getDistance((double)chunkData.x, NewChunks.mc.player.posY, (double)chunkData.y) > 120.0) {
                    found.add(chunkData);
                }
                double renderPosX = (double)chunkData.x - NewChunks.mc.getRenderManager().viewerPosX;
                double renderPosY = -NewChunks.mc.getRenderManager().viewerPosY;
                double renderPosZ = (double)chunkData.y - NewChunks.mc.getRenderManager().viewerPosZ;
                AxisAlignedBB bb = new AxisAlignedBB(renderPosX, renderPosY, renderPosZ, renderPosX + 16.0, renderPosY + 1.0, renderPosZ + 16.0);
                NewChunks.RenderBlock(bb, -1, 1.2);
            }
            this.chunkDataList.removeAll(found);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void OpenGl() {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask((boolean)false);
        GL11.glHint((int)3154, (int)4354);
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GL11.glEnable((int)2848);
        GL11.glEnable((int)34383);
    }

    public static void ReleaseGl() {
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GlStateManager.depthMask((boolean)true);
        GL11.glHint((int)3154, (int)4352);
        GL11.glDisable((int)2848);
        GL11.glDisable((int)34383);
        GlStateManager.shadeModel((int)7424);
    }

    public static void RenderBlock(AxisAlignedBB bb, int c, Double width) {
        NewChunks.OpenGl();
        GlStateManager.glLineWidth((float)((float)(1.5 * (width + 1.0E-4))));
        float a = (float)(c >> 24 & 0xFF) / 255.0f;
        float r = (float)(c >> 16 & 0xFF) / 255.0f;
        float g = (float)(c >> 8 & 0xFF) / 255.0f;
        float b = (float)(c >> 0 & 0xFF) / 255.0f;
        RenderGlobal.renderFilledBox((double)bb.minX, (double)bb.minY, (double)bb.minZ, (double)bb.maxX, (double)bb.minY, (double)bb.maxZ, (float)r, (float)g, (float)b, (float)a);
        NewChunks.ReleaseGl();
    }
}

