/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.common.Mod$EventHandler
 *  net.minecraftforge.fml.common.Mod$Instance
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPreInitializationEvent
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.opengl.Display
 */
package cascade;

import cascade.manager.ColorManager;
import cascade.manager.CommandManager;
import cascade.manager.ConfigManager;
import cascade.manager.EventManager;
import cascade.manager.FriendManager;
import cascade.manager.InventoryManager;
import cascade.manager.ModuleManager;
import cascade.manager.MovementManager;
import cascade.manager.PositionManager;
import cascade.manager.PotionManager;
import cascade.manager.RotationManager;
import cascade.manager.ServerManager;
import cascade.manager.SpeedManager;
import cascade.manager.TextManager;
import cascade.manager.TimerManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@net.minecraftforge.fml.common.Mod(modid="cascade", name="Cascade", version="0.0.9")
public class Mod {
    public static final String MODNAME = "Cascade";
    public static final String MODVER = "0.0.9";
    public static final Logger LOGGER = LogManager.getLogger((String)"Cascade");
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static ColorManager colorManager;
    public static InventoryManager inventoryManager;
    public static PotionManager potionManager;
    public static RotationManager rotationManager;
    public static PositionManager positionManager;
    public static MovementManager movement;
    public static SpeedManager speedManager;
    public static ConfigManager configManager;
    public static ServerManager serverManager;
    public static EventManager eventManager;
    public static TimerManager timerManager;
    public static TextManager textManager;
    public static Minecraft mc;
    @Mod.Instance
    public static Mod INSTANCE;
    private static boolean unloaded;

    public static void load() {
        LOGGER.info("\n\nLoading Cascade");
        unloaded = false;
        textManager = new TextManager();
        commandManager = new CommandManager();
        friendManager = new FriendManager();
        moduleManager = new ModuleManager();
        rotationManager = new RotationManager();
        eventManager = new EventManager();
        movement = new MovementManager();
        timerManager = new TimerManager();
        speedManager = new SpeedManager();
        potionManager = new PotionManager();
        inventoryManager = new InventoryManager();
        serverManager = new ServerManager();
        colorManager = new ColorManager();
        positionManager = new PositionManager();
        configManager = new ConfigManager();
        moduleManager.init();
        configManager.init();
        eventManager.init();
        textManager.init(true);
        moduleManager.onLoad();
        LOGGER.info("Cascade successfully loaded!\n");
    }

    public static void onUnload() {
        if (!unloaded) {
            eventManager.onUnload();
            timerManager.unload();
            moduleManager.onUnload();
            configManager.saveConfig(Mod.configManager.config.replaceFirst("cascade/", ""));
            moduleManager.onUnloadPost();
            unloaded = true;
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        LOGGER.info("Friday da 13th?");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle((String)"Cascade 0.0.9");
        Mod.load();
    }

    static {
        mc = Minecraft.getMinecraft();
        unloaded = false;
    }
}

