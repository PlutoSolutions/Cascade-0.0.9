/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiChat
 *  net.minecraft.client.settings.KeyBinding
 *  net.minecraft.util.MovementInput
 *  net.minecraftforge.client.event.InputUpdateEvent
 *  net.minecraftforge.client.settings.IKeyConflictContext
 *  net.minecraftforge.client.settings.KeyConflictContext
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.input.Keyboard
 */
package cascade.features.modules.movement;

import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInput;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class NoSlow
extends Module {
    public Setting<Boolean> invMove = this.register(new Setting<Boolean>("InventoryMove", false));
    private static final KeyBinding[] KEYS = new KeyBinding[]{NoSlow.mc.gameSettings.keyBindForward, NoSlow.mc.gameSettings.keyBindRight, NoSlow.mc.gameSettings.keyBindBack, NoSlow.mc.gameSettings.keyBindLeft, NoSlow.mc.gameSettings.keyBindJump, NoSlow.mc.gameSettings.keyBindSprint};

    public NoSlow() {
        super("NoSlow", Module.Category.MOVEMENT, "omg no slow");
    }

    @Override
    public void onUpdate() {
        if (NoSlow.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (NoSlow.mc.currentScreen instanceof GuiChat || NoSlow.mc.currentScreen == null) {
            return;
        }
        this.walk();
    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent e) {
        if (NoSlow.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (NoSlow.mc.player.isHandActive() && !NoSlow.mc.player.isRiding()) {
            MovementInput movementInput = e.getMovementInput();
            movementInput.moveStrafe *= 5.0f;
            MovementInput movementInput2 = e.getMovementInput();
            movementInput2.moveForward *= 5.0f;
        }
    }

    public void walk() {
        for (KeyBinding key_binding : KEYS) {
            if (Keyboard.isKeyDown((int)key_binding.getKeyCode())) {
                if (key_binding.getKeyConflictContext() != KeyConflictContext.UNIVERSAL) {
                    key_binding.setKeyConflictContext((IKeyConflictContext)KeyConflictContext.UNIVERSAL);
                }
                KeyBinding.setKeyBindState((int)key_binding.getKeyCode(), (boolean)true);
                continue;
            }
            KeyBinding.setKeyBindState((int)key_binding.getKeyCode(), (boolean)false);
        }
    }
}

