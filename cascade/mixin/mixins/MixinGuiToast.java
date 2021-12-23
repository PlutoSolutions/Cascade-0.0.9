/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.gui.toasts.GuiToast
 */
package cascade.mixin.mixins;

import cascade.features.modules.visual.NoRender;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.toasts.GuiToast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GuiToast.class})
public class MixinGuiToast {
    @Inject(method={"drawToast"}, at={@At(value="HEAD")}, cancellable=true)
    public void drawToastHook(ScaledResolution resolution, CallbackInfo info) {
        if (NoRender.getInstance().isEnabled() && NoRender.getInstance().noAdvancements.getValue().booleanValue()) {
            info.cancel();
        }
    }
}

