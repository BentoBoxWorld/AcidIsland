package bskyblock.addon.acidisland;

import org.bukkit.World;
import org.bukkit.plugin.PluginManager;

import bskyblock.addon.acidisland.commands.AcidCommand;
import bskyblock.addon.acidisland.commands.AiCommand;
import bskyblock.addon.acidisland.listeners.AcidEffect;
import bskyblock.addon.acidisland.listeners.LavaCheck;
import bskyblock.addon.acidisland.world.AcidIslandWorld;
import bskyblock.addon.acidisland.world.AcidTask;
import us.tastybento.bskyblock.api.addons.Addon;
import us.tastybento.bskyblock.api.configuration.BSBConfig;

/**
 * Addon to BSkyBlock that enables AcidIsland
 * @author tastybento
 *
 */
public class AcidIsland extends Addon {

    private static AcidIsland addon;
    private AISettings settings;
    private AcidIslandWorld aiw;

    @Override
    public void onLoad() {
        addon = this;
        saveDefaultConfig();
        // Load settings
        settings = new BSBConfig<>(this, AISettings.class).loadConfigObject("");
        // Create worlds
        aiw = new AcidIslandWorld(this);
    }

    @Override
    public void onEnable() {
        // Register listeners
        PluginManager manager = getServer().getPluginManager();
        // Acid Effects
        manager.registerEvents(new AcidEffect(this), this.getBSkyBlock());
        manager.registerEvents(new LavaCheck(this), this.getBSkyBlock());
        // New Islands
        //manager.registerEvents(new IslandBuilder(this), this.getBSkyBlock());
        // Register commands
        new AcidCommand(this);
        new AiCommand(this);
        // Burn everything
        new AcidTask(this);
    }

    @Override
    public void onDisable(){
    }

    public AISettings getSettings() {
        return settings;
    }

    /**
     * @return the aiw
     */
    public AcidIslandWorld getAiw() {
        return aiw;
    }

    /**
     * Convenience method to obtain the AcidIsland overworld
     * @return Island World
     */
    public World getIslandWorld() {
        return aiw.getOverWorld();
    }

    @Override
    public void log(String string) {
        getBSkyBlock().log(string);
    }

    public static AcidIsland getInstance() {
        return addon;
    }

}
