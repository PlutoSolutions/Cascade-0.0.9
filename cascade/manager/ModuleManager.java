/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  org.lwjgl.input.Keyboard
 */
package cascade.manager;

import cascade.event.events.Render2DEvent;
import cascade.event.events.Render3DEvent;
import cascade.features.Feature;
import cascade.features.gui.CascadeGui;
import cascade.features.modules.Module;
import cascade.features.modules.combat.AK47;
import cascade.features.modules.combat.Anchor;
import cascade.features.modules.combat.AntiTrap;
import cascade.features.modules.combat.Aura;
import cascade.features.modules.combat.AutoAim;
import cascade.features.modules.combat.AutoArmor;
import cascade.features.modules.combat.AutoCrystal;
import cascade.features.modules.combat.AutoCrystalRewrite;
import cascade.features.modules.combat.AutoTrap;
import cascade.features.modules.combat.Crits;
import cascade.features.modules.combat.FastProjectile;
import cascade.features.modules.combat.HoleFiller;
import cascade.features.modules.combat.Offhand;
import cascade.features.modules.combat.Platform;
import cascade.features.modules.combat.Quiver;
import cascade.features.modules.combat.SelfFill;
import cascade.features.modules.combat.Surround;
import cascade.features.modules.combat.TrollBow;
import cascade.features.modules.combat.WebAura;
import cascade.features.modules.core.ClickGui;
import cascade.features.modules.core.FontMod;
import cascade.features.modules.core.Gradient;
import cascade.features.modules.core.HUD;
import cascade.features.modules.core.HudNotifications;
import cascade.features.modules.core.Management;
import cascade.features.modules.exploit.ChorusDelay;
import cascade.features.modules.exploit.ClipBypass;
import cascade.features.modules.exploit.EntityForce;
import cascade.features.modules.exploit.FunnyPacket;
import cascade.features.modules.exploit.Godmode;
import cascade.features.modules.exploit.LagCancel;
import cascade.features.modules.exploit.PacketFly;
import cascade.features.modules.exploit.Phase;
import cascade.features.modules.exploit.Timer;
import cascade.features.modules.misc.Backpack;
import cascade.features.modules.misc.ChatModifications;
import cascade.features.modules.misc.ChorusPredict;
import cascade.features.modules.misc.CoordsReply;
import cascade.features.modules.misc.EntityTrace;
import cascade.features.modules.misc.FakePlayer;
import cascade.features.modules.misc.KillEffect;
import cascade.features.modules.misc.LogCoords;
import cascade.features.modules.misc.LogSpots;
import cascade.features.modules.misc.NoForceRotate;
import cascade.features.modules.misc.NoInteract;
import cascade.features.modules.misc.SmartWhisper;
import cascade.features.modules.misc.Target;
import cascade.features.modules.movement.Boost;
import cascade.features.modules.movement.LiquidControl;
import cascade.features.modules.movement.NoSlow;
import cascade.features.modules.movement.ReverseStep;
import cascade.features.modules.movement.Scaffold;
import cascade.features.modules.movement.Sprint;
import cascade.features.modules.movement.Step;
import cascade.features.modules.movement.Strafe;
import cascade.features.modules.movement.Tickshift;
import cascade.features.modules.movement.Velocity;
import cascade.features.modules.movement.YPort;
import cascade.features.modules.player.AntiAim;
import cascade.features.modules.player.Blink;
import cascade.features.modules.player.BoatClip;
import cascade.features.modules.player.EntityTweaks;
import cascade.features.modules.player.FastUse;
import cascade.features.modules.player.Freecam;
import cascade.features.modules.player.LiquidInteract;
import cascade.features.modules.player.MCF;
import cascade.features.modules.player.Mine;
import cascade.features.modules.player.Nuker;
import cascade.features.modules.player.Refill;
import cascade.features.modules.player.SelfAnvil;
import cascade.features.modules.player.XCarry;
import cascade.features.modules.visual.BlockHighlight;
import cascade.features.modules.visual.CameraClip;
import cascade.features.modules.visual.Chams;
import cascade.features.modules.visual.Crosshair;
import cascade.features.modules.visual.CrystalChams;
import cascade.features.modules.visual.HandChams;
import cascade.features.modules.visual.HoleESP;
import cascade.features.modules.visual.MoveCancel;
import cascade.features.modules.visual.Nametags;
import cascade.features.modules.visual.NoRender;
import cascade.features.modules.visual.OffscreenESP;
import cascade.features.modules.visual.PopESP;
import cascade.features.modules.visual.ShaderChams;
import cascade.features.modules.visual.Trajectories;
import cascade.features.modules.visual.ViewMod;
import cascade.features.modules.visual.ViewTweaks;
import cascade.features.modules.visual.Wallhack;
import cascade.features.modules.visual.Wireframe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.lwjgl.input.Keyboard;

