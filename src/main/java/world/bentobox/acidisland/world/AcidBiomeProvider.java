package world.bentobox.acidisland.world;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

import world.bentobox.acidisland.AcidIsland;

/**
 * Biome provider for AcidIsland
 * @author tastybento
 *
 */
public class AcidBiomeProvider extends BiomeProvider {

    private final AcidIsland addon;
    private final Map<Environment, Biome> biomes = new EnumMap<>(Environment.class);

    /**
     * @param addon Addon
     */
    public AcidBiomeProvider(AcidIsland addon) {
        this.addon = addon;
    }

    @Override
    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        return biomes.computeIfAbsent(worldInfo.getEnvironment(), env -> switch (env) {
        case NETHER -> resolve(addon.getSettings().getDefaultNetherBiome(), Biome.NETHER_WASTES);
        case THE_END -> resolve(addon.getSettings().getDefaultEndBiome(), Biome.THE_END);
        default -> resolve(addon.getSettings().getDefaultBiome(), Biome.WARM_OCEAN);
        });
    }

    /**
     * Resolves a configured biome name, falling back if the biome does not exist on
     * this server version, e.g. SULFUR_CAVES on servers older than Minecraft 26.2.
     * The biome is stored in the config as a plain string so that loading and saving
     * never touch the biome registry - a registry miss at load time would otherwise
     * write null back to the config and error on every following start.
     */
    private Biome resolve(String name, Biome fallback) {
        if (name == null || name.isBlank()) {
            return fallback;
        }
        NamespacedKey key = NamespacedKey.fromString(name.toLowerCase(Locale.ENGLISH));
        Biome biome = key == null ? null : Registry.BIOME.get(key);
        return biome == null ? fallback : biome;
    }

    @Override
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        return List.of(this.getBiome(worldInfo, 0, 0, 0));
    }

}
