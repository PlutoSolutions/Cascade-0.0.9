/*
 * Decompiled with CFR 0.150.
 */
package cascade.features.modules.misc;

import cascade.features.modules.Module;
import cascade.features.setting.Setting;

public class Sound
extends Module {
    public Setting<Boolean> mute = this.register(new Setting<Boolean>("Mute", false));

    public Sound() {
        super("Sound", Module.Category.MISC, "Tweaks sounds");
    }
}

