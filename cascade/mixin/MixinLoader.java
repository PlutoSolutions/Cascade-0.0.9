/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
 */
package cascade.mixin;

import cascade.Mod;
import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

public class MixinLoader
implements IFMLLoadingPlugin {
    private static boolean isObfuscatedEnvironment = false;

    public MixinLoader() {
        if (!MixinLoader.runningFromIntelliJ()) {
            Mod.LOGGER.info("\n\nLoading mixins");
            MixinBootstrap.init();
            Mixins.addConfiguration("mixins.cascade.json");
            MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
            Mod.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
        }
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {
        isObfuscatedEnvironment = (Boolean)data.get("runtimeDeobfuscationEnabled");
    }

    public String getAccessTransformerClass() {
        return null;
    }

    public static boolean runningFromIntelliJ() {
        return System.getProperty("java.class.path").contains("idea_rt.jar");
    }
}

