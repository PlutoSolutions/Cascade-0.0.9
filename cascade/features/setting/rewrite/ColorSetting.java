/*
 * Decompiled with CFR 0.150.
 */
package cascade.features.setting.rewrite;

import cascade.features.setting.rewrite.SettingAbstract;
import java.awt.Color;

public class ColorSetting
extends SettingAbstract {
    private Color color;

    public ColorSetting(String name, Color color) {
        super(name);
        this.color = color;
    }

    public ColorSetting(String name, Color color, Boolean ... visible) {
        super(name, visible);
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}

