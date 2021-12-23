/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.movement;

import cascade.Mod;
import cascade.event.events.UpdateWalkingPlayerEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.BlockUtil;
import cascade.util.EntityUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Step
extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Vanilla));
    public Setting<Float> height = this.register(new Setting<Float>("Height", Float.valueOf(2.3f), Float.valueOf(0.1f), Float.valueOf(2.5f)));
    public Setting<Boolean> check = this.register(new Setting<Boolean>("Check", false));
    public Setting<Boolean> pauseLiquid = this.register(new Setting<Boolean>("PauseInLiquid", true));

    public Step() {
        super("Step", Module.Category.MOVEMENT, "Allows you to step up blocks");
    }

    @Override
    public void onToggle() {
        if (Step.fullNullCheck()) {
            return;
        }
        Step.mc.player.stepHeight = 0.6f;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent e) {
        if (this.megaNullCheck() || this.pauseLiquid.getValue().booleanValue() && EntityUtil.isInLiquid()) {
            return;
        }
        if (this.check.getValue().booleanValue()) {
            BlockPos pos = new BlockPos(Step.mc.player.posX, Step.mc.player.posY + 0.2, Step.mc.player.posZ);
            if (Step.mc.world.getBlockState(pos).getBlock() == Blocks.AIR || Step.mc.world.getBlockState(pos).getBlock() == BlockUtil.unSolidBlocks) {
                Mod.movement.doStep(this.height.getValue().floatValue(), true);
            }
        } else {
            Mod.movement.doStep(this.height.getValue().floatValue(), true);
        }
    }

    public static enum Mode {
        Vanilla,
        NCP;

    }
}

