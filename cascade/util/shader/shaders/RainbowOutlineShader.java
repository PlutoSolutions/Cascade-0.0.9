/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.ScaledResolution
 *  org.lwjgl.opengl.GL20
 */
package cascade.util.shader.shaders;

import cascade.util.RenderUtil;
import cascade.util.shader.FramebufferShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;

public class RainbowOutlineShader
extends FramebufferShader {
    public static RainbowOutlineShader RAINBOW_OUTLINE_SHADER = new RainbowOutlineShader();
    public float time;

    public RainbowOutlineShader() {
        super("rainbowoutline.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform2f((int)this.getUniform("resolution"), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
        GL20.glUniform1f((int)this.getUniform("time"), (float)this.time);
        this.time += Float.intBitsToFloat(Float.floatToIntBits(1015.0615f) ^ 0x7F395856) * (float)RenderUtil.deltaTime;
    }
}

