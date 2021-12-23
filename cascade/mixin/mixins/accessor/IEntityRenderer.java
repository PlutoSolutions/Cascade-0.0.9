/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.EntityRenderer
 */
package cascade.mixin.mixins.accessor;

import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={EntityRenderer.class})
public interface IEntityRenderer {
    @Invoker(value="setupCameraTransform")
    public void setupCameraTransformInvoker(float var1, int var2);
}

