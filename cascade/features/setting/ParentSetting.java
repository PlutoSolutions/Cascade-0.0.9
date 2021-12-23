/*
 * Decompiled with CFR 0.150.
 */
package cascade.features.setting;

import cascade.features.setting.Setting;
import java.util.ArrayList;
import java.util.function.Predicate;

public class ParentSetting
extends Setting<Boolean> {
    ArrayList<Setting> children = new ArrayList();

    public ParentSetting(String name) {
        super(name, false);
    }

    public ParentSetting(String name, Predicate<Boolean> visibility) {
        super(name, Boolean.valueOf(false), visibility);
    }

    public boolean isOpened() {
        return (Boolean)this.getValue();
    }

    public ArrayList<Setting> getChildren() {
        return this.children;
    }

    public void addChild(Setting setting) {
        this.children.add(setting);
    }

    public void setOpened(boolean value) {
        this.value = value;
    }
}

