package bskyblock.addon.acidisland;

import org.bukkit.World;
import org.bukkit.plugin.PluginManager;

import bskyblock.addon.acidisland.commands.AcidCommand;
import bskyblock.addon.acidisland.commands.AiCommand;
import bskyblock.addon.acidisland.listeners.AcidEffect;
import bskyblock.addon.acidisland.listeners.IslandBuilder;
import bskyblock.addon.acidisland.world.AcidIslandWorld;
import us.tastybento.bskyblock.api.addons.Addon;

/**
 * Addin to BSkyBlock that enables AcidIsland
 * @author tastybento
 *
 */
public class AcidIsland extends Addon {
    
    private AISettings settings;
    private AcidIslandWorld aiw;

    @Override
    public void onLoad() {
        // Load settings
        settings = new AISettings();
        // Create worlds
        aiw = new AcidIslandWorld(this);
        // TODO Register settings
        //getBSkyBlock().getSettings().register(settings);
    }

    @Override
    public void onEnable() {
        // Register listeners
        PluginManager manager = getServer().getPluginManager();
        // Acid Effects
        manager.registerEvents(new AcidEffect(this), this.getBSkyBlock());
        // New Islands
        manager.registerEvents(new IslandBuilder(this), this.getBSkyBlock());
        // Register commands
        new AcidCommand(this);
        new AiCommand(this);
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
     * @return
     */
    public World getIslandWorld() {
        return aiw.getOverWorld();
    }

    public void log(String string) {
        getBSkyBlock().log(string);       
    }

 

}
