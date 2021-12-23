/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 */
package cascade.features.modules.movement;

import cascade.Mod;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;

public class LiquidControl
extends Module {
    public Setting<Double> vertical = this.register(new Setting<Double>("Vertical", 1.0, 0.0, 20.0));
    public Setting<Double> horizontal = this.register(new Setting<Double>("Horizontal", 1.0, 0.0, 20.0));
    public Setting<Boolean> stopMotion = this.register(new Setting<Boolean>("StopMotion", true));

    public LiquidControl() {
        super("LiquidControl", Module.Category.MOVEMENT, "Controls ur motion when in liquids");
    }

    @Override
    public void onUpdate() {
        if (LiquidControl.fullNullCheck()) {
            return;
        }
        if (!EntityUtil.isInLiquid()) {
            return;
        }
        if (LiquidControl.mc.world.getBlockState(EntityUtil.getPlayerPos((EntityPlayer)LiquidControl.mc.player)).getBlock().equals((Object)Blocks.AIR) || LiquidControl.mc.world.getBlockState(EntityUtil.getPlayerPos((EntityPlayer)LiquidControl.mc.player).up()).getBlock().equals((Object)Blocks.AIR)) {
            return;
        }
        if (LiquidControl.mc.player.onGround) {
            return;
        }
        if (this.stopMotion.getValue().booleanValue()) {
            Mod.movement.setMotion(LiquidControl.mc.player.motionX, LiquidControl.mc.gameSettings.keyBindJump.isKeyDown() ? 0.0 : LiquidControl.mc.player.motionY, LiquidControl.mc.player.motionZ);
        }
        if (LiquidControl.mc.gameSettings.keyBindJump.isKeyDown()) {
            LiquidControl.mc.player.motionY = this.vertical.getValue() / 40.0;
        }
        if (LiquidControl.mc.gameSettings.keyBindSneak.isKeyDown()) {
            LiquidControl.mc.player.motionY = -this.vertical.getValue().doubleValue() / 40.0;
        }
        if (LiquidControl.mc.gameSettings.keyBindForward.isKeyDown() || LiquidControl.mc.gameSettings.keyBindBack.isKeyDown() || LiquidControl.mc.gameSettings.keyBindLeft.isKeyDown() || LiquidControl.mc.gameSettings.keyBindRight.isKeyDown()) {
            LiquidControl.mc.player.motionX *= this.horizontal.getValue() / 10.0;
            LiquidControl.mc.player.motionZ *= this.horizontal.getValue() / 10.0;
        }
    }
}

