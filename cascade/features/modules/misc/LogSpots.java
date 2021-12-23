/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.misc;

import cascade.event.events.ConnectionEvent;
import cascade.event.events.Render3DEvent;
import cascade.features.command.Command;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.ColorUtil;
import cascade.util.MathUtil;
import cascade.util.RenderUtil;
import java.awt.Color;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LogSpots
extends Module {
    public Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(300.0f), Float.valueOf(50.0f), Float.valueOf(300.0f)));
    private final Setting<Color> c = this.register(new Setting<Color>("Color", new Color(-1)));
    public Setting<Boolean> ignorePractice = this.register(new Setting<Boolean>("IgnorePractice", true));
    private final Setting<Boolean> scaleing = this.register(new Setting<Boolean>("Scale", false));
    private final Setting<Float> scaling = this.register(new Setting<Float>("Size", Float.valueOf(4.0f), Float.valueOf(0.1f), Float.valueOf(20.0f)));
    private final Setting<Float> factor = this.register(new Setting<Object>("Factor", Float.valueOf(0.3f), Float.valueOf(0.1f), Float.valueOf(1.0f), v -> this.scaleing.getValue()));
    private final Setting<Boolean> smartScale = this.register(new Setting<Object>("SmartScale", Boolean.valueOf(false), v -> this.scaleing.getValue()));
    private final Setting<Boolean> rect = this.register(new Setting<Boolean>("Rectangle", true));
    private final Setting<Boolean> coords = this.register(new Setting<Boolean>("Coords", true));
    private final Setting<Boolean> notification = this.register(new Setting<Boolean>("Notification", true));
    public Setting<Boolean> message = this.register(new Setting<Boolean>("Message", false));
    private final List<LogoutPos> spots = new CopyOnWriteArrayList<LogoutPos>();

    public LogSpots() {
        super("LogSpots", Module.Category.MISC, "Logs the spots when a player leaves");
    }

    @Override
    public void onLogout() {
        this.spots.clear();
    }

    @Override
    public void onDisable() {
        this.spots.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onRender3D(Render3DEvent event) {
        if (!this.spots.isEmpty()) {
            List<LogoutPos> list = this.spots;
            synchronized (list) {
                this.spots.forEach(spot -> {
                    if (spot.getEntity() != null) {
                        AxisAlignedBB interpolateAxis = RenderUtil.interpolateAxis(spot.getEntity().getEntityBoundingBox());
                        RenderUtil.drawBlockOutline(interpolateAxis, new Color(this.c.getValue().getRed(), this.c.getValue().getGreen(), this.c.getValue().getBlue(), this.c.getValue().getAlpha()), 1.0f);
                        double x = this.interpolate(spot.getEntity().lastTickPosX, spot.getEntity().posX, event.getPartialTicks()) - LogSpots.mc.getRenderManager().renderPosX;
                        double y = this.interpolate(spot.getEntity().lastTickPosY, spot.getEntity().posY, event.getPartialTicks()) - LogSpots.mc.getRenderManager().renderPosY;
                        double z = this.interpolate(spot.getEntity().lastTickPosZ, spot.getEntity().posZ, event.getPartialTicks()) - LogSpots.mc.getRenderManager().renderPosZ;
                        this.renderNameTag(spot.getName(), x, y, z, event.getPartialTicks(), spot.getX(), spot.getY(), spot.getZ());
                    }
                });
            }
        }
    }

    @Override
    public void onUpdate() {
        if (LogSpots.fullNullCheck()) {
            return;
        }
        if (this.ignorePractice.getValue().booleanValue() && (LogSpots.mc.getCurrentServerData().serverIP.equalsIgnoreCase("crystalpvp.cc") || LogSpots.mc.getCurrentServerData().serverIP.equalsIgnoreCase("us.crystalpvp.cc") || LogSpots.mc.getCurrentServerData().serverIP.equalsIgnoreCase("2b2tpvp.net") || LogSpots.mc.getCurrentServerData().serverIP.equalsIgnoreCase("strict.2b2tpvp.net"))) {
            return;
        }
        this.spots.removeIf(spot -> LogSpots.mc.player.getDistanceSq((Entity)spot.getEntity()) >= MathUtil.square(this.range.getValue().floatValue()));
    }

    @SubscribeEvent
    public void onConnection(ConnectionEvent event) {
        if (event.getStage() == 0) {
            UUID uuid = event.getUuid();
            EntityPlayer entity = LogSpots.mc.world.getPlayerEntityByUUID(uuid);
            if (entity != null && this.message.getValue().booleanValue()) {
                Command.sendMessage("\u00a7a" + entity.getName() + " just logged in" + (this.coords.getValue() != false ? " at (" + (int)entity.posX + ", " + (int)entity.posY + ", " + (int)entity.posZ + ")!" : "!"), true, true);
            }
            this.spots.removeIf(pos -> pos.getName().equalsIgnoreCase(event.getName()));
        } else if (event.getStage() == 1) {
            EntityPlayer entity2 = event.getEntity();
            UUID uuid2 = event.getUuid();
            String name = event.getName();
            if (this.message.getValue().booleanValue()) {
                Command.sendMessage("\u00a7c" + event.getName() + " just logged out" + (this.coords.getValue() != false ? " at (" + (int)entity2.posX + ", " + (int)entity2.posY + ", " + (int)entity2.posZ + ")!" : "!"), true, true);
            }
            if (name != null && entity2 != null && uuid2 != null) {
                this.spots.add(new LogoutPos(name, uuid2, entity2));
            }
        }
    }

    private void renderNameTag(String name, double x, double yi, double z, float delta, double xPos, double yPos, double zPos) {
        double y = yi + 0.7;
        Entity camera = mc.getRenderViewEntity();
        assert (camera != null);
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = this.interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = this.interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = this.interpolate(camera.prevPosZ, camera.posZ, delta);
        String displayTag = name + " XYZ: " + (int)xPos + ", " + (int)yPos + ", " + (int)zPos;
        double distance = camera.getDistance(x + LogSpots.mc.getRenderManager().viewerPosX, y + LogSpots.mc.getRenderManager().viewerPosY, z + LogSpots.mc.getRenderManager().viewerPosZ);
        int width = this.renderer.getStringWidth(displayTag) / 2;
        double scale = (0.0018 + (double)this.scaling.getValue().floatValue() * (distance * (double)this.factor.getValue().floatValue())) / 1000.0;
        if (distance <= 8.0 && this.smartScale.getValue().booleanValue()) {
            scale = 0.0245;
        }
        if (!this.scaleing.getValue().booleanValue()) {
            scale = (double)this.scaling.getValue().floatValue() / 100.0;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset((float)1.0f, (float)-1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float)((float)x), (float)((float)y + 1.4f), (float)((float)z));
        GlStateManager.rotate((float)(-LogSpots.mc.getRenderManager().playerViewY), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)LogSpots.mc.getRenderManager().playerViewX, (float)(LogSpots.mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f), (float)0.0f, (float)0.0f);
        GlStateManager.scale((double)(-scale), (double)(-scale), (double)scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableBlend();
        if (this.rect.getValue().booleanValue()) {
            RenderUtil.drawRect(-width - 2, -(this.renderer.getFontHeight() + 1), (float)width + 2.0f, 1.5f, 0x55000000);
        }
        GlStateManager.disableBlend();
        this.renderer.drawStringWithShadow(displayTag, -width, -(this.renderer.getFontHeight() - 1), ColorUtil.toRGBA(new Color(this.c.getValue().getRed(), this.c.getValue().getGreen(), this.c.getValue().getBlue(), this.c.getValue().getAlpha())));
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset((float)1.0f, (float)1500000.0f);
        GlStateManager.popMatrix();
    }

    private double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double)delta;
    }

    private static class LogoutPos {
        private final String name;
        private final UUID uuid;
        private final EntityPlayer entity;
        private final double x;
        private final double y;
        private final double z;

        public LogoutPos(String name, UUID uuid, EntityPlayer entity) {
            this.name = name;
            this.uuid = uuid;
            this.entity = entity;
            this.x = entity.posX;
            this.y = entity.posY;
            this.z = entity.posZ;
        }

        public String getName() {
            return this.name;
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public EntityPlayer getEntity() {
            return this.entity;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getZ() {
            return this.z;
        }
    }
}

