/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package cascade.event.events;

import cascade.event.EventStage;
import net.minecraft.entity.player.EntityPlayer;

public class DeathEvent
extends EventStage {
    public EntityPlayer player;

    public DeathEvent(EntityPlayer player) {
        this.player = player;
    }
}

