/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.movement;

import cascade.event.events.MoveEvent;
import cascade.event.events.UpdateWalkingPlayerEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.EntityUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ReverseStep
extends Module {
    public Setting<Y> test = this.register(new Setting<Y>("Test", Y.Normal));
    public Setting<Double> speed = this.register(new Setting<Double>("Speed", 5.0, 0.1, 9.0));
    public Setting<Boolean> pauseLiquid = this.register(new Setting<Boolean>("PauseInLiquid", true));

    public ReverseStep() {
        super("ReverseStep", Module.Category.MOVEMENT, "Fast fall");
    }

    @Override
    public void onUpdate() {
        if (ReverseStep.fullNullCheck()) {
            return;
        }
        if (EntityUtil.isInLiquid() && this.pauseLiquid.getValue().booleanValue()) {
            return;
        }
        if (ReverseStep.mc.player.isOnLadder() || ReverseStep.mc.gameSettings.keyBindJump.isKeyDown()) {
            return;
        }
        if (ReverseStep.mc.player.onGround && this.test.getValue() == Y.Normal) {
            ReverseStep.mc.player.motionY -= this.speed.getValue().doubleValue();
        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent e) {
        if (this.megaNullCheck()) {
            return;
        }
        if (EntityUtil.isInLiquid() && this.pauseLiquid.getValue().booleanValue()) {
            return;
        }
        if (ReverseStep.mc.player.isOnLadder() || ReverseStep.mc.gameSettings.keyBindJump.isKeyDown()) {
            return;
        }
        if (ReverseStep.mc.player.onGround && this.test.getValue() == Y.Move) {
            e.setY(0.0);
            ReverseStep.mc.player.motionY -= this.speed.getValue().doubleValue();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent e) {
        if (this.megaNullCheck()) {
            return;
        }
        if (EntityUtil.isInLiquid() && this.pauseLiquid.getValue().booleanValue()) {
            return;
        }
        if (ReverseStep.mc.player.isOnLadder() || ReverseStep.mc.gameSettings.keyBindJump.isKeyDown()) {
            return;
        }
        if (ReverseStep.mc.player.onGround && this.test.getValue() == Y.Player) {
            ReverseStep.mc.player.motionY -= this.speed.getValue().doubleValue();
        }
    }

    public static enum Y {
        Move,
        Normal,
        Player;

    }
}

