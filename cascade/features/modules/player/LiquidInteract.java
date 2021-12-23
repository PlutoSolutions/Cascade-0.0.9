/*
 * Decompiled with CFR 0.150.
 */
package cascade.features.modules.player;

import cascade.features.modules.Module;

public class LiquidInteract
extends Module {
    private static LiquidInteract INSTANCE;

    public LiquidInteract() {
        super("LiquidInteract", Module.Category.PLAYER, "lets u interact with ");
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static LiquidInteract getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LiquidInteract();
        }
        return INSTANCE;
    }
}

