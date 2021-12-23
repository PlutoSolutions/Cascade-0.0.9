/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelEnderCrystal
 *  net.minecraft.client.renderer.entity.RenderEnderCrystal
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 */
package cascade.mixin.mixins;

import cascade.event.events.CrystalTextureEvent;
import cascade.event.events.RenderCrystalEvent;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderEnderCrystal.class})
public class MixinRenderEnderCrystal {
    @Shadow
    private ModelBase modelEnderCrystal = new ModelEnderCrystal(0.0f, true);
    @Shadow
    private ModelBase modelEnderCrystalNoBase = new ModelEnderCrystal(0.0f, false);

    @Redirect(method={"doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    private void doRender(ModelBase modelBase, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        RenderCrystalEvent.RenderCrystalPreEvent renderCrystalEvent = new RenderCrystalEvent.RenderCrystalPreEvent(modelBase, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        MinecraftForge.EVENT_BUS.post((Event)renderCrystalEvent);
        if (!renderCrystalEvent.isCanceled()) {
            this.modelEnderCrystalNoBase.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
        CrystalTextureEvent crystalTextureEvent = new CrystalTextureEvent();
        MinecraftForge.EVENT_BUS.post((Event)crystalTextureEvent);
    }

    @Inject(method={"doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V"}, at={@At(value="RETURN")}, cancellable=true)
    public void doRender(EntityEnderCrystal entityEnderCrystal, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        RenderCrystalEvent.RenderCrystalPostEvent renderCrystalEvent = new RenderCrystalEvent.RenderCrystalPostEvent(this.modelEnderCrystal, this.modelEnderCrystalNoBase, entityEnderCrystal, x, y, z, entityYaw, partialTicks);
        MinecraftForge.EVENT_BUS.post((Event)renderCrystalEvent);
        if (renderCrystalEvent.isCanceled()) {
            info.cancel();
        }
    }
}

