/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.FontRenderer
 */
package cascade.mixin.mixins;

import cascade.Mod;
import cascade.features.modules.core.FontMod;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={FontRenderer.class})
public abstract class MixinFontRenderer {
    @Shadow
    protected abstract int renderString(String var1, float var2, float var3, int var4, boolean var5);

    @Shadow
    protected abstract void renderStringAtPos(String var1, boolean var2);

    @Inject(method={"drawString(Ljava/lang/String;FFIZ)I"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderStringHook(String text, float x, float y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> info) {
        if (FontMod.getInstance().isEnabled() && FontMod.getInstance().customAll.getValue().booleanValue() && Mod.textManager != null) {
            float result = Mod.textManager.drawFontString(text, x, y, color, dropShadow);
            info.setReturnValue((int)result);
        }
    }
}

