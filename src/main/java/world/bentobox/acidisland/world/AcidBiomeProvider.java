package world.bentobox.acidisland.world;

import java.util.List;
import java.util.Objects;

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

    /**
     * @param addon Addon
     */
    public AcidBiomeProvider(AcidIsland addon) {
        this.addon = addon;
    }

    @Override
    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        // Biomes are null if the configured biome does not exist on this server version,
        // e.g. SULFUR_CAVES on servers older than Minecraft 26.2, so fall back to a vanilla one
        return switch(worldInfo.getEnvironment()) {
        case NETHER -> Objects.requireNonNullElse(addon.getSettings().getDefaultNetherBiome(), Biome.NETHER_WASTES);
        case THE_END -> Objects.requireNonNullElse(addon.getSettings().getDefaultEndBiome(), Biome.THE_END);
        default -> Objects.requireNonNullElse(addon.getSettings().getDefaultBiome(), Biome.WARM_OCEAN);
        };
    }

    @Override
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        return List.of(this.getBiome(worldInfo, 0, 0, 0));
    }

}
