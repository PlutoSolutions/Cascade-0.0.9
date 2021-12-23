/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  org.lwjgl.opengl.Display
 *  org.lwjgl.opengl.GL11
 */
package cascade.features.modules.visual;

import cascade.Mod;
import cascade.event.events.Render2DEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.ColorUtil;
import cascade.util.RenderUtil;
import cascade.util.Util;
import com.google.common.collect.Maps;
import java.awt.Color;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class OffscreenESP
extends Module {
    public Setting<Boolean> onlyFriends = this.register(new Setting<Boolean>("OnlyFriends", false));
    private final Setting<Color> c = this.register(new Setting<Color>("Color", new Color(-1)));
    private final Setting<Integer> radius = this.register(new Setting<Integer>("Radius", 45, 1, 200));
    private final Setting<Float> size = this.register(new Setting<Float>("Size", Float.valueOf(10.0f), Float.valueOf(0.1f), Float.valueOf(25.0f)));
    private final Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", true));
    private final Setting<Float> outlineWidth = this.register(new Setting<Object>("Width", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(3.0f), v -> this.outline.getValue()));
    private final Setting<Integer> fadeDistance = this.register(new Setting<Integer>("FadeDistance", 100, 1, 200));
    private final EntityListener entityListener = new EntityListener();

    public OffscreenESP() {
        super("OffscreenESP", Module.Category.VISUAL, "Shows the direction players are in");
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (OffscreenESP.fullNullCheck()) {
            this.disable();
            return;
        }
        this.entityListener.render();
        OffscreenESP.mc.world.loadedEntityList.forEach(o -> {
            if (o instanceof EntityPlayer && this.isValid((EntityPlayer)o)) {
                EntityPlayer entity = (EntityPlayer)o;
                Vec3d pos = this.entityListener.getEntityLowerBounds().get((Object)entity);
                if (pos != null && !this.isOnScreen(pos) && !RenderUtil.isInViewFrustrum((Entity)entity)) {
                    Color color = ColorUtil.getColor((Entity)entity, this.c.getValue().getRed(), this.c.getValue().getGreen(), this.c.getValue().getBlue(), (int)MathHelper.clamp((float)(255.0f - 255.0f / (float)this.fadeDistance.getValue().intValue() * OffscreenESP.mc.player.getDistance((Entity)entity)), (float)100.0f, (float)255.0f), this.onlyFriends.getValue() == false);
                    int x = Display.getWidth() / 2 / (OffscreenESP.mc.gameSettings.guiScale == 0 ? 1 : OffscreenESP.mc.gameSettings.guiScale);
                    int y = Display.getHeight() / 2 / (OffscreenESP.mc.gameSettings.guiScale == 0 ? 1 : OffscreenESP.mc.gameSettings.guiScale);
                    float yaw = this.getRotations((EntityLivingBase)entity) - OffscreenESP.mc.player.rotationYaw;
                    GL11.glTranslatef((float)x, (float)y, (float)0.0f);
                    GL11.glRotatef((float)yaw, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glTranslatef((float)(-x), (float)(-y), (float)0.0f);
                    RenderUtil.drawTracerPointer(x, y - this.radius.getValue(), this.size.getValue().floatValue(), 2.0f, 1.0f, this.outline.getValue(), this.outlineWidth.getValue().floatValue(), color.getRGB());
                    GL11.glTranslatef((float)x, (float)y, (float)0.0f);
                    GL11.glRotatef((float)(-yaw), (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glTranslatef((float)(-x), (float)(-y), (float)0.0f);
                }
            }
        });
    }

    private boolean isOnScreen(Vec3d pos) {
        int n3;
        int n2;
        int n;
        if (!(pos.x > -1.0)) {
            return false;
        }
        if (!(pos.y < 1.0)) {
            return false;
        }
        if (!(pos.x > -1.0)) {
            return false;
        }
        if (!(pos.z < 1.0)) {
            return false;
        }
        int n4 = n = OffscreenESP.mc.gameSettings.guiScale == 0 ? 1 : OffscreenESP.mc.gameSettings.guiScale;
        if (!(pos.x / (double)n >= 0.0)) {
            return false;
        }
        int n5 = n2 = OffscreenESP.mc.gameSettings.guiScale == 0 ? 1 : OffscreenESP.mc.gameSettings.guiScale;
        if (!(pos.x / (double)n2 <= (double)Display.getWidth())) {
            return false;
        }
        int n6 = n3 = OffscreenESP.mc.gameSettings.guiScale == 0 ? 1 : OffscreenESP.mc.gameSettings.guiScale;
        if (!(pos.y / (double)n3 >= 0.0)) {
            return false;
        }
        int n42 = OffscreenESP.mc.gameSettings.guiScale == 0 ? 1 : OffscreenESP.mc.gameSettings.guiScale;
        return pos.y / (double)n42 <= (double)Display.getHeight();
    }

    private boolean isValid(EntityPlayer entity) {
        if (entity != OffscreenESP.mc.player && !entity.isInvisible()) {
            if (Mod.friendManager.isFriend(entity) && this.onlyFriends.getValue().booleanValue()) {
                return true;
            }
            if (!this.onlyFriends.getValue().booleanValue()) {
                return true;
            }
        }
        return false;
    }

    private float getRotations(EntityLivingBase ent) {
        double x = ent.posX - OffscreenESP.mc.player.posX;
        double z = ent.posZ - OffscreenESP.mc.player.posZ;
        return (float)(-(Math.atan2(x, z) * 57.29577951308232));
    }

    private static class EntityListener {
        private final Map<Entity, Vec3d> entityUpperBounds = Maps.newHashMap();
        private final Map<Entity, Vec3d> entityLowerBounds = Maps.newHashMap();

        private EntityListener() {
        }

        private void render() {
            if (!this.entityUpperBounds.isEmpty()) {
                this.entityUpperBounds.clear();
            }
            if (!this.entityLowerBounds.isEmpty()) {
                this.entityLowerBounds.clear();
            }
            for (Entity e : Util.mc.world.loadedEntityList) {
                Vec3d bound = this.getEntityRenderPosition(e);
                bound.add(new Vec3d(0.0, (double)e.height + 0.2, 0.0));
                Vec3d upperBounds = RenderUtil.to2D(bound.x, bound.y, bound.z);
                Vec3d lowerBounds = RenderUtil.to2D(bound.x, bound.y - 2.0, bound.z);
                if (upperBounds == null || lowerBounds == null) continue;
                this.entityUpperBounds.put(e, upperBounds);
                this.entityLowerBounds.put(e, lowerBounds);
            }
        }

        private Vec3d getEntityRenderPosition(Entity entity) {
            double partial = Util.mc.timer.renderPartialTicks;
            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partial - Util.mc.getRenderManager().viewerPosX;
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partial - Util.mc.getRenderManager().viewerPosY;
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partial - Util.mc.getRenderManager().viewerPosZ;
            return new Vec3d(x, y, z);
        }

        public Map<Entity, Vec3d> getEntityLowerBounds() {
            return this.entityLowerBounds;
        }
    }
}

