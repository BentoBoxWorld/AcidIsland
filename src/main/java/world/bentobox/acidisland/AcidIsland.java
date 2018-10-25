package world.bentobox.acidisland;

import org.bukkit.World;
import org.bukkit.plugin.PluginManager;

import world.bentobox.acidisland.commands.AcidCommand;
import world.bentobox.acidisland.commands.AiCommand;
import world.bentobox.acidisland.listeners.AcidEffect;
import world.bentobox.acidisland.listeners.LavaCheck;
import world.bentobox.acidisland.world.AcidIslandWorld;
import world.bentobox.acidisland.world.AcidTask;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.configuration.Config;

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
        settings = new Config<>(this, AISettings.class).loadConfigObject();
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
        new AcidCommand(this, settings.getAdminCommand());
        new AiCommand(this, settings.getIslandCommand());
        // Burn everything
        acidTask = new AcidTask(this);
    }

    @Override
    public void onDisable(){
        acidTask.cancelTasks();
        // Save settings
        if (settings != null) {
            new Config<>(this, AISettings.class).saveConfigObject(settings);
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
