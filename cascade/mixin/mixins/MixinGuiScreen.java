/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.item.ItemShulkerBox
 *  net.minecraft.item.ItemStack
 */
package cascade.mixin.mixins;

import cascade.features.modules.visual.ViewTweaks;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GuiScreen.class})
public class MixinGuiScreen
extends Gui {
    @Inject(method={"renderToolTip"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderToolTipHook(ItemStack stack, int x, int y, CallbackInfo info) {
        if (ViewTweaks.getInstance().isEnabled() && ViewTweaks.getInstance().shulkerViewer.getValue().booleanValue() && stack.getItem() instanceof ItemShulkerBox) {
            ViewTweaks.getInstance().renderShulkerToolTip(stack, x, y, null);
            info.cancel();
        }
    }
}

