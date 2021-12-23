/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.client.event.RenderPlayerEvent$Pre
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.visual;

import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import java.awt.Color;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Wireframe
extends Module {
    public Setting<RenderMode> pmode = this.register(new Setting<RenderMode>("Mode", RenderMode.Solid));
    public Setting<Boolean> pmod = this.register(new Setting<Boolean>("PlayerModel", false));
    public Setting<Color> c = this.register(new Setting<Color>("Color", new Color(-1)));
    public Setting<Float> plw = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(3.0f)));
    private static Wireframe INSTANCE;

    public Wireframe() {
        super("Wireframe", Module.Category.VISUAL, "Draws wireframe ESP around players");
        this.setInstance();
    }

    public static Wireframe getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new Wireframe();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRenderPlayerEvent(RenderPlayerEvent.Pre event) {
        event.getEntityPlayer().hurtTime = 0;
    }

    public static enum RenderMode {
        Solid,
        Wireframe;

    }
}

