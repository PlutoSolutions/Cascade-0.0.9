/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityOtherPlayerMP
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.GL11
 */
package cascade.features.modules.visual;

import cascade.event.events.Render3DEvent;
import cascade.event.events.TotemPopEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class PopESP
extends Module {
    private static PopESP INSTANCE = new PopESP();
    public EntityOtherPlayerMP fakeEntity;
    public Setting<Boolean> solidParent = this.register(new Setting<Boolean>("Solid", false));
    public Setting<Boolean> solidSetting = this.register(new Setting<Object>("RenderSolid", Boolean.valueOf(true), v -> this.solidParent.getValue()));
    public Setting<Float> red = this.register(new Setting<Float>("SolidRed", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(255.0f), v -> this.solidParent.getValue() != false && this.solidSetting.getValue() != false));
    public Setting<Float> green = this.register(new Setting<Float>("SolidGreen", Float.valueOf(255.0f), Float.valueOf(0.0f), Float.valueOf(255.0f), v -> this.solidParent.getValue() != false && this.solidSetting.getValue() != false));
    public Setting<Float> blue = this.register(new Setting<Float>("SolidBlue", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(255.0f), v -> this.solidParent.getValue() != false && this.solidSetting.getValue() != false));
    public Setting<Boolean> wireFrameParent = this.register(new Setting<Boolean>("WireFrame", false));
    public Setting<Boolean> wireFrameSetting = this.register(new Setting<Object>("RenderWire", Boolean.valueOf(true), v -> this.wireFrameParent.getValue()));
    public Setting<Float> wireRed = this.register(new Setting<Float>("WireRed", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(255.0f), v -> this.wireFrameParent.getValue() != false && this.wireFrameSetting.getValue() != false));
    public Setting<Float> wireGreen = this.register(new Setting<Float>("WireGreen", Float.valueOf(255.0f), Float.valueOf(0.0f), Float.valueOf(255.0f), v -> this.wireFrameParent.getValue() != false && this.wireFrameSetting.getValue() != false));
    public Setting<Float> wireBlue = this.register(new Setting<Float>("WireBlue", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(255.0f), v -> this.wireFrameParent.getValue() != false && this.wireFrameSetting.getValue() != false));
    public Setting<Boolean> fadeParent = this.register(new Setting<Boolean>("Fade", false));
    public Setting<Integer> startAlpha = this.register(new Setting<Integer>("StartAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.fadeParent.getValue()));
    public Setting<Integer> endAlpha = this.register(new Setting<Integer>("EndAlpha", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.fadeParent.getValue()));
    public Setting<Integer> fadeStep = this.register(new Setting<Integer>("FadeStep", Integer.valueOf(10), Integer.valueOf(10), Integer.valueOf(100), v -> this.fadeParent.getValue()));
    public Setting<Boolean> yTravelParent = this.register(new Setting<Boolean>("YMovement", false));
    public Setting<Boolean> yTravel = this.register(new Setting<Object>("YTravel", Boolean.valueOf(false), v -> this.yTravelParent.getValue()));
    public Setting<YTravelMode> yTravelMode = this.register(new Setting<Object>("TravelMode", (Object)YTravelMode.UP, v -> this.yTravelParent.getValue() != false && this.yTravel.getValue() != false));
    public Setting<Double> yTravelSpeed = this.register(new Setting<Double>("TravelSpeed", Double.valueOf(0.1), Double.valueOf(0.0), Double.valueOf(2.0), v -> this.yTravel.getValue()));
    public Setting<Boolean> miscParent = this.register(new Setting<Boolean>("Misc", false));
    public Setting<Boolean> onDeath = this.register(new Setting<Object>("OnDeath", Boolean.valueOf(false), v -> this.miscParent.getValue()));
    public Setting<Boolean> clearListOnPop = this.register(new Setting<Object>("ClearListOnPop", Boolean.valueOf(false), v -> this.miscParent.getValue()));
    public Setting<Boolean> clearListOnDeath = this.register(new Setting<Object>("ClearListOnDeath", Boolean.valueOf(false), v -> this.miscParent.getValue()));
    public Setting<Boolean> antiSelf = this.register(new Setting<Object>("AntiSelf", Boolean.valueOf(false), v -> this.miscParent.getValue()));
    public HashMap<EntityPlayer, Integer> poppedPlayers = new HashMap();

    public PopESP() {
        super("PopChams", Module.Category.VISUAL, "Sponsored by Mint");
        this.setInstance();
    }

    public static PopESP getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PopESP();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        for (Map.Entry<EntityPlayer, Integer> pop : this.poppedPlayers.entrySet()) {
            this.poppedPlayers.put(pop.getKey(), pop.getValue() - (this.fadeStep.getValue() + 10) / 20);
            if (pop.getValue() <= this.endAlpha.getValue()) {
                this.poppedPlayers.remove((Object)pop.getKey());
                return;
            }
            if (PopESP.getInstance().yTravel.getValue().booleanValue()) {
                if (PopESP.getInstance().yTravelMode.getValue() == YTravelMode.UP) {
                    pop.getKey().posY += PopESP.getInstance().yTravelSpeed.getValue() / 20.0;
                } else if (PopESP.getInstance().yTravelMode.getValue() == YTravelMode.DOWN) {
                    pop.getKey().posY -= PopESP.getInstance().yTravelSpeed.getValue() / 20.0;
                }
            }
            if (this.wireFrameSetting.getValue().booleanValue()) {
                GlStateManager.pushMatrix();
                GL11.glPushAttrib((int)1048575);
                GL11.glPolygonMode((int)1032, (int)6913);
                GL11.glDisable((int)3553);
                GL11.glDisable((int)2896);
                GL11.glDisable((int)2929);
                GL11.glEnable((int)2848);
                GL11.glEnable((int)3042);
                GL11.glBlendFunc((int)770, (int)771);
                GL11.glColor4f((float)(this.wireRed.getValue().floatValue() / 255.0f), (float)(this.wireGreen.getValue().floatValue() / 255.0f), (float)(this.wireBlue.getValue().floatValue() / 255.0f), (float)((float)pop.getValue().intValue() / 255.0f));
                this.renderEntityStatic((Entity)pop.getKey(), event.getPartialTicks(), false);
                GL11.glLineWidth((float)1.0f);
                GL11.glEnable((int)2896);
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }
            if (!this.solidSetting.getValue().booleanValue()) continue;
            GL11.glPushMatrix();
            GL11.glDepthRange((double)0.01, (double)1.0);
            GL11.glPushAttrib((int)-1);
            GL11.glEnable((int)3008);
            GL11.glDisable((int)3553);
            GL11.glEnable((int)3042);
            GL11.glDisable((int)2929);
            GL11.glDepthMask((boolean)false);
            GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
            GL11.glLineWidth((float)1.0f);
            GL11.glColor4f((float)(this.red.getValue().floatValue() / 255.0f), (float)(this.green.getValue().floatValue() / 255.0f), (float)(this.blue.getValue().floatValue() / 255.0f), (float)((float)pop.getValue().intValue() / 255.0f));
            this.renderEntityStatic((Entity)pop.getKey(), event.getPartialTicks(), false);
            GL11.glEnable((int)2929);
            GL11.glDepthMask((boolean)true);
            GL11.glDisable((int)3008);
            GL11.glEnable((int)3553);
            GL11.glDisable((int)3042);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GL11.glPopAttrib();
            GL11.glDepthRange((double)0.0, (double)1.0);
            GL11.glPopMatrix();
        }
    }

    @SubscribeEvent
    public void onPop(TotemPopEvent event) {
        if (PopESP.mc.world.getEntityByID(event.getEntity().entityId) != null) {
            if (this.antiSelf.getValue().booleanValue() && event.getEntity().entityId == PopESP.mc.player.getEntityId()) {
                return;
            }
            Entity entity = PopESP.mc.world.getEntityByID(event.getEntity().entityId);
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)entity;
                this.fakeEntity = new EntityOtherPlayerMP((World)PopESP.mc.world, player.getGameProfile());
                this.fakeEntity.copyLocationAndAnglesFrom((Entity)player);
                this.fakeEntity.rotationYawHead = player.rotationYawHead;
                this.fakeEntity.prevRotationYawHead = player.rotationYawHead;
                this.fakeEntity.rotationYaw = player.rotationYaw;
                this.fakeEntity.prevRotationYaw = player.rotationYaw;
                this.fakeEntity.rotationPitch = player.rotationPitch;
                this.fakeEntity.prevRotationPitch = player.rotationPitch;
                this.fakeEntity.cameraYaw = this.fakeEntity.rotationYaw;
                this.fakeEntity.cameraPitch = this.fakeEntity.rotationPitch;
                if (this.clearListOnPop.getValue().booleanValue()) {
                    this.poppedPlayers.clear();
                }
                this.poppedPlayers.put((EntityPlayer)this.fakeEntity, this.startAlpha.getValue());
            }
        }
    }

    public void onDeath(int entityId) {
        Entity entity;
        if (this.onDeath.getValue().booleanValue() && PopESP.mc.world.getEntityByID(entityId) != null && (entity = PopESP.mc.world.getEntityByID(entityId)) instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            this.fakeEntity = new EntityOtherPlayerMP((World)PopESP.mc.world, player.getGameProfile());
            this.fakeEntity.copyLocationAndAnglesFrom((Entity)player);
            this.fakeEntity.rotationYawHead = player.rotationYawHead;
            this.fakeEntity.prevRotationYawHead = player.rotationYawHead;
            this.fakeEntity.rotationYaw = player.rotationYaw;
            this.fakeEntity.prevRotationYaw = player.rotationYaw;
            this.fakeEntity.rotationPitch = player.rotationPitch;
            this.fakeEntity.prevRotationPitch = player.rotationPitch;
            this.fakeEntity.cameraYaw = this.fakeEntity.rotationYaw;
            this.fakeEntity.cameraPitch = this.fakeEntity.rotationPitch;
            if (this.clearListOnDeath.getValue().booleanValue()) {
                this.poppedPlayers.clear();
            }
            this.poppedPlayers.put((EntityPlayer)this.fakeEntity, this.startAlpha.getValue());
        }
    }

    public void handlePopESP(int entityId) {
        Entity entity;
        if (PopESP.mc.world.getEntityByID(entityId) != null && (entity = PopESP.mc.world.getEntityByID(entityId)) instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            this.fakeEntity = new EntityOtherPlayerMP((World)PopESP.mc.world, player.getGameProfile());
            this.fakeEntity.copyLocationAndAnglesFrom((Entity)player);
            this.fakeEntity.rotationYawHead = player.rotationYawHead;
            this.fakeEntity.prevRotationYawHead = player.rotationYawHead;
            this.fakeEntity.rotationYaw = player.rotationYaw;
            this.fakeEntity.prevRotationYaw = player.rotationYaw;
            this.fakeEntity.rotationPitch = player.rotationPitch;
            this.fakeEntity.prevRotationPitch = player.rotationPitch;
            this.fakeEntity.cameraYaw = this.fakeEntity.rotationYaw;
            this.fakeEntity.cameraPitch = this.fakeEntity.rotationPitch;
            if (this.clearListOnDeath.getValue().booleanValue()) {
                this.poppedPlayers.clear();
            }
            this.poppedPlayers.put((EntityPlayer)this.fakeEntity, this.startAlpha.getValue());
        }
    }

    public void renderEntityStatic(Entity entityIn, float partialTicks, boolean p_188388_3_) {
        if (entityIn.ticksExisted == 0) {
            entityIn.lastTickPosX = entityIn.posX;
            entityIn.lastTickPosY = entityIn.posY;
            entityIn.lastTickPosZ = entityIn.posZ;
        }
        double d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
        double d1 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
        double d2 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;
        float f = entityIn.prevRotationYaw + (entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks;
        int i = entityIn.getBrightnessForRender();
        mc.getRenderManager().renderEntity(entityIn, d0 - PopESP.mc.getRenderManager().viewerPosX, d1 - PopESP.mc.getRenderManager().viewerPosY, d2 - PopESP.mc.getRenderManager().viewerPosZ, f, partialTicks, p_188388_3_);
    }

    public static enum YTravelMode {
        UP,
        DOWN;

    }
}

