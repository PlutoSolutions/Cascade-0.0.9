/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.effect.EntityLightningBolt
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  sun.audio.AudioPlayer
 *  sun.audio.AudioStream
 */
package cascade.features.modules.misc;

import cascade.event.events.DeathEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import java.io.InputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class KillEffect
extends Module {
    public Setting<Boolean> lightning = this.register(new Setting<Boolean>("Lightning", true));
    public Setting<Sounds> sound = this.register(new Setting<Sounds>("Sound", Sounds.Lightning, v -> this.lightning.getValue()));
    int ticks;

    public KillEffect() {
        super("KillEffect", Module.Category.MISC, "Renders effects when someone dies");
    }

    @Override
    public void onTick() {
        if (this.ticks < 20) {
            ++this.ticks;
        }
    }

    @SubscribeEvent
    public void onDeath(DeathEvent e) {
        if (KillEffect.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (this.lightning.getValue().booleanValue() && this.ticks == 20) {
            EntityLightningBolt bolt = new EntityLightningBolt((World)KillEffect.mc.world, e.player.posX, e.player.posY, e.player.posZ, false);
            switch (this.sound.getValue()) {
                case Lightning: {
                    KillEffect.mc.world.playSound(e.player.getPosition(), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER, 1.0f, 1.0f, false);
                    break;
                }
                case Custom: {
                    this.playSound(new ResourceLocation("textures/cykablyat.wav"));
                    break;
                }
            }
            bolt.setLocationAndAngles(e.player.posX, e.player.posY, e.player.posZ, KillEffect.mc.player.rotationYaw, KillEffect.mc.player.rotationPitch);
            KillEffect.mc.world.spawnEntity((Entity)bolt);
            this.ticks = 0;
        }
    }

    public void playSound(ResourceLocation resourceLocation) {
        try {
            InputStream sound = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
            AudioStream audioStream = new AudioStream(sound);
            AudioPlayer.player.start((InputStream)audioStream);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static enum Sounds {
        Lightning,
        Custom,
        None;

    }
}

