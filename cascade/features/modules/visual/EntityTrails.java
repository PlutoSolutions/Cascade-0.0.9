/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderPearl
 *  net.minecraft.util.math.Vec3d
 *  org.lwjgl.opengl.GL11
 */
package cascade.features.modules.visual;

import cascade.event.events.Render3DEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.ColorUtil;
import cascade.util.RenderUtil;
import cascade.util.Timer;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class EntityTrails
extends Module {
    public Setting<Boolean> players = this.register(new Setting<Boolean>("Players", false));
    public Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(2.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.players.getValue()));
    public Setting<Boolean> fade = this.register(new Setting<Boolean>("Fade", Boolean.valueOf(false), v -> this.players.getValue()));
    public Setting<Integer> removeDelay = this.register(new Setting<Integer>("RemoveDelay", Integer.valueOf(1000), Integer.valueOf(0), Integer.valueOf(2000), v -> this.players.getValue()));
    public Setting<Color> startColor = this.register(new Setting<Color>("StartColor", new Color(-1), v -> this.players.getValue()));
    public Setting<Color> endColor = this.register(new Setting<Color>("EndColor", new Color(-1), v -> this.players.getValue()));
    public Setting<Boolean> pearls = this.register(new Setting<Boolean>("Pearls", false));
    public Setting<Color> pearlColor = this.register(new Setting<Color>("PearlColor", new Color(-1), v -> this.pearls.getValue()));
    public Setting<Float> pearlLineWidth = this.register(new Setting<Float>("PearlLineWidth", Float.valueOf(3.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.pearls.getValue()));
    HashMap<UUID, List<Vec3d>> pearlPos = new HashMap();
    HashMap<UUID, Double> removeWait = new HashMap();
    Map<UUID, ItemTrail> trails = new HashMap<UUID, ItemTrail>();

    public EntityTrails() {
        super("EntityTrails", Module.Category.VISUAL, "Draws a line behind entities (Breadcrumbs)");
    }

    @Override
    public void onUpdate() {
        if (this.pearls.getValue().booleanValue()) {
            UUID pearlPos = null;
            for (UUID uuid : this.removeWait.keySet()) {
                if (this.removeWait.get(uuid) <= 0.0) {
                    this.pearlPos.remove(uuid);
                    pearlPos = uuid;
                    continue;
                }
                this.removeWait.replace(uuid, this.removeWait.get(uuid) - 0.05);
            }
            if (pearlPos != null) {
                this.removeWait.remove(pearlPos);
            }
            for (Entity e : EntityTrails.mc.world.getLoadedEntityList()) {
                if (!(e instanceof EntityEnderPearl)) continue;
                if (!this.pearlPos.containsKey(e.getUniqueID())) {
                    this.pearlPos.put(e.getUniqueID(), new ArrayList<Vec3d>(Collections.singletonList(e.getPositionVector())));
                    this.removeWait.put(e.getUniqueID(), 0.1);
                    continue;
                }
                this.removeWait.replace(e.getUniqueID(), 0.1);
                List<Vec3d> v = this.pearlPos.get(e.getUniqueID());
                v.add(e.getPositionVector());
            }
        }
    }

    @Override
    public void onTick() {
        if (this.players.getValue().booleanValue()) {
            if (this.megaNullCheck()) {
                return;
            }
            if (this.trails.containsKey(EntityTrails.mc.player.getUniqueID())) {
                ItemTrail playerTrail = this.trails.get(EntityTrails.mc.player.getUniqueID());
                playerTrail.timer.reset();
                List toRemove = playerTrail.positions.stream().filter(position -> System.currentTimeMillis() - position.time > this.removeDelay.getValue().longValue()).collect(Collectors.toList());
                playerTrail.positions.removeAll(toRemove);
                playerTrail.positions.add(new Position(EntityTrails.mc.player.getPositionVector()));
            } else {
                this.trails.put(EntityTrails.mc.player.getUniqueID(), new ItemTrail((Entity)EntityTrails.mc.player));
            }
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.megaNullCheck()) {
            return;
        }
        if (this.players.getValue().booleanValue()) {
            this.trails.forEach((key, value) -> {
                if (value.entity.isDead || EntityTrails.mc.world.getEntityByID(value.entity.getEntityId()) == null) {
                    if (value.timer.isPaused()) {
                        value.timer.reset();
                    }
                    value.timer.setPaused(false);
                }
                if (!value.timer.isPassed()) {
                    this.drawTrail((ItemTrail)value);
                }
            });
        }
        if (this.pearlPos.isEmpty() || !this.pearls.getValue().booleanValue()) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glLineWidth((float)this.pearlLineWidth.getValue().floatValue());
        this.pearlPos.keySet().stream().filter(uuid -> this.pearlPos.get(uuid).size() > 2).forEach(uuid -> {
            GL11.glBegin((int)1);
            IntStream.range(1, this.pearlPos.get(uuid).size()).forEach(i -> {
                Color color = this.pearlColor.getValue();
                GL11.glColor3d((double)((float)color.getRed() / 255.0f), (double)((float)color.getGreen() / 255.0f), (double)((float)color.getBlue() / 255.0f));
                List<Vec3d> pos = this.pearlPos.get(uuid);
                GL11.glVertex3d((double)(pos.get((int)i).x - EntityTrails.mc.getRenderManager().viewerPosX), (double)(pos.get((int)i).y - EntityTrails.mc.getRenderManager().viewerPosY), (double)(pos.get((int)i).z - EntityTrails.mc.getRenderManager().viewerPosZ));
                GL11.glVertex3d((double)(pos.get((int)(i - 1)).x - EntityTrails.mc.getRenderManager().viewerPosX), (double)(pos.get((int)(i - 1)).y - EntityTrails.mc.getRenderManager().viewerPosY), (double)(pos.get((int)(i - 1)).z - EntityTrails.mc.getRenderManager().viewerPosZ));
            });
            GL11.glEnd();
        });
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    void drawTrail(ItemTrail trail) {
        Color fadeColor = this.endColor.getValue();
        RenderUtil.prepare();
        GL11.glLineWidth((float)this.lineWidth.getValue().floatValue());
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderUtil.builder = RenderUtil.tessellator2.getBuffer();
        RenderUtil.builder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        this.buildBuffer(RenderUtil.builder, trail, this.startColor.getValue(), this.fade.getValue() != false ? fadeColor : this.startColor.getValue());
        RenderUtil.tessellator2.draw();
        RenderUtil.release();
    }

    void buildBuffer(BufferBuilder builder, ItemTrail trail, Color start, Color end) {
        for (Position p : trail.positions) {
            Vec3d pos = RenderUtil.updateToCamera(p.pos);
            double value = this.normalize(trail.positions.indexOf(p), trail.positions.size());
            RenderUtil.addBuilderVertex(builder, pos.x, pos.y, pos.z, ColorUtil.interpolate((float)value, start, end));
        }
    }

    double normalize(double value, double max) {
        return (value - 0.0) / (max - 0.0);
    }

    static class Position {
        public Vec3d pos;
        public long time;

        public Position(Vec3d pos) {
            this.pos = pos;
            this.time = System.currentTimeMillis();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Position position = (Position)o;
            return this.time == position.time && Objects.equals((Object)this.pos, (Object)position.pos);
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.pos, this.time});
        }
    }

    static class ItemTrail {
        public Entity entity;
        public List<Position> positions;
        public Timer timer;

        ItemTrail(Entity entity) {
            this.entity = entity;
            this.positions = new ArrayList<Position>();
            this.timer = new Timer();
            this.timer.setDelay(1000L);
            this.timer.setPaused(true);
        }
    }
}

