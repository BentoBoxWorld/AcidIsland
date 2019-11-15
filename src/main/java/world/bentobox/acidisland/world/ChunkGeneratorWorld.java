package world.bentobox.acidisland.world;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.PerlinOctaveGenerator;

import world.bentobox.acidisland.AcidIsland;

/**
 * Generates the AcidIsland world
 * @author tastybento
 *
 */
public class ChunkGeneratorWorld extends ChunkGenerator {

    private final AcidIsland addon;
    private final Random rand = new Random();
    private Map<Environment, Integer> seaHeight = new EnumMap<>(Environment.class);

    /**
     * @param addon - addon
     */
    public ChunkGeneratorWorld(AcidIsland addon) {
        super();
        this.addon = addon;
        seaHeight.put(Environment.NORMAL, addon.getSettings().getSeaHeight());
        seaHeight.put(Environment.NETHER, addon.getSettings().getNetherSeaHeight());
        seaHeight.put(Environment.THE_END, addon.getSettings().getEndSeaHeight());
    }

    public ChunkData generateChunks(World world) {
        ChunkData result = createChunkData(world);
        result.setRegion(0, 0, 0, 16, seaHeight.getOrDefault(world.getEnvironment(), 0) + 1, 16, Material.WATER);
        if (world.getEnvironment().equals(Environment.NETHER) && addon.getSettings().isNetherRoof()) {
            makeNetherRoof(result, world);
        }
        return result;
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid) {
        if (world.getEnvironment().equals(Environment.NORMAL)) biomeGrid = new AcidBiomeGrid();
        return generateChunks(world);
    }

    // This needs to be set to return true to override minecraft's default
    // behavior
    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(final World world) {
        return Collections.emptyList();
    }

    /*
     * Nether Section
     */
    private void makeNetherRoof(ChunkData netherResult, World world) {
        rand.setSeed(world.getSeed());
        PerlinOctaveGenerator gen = new PerlinOctaveGenerator((long) (rand.nextLong() * rand.nextGaussian()), 8);

        // Make the roof - common across the world
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // Do the ceiling
                int maxHeight = world.getMaxHeight();
                netherResult.setBlock(x, (maxHeight - 1), z, Material.BEDROCK);
                // Next three layers are a mix of bedrock and netherrack
                for (int y = 2; y < 5; y++) {
                    double r = gen.noise(x, (maxHeight - y), z, 0.5, 0.5);
                    if (r > 0D) {
                        netherResult.setBlock(x, (maxHeight - y), z, Material.BEDROCK);
                    }
                }
                // Next three layers are a mix of netherrack and air
                for (int y = 5; y < 8; y++) {
                    double r = gen.noise(x, (double)maxHeight - y, z, 0.5, 0.5);
                    if (r > 0D) {
                        netherResult.setBlock(x, (maxHeight - y), z, Material.NETHERRACK);
                    } else {
                        netherResult.setBlock(x, (maxHeight - y), z, Material.AIR);
                    }
                }
                // Layer 8 may be glowstone
                double r = gen.noise(x, (double)maxHeight - 8, z, rand.nextFloat(), rand.nextFloat());
                if (r > 0.5D) {
                    // Have blobs of glowstone
                    switch (rand.nextInt(4)) {
                    case 1:
                        // Single block
                        netherResult.setBlock(x, (maxHeight - 8), z, Material.GLOWSTONE);
                        if (x < 14 && z < 14) {
                            netherResult.setBlock(x + 1, (maxHeight - 8), z + 1, Material.GLOWSTONE);
                            netherResult.setBlock(x + 2, (maxHeight - 8), z + 2, Material.GLOWSTONE);
                            netherResult.setBlock(x + 1, (maxHeight - 8), z + 2, Material.GLOWSTONE);
                            netherResult.setBlock(x + 1, (maxHeight - 8), z + 2, Material.GLOWSTONE);
                        }
                        break;
                    case 2:
                        // Stalatite
                        for (int i = 0; i < rand.nextInt(10); i++) {
                            netherResult.setBlock(x, (maxHeight - 8 - i), z, Material.GLOWSTONE);
                        }
                        break;
                    case 3:
                        netherResult.setBlock(x, (maxHeight - 8), z, Material.GLOWSTONE);
                        if (x > 3 && z > 3) {
                            for (int xx = 0; xx < 3; xx++) {
                                for (int zz = 0; zz < 3; zz++) {
                                    netherResult.setBlock(x - xx, (maxHeight - 8 - rand.nextInt(2)), z - xx, Material.GLOWSTONE);
                                }
                            }
                        }
                        break;
                    default:
                        netherResult.setBlock(x, (maxHeight - 8), z, Material.GLOWSTONE);
                    }
                    netherResult.setBlock(x, (maxHeight - 8), z, Material.GLOWSTONE);
                } else {
                    netherResult.setBlock(x, (maxHeight - 8), z, Material.AIR);
                }
            }

        }
    }

    class AcidBiomeGrid implements BiomeGrid {

        @Override
        public Biome getBiome(int x, int z) {
            return addon.getSettings().getDefaultBiome();
        }

        @Override
        public void setBiome(int x, int z, Biome bio) {
            // Nothing to do
        }

    }
}