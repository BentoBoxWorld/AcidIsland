package world.bentobox.acidisland.world;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.PerlinOctaveGenerator;
import org.eclipse.jdt.annotation.NonNull;

import world.bentobox.acidisland.AcidIsland;

/**
 * Generates the AcidIsland world
 * @author tastybento
 *
 */
public class ChunkGeneratorWorld extends ChunkGenerator {

    private final AcidIsland addon;
    private final Random rand = new Random();
    private final Map<Environment, WorldConfig> seaHeight = new EnumMap<>(Environment.class);
    private final Map<Vector, Material> roofChunk = new HashMap<>();
    private PerlinOctaveGenerator gen;

    private record WorldConfig(int seaHeight, Material waterBlock) {}

    /**
     * @param addon - addon
     */
    public ChunkGeneratorWorld(AcidIsland addon) {
        super();
        this.addon = addon;
        seaHeight.put(Environment.NORMAL, new WorldConfig(addon.getSettings().getSeaHeight(), addon.getSettings().getWaterBlock()));
        seaHeight.put(Environment.NETHER, new WorldConfig(addon.getSettings().getNetherSeaHeight(), addon.getSettings().getNetherWaterBlock()));
        seaHeight.put(Environment.THE_END, new WorldConfig(addon.getSettings().getEndSeaHeight(), addon.getSettings().getEndWaterBlock()));
        rand.setSeed(System.currentTimeMillis());
        gen = new PerlinOctaveGenerator((long) (rand.nextLong() * rand.nextGaussian()), 8);
        gen.setScale(1.0/30.0);
        makeNetherRoof();
    }

    @Override
    public void generateNoise(@NonNull WorldInfo worldInfo, @NonNull Random random, int chunkX, int chunkZ, @NonNull ChunkData chunkData) {
        WorldConfig wc = seaHeight.get(worldInfo.getEnvironment());
        int sh = wc.seaHeight();
        if (sh > worldInfo.getMinHeight()) {
            chunkData.setRegion(0, worldInfo.getMinHeight(), 0, 16, worldInfo.getMinHeight() + 1, 16, Material.BEDROCK);
            chunkData.setRegion(0, worldInfo.getMinHeight() + 1, 0, 16, sh + 1, 16, wc.waterBlock());
            // Add some noise
            addNoise(worldInfo, chunkX, chunkZ, chunkData);
        }
        if (worldInfo.getEnvironment().equals(Environment.NETHER) && addon.getSettings().isNetherRoof()) {
            roofChunk.forEach((k,v) -> chunkData.setBlock(k.getBlockX(), worldInfo.getMaxHeight() + k.getBlockY(), k.getBlockZ(), v));
        }
    }

    private void addNoise(@NonNull WorldInfo worldInfo, int chunkX, int chunkZ, @NonNull ChunkData chunkData) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int n = (int)(25 * gen.noise((chunkX << 4) + (double)x, (chunkZ << 4) + (double)z, 0.5, 0.5, true));
                for (int y = worldInfo.getMinHeight(); y < 25 + n; y++) {
                    chunkData.setBlock(x, y, z, rand.nextBoolean() ? Material.SAND : Material.SANDSTONE);
                }
            }
        }
    }

    @Override
    public boolean shouldGenerateNoise() {
        return false;
    }
    @Override
    public boolean shouldGenerateSurface()  {
        return true;
    }
    @Override
    public boolean shouldGenerateCaves()  {
        return true;
    }
    @Override
    public boolean shouldGenerateDecorations()  {
        return true;
    }
    @Override
    public boolean shouldGenerateMobs()  {
        return true;
    }
    @Override
    public boolean shouldGenerateStructures()  {
        return true;
    }

    @Override
    public BiomeProvider getDefaultBiomeProvider(WorldInfo worldInfo) {
        return addon.getBiomeProvider();
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
    private void makeNetherRoof() {

        // Make the roof - common across the world
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // Do the ceiling
                setBlock(x, -1, z, Material.BEDROCK);
                // Next three layers are a mix of bedrock and netherrack
                for (int y = 2; y < 5; y++) {
                    double r = gen.noise(x, - y, z, 0.5, 0.5);
                    if (r > 0D) {
                        setBlock(x, - y, z, Material.BEDROCK);
                    }
                }
                // Next three layers are a mix of netherrack and air
                for (int y = 5; y < 8; y++) {
                    double r = gen.noise(x, - y, z, 0.5, 0.5);
                    if (r > 0D) {
                        setBlock(x, -y, z, Material.NETHERRACK);
                    } else {
                        setBlock(x, -y, z, Material.AIR);
                    }
                }
                // Layer 8 may be glowstone
                double r = gen.noise(x, - 8, z, rand.nextFloat(), rand.nextFloat());
                if (r > 0.5D) {
                    // Have blobs of glowstone
                    switch (rand.nextInt(4)) {
                    case 1:
                        // Single block
                        setBlock(x, -8, z, Material.GLOWSTONE);
                        if (x < 14 && z < 14) {
                            setBlock(x + 1, -8, z + 1, Material.GLOWSTONE);
                            setBlock(x + 2, -8, z + 2, Material.GLOWSTONE);
                            setBlock(x + 1, -8, z + 2, Material.GLOWSTONE);
                            setBlock(x + 1, -8, z + 2, Material.GLOWSTONE);
                        }
                        break;
                    case 2:
                        // Stalatite
                        for (int i = 0; i < rand.nextInt(10); i++) {
                            setBlock(x, - 8 - i, z, Material.GLOWSTONE);
                        }
                        break;
                    case 3:
                        setBlock(x, -8, z, Material.GLOWSTONE);
                        if (x > 3 && z > 3) {
                            for (int xx = 0; xx < 3; xx++) {
                                for (int zz = 0; zz < 3; zz++) {
                                    setBlock(x - xx, - 8 - rand.nextInt(2), z - xx, Material.GLOWSTONE);
                                }
                            }
                        }
                        break;
                    default:
                        setBlock(x, -8, z, Material.GLOWSTONE);
                    }
                    setBlock(x, -8, z, Material.GLOWSTONE);
                } else {
                    setBlock(x, -8, z, Material.AIR);
                }
            }

        }
    }

    private void setBlock(int x, int y, int z, Material m) {
        roofChunk.put(new Vector(x, y, z), m);
    }

}
