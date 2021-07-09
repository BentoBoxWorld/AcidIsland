package world.bentobox.acidisland;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import world.bentobox.acidisland.commands.IslandAboutCommand;
import world.bentobox.acidisland.listeners.AcidEffect;
import world.bentobox.acidisland.listeners.LavaCheck;
import world.bentobox.acidisland.world.AcidTask;
import world.bentobox.acidisland.world.ChunkGeneratorWorld;
import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.commands.admin.DefaultAdminCommand;
import world.bentobox.bentobox.api.commands.island.DefaultPlayerCommand;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.bentobox.api.configuration.WorldSettings;
import world.bentobox.bentobox.lists.Flags;

/**
 * Add-on to BentoBox that enables AcidIsland
 * @author tastybento
 *
 */
public class AcidIsland extends GameModeAddon {

    private @Nullable AISettings settings;
    private @Nullable AcidTask acidTask;
    private @Nullable ChunkGenerator chunkGenerator;
    private final Config<AISettings> config = new Config<>(this, AISettings.class);

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
        // Register commands
        playerCommand = new DefaultPlayerCommand(this)

        {
            @Override
            public void setup()
            {
                super.setup();
                new IslandAboutCommand(this);
            }
        };
        adminCommand = new DefaultAdminCommand(this) {};
    }

    private boolean loadSettings() {
        settings = config.loadConfigObject();
        if (settings == null) {
            // Woops
            this.logError("AcidIsland settings could not load! Addon disabled.");
            this.setState(State.DISABLED);
            if (acidTask != null) {
                acidTask.cancelTasks();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onEnable() {
        if (settings == null) {
            return;
        }
        // Set default access to boats
        Flags.BOAT.setDefaultSetting(islandWorld, true);
        if (netherWorld != null) Flags.BOAT.setDefaultSetting(netherWorld, true);
        if (endWorld != null) Flags.BOAT.setDefaultSetting(endWorld, true);

        // Register listeners
        // Acid Effects
        registerListener(new AcidEffect(this));
        registerListener(new LavaCheck(this));
        // Burn everything
        acidTask = new AcidTask(this);
    }

    @Override
    public void onDisable() {
        if (acidTask != null) acidTask.cancelTasks();
    }

    @NonNull
    public AISettings getSettings() {
        return Objects.requireNonNull(settings);
    }

    /* (non-Javadoc)
     * @see world.bentobox.bentobox.api.addons.GameModeAddon#createWorlds()
     */
    @Override
    public void createWorlds() {
        String worldName = settings.getWorldName().toLowerCase();
        if (Bukkit.getWorld(worldName) == null) {
            log("Creating AcidIsland...");
        }
        // Create the world if it does not exist
        chunkGenerator = new ChunkGeneratorWorld(this);
        islandWorld = getWorld(worldName, World.Environment.NORMAL, chunkGenerator);
        // Make the nether if it does not exist
        if (settings.isNetherGenerate()) {
            if (Bukkit.getWorld(worldName + NETHER) == null) {
                log("Creating AcidIsland's Nether...");
            }
            netherWorld = settings.isNetherIslands() ? getWorld(worldName, World.Environment.NETHER, chunkGenerator) : getWorld(worldName, World.Environment.NETHER, null);
        }
        // Make the end if it does not exist
        if (settings.isEndGenerate()) {
            if (Bukkit.getWorld(worldName + THE_END) == null) {
                log("Creating AcidIsland's End World...");
            }
            endWorld = settings.isEndIslands() ? getWorld(worldName, World.Environment.THE_END, chunkGenerator) : getWorld(worldName, World.Environment.THE_END, null);
        }
    }

    /**
     * Gets a world or generates a new world if it does not exist
     * @param worldName2 - the overworld name
     * @param env - the environment
     * @param chunkGenerator2 - the chunk generator. If <tt>null</tt> then the generator will not be specified
     * @return world loaded or generated
     */
    private World getWorld(String worldName2, Environment env, @Nullable ChunkGenerator chunkGenerator2) {
        // Set world name
        worldName2 = env.equals(World.Environment.NETHER) ? worldName2 + NETHER : worldName2;
        worldName2 = env.equals(World.Environment.THE_END) ? worldName2 + THE_END : worldName2;
        WorldCreator wc = WorldCreator.name(worldName2).type(WorldType.FLAT).environment(env);
        World w = settings.isUseOwnGenerator() ? wc.createWorld() : wc.generator(chunkGenerator2).createWorld();
        // Set spawn rates
        if (w != null && getSettings() != null) {
            if (getSettings().getSpawnLimitMonsters() > 0) {
                w.setMonsterSpawnLimit(getSettings().getSpawnLimitMonsters());
            }
            if (getSettings().getSpawnLimitAmbient() > 0) {
                w.setAmbientSpawnLimit(getSettings().getSpawnLimitAmbient());
            }
            if (getSettings().getSpawnLimitAnimals() > 0) {
                w.setAnimalSpawnLimit(getSettings().getSpawnLimitAnimals());
            }
            if (getSettings().getSpawnLimitWaterAnimals() > 0) {
                w.setWaterAnimalSpawnLimit(getSettings().getSpawnLimitWaterAnimals());
            }
            if (getSettings().getTicksPerAnimalSpawns() > 0) {
                w.setTicksPerAnimalSpawns(getSettings().getTicksPerAnimalSpawns());
            }
            if (getSettings().getTicksPerMonsterSpawns() > 0) {
                w.setTicksPerMonsterSpawns(getSettings().getTicksPerMonsterSpawns());
            }
        }
        return w;

    }

    @Override
    public WorldSettings getWorldSettings() {
        return getSettings();
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
            config.saveConfigObject(settings);
        }
    }

    /* (non-Javadoc)
     * @see world.bentobox.bentobox.api.addons.Addon#allLoaded()
     */
    @Override
    public void allLoaded() {
        // Save settings. This will occur after all addons have loaded
        this.saveWorldSettings();
    }
}
