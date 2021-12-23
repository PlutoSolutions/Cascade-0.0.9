/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.movement;

import cascade.event.events.MoveEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Sprint
extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Legit));
    private static Sprint INSTANCE;

    public Sprint() {
        super("Sprint", Module.Category.MOVEMENT, "Modifies sprinting");
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static Sprint getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Sprint();
        }
        return INSTANCE;
    }

    @SubscribeEvent
    public void onSprint(MoveEvent event) {
        if (this.megaNullCheck()) {
            return;
        }
        if (event.getStage() == 1 && this.mode.getValue() == Mode.Rage && (Sprint.mc.player.movementInput.moveForward != 0.0f || Sprint.mc.player.movementInput.moveStrafe != 0.0f)) {
            event.setCanceled(true);
        }
    }

    @Override
    public void onUpdate() {
        switch (this.mode.getValue()) {
            case Rage: {
                if (!Sprint.mc.gameSettings.keyBindForward.isKeyDown() && !Sprint.mc.gameSettings.keyBindBack.isKeyDown() && !Sprint.mc.gameSettings.keyBindLeft.isKeyDown() && !Sprint.mc.gameSettings.keyBindRight.isKeyDown() || Sprint.mc.player.isSneaking() || Sprint.mc.player.collidedHorizontally || !((float)Sprint.mc.player.getFoodStats().getFoodLevel() > 6.0f)) break;
                Sprint.mc.player.setSprinting(true);
                break;
            }
            case Legit: {
                if (!Sprint.mc.gameSettings.keyBindForward.isKeyDown() || Sprint.mc.player.isSneaking() || Sprint.mc.player.isHandActive() || Sprint.mc.player.collidedHorizontally || !((float)Sprint.mc.player.getFoodStats().getFoodLevel() > 6.0f) || Sprint.mc.currentScreen != null) break;
                Sprint.mc.player.setSprinting(true);
            }
        }
    }

    @Override
    public void onDisable() {
        if (!Sprint.nullCheck()) {
            Sprint.mc.player.setSprinting(false);
        }
    }

    @Override
    public String getDisplayInfo() {
        return this.mode.currentEnumName();
    }

    public static enum Mode {
        Legit,
        Rage;

    }
}

