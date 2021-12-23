/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.BufferBuilder
 */
package cascade.mixin.mixins;

import cascade.features.modules.visual.Wallhack;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={BufferBuilder.class})
public abstract class MixinBufferBuilder {
    @Redirect(method={"putColorMultiplier"}, at=@At(value="INVOKE", target="java/nio/IntBuffer.put(II)Ljava/nio/IntBuffer;", remap=false))
    private IntBuffer putColorMultiplier(IntBuffer buffer, int i, int j) {
        return buffer.put(i, Wallhack.INSTANCE.isEnabled() ? j & 0xFFFFFF | Wallhack.INSTANCE.opacity.getValue() << 24 : j);
    }
}

