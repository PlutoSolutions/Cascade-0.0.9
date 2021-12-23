/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBow
 *  net.minecraft.item.ItemEgg
 *  net.minecraft.item.ItemEnderPearl
 *  net.minecraft.item.ItemExpBottle
 *  net.minecraft.item.ItemFishingRod
 *  net.minecraft.item.ItemLingeringPotion
 *  net.minecraft.item.ItemSnowball
 *  net.minecraft.item.ItemSplashPotion
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.util.glu.Cylinder
 */
package cascade.features.modules.visual;

import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemLingeringPotion;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemSplashPotion;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

public class Trajectories
extends Module {
    public Setting<Color> c = this.register(new Setting<Color>("Color", new Color(-1)));

    public Trajectories() {
        super("Trajectories", Module.Category.VISUAL, "draws trajectories for projectiles");
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent renderEvent) {
        float pow;
        if (Trajectories.fullNullCheck() || this.isDisabled()) {
            return;
        }
        double renderPosX = Trajectories.mc.player.lastTickPosX + (Trajectories.mc.player.posX - Trajectories.mc.player.lastTickPosX) * (double)renderEvent.getPartialTicks();
        double renderPosY = Trajectories.mc.player.lastTickPosY + (Trajectories.mc.player.posY - Trajectories.mc.player.lastTickPosY) * (double)renderEvent.getPartialTicks();
        double renderPosZ = Trajectories.mc.player.lastTickPosZ + (Trajectories.mc.player.posZ - Trajectories.mc.player.lastTickPosZ) * (double)renderEvent.getPartialTicks();
        Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND);
        if (Trajectories.mc.gameSettings.thirdPersonView != 0) {
            return;
        }
        if (!(Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBow) && !(Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemFishingRod) && !(Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemEnderPearl) && !(Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemEgg) && !(Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSnowball) && !(Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemExpBottle) || Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemLingeringPotion || Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSplashPotion) {
            return;
        }
        GL11.glPushMatrix();
        Item item = Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem();
        double posX = renderPosX - (double)(MathHelper.cos((float)(Trajectories.mc.player.rotationYaw / 180.0f * (float)Math.PI)) * 0.16f);
        double posY = renderPosY + (double)Trajectories.mc.player.getEyeHeight() - 0.1000000014901161;
        double posZ = renderPosZ - (double)(MathHelper.sin((float)(Trajectories.mc.player.rotationYaw / 180.0f * (float)Math.PI)) * 0.16f);
        double motionX = (double)(-MathHelper.sin((float)(Trajectories.mc.player.rotationYaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(Trajectories.mc.player.rotationPitch / 180.0f * (float)Math.PI))) * (item instanceof ItemBow ? 1.0 : 0.4);
        double motionY = (double)(-MathHelper.sin((float)(Trajectories.mc.player.rotationPitch / 180.0f * (float)Math.PI))) * (item instanceof ItemBow ? 1.0 : 0.4);
        double motionZ = (double)(MathHelper.cos((float)(Trajectories.mc.player.rotationYaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(Trajectories.mc.player.rotationPitch / 180.0f * (float)Math.PI))) * (item instanceof ItemBow ? 1.0 : 0.4);
        int useCount = 72000 - Trajectories.mc.player.getItemInUseCount();
        float power = (float)useCount / 20.0f;
        power = (power * power + power * 2.0f) / 3.0f;
        if (power > 1.0f) {
            power = 1.0f;
        }
        float distance = MathHelper.sqrt((double)(motionX * motionX + motionY * motionY + motionZ * motionZ));
        motionX /= (double)distance;
        motionY /= (double)distance;
        motionZ /= (double)distance;
        float f = item instanceof ItemBow ? power * 2.0f : (item instanceof ItemFishingRod ? 1.25f : (pow = Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.EXPERIENCE_BOTTLE ? 0.9f : 1.0f));
        motionX *= (double)(pow * (item instanceof ItemFishingRod ? 0.75f : (Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.EXPERIENCE_BOTTLE ? 0.75f : 1.5f)));
        motionY *= (double)(pow * (item instanceof ItemFishingRod ? 0.75f : (Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.EXPERIENCE_BOTTLE ? 0.75f : 1.5f)));
        motionZ *= (double)(pow * (item instanceof ItemFishingRod ? 0.75f : (Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.EXPERIENCE_BOTTLE ? 0.75f : 1.5f)));
        GL11.glDisable((int)3008);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glEnable((int)2884);
        Trajectories.mc.entityRenderer.disableLightmap();
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glHint((int)3155, (int)4354);
        GL11.glLineWidth((float)2.0f);
        GlStateManager.color((float)((float)this.c.getValue().getRed() / 255.0f), (float)((float)this.c.getValue().getGreen() / 255.0f), (float)((float)this.c.getValue().getBlue() / 255.0f), (float)((float)this.c.getValue().getAlpha() / 255.0f));
        GL11.glEnable((int)2848);
        float size = (float)(item instanceof ItemBow ? 0.3 : 0.25);
        boolean hasLanded = false;
        Entity landingOnEntity = null;
        RayTraceResult landingPosition = null;
        while (!hasLanded && posY > 0.0) {
            Vec3d present = new Vec3d(posX, posY, posZ);
            Vec3d future = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            RayTraceResult possibleLandingStrip = Trajectories.mc.world.rayTraceBlocks(present, future, false, true, false);
            if (possibleLandingStrip != null && possibleLandingStrip.typeOfHit != RayTraceResult.Type.MISS) {
                landingPosition = possibleLandingStrip;
                hasLanded = true;
            }
            AxisAlignedBB arrowBox = new AxisAlignedBB(posX - (double)size, posY - (double)size, posZ - (double)size, posX + (double)size, posY + (double)size, posZ + (double)size);
            List<Entity> entities = this.getEntitiesWithinAABB(arrowBox.offset(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0));
            for (Entity entity : entities) {
                if (!entity.canBeCollidedWith() || entity == Trajectories.mc.player) continue;
                float var7 = 0.3f;
                AxisAlignedBB var8 = entity.getEntityBoundingBox().expand((double)var7, (double)var7, (double)var7);
                RayTraceResult possibleEntityLanding = var8.calculateIntercept(present, future);
                if (possibleEntityLanding == null) continue;
                hasLanded = true;
                landingOnEntity = entity;
                landingPosition = possibleEntityLanding;
            }
            if (landingOnEntity != null) {
                GlStateManager.color((float)((float)this.c.getValue().getRed() / 255.0f), (float)((float)this.c.getValue().getGreen() / 255.0f), (float)((float)this.c.getValue().getBlue() / 255.0f), (float)((float)this.c.getValue().getAlpha() / 255.0f));
            }
            float motionAdjustment = 0.99f;
            motionY *= (double)motionAdjustment;
            this.drawLine3D((posX += (motionX *= (double)motionAdjustment)) - renderPosX, (posY += (motionY -= item instanceof ItemBow ? 0.05 : 0.03)) - renderPosY, (posZ += (motionZ *= (double)motionAdjustment)) - renderPosZ);
        }
        if (landingPosition != null && landingPosition.typeOfHit == RayTraceResult.Type.BLOCK) {
            GlStateManager.translate((double)(posX - renderPosX), (double)(posY - renderPosY), (double)(posZ - renderPosZ));
            int side = landingPosition.sideHit.getIndex();
            if (side == 2) {
                GlStateManager.rotate((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            } else if (side == 3) {
                GlStateManager.rotate((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            } else if (side == 4) {
                GlStateManager.rotate((float)90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            } else if (side == 5) {
                GlStateManager.rotate((float)90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            }
            Cylinder cyl = new Cylinder();
            GlStateManager.rotate((float)-90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            cyl.setDrawStyle(100011);
            if (landingOnEntity != null) {
                GlStateManager.color((float)((float)this.c.getValue().getRed() / 255.0f), (float)((float)this.c.getValue().getGreen() / 255.0f), (float)((float)this.c.getValue().getBlue() / 255.0f), (float)((float)this.c.getValue().getAlpha() / 255.0f));
                GL11.glLineWidth((float)2.5f);
                cyl.draw(0.6f, 0.3f, 0.0f, 4, 1);
                GL11.glLineWidth((float)0.1f);
                GlStateManager.color((float)((float)this.c.getValue().getRed() / 255.0f), (float)((float)this.c.getValue().getGreen() / 255.0f), (float)((float)this.c.getValue().getBlue() / 255.0f), (float)((float)this.c.getValue().getAlpha() / 255.0f));
            }
            cyl.draw(0.6f, 0.3f, 0.0f, 4, 1);
        }
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)3008);
        GL11.glDepthMask((boolean)true);
        GL11.glCullFace((int)1029);
        GL11.glDisable((int)2848);
        GL11.glHint((int)3154, (int)4352);
        GL11.glHint((int)3155, (int)4352);
        GL11.glPopMatrix();
    }

    public void drawLine3D(double var1, double var2, double var3) {
        GL11.glVertex3d((double)var1, (double)var2, (double)var3);
    }

    List<Entity> getEntitiesWithinAABB(AxisAlignedBB bb) {
        ArrayList<Entity> list = new ArrayList<Entity>();
        int chunkMinX = MathHelper.floor((double)((bb.minX - 2.0) / 16.0));
        int chunkMaxX = MathHelper.floor((double)((bb.maxX + 2.0) / 16.0));
        int chunkMinZ = MathHelper.floor((double)((bb.minZ - 2.0) / 16.0));
        int chunkMaxZ = MathHelper.floor((double)((bb.maxZ + 2.0) / 16.0));
        for (int x = chunkMinX; x <= chunkMaxX; ++x) {
            for (int z = chunkMinZ; z <= chunkMaxZ; ++z) {
                if (Trajectories.mc.world.getChunkProvider().getLoadedChunk(x, z) == null) continue;
                Trajectories.mc.world.getChunk(x, z).getEntitiesWithinAABBForEntity((Entity)Trajectories.mc.player, bb, list, null);
            }
        }
        return list;
    }
}

