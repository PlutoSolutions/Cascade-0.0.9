/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.passive.AbstractHorse
 */
package cascade.mixin.mixins;

import cascade.features.modules.player.EntityTweaks;
import net.minecraft.entity.passive.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={AbstractHorse.class})
public class MixinAbstractHorse {
    @Inject(method={"isHorseSaddled"}, at={@At(value="HEAD")}, cancellable=true)
    public void isHorseSaddled(CallbackInfoReturnable<Boolean> cir) {
        if (EntityTweaks.getInstance().isEnabled() && EntityTweaks.getInstance().control.getValue().booleanValue()) {
            cir.setReturnValue(true);
        }
    }
}

