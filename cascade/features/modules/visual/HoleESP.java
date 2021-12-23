/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package cascade.features.modules.visual;

import cascade.event.events.Render3DEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.BlockUtil;
import cascade.util.RenderUtil;
import java.awt.Color;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class HoleESP
extends Module {
    private static HoleESP INSTANCE;
    private final Setting<Integer> range = this.register(new Setting<Integer>("RangeX", 0, 0, 10));
    private final Setting<Integer> rangeY = this.register(new Setting<Integer>("RangeY", 0, 0, 10));
    public Setting<Color> c = this.register(new Setting<Color>("ObsidianColor", new Color(-1)));
    private final Setting<Integer> boxAlpha = this.register(new Setting<Integer>("BoxAlpha", 100, 0, 255));
    private final Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
    public Setting<Color> safec = this.register(new Setting<Color>("BedrockColor", new Color(-1)));
    public Setting<Boolean> doubleHoles = this.register(new Setting<Boolean>("DoubleHoles", true));
    public Setting<Boolean> fov = this.register(new Setting<Boolean>("InFov", true));
    public Setting<Boolean> renderOwn = this.register(new Setting<Boolean>("RenderOwn", true));
    public Setting<Boolean> box = this.register(new Setting<Boolean>("Box", true));
    public Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", true));
    public Setting<Color> olc = this.register(new Setting<Color>("OL-ObsidianColor", new Color(-1)));
    public Setting<Color> olsafec = this.register(new Setting<Color>("OL-BedrockColor", new Color(-1)));

    public HoleESP() {
        super("HoleESP", Module.Category.VISUAL, "Shows safe spots");
        this.setInstance();
    }

    public static HoleESP getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HoleESP();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        assert (HoleESP.mc.renderViewEntity != null);
        Vec3i playerPos = new Vec3i(HoleESP.mc.renderViewEntity.posX, HoleESP.mc.renderViewEntity.posY, HoleESP.mc.renderViewEntity.posZ);
        for (int x = playerPos.getX() - this.range.getValue(); x < playerPos.getX() + this.range.getValue(); ++x) {
            for (int z = playerPos.getZ() - this.range.getValue(); z < playerPos.getZ() + this.range.getValue(); ++z) {
                for (int y = playerPos.getY() + this.rangeY.getValue(); y > playerPos.getY() - this.rangeY.getValue(); --y) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!HoleESP.mc.world.getBlockState(pos).getBlock().equals((Object)Blocks.AIR) || !HoleESP.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals((Object)Blocks.AIR) || !HoleESP.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals((Object)Blocks.AIR) || pos.equals((Object)new BlockPos(HoleESP.mc.player.posX, HoleESP.mc.player.posY, HoleESP.mc.player.posZ)) && !this.renderOwn.getValue().booleanValue() || !BlockUtil.isPosInFov(pos).booleanValue() && this.fov.getValue().booleanValue()) continue;
                    if (this.doubleHoles.getValue().booleanValue()) {
                        if (HoleESP.mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR && HoleESP.mc.world.getBlockState(pos.north().up()).getBlock() == Blocks.AIR && HoleESP.mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.north(2)).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK) {
                            RenderUtil.drawCrossESP(pos, new Color(this.safec.getValue().getRed(), this.safec.getValue().getGreen(), this.safec.getValue().getBlue(), this.safec.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true);
                            RenderUtil.drawCrossESP(pos.north(), new Color(this.safec.getValue().getRed(), this.safec.getValue().getGreen(), this.safec.getValue().getBlue(), this.safec.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true);
                        } else if (!(HoleESP.mc.world.getBlockState(pos.north()).getBlock() != Blocks.AIR || HoleESP.mc.world.getBlockState(pos.north().up()).getBlock() != Blocks.AIR || HoleESP.mc.world.getBlockState(pos.north().down()).getBlock() != Blocks.OBSIDIAN && HoleESP.mc.world.getBlockState(pos.north().down()).getBlock() != Blocks.BEDROCK || HoleESP.mc.world.getBlockState(pos.north(2)).getBlock() != Blocks.OBSIDIAN && HoleESP.mc.world.getBlockState(pos.north(2)).getBlock() != Blocks.BEDROCK || HoleESP.mc.world.getBlockState(pos.east()).getBlock() != Blocks.OBSIDIAN && HoleESP.mc.world.getBlockState(pos.east()).getBlock() != Blocks.BEDROCK || HoleESP.mc.world.getBlockState(pos.north().east()).getBlock() != Blocks.OBSIDIAN && HoleESP.mc.world.getBlockState(pos.north().east()).getBlock() != Blocks.BEDROCK || HoleESP.mc.world.getBlockState(pos.west()).getBlock() != Blocks.OBSIDIAN && HoleESP.mc.world.getBlockState(pos.west()).getBlock() != Blocks.BEDROCK || HoleESP.mc.world.getBlockState(pos.north().west()).getBlock() != Blocks.OBSIDIAN && HoleESP.mc.world.getBlockState(pos.north().west()).getBlock() != Blocks.BEDROCK || HoleESP.mc.world.getBlockState(pos.south()).getBlock() != Blocks.OBSIDIAN && HoleESP.mc.world.getBlockState(pos.south()).getBlock() != Blocks.BEDROCK || HoleESP.mc.world.getBlockState(pos.down()).getBlock() != Blocks.OBSIDIAN && HoleESP.mc.world.getBlockState(pos.down()).getBlock() != Blocks.BEDROCK)) {
                            RenderUtil.drawCrossESP(pos, new Color(this.c.getValue().getRed(), this.c.getValue().getGreen(), this.c.getValue().getBlue(), this.c.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true);
                            RenderUtil.drawCrossESP(pos.north(), new Color(this.c.getValue().getRed(), this.c.getValue().getGreen(), this.c.getValue().getBlue(), this.c.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true);
                        }
                        if (HoleESP.mc.world.getBlockState(pos.east()).getBlock() == Blocks.AIR && HoleESP.mc.world.getBlockState(pos.east().up()).getBlock() == Blocks.AIR && HoleESP.mc.world.getBlockState(pos.east().down()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east(2)).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east(2).down()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east().north()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east().south()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK) {
                            RenderUtil.drawCrossESP(pos, new Color(this.safec.getValue().getRed(), this.safec.getValue().getGreen(), this.safec.getValue().getBlue(), this.safec.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true);
                            RenderUtil.drawCrossESP(pos.east(), new Color(this.safec.getValue().getRed(), this.safec.getValue().getGreen(), this.safec.getValue().getBlue(), this.safec.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true);
                        } else if (!(HoleESP.mc.world.getBlockState(pos.east()).getBlock() != Blocks.AIR || HoleESP.mc.world.getBlockState(pos.east().up()).getBlock() != Blocks.AIR || HoleESP.mc.world.getBlockState(pos.east().down()).getBlock() != Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east().down()).getBlock() != Blocks.OBSIDIAN || HoleESP.mc.world.getBlockState(pos.east(2)).getBlock() != Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east(2)).getBlock() != Blocks.OBSIDIAN || HoleESP.mc.world.getBlockState(pos.north()).getBlock() != Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.north()).getBlock() != Blocks.OBSIDIAN || HoleESP.mc.world.getBlockState(pos.east().north()).getBlock() != Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east().north()).getBlock() != Blocks.OBSIDIAN || HoleESP.mc.world.getBlockState(pos.west()).getBlock() != Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.west()).getBlock() != Blocks.OBSIDIAN || HoleESP.mc.world.getBlockState(pos.east().south()).getBlock() != Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east().south()).getBlock() != Blocks.OBSIDIAN || HoleESP.mc.world.getBlockState(pos.south()).getBlock() != Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.south()).getBlock() != Blocks.OBSIDIAN || HoleESP.mc.world.getBlockState(pos.down()).getBlock() != Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.down()).getBlock() != Blocks.OBSIDIAN)) {
                            RenderUtil.drawCrossESP(pos, new Color(this.c.getValue().getRed(), this.c.getValue().getGreen(), this.c.getValue().getBlue(), this.c.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true);
                            RenderUtil.drawCrossESP(pos.east(), new Color(this.c.getValue().getRed(), this.c.getValue().getGreen(), this.c.getValue().getBlue(), this.c.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true);
                        }
                    }
                    if (HoleESP.mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK) {
                        RenderUtil.drawCrossESP(pos, new Color(this.safec.getValue().getRed(), this.safec.getValue().getGreen(), this.safec.getValue().getBlue(), this.safec.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true);
                        continue;
                    }
                    if (!BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.down()).getBlock()) || !BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.east()).getBlock()) || !BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.west()).getBlock()) || !BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.south()).getBlock()) || !BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.north()).getBlock())) continue;
                    RenderUtil.drawCrossESP(pos, new Color(this.c.getValue().getRed(), this.c.getValue().getGreen(), this.c.getValue().getBlue(), this.c.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true);
                }
            }
        }
    }
}

