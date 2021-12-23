/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.misc;

import cascade.event.events.DeathEvent;
import cascade.event.events.TotemPopEvent;
import cascade.features.command.Command;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.TextUtil;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Notifications
extends Module {
    public Setting<Boolean> pop = this.register(new Setting<Boolean>("Pop", true));
    public Setting<Boolean> selfPop = this.register(new Setting<Object>("SelfPop", Boolean.valueOf(true), v -> this.pop.getValue()));
    public Setting<Nigger> popMode = this.register(new Setting<Object>("PopMode", (Object)Nigger.Count, v -> this.pop.getValue()));
    public Setting<TextUtil.Color> color1 = this.register(new Setting<Object>("Color1", (Object)TextUtil.Color.RED, v -> this.pop.getValue()));
    public Setting<TextUtil.Color> color2 = this.register(new Setting<Object>("Color2", (Object)TextUtil.Color.GREEN, v -> this.pop.getValue()));
    public static HashMap<String, Integer> TotemPopContainer = new HashMap();
    private static Notifications INSTANCE = new Notifications();

    public Notifications() {
        super("Notifications", Module.Category.MISC, "Notifies u in chat when an event happens");
        this.setInstance();
    }

    public static Notifications getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Notifications();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        TotemPopContainer.clear();
    }

    @SubscribeEvent
    public void onDeath(DeathEvent event) {
        if (!this.isEnabled() || Notifications.fullNullCheck()) {
            return;
        }
        EntityPlayer player = event.player;
        if (TotemPopContainer.containsKey(player.getName())) {
            int l_Count = TotemPopContainer.get(player.getName());
            TotemPopContainer.remove(player.getName());
            int id = 0;
            for (char character : player.getName().toCharArray()) {
                id += character;
                id *= 10;
            }
            if (player != Notifications.mc.player) {
                if (this.popMode.getValue() == Nigger.Count) {
                    if (l_Count == 1) {
                        Command.sendRemovableMessage(TextUtil.coloredString(player.getName() + " died after popping ", this.color1.getValue()) + TextUtil.coloredString("" + l_Count, this.color2.getValue()) + TextUtil.coloredString(" Totems!", this.color1.getValue()), id);
                    } else {
                        Command.sendRemovableMessage(TextUtil.coloredString(player.getName() + " died after popping ", this.color1.getValue()) + TextUtil.coloredString("" + l_Count, this.color2.getValue()) + TextUtil.coloredString(" Totems!", this.color1.getValue()), id);
                    }
                }
                if (this.popMode.getValue() == Nigger.Name) {
                    if (l_Count == 1) {
                        Command.sendRemovableMessage(TextUtil.coloredString(player.getName(), this.color2.getValue()) + TextUtil.coloredString(" died after popping ", this.color1.getValue()) + TextUtil.coloredString("" + l_Count, this.color2.getValue()) + TextUtil.coloredString(" Totems!", this.color1.getValue()), id);
                    } else {
                        Command.sendRemovableMessage(TextUtil.coloredString(player.getName(), this.color2.getValue()) + TextUtil.coloredString(" died after popping ", this.color1.getValue()) + TextUtil.coloredString("" + l_Count, this.color2.getValue()) + TextUtil.coloredString(" Totems!", this.color1.getValue()), id);
                    }
                }
            } else {
                if (this.selfPop.getValue().booleanValue()) {
                    if (l_Count == 1) {
                        Command.sendRemovableMessage(TextUtil.coloredString("You died after popping ", this.color1.getValue()) + TextUtil.coloredString("" + l_Count, this.color2.getValue()) + TextUtil.coloredString(" Totems!", this.color1.getValue()), id);
                    } else {
                        Command.sendRemovableMessage(TextUtil.coloredString("You died after popping ", this.color1.getValue()) + TextUtil.coloredString("" + l_Count, this.color2.getValue()) + TextUtil.coloredString(" Totems!", this.color1.getValue()), id);
                    }
                }
                if (this.popMode.getValue() == Nigger.Name) {
                    if (l_Count == 1) {
                        Command.sendRemovableMessage(TextUtil.coloredString("You", this.color2.getValue()) + TextUtil.coloredString(" died after popping ", this.color1.getValue()) + TextUtil.coloredString("" + l_Count, this.color2.getValue()) + TextUtil.coloredString(" Totems!", this.color1.getValue()), id);
                    } else {
                        Command.sendRemovableMessage(TextUtil.coloredString("You", this.color2.getValue()) + TextUtil.coloredString(" died after popping ", this.color1.getValue()) + TextUtil.coloredString("" + l_Count, this.color2.getValue()) + TextUtil.coloredString(" Totems!", this.color1.getValue()), id);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onTotemPop(TotemPopEvent event) {
        EntityPlayer player = event.getEntity();
        if (!this.isEnabled() || Notifications.fullNullCheck()) {
            return;
        }
        int l_Count = 1;
        if (TotemPopContainer.containsKey(player.getName())) {
            l_Count = TotemPopContainer.get(player.getName());
            TotemPopContainer.put(player.getName(), ++l_Count);
        } else {
            TotemPopContainer.put(player.getName(), l_Count);
        }
        int id = 0;
        for (char character : player.getName().toCharArray()) {
            id += character;
            id *= 10;
        }
        if (player != Notifications.mc.player) {
            if (this.popMode.getValue() == Nigger.Count) {
                if (l_Count == 1) {
                    Command.sendRemovableMessage(TextUtil.coloredString(player.getName() + " popped ", this.color1.getValue()) + TextUtil.coloredString("" + l_Count, this.color2.getValue()) + TextUtil.coloredString(" Totems.", this.color1.getValue()), id);
                } else {
                    Command.sendRemovableMessage(TextUtil.coloredString(player.getName() + " popped ", this.color1.getValue()) + TextUtil.coloredString("" + l_Count, this.color2.getValue()) + TextUtil.coloredString(" Totems.", this.color1.getValue()), id);
                }
            }
            if (this.popMode.getValue() == Nigger.Name) {
                if (l_Count == 1) {
                    Command.sendRemovableMessage(TextUtil.coloredString(player.getName(), this.color2.getValue()) + TextUtil.coloredString(" popped ", this.color1.getValue()) + TextUtil.coloredString("" + l_Count, this.color2.getValue()) + TextUtil.coloredString(" Totems.", this.color1.getValue()), id);
                } else {
                    Command.sendRemovableMessage(TextUtil.coloredString(player.getName(), this.color2.getValue()) + TextUtil.coloredString(" popped ", this.color1.getValue()) + TextUtil.coloredString("" + l_Count, this.color2.getValue()) + TextUtil.coloredString(" Totems.", this.color1.getValue()), id);
                }
            }
        } else if (this.selfPop.getValue().booleanValue()) {
            if (this.popMode.getValue() == Nigger.Count) {
                if (l_Count == 1) {
                    Command.sendRemovableMessage(TextUtil.coloredString("You popped ", this.color1.getValue()) + TextUtil.coloredString("" + l_Count, this.color2.getValue()) + TextUtil.coloredString(" Totems.", this.color1.getValue()), id);
                } else {
                    Command.sendRemovableMessage(TextUtil.coloredString("You popped ", this.color1.getValue()) + TextUtil.coloredString("" + l_Count, this.color2.getValue()) + TextUtil.coloredString(" Totems.", this.color1.getValue()), id);
                }
            }
            if (this.popMode.getValue() == Nigger.Name) {
                if (l_Count == 1) {
                    Command.sendRemovableMessage(TextUtil.coloredString("You", this.color2.getValue()) + TextUtil.coloredString(" popped ", this.color1.getValue()) + TextUtil.coloredString("" + l_Count, this.color2.getValue()) + TextUtil.coloredString(" Totems.", this.color1.getValue()), id);
                } else {
                    Command.sendRemovableMessage(TextUtil.coloredString("You", this.color2.getValue()) + TextUtil.coloredString(" popped ", this.color1.getValue()) + TextUtil.coloredString("" + l_Count, this.color2.getValue()) + TextUtil.coloredString(" Totems.", this.color1.getValue()), id);
                }
            }
        }
    }

    public static enum Nigger {
        Count,
        Name;

    }
}

