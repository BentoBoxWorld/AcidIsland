package world.bentobox.acidisland;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.plugin.PluginManager;

import world.bentobox.acidisland.commands.AcidCommand;
import world.bentobox.acidisland.commands.AiCommand;
import world.bentobox.acidisland.listeners.AcidEffect;
import world.bentobox.acidisland.listeners.LavaCheck;
import world.bentobox.acidisland.world.AcidTask;
import world.bentobox.acidisland.world.ChunkGeneratorWorld;
import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.bentobox.api.configuration.WorldSettings;

/**
 * Add-on to BentoBox that enables AcidIsland
 * @author tastybento
 *
 */
public class AcidIsland extends GameModeAddon {

    private AISettings settings;
    private AcidTask acidTask;

    private static final String NETHER = "_nether";
    private static final String THE_END = "_the_end";

    @Override
    public void onLoad() {
        saveDefaultConfig();
        // Load settings
        settings = new Config<>(this, AISettings.class).loadConfigObject();
        if (settings == null) {
            // Woops
            this.logError("Settings could not load! Addon disabled.");
        }
    }

    @Override
    public void onEnable() {
        if (settings == null) {
            return;
        }
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
        if (settings == null) {
            return;
        }
        acidTask.cancelTasks();
        // Save settings
        if (settings != null) {
            new Config<>(this, AISettings.class).saveConfigObject(settings);
        }
    }

    public AISettings getSettings() {
        return settings;
    }

    @Override
    public void log(String string) {
        getPlugin().log(string);
    }

    @Override
    public void createWorlds() {
        String worldName = settings.getWorldName();
        if (getServer().getWorld(worldName) == null) {
            getLogger().info("Creating AcidIsland...");
        }
        // Create the world if it does not exist
        islandWorld = WorldCreator.name(worldName).type(WorldType.FLAT).environment(World.Environment.NORMAL).generator(new ChunkGeneratorWorld(this))
                .createWorld();

        // Make the nether if it does not exist
        if (settings.isNetherGenerate()) {
            if (getServer().getWorld(worldName + NETHER) == null) {
                log("Creating AcidIsland's Nether...");
            }
            if (!settings.isNetherIslands()) {
                netherWorld = WorldCreator.name(worldName + NETHER).type(WorldType.NORMAL).environment(World.Environment.NETHER).createWorld();
            } else {
                netherWorld = WorldCreator.name(worldName + NETHER).type(WorldType.FLAT).generator(new ChunkGeneratorWorld(this))
                        .environment(World.Environment.NETHER).createWorld();
            }
        }
        // Make the end if it does not exist
        if (settings.isEndGenerate()) {
            if (getServer().getWorld(worldName + THE_END) == null) {
                log("Creating AcidIsland's End World...");
            }
            if (!settings.isEndIslands()) {
                endWorld = WorldCreator.name(worldName + THE_END).type(WorldType.NORMAL).environment(World.Environment.THE_END).createWorld();
            } else {
                endWorld = WorldCreator.name(worldName + THE_END).type(WorldType.FLAT).generator(new ChunkGeneratorWorld(this))
                        .environment(World.Environment.THE_END).createWorld();
            }
        }
    }

    @Override
    public WorldSettings getWorldSettings() {
        return settings;
    }

}
