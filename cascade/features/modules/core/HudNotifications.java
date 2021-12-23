/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.core;

import cascade.event.events.ModuleToggleEvent;
import cascade.event.events.Render2DEvent;
import cascade.features.modules.Module;
import cascade.features.notifications.Notification;
import cascade.features.notifications.NotificationOffsetType;
import cascade.features.notifications.NotificationToggleType;
import cascade.features.setting.Setting;
import java.awt.Color;
import java.util.ArrayList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HudNotifications
extends Module {
    public ArrayList<Notification> notifications;
    public static HudNotifications INSTANCE = new HudNotifications();
    public int y = 0;
    public Setting<Color> backgroundColor = this.register(new Setting<Color>("BackgroundColor", new Color(37, 37, 37, 255)));
    public Setting<Boolean> rainbowOutline = this.register(new Setting<Boolean>("RainbowOutline", false));
    public Setting<Color> outlineColor = this.register(new Setting<Color>("OutlineColor", new Color(20, 20, 20, 255), v -> this.rainbowOutline.getValue() == false));
    public Setting<Integer> yOffset = this.register(new Setting<Integer>("yOffset", 400, 0, 500));
    public Setting<Integer> height = this.register(new Setting<Integer>("Height", 20, 0, 100));
    public Setting<Integer> animationSpeed = this.register(new Setting<Integer>("AnimationSpeed", 2, 0, 10));
    public Setting<Integer> staticTicks = this.register(new Setting<Integer>("StaticTicks", 50, 0, 100));
    public Setting<Integer> startX = this.register(new Setting<Integer>("StartX", -100, -200, 200));
    public Setting<Integer> targetX = this.register(new Setting<Integer>("TargetX", 0, -200, 200));
    public Setting<NotificationOffsetType> offsetType = this.register(new Setting<NotificationOffsetType>("OffsetType", NotificationOffsetType.Left));

    public HudNotifications() {
        super("HudNotifications", Module.Category.CORE, "");
        this.notifications = new ArrayList();
        this.setInstance();
    }

    public static HudNotifications getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HudNotifications();
        }
        return INSTANCE;
    }

    void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onModuleEnable(ModuleToggleEvent.Enable event) {
        if (this.megaNullCheck()) {
            return;
        }
        this.notifications.clear();
        this.notifications.add(new Notification(event.getModule(), this.backgroundColor.getValue(), this.outlineColor.getValue(), this.offsetType.getValue(), NotificationToggleType.Enable, this.yOffset.getValue(), this.height.getValue(), this.startX.getValue(), this.targetX.getValue(), this.animationSpeed.getValue(), this.staticTicks.getValue()));
    }

    @SubscribeEvent
    public void onModuleDisable(ModuleToggleEvent.Disable event) {
        if (this.megaNullCheck()) {
            return;
        }
        this.notifications.clear();
        this.notifications.add(new Notification(event.getModule(), this.backgroundColor.getValue(), this.outlineColor.getValue(), this.offsetType.getValue(), NotificationToggleType.Disable, this.yOffset.getValue(), this.height.getValue(), this.startX.getValue(), this.targetX.getValue(), this.animationSpeed.getValue(), this.staticTicks.getValue()));
    }

    @Override
    public void onTick() {
        for (int i = 0; i < this.notifications.size(); ++i) {
            this.notifications.get(i).onTick();
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        for (int i = 0; i < this.notifications.size(); ++i) {
            this.notifications.get(i).drawNotification();
        }
    }
}

