/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 */
package cascade.features.modules.misc;

import cascade.event.events.Render2DEvent;
import cascade.features.modules.Module;
import cascade.features.modules.combat.Aura;
import cascade.features.modules.combat.AutoCrystal;
import cascade.features.modules.core.ClickGui;
import cascade.features.setting.Setting;
import cascade.util.ColorUtil;
import cascade.util.CombatUtil;
import cascade.util.EntityUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class Target
extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Arraylist));
    public Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(80.0f), Float.valueOf(0.1f), Float.valueOf(150.0f)));
    public Setting<Boolean> name = this.register(new Setting<Boolean>("Name", true));
    public Setting<Boolean> distance = this.register(new Setting<Boolean>("Distance", true));
    public Setting<Boolean> health = this.register(new Setting<Boolean>("Health", true));
    public Setting<Integer> offsetY = this.register(new Setting<Object>("OffsetY", Integer.valueOf(4), Integer.valueOf(0), Integer.valueOf(250), v -> this.mode.getValue() == Mode.HUD));
    public EntityLivingBase trgt;
    private String targetName;
    private String targetDistance;
    private String targetHealth;

    public Target() {
        super("Target", Module.Category.MISC, "Displays info about ur target");
    }

    @Override
    public void onToggle() {
        this.trgt = null;
        this.targetName = null;
        this.targetDistance = null;
        this.targetHealth = null;
    }

    @Override
    public void onUpdate() {
        if (Target.fullNullCheck()) {
            return;
        }
        if (AutoCrystal.getInstance().target != null) {
            this.trgt = AutoCrystal.getInstance().target;
        }
        Aura.getInstance();
        if (Aura.target != null) {
            Aura.getInstance();
            this.trgt = (EntityLivingBase)Aura.target;
        }
        if (AutoCrystal.getInstance().target == null) {
            Aura.getInstance();
            if (Aura.target == null) {
                this.trgt = CombatUtil.getTarget(this.range.getValue().floatValue());
            }
        }
        if (this.name.getValue().booleanValue() && this.trgt != null) {
            if (AutoCrystal.getInstance().target == this.trgt) {
                this.targetName = (Object)ChatFormatting.DARK_PURPLE + this.trgt.getName() + (Object)ChatFormatting.RESET;
            }
            Aura.getInstance();
            if (Aura.target == this.trgt) {
                this.targetName = (Object)ChatFormatting.BLUE + this.trgt.getName() + (Object)ChatFormatting.RESET;
            }
            if (AutoCrystal.getInstance().target != this.trgt) {
                Aura.getInstance();
                if (Aura.target != this.trgt) {
                    this.targetName = (Object)ChatFormatting.WHITE + this.trgt.getName() + (Object)ChatFormatting.RESET;
                }
            }
        }
        if (this.distance.getValue().booleanValue() && this.trgt != null) {
            float range = Target.mc.player.getDistance((Entity)this.trgt);
            if (range < 15.0f) {
                this.targetDistance = (Object)ChatFormatting.RED + String.format("%.1f", Float.valueOf(range)) + (Object)ChatFormatting.RESET;
            }
            if (range >= 15.0f && range < 22.0f) {
                this.targetDistance = (Object)ChatFormatting.YELLOW + String.format("%.1f", Float.valueOf(range)) + (Object)ChatFormatting.RESET;
            }
            if (range >= 22.0f) {
                this.targetDistance = (Object)ChatFormatting.RED + String.format("%.1f", Float.valueOf(range)) + (Object)ChatFormatting.RESET;
            }
        }
        if (this.health.getValue().booleanValue() && this.trgt != null) {
            float hp = EntityUtil.getHealth((Entity)this.trgt);
            if (hp < 12.0f) {
                this.targetHealth = (Object)ChatFormatting.RED + String.format("%.1f", Float.valueOf(hp)) + (Object)ChatFormatting.RESET;
            }
            if (hp >= 12.0f && hp < 18.0f) {
                this.targetHealth = (Object)ChatFormatting.YELLOW + String.format("%.1f", Float.valueOf(hp)) + (Object)ChatFormatting.RESET;
            }
            if (hp >= 18.0f) {
                this.targetHealth = (Object)ChatFormatting.GREEN + String.format("%.1f", Float.valueOf(hp)) + (Object)ChatFormatting.RESET;
            }
        }
    }

    @Override
    public String getDisplayInfo() {
        if (this.trgt != null && this.mode.getValue() == Mode.Arraylist) {
            return (this.name.getValue() != false ? this.targetName + ", " : "") + (this.distance.getValue() != false ? this.targetDistance + ", " : "") + (this.health.getValue() != false ? this.targetHealth : "");
        }
        return null;
    }

    @Override
    public void onRender2D(Render2DEvent e) {
        if (Target.fullNullCheck()) {
            return;
        }
        if (this.mode.getValue() == Mode.HUD && this.trgt != null) {
            int color = ColorUtil.toRGBA(ClickGui.getInstance().c.getValue().getRed(), ClickGui.getInstance().c.getValue().getGreen(), ClickGui.getInstance().c.getValue().getBlue());
            String string = (this.name.getValue() != false ? this.targetName + ", " : "") + (this.distance.getValue() != false ? this.targetDistance + ", " : "") + (this.health.getValue() != false ? this.targetHealth : "");
            if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(string, 2.0f, this.offsetY.getValue().intValue(), ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                } else {
                    int[] arrayOfInt = new int[]{1};
                    char[] stringToCharArray = string.toCharArray();
                    float f = 0.0f;
                    for (char c : stringToCharArray) {
                        this.renderer.drawString(String.valueOf(c), 2.0f + f, this.offsetY.getValue().intValue(), ColorUtil.rainbow(arrayOfInt[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                        f += (float)this.renderer.getStringWidth(String.valueOf(c));
                        arrayOfInt[0] = arrayOfInt[0] + 1;
                    }
                }
            } else {
                this.renderer.drawString(string, 2.0f, this.offsetY.getValue().intValue(), color, true);
            }
        }
    }

    static enum Mode {
        Arraylist,
        HUD;

    }
}

