/*
 * Decompiled with CFR 0.150.
 */
package cascade.features.modules.visual;

import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import java.awt.Color;

public class HandChams
extends Module {
    public Setting<RenderMode> mode = this.register(new Setting<RenderMode>("Mode", RenderMode.Wireframe));
    public Setting<Color> c = this.register(new Setting<Color>("Color", new Color(-1)));
    private static HandChams INSTANCE;

    public HandChams() {
        super("HandChams", Module.Category.VISUAL, "Changes the look of ur hand");
        this.setInstance();
    }

    public static HandChams getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new HandChams();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static enum RenderMode {
        Solid,
        Wireframe;

    }
}

