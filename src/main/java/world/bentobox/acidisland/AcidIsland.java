package world.bentobox.acidisland;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import org.eclipse.jdt.annotation.NonNull;

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
    private @NonNull ChunkGenerator chunkGenerator;

    private static final String NETHER = "_nether";
    private static final String THE_END = "_the_end";

    @Override
    public void onLoad() {
        // Save the default config from config.yml
        saveDefaultConfig();
        // Load settings from config.yml. This will check if there are any issues with it too.
        loadSettings();
        // Chunk generator
        chunkGenerator = settings.isUseOwnGenerator() ? null : new ChunkGeneratorWorld(this);
        // Register commands
        adminCommand = new AcidCommand(this, settings.getAdminCommand());
        playerCommand = new AiCommand(this, settings.getIslandCommand());
    }

    private boolean loadSettings() {
        settings = new Config<>(this, AISettings.class).loadConfigObject();
        if (settings == null) {
            // Woops
            this.logError("AcidIsland settings could not load! Addon disabled.");
            this.setState(State.DISABLED);
            if (acidTask != null) {
                acidTask.cancelTasks();
            }
            return false;
        }
        // Save settings
        saveWorldSettings();
        return true;
    }

    @Override
    public void onEnable() {
        if (settings == null) {
            return;
        }
        // Register listeners
        // Acid Effects
        registerListener(new AcidEffect(this));
        registerListener(new LavaCheck(this));
        // Burn everything
        acidTask = new AcidTask(this);
    }

    @Override
    public void onDisable(){
        if (settings == null) {
            // Save settings that have changed
            new Config<>(this, AISettings.class).saveConfigObject(settings);
            return;
        }
        acidTask.cancelTasks();
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
        chunkGenerator = new ChunkGeneratorWorld(this);
        islandWorld = WorldCreator.name(worldName).type(WorldType.FLAT).environment(World.Environment.NORMAL).generator(chunkGenerator)
                .createWorld();

        // Make the nether if it does not exist
        if (settings.isNetherGenerate()) {
            if (getServer().getWorld(worldName + NETHER) == null) {
                log("Creating AcidIsland's Nether...");
            }
            if (!settings.isNetherIslands()) {
                netherWorld = WorldCreator.name(worldName + NETHER).type(WorldType.NORMAL).environment(World.Environment.NETHER).createWorld();
            } else {
                netherWorld = WorldCreator.name(worldName + NETHER).type(WorldType.FLAT).generator(chunkGenerator)
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
                endWorld = WorldCreator.name(worldName + THE_END).type(WorldType.FLAT).generator(chunkGenerator)
                        .environment(World.Environment.THE_END).createWorld();
            }
        }
    }

    @Override
    public WorldSettings getWorldSettings() {
        return settings;
    }

    @Override
    public void onReload() {
        if (loadSettings()) {
            log("Reloaded AcidIsland settings");
        }
    }

    @Override
    public @NonNull ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return chunkGenerator;
    }

    @Override
    public void saveWorldSettings() {
        if (settings != null) {
            new Config<>(this, AISettings.class).saveConfigObject(settings);
        }
    }

}