public class ModuleManager
extends Feature {
    public ArrayList<Module> mods = new ArrayList();
    public List<Module> sortedModules = new ArrayList<Module>();
    public List<String> sortedModulesABC = new ArrayList<String>();

    public void init() {
        this.mods.add(new ClickGui());
        this.mods.add(new FontMod());
        this.mods.add(new Gradient());
        this.mods.add(new HUD());
        this.mods.add(new Management());
        this.mods.add(new HudNotifications());
        this.mods.add(new Anchor());
        this.mods.add(new AntiTrap());
        this.mods.add(new Aura());
        this.mods.add(new AutoAim());
        this.mods.add(new AutoArmor());
        this.mods.add(new AutoCrystal());
        this.mods.add(new AutoTrap());
        this.mods.add(new Crits());
        this.mods.add(new HoleFiller());
        this.mods.add(new FastProjectile());
        this.mods.add(new Offhand());
        this.mods.add(new Platform());
        this.mods.add(new Quiver());
        this.mods.add(new SelfFill());
        this.mods.add(new Surround());
        this.mods.add(new TrollBow());
        this.mods.add(new WebAura());
        this.mods.add(new AutoCrystalRewrite());
        this.mods.add(new AK47());
        this.mods.add(new ChorusDelay());
        this.mods.add(new ClipBypass());
        this.mods.add(new EntityForce());
        this.mods.add(new Godmode());
        this.mods.add(new LagCancel());
        this.mods.add(new PacketFly());
        this.mods.add(new Phase());
        this.mods.add(new Timer());
        this.mods.add(new FunnyPacket());
        this.mods.add(new Backpack());
        this.mods.add(new ChorusPredict());
        this.mods.add(new EntityTrace());
        this.mods.add(new FakePlayer());
        this.mods.add(new KillEffect());
        this.mods.add(new LogCoords());
        this.mods.add(new LogSpots());
        this.mods.add(new NoForceRotate());
        this.mods.add(new NoInteract());
        this.mods.add(new SmartWhisper());
        this.mods.add(new Target());
        this.mods.add(new ChatModifications());
        this.mods.add(new CoordsReply());
        this.mods.add(new BlockHighlight());
        this.mods.add(new CameraClip());
        this.mods.add(new Chams());
        this.mods.add(new CrystalChams());
        this.mods.add(new HandChams());
        this.mods.add(new HoleESP());
        this.mods.add(new MoveCancel());
        this.mods.add(new Nametags());
        this.mods.add(new NoRender());
        this.mods.add(new OffscreenESP());
        this.mods.add(new Trajectories());
        this.mods.add(new ViewMod());
        this.mods.add(new ViewTweaks());
        this.mods.add(new Wallhack());
        this.mods.add(new Wireframe());
        this.mods.add(new PopESP());
        this.mods.add(new Crosshair());
        this.mods.add(new ShaderChams());
        this.mods.add(new Boost());
        this.mods.add(new LiquidControl());
        this.mods.add(new NoSlow());
        this.mods.add(new ReverseStep());
        this.mods.add(new Scaffold());
        this.mods.add(new Sprint());
        this.mods.add(new Step());
        this.mods.add(new Strafe());
        this.mods.add(new Tickshift());
        this.mods.add(new Velocity());
        this.mods.add(new YPort());
        this.mods.add(new AntiAim());
        this.mods.add(new Blink());
        this.mods.add(new BoatClip());
        this.mods.add(new EntityTweaks());
        this.mods.add(new FastUse());
        this.mods.add(new Freecam());
        this.mods.add(new LiquidInteract());
        this.mods.add(new MCF());
        this.mods.add(new Mine());
        this.mods.add(new Nuker());
        this.mods.add(new Refill());
        this.mods.add(new SelfAnvil());
        this.mods.add(new XCarry());
    }

    public Module getModuleByName(String name) {
        for (Module module : this.mods) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        for (Module module : this.mods) {
            if (!clazz.isInstance(module)) continue;
            return (T)module;
        }
        return null;
    }

    public boolean isModuleEnabled(String name) {
        Module module = this.getModuleByName(name);
        return module != null && module.isEnabled();
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList<Module>();
        for (Module module : this.mods) {
            if (!module.isEnabled()) continue;
            enabledModules.add(module);
        }
        return enabledModules;
    }

    public ArrayList<String> getEnabledModulesName() {
        ArrayList<String> enabledModules = new ArrayList<String>();
        for (Module module : this.mods) {
            if (!module.isEnabled() || !module.isDrawn()) continue;
            enabledModules.add(module.getFullArrayString());
        }
        return enabledModules;
    }

    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList<Module>();
        this.mods.forEach(module -> {
            if (module.getCategory() == category) {
                modulesCategory.add((Module)module);
            }
        });
        return modulesCategory;
    }

    public List<Module.Category> getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onLoad() {
        this.mods.forEach(((EventBus)MinecraftForge.EVENT_BUS)::register);
        this.mods.forEach(Module::onLoad);
    }

    public void onUpdate() {
        this.mods.stream().filter(Feature::isEnabled).forEach(Module::onUpdate);
    }

    public void onTick() {
        this.mods.stream().filter(Feature::isEnabled).forEach(Module::onTick);
    }

    public void onRender2D(Render2DEvent event) {
        this.mods.stream().filter(Feature::isEnabled).forEach(module -> module.onRender2D(event));
    }

    public void onRender3D(Render3DEvent event) {
        this.mods.stream().filter(Feature::isEnabled).forEach(module -> module.onRender3D(event));
    }

    public void sortModules(boolean reverse) {
        this.sortedModules = this.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> this.renderer.getStringWidth(module.getFullArrayString()) * (reverse ? -1 : 1))).collect(Collectors.toList());
    }

    public void sortModulesABC() {
        this.sortedModulesABC = new ArrayList<String>(this.getEnabledModulesName());
        this.sortedModulesABC.sort(String.CASE_INSENSITIVE_ORDER);
    }

    public void onLogout() {
        this.mods.forEach(Module::onLogout);
    }

    public void onLogin() {
        this.mods.forEach(Module::onLogin);
    }

    public void onUnload() {
        this.mods.forEach(((EventBus)MinecraftForge.EVENT_BUS)::unregister);
    }

    public void onUnloadPost() {
        for (Module module : this.mods) {
            module.enabled.setValue(false);
        }
    }

    public void onKeyPressed(int eventKey) {
        if (eventKey == 0 || !Keyboard.getEventKeyState() || ModuleManager.mc.currentScreen instanceof CascadeGui) {
            return;
        }
        this.mods.forEach(module -> {
            if (module.getBind().getKey() == eventKey) {
                module.toggle();
            }
        });
    }
}

