package world.bentobox.acidisland.world;

import java.util.List;

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
        return switch(worldInfo.getEnvironment()) {
        case NETHER -> addon.getSettings().getDefaultNetherBiome();
        case THE_END -> addon.getSettings().getDefaultEndBiome();
        default -> addon.getSettings().getDefaultBiome();
        };
    }

    @Override
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        return List.of(this.getBiome(worldInfo, 0, 0, 0));
    }

}
