/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 */
package cascade.features.notifications;

import cascade.Mod;
import cascade.features.modules.Module;
import cascade.features.modules.core.HudNotifications;
import cascade.features.notifications.NotificationOffsetType;
import cascade.features.notifications.NotificationToggleType;
import cascade.util.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;

public class Notification {
    Module module;
    Color backgroundColor;
    Color color;
    Color outlineColor;
    NotificationOffsetType notificationsOffsetType;
    NotificationToggleType notificationToggleType;
    int y;
    int height;
    int waitTime;
    int animSpeed;
    int currX;
    int startX;
    int targetX;
    int sX;
    int tX;
    boolean reachedTarget;
    int currWait;
    boolean waitedFully;
    boolean isFullyDone;
    String notificationString;

    public Notification(Module module, Color backgroundColor, Color outlineColor, NotificationOffsetType notificationOffsetType, NotificationToggleType notificationToggleType, int y, int height, int sX, int tX, int animSpeed, int waitTime) {
        this.module = module;
        this.backgroundColor = backgroundColor;
        this.outlineColor = outlineColor;
        this.notificationsOffsetType = notificationOffsetType;
        this.notificationToggleType = notificationToggleType;
        this.y = y;
        this.sX = sX;
        this.tX = tX;
        this.height = height;
        this.waitTime = waitTime;
        this.animSpeed = animSpeed;
        this.notificationString = module.getName() + (notificationToggleType.equals((Object)NotificationToggleType.Enable) ? " has been" + (Object)ChatFormatting.GREEN + " Enabled." : " has been" + (Object)ChatFormatting.RED + " Disabled.");
        this.setup();
    }

    public void setup() {
        if (this.notificationsOffsetType.equals((Object)NotificationOffsetType.Left)) {
            this.startX = this.tX;
            this.targetX = 100 + this.tX;
        } else if (this.notificationsOffsetType.equals((Object)NotificationOffsetType.Right)) {
            this.startX = 900 + this.sX;
            this.targetX = 800 + this.tX;
        }
        this.currX = this.startX;
    }

    public void onTick() {
        if (this.reachedTarget) {
            if (this.currWait < this.waitTime) {
                ++this.currWait;
            }
            if (this.currWait == this.waitTime) {
                this.waitedFully = true;
                this.targetX = this.notificationsOffsetType.equals((Object)NotificationOffsetType.Left) ? -100 : 1100;
            }
        }
        if (this.isFullyDone()) {
            HudNotifications.getInstance().notifications.remove(this);
        }
    }

    public void drawNotification() {
        if (this.notificationsOffsetType.equals((Object)NotificationOffsetType.Left)) {
            if (!this.reachedTarget) {
                if (this.currX < this.targetX) {
                    this.currX += this.animSpeed;
                }
                if (this.currX > this.targetX) {
                    this.currX = this.targetX;
                }
            }
            if (this.waitedFully) {
                this.targetX = -100;
                if (this.currX > this.targetX) {
                    this.currX -= this.animSpeed;
                }
                if (this.currX < this.targetX) {
                    this.currX = this.targetX;
                }
                if (this.currX == this.targetX) {
                    this.isFullyDone = true;
                }
            }
        } else if (this.notificationsOffsetType.equals((Object)NotificationOffsetType.Right)) {
            if (!this.reachedTarget) {
                if (this.currX > this.targetX) {
                    this.currX -= this.animSpeed;
                }
                if (this.currX < this.targetX) {
                    this.currX = this.targetX;
                }
            }
            if (this.waitedFully) {
                this.targetX = 1100;
                if (this.currX < this.targetX) {
                    this.currX += this.animSpeed;
                }
                if (this.currX > this.targetX) {
                    this.currX = this.targetX;
                }
                if (this.currX == this.targetX) {
                    this.isFullyDone = true;
                }
            }
        }
        if (this.currX == this.targetX) {
            this.reachedTarget = true;
        }
        RenderUtil.drawRect(this.currX, this.y, this.currX + Mod.textManager.getStringWidth(this.notificationString) + 4, this.y + this.height, this.backgroundColor.getRGB());
        if (HudNotifications.getInstance().rainbowOutline.getValue().booleanValue()) {
            RenderUtil.drawGradientRainbowOutLine(this.currX, this.y, this.currX + Mod.textManager.getStringWidth(this.notificationString) + 4, this.y + this.height, 1.0f);
        } else {
            RenderUtil.drawOutlineRect(this.currX, this.y, this.currX + Mod.textManager.getStringWidth(this.notificationString) + 4, this.y + this.height, this.outlineColor, 1.0f);
        }
        Mod.mc.fontRenderer.drawStringWithShadow(this.notificationString, (float)(this.currX + 2), (float)this.y + (float)this.height / 2.0f - (float)Mod.mc.fontRenderer.FONT_HEIGHT / 2.0f, -1);
    }

    public boolean isFullyDone() {
        return this.isFullyDone;
    }
}

