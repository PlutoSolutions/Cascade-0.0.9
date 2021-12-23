/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderItem
 *  net.minecraft.client.renderer.block.model.IBakedModel
 *  net.minecraft.client.renderer.block.model.ItemCameraTransforms$TransformType
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 */
package cascade.mixin.mixins;

import cascade.event.events.RenderItemEvent;
import cascade.features.modules.visual.ViewMod;
import java.awt.Color;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderItem.class})
public abstract class MixinRenderItem {
    @Shadow
    private void renderModel(IBakedModel model, int color, ItemStack stack) {
    }

    @Inject(method={"renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V"}, at={@At(value="INVOKE")})
    public void renderItem(ItemStack stack, EntityLivingBase entityLivingBaseIn, ItemCameraTransforms.TransformType transform, boolean leftHanded, CallbackInfo ci) {
        if (transform == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND || transform == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
            if (transform.equals((Object)ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND)) {
                MinecraftForge.EVENT_BUS.post((Event)new RenderItemEvent.Offhand(stack));
            } else {
                MinecraftForge.EVENT_BUS.post((Event)new RenderItemEvent.MainHand(stack));
            }
        }
    }

    @Redirect(method={"renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/RenderItem;renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/item/ItemStack;)V"))
    private void renderModelColor(RenderItem renderItem, IBakedModel model, ItemStack stack) {
        this.renderModel(model, new Color(1.0f, 1.0f, 1.0f, ViewMod.getInstance().isEnabled() ? ViewMod.getInstance().a.getValue().floatValue() / 255.0f : 1.0f).getRGB(), stack);
    }
}

