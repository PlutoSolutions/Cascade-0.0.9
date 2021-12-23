/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.crash.CrashReport
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.opengl.Display
 */
package cascade.mixin.mixins;

import cascade.Mod;
import cascade.event.events.KeyEvent;
import cascade.features.modules.core.Management;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Minecraft.class})
public abstract class MixinMinecraft {
    @Inject(method={"shutdownMinecraftApplet"}, at={@At(value="HEAD")})
    private void stopClient(CallbackInfo callbackInfo) {
        this.unload();
    }

    @Redirect(method={"run"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V"))
    public void displayCrashReport(Minecraft minecraft, CrashReport crashReport) {
        this.unload();
    }

    @Inject(method={"runTickKeyboard"}, at={@At(value="INVOKE", remap=false, target="Lorg/lwjgl/input/Keyboard;getEventKey()I", ordinal=0, shift=At.Shift.BEFORE)})
    private void onKeyboard(CallbackInfo callbackInfo) {
        int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
        int n = i;
        if (Keyboard.getEventKeyState()) {
            KeyEvent event = new KeyEvent(i);
            MinecraftForge.EVENT_BUS.post((Event)event);
        }
    }

    @Inject(method={"getLimitFramerate"}, at={@At(value="HEAD")}, cancellable=true)
    public void getLimitFramerateHook(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        try {
            if (Management.getInstance().isEnabled() && Management.getInstance().unfocusedCPU.getValue().booleanValue() && !Display.isActive()) {
                callbackInfoReturnable.setReturnValue(Management.getInstance().cpuFPS.getValue());
            }
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
    }

    private void unload() {
        Mod.LOGGER.info("Initiated client shutdown.");
        Mod.onUnload();
        Mod.LOGGER.info("Finished client shutdown.");
    }
}

