package bentobox.addon.acidisland;

import org.bukkit.World;
import org.bukkit.plugin.PluginManager;

import bentobox.addon.acidisland.commands.AcidCommand;
import bentobox.addon.acidisland.commands.AiCommand;
import bentobox.addon.acidisland.listeners.AcidEffect;
import bentobox.addon.acidisland.listeners.LavaCheck;
import bentobox.addon.acidisland.world.AcidIslandWorld;
import bentobox.addon.acidisland.world.AcidTask;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.configuration.BBConfig;

/**
 * Add-on to BentoBox that enables AcidIsland
 * @author tastybento
 *
 */
public class AcidIsland extends Addon {

    private static AcidIsland addon;
    private AISettings settings;
    private AcidIslandWorld aiw;
    private AcidTask acidTask;

    @Override
    public void onLoad() {
        addon = this;
        saveDefaultConfig();
        // Load settings
        settings = new BBConfig<>(this, AISettings.class).loadConfigObject();
        // Create worlds
        aiw = new AcidIslandWorld(this);
    }

    @Override
    public void onEnable() {
        // Register listeners
        PluginManager manager = getServer().getPluginManager();
        // Acid Effects
        manager.registerEvents(new AcidEffect(this), this.getPlugin());
        manager.registerEvents(new LavaCheck(this), this.getPlugin());
        // Register commands
        new AcidCommand(this);
        new AiCommand(this);
        // Burn everything
        acidTask = new AcidTask(this);
    }

    @Override
    public void onDisable(){
        acidTask.cancelTasks();
        // Save settings
        if (settings != null) {
            new BBConfig<>(this, AISettings.class).saveConfigObject(settings);
        }
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
        getPlugin().log(string);
    }

    public static AcidIsland getInstance() {
        return addon;
    }

}
