/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 */
package cascade.features.modules.visual;

import cascade.event.events.Render3DEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.ColorUtil;
import cascade.util.RenderUtil;
import java.awt.Color;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class BlockHighlight
extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Outline));
    public Setting<Color> c = this.register(new Setting<Color>("Color", new Color(-1)));
    public Setting<Float> width = this.register(new Setting<Float>("Width", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
    public Setting<Boolean> rainbow = this.register(new Setting<Boolean>("Rainbow", false));
    public Setting<Integer> rainbowhue = this.register(new Setting<Object>("RainbowHue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue()));

    public BlockHighlight() {
        super("BlockHighlight", Module.Category.VISUAL, "ye");
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        RayTraceResult ray = BlockHighlight.mc.objectMouseOver;
        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos blockPos = ray.getBlockPos();
            RenderUtil.drawBoxESP(blockPos, this.rainbow.getValue() != false ? ColorUtil.rainbow(this.rainbowhue.getValue()) : new Color(this.c.getValue().getRed(), this.c.getValue().getGreen(), this.c.getValue().getBlue(), this.c.getValue().getAlpha()), false, this.rainbow.getValue() != false ? ColorUtil.rainbow(this.rainbowhue.getValue()) : new Color(this.c.getValue().getRed(), this.c.getValue().getGreen(), this.c.getValue().getBlue(), this.c.getValue().getAlpha()), this.width.getValue().floatValue(), this.mode.getValue() == Mode.Outline || this.mode.getValue() == Mode.Both, this.mode.getValue() == Mode.Fill || this.mode.getValue() == Mode.Both, this.c.getValue().getAlpha(), false);
        }
    }

    public static enum Mode {
        Fill,
        Outline,
        Both;

    }
}

