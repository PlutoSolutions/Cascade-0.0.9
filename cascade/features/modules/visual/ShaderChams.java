/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.Vec3d
 */
package cascade.features.modules.visual;

import cascade.event.events.Render3DEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.MathUtil;
import cascade.util.shader.shaders.RainbowOutlineShader;
import java.util.Objects;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class ShaderChams
extends Module {
    public Setting<ShaderMode> mode = this.register(new Setting<ShaderMode>("Mode", ShaderMode.RainbowOutline));

    public ShaderChams() {
        super("ShaderChams", Module.Category.VISUAL, "Makes shader on cham");
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (ShaderChams.fullNullCheck()) {
            return;
        }
        RainbowOutlineShader framebufferShader = null;
        if (this.mode.getValue().equals((Object)ShaderMode.RainbowOutline)) {
            framebufferShader = RainbowOutlineShader.RAINBOW_OUTLINE_SHADER;
        }
        if (framebufferShader == null) {
            return;
        }
        GlStateManager.matrixMode((int)5889);
        GlStateManager.pushMatrix();
        GlStateManager.matrixMode((int)5888);
        GlStateManager.pushMatrix();
        framebufferShader.startDraw(event.getPartialTicks());
        for (Entity entity : ShaderChams.mc.world.loadedEntityList) {
            if (entity == ShaderChams.mc.player || entity == mc.getRenderViewEntity() || !(entity instanceof EntityPlayer)) continue;
            Vec3d vector = MathUtil.getInterpolatedRenderPos(entity, event.getPartialTicks());
            Objects.requireNonNull(mc.getRenderManager().getEntityRenderObject(entity)).doRender(entity, vector.x, vector.y, vector.z, entity.rotationYaw, event.getPartialTicks());
        }
        framebufferShader.stopDraw();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.matrixMode((int)5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode((int)5888);
        GlStateManager.popMatrix();
    }

    public static enum ShaderMode {
        RainbowOutline;

    }
}

