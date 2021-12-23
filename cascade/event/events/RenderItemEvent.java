/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package cascade.event.events;

import cascade.event.EventStage;
import net.minecraft.item.ItemStack;

public class RenderItemEvent
extends EventStage {
    ItemStack stack;

    public RenderItemEvent(ItemStack stack) {
        this.stack = stack;
    }

    public static class Offhand
    extends RenderItemEvent {
        public Offhand(ItemStack stack) {
            super(stack);
        }
    }

    public static class MainHand
    extends RenderItemEvent {
        public MainHand(ItemStack stack) {
            super(stack);
        }
    }
}

