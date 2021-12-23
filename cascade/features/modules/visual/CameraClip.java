/*
 * Decompiled with CFR 0.150.
 */
package cascade.features.modules.visual;

import cascade.features.modules.Module;
import cascade.features.setting.Setting;

public class CameraClip
extends Module {
    public Setting<Boolean> extend = this.register(new Setting<Boolean>("Extend", false));
    public Setting<Double> distance = this.register(new Setting<Object>("Distance", Double.valueOf(4.2), Double.valueOf(0.0), Double.valueOf(50.0), v -> this.extend.getValue()));
    private static CameraClip INSTANCE = new CameraClip();

    public CameraClip() {
        super("CameraClip", Module.Category.VISUAL, "yall know");
        this.setInstance();
    }

    public static CameraClip getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CameraClip();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}

