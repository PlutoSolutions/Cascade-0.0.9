/*
 * Decompiled with CFR 0.150.
 */
package cascade.features.setting.rewrite;

public abstract class SettingAbstract {
    private final String name;
    private final Boolean[] visible;

    public SettingAbstract(String name) {
        this.name = name;
        this.visible = null;
    }

    public SettingAbstract(String name, Boolean ... visible) {
        this.name = name;
        this.visible = visible;
    }

    public String getName() {
        return this.name;
    }

    public boolean isVisible() {
        if (this.visible == null) {
            return true;
        }
        for (Boolean visible : this.visible) {
            if (visible.booleanValue()) continue;
            return false;
        }
        return true;
    }
}

