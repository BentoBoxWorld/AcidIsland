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
import org.bukkit.block.data.BlockData;
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

    private record FloorMats(Material base, Material top) {
    }

    private final AcidIsland addon;
    private final Random rand = new Random();
    private final Map<Environment, WorldConfig> seaHeight = new EnumMap<>(Environment.class);
    private final Map<Vector, Material> roofChunk = new HashMap<>();
    private static final Map<Environment, FloorMats> floorMats = Map.of(Environment.NETHER,
            new FloorMats(Material.NETHERRACK, Material.SOUL_SAND), Environment.NORMAL,
            new FloorMats(Material.SANDSTONE, Material.SAND), Environment.THE_END,
            new FloorMats(Material.END_STONE, Material.END_STONE));
    // Only exist on Minecraft 26.2 and later; null on older servers, which disables sulfur vents
    private static final Material POTENT_SULFUR = Material.getMaterial("POTENT_SULFUR");
    private static final Material SULFUR = Material.getMaterial("SULFUR");
    private static final Material SULFUR_SPIKE = Material.getMaterial("SULFUR_SPIKE");
    // Depth of the vent cap below the sea surface. Must be 4 or less for the potent
    // sulfur to gas the surface, and sets the geyser height (5 x water depth)
    private static final int VENT_DEPTH = 3;
    // Offsets to the four horizontal neighbours
    private static final int[][] SIDES = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    private PerlinOctaveGenerator gen;
    // Generator-placed potent sulfur keeps its default 'dry' state and never activates
    // because worldgen blocks get no placement update, so the cap is pre-set to
    // 'dormant' (water above, magma below), which starts the eruption cycle
    private final BlockData ventCap;
    // Spikes are placed waterlogged so they do not leave air pockets in the sea
    private final BlockData ventSpike;

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
        ventCap = POTENT_SULFUR == null ? null : POTENT_SULFUR.createBlockData("[potent_sulfur_state=dormant]");
        ventSpike = SULFUR_SPIKE == null ? null : SULFUR_SPIKE.createBlockData("[waterlogged=true]");
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
            chunkData.setRegion(0, worldInfo.getMinHeight() + 1, 0, 16, sh + 1, 16, wc.waterBlock());
            // Add some noise
            if (addon.getSettings().isOceanFloor()) {
                chunkData.setRegion(0, worldInfo.getMinHeight(), 0, 16, worldInfo.getMinHeight() + 1, 16, Material.BEDROCK);
                addNoise(worldInfo, chunkX, chunkZ, chunkData);
            }
            addSulfurVent(worldInfo, random, chunkData, wc);
        }
        if (worldInfo.getEnvironment().equals(Environment.NETHER) && addon.getSettings().isNetherRoof()) {
            roofChunk.forEach((k,v) -> chunkData.setBlock(k.getBlockX(), worldInfo.getMaxHeight() + k.getBlockY(), k.getBlockZ(), v));
        }
    }

    /**
     * Randomly places a sulfur vent just below the sea surface in one of four natural
     * shapes. Every vent has at least one potent sulfur cap over a magma block, which
     * bubbles, gasses the surface with nausea, and periodically erupts as a geyser.
     * Minecraft 26.2+ only - does nothing on older servers.
     */
    private void addSulfurVent(@NonNull WorldInfo worldInfo, @NonNull Random random, @NonNull ChunkData chunkData,
            WorldConfig wc) {
        int capY = wc.seaHeight() - VENT_DEPTH;
        if (ventCap == null || SULFUR == null || !worldInfo.getEnvironment().equals(Environment.NORMAL)
                || !wc.waterBlock().equals(Material.WATER) || capY - 4 <= worldInfo.getMinHeight()
                || random.nextInt(100) >= Math.clamp(addon.getSettings().getSulfurVentChance(), 0, 100)) {
            return;
        }
        // Keep one block away from the chunk edge so shoulder blocks stay in this chunk
        int x = 1 + random.nextInt(14);
        int z = 1 + random.nextInt(14);
        switch (random.nextInt(4)) {
        case 0 -> ventChimney(chunkData, random, x, capY, z);
        case 1 -> ventMound(chunkData, random, x, capY, z);
        case 2 -> ventTwin(chunkData, random, x, capY, z);
        default -> ventCrag(chunkData, random, x, capY, z);
        }
    }

    /**
     * Places a potent sulfur cap over a magma block on a sulfur base - the working
     * heart of every vent
     */
    private void placeCap(ChunkData chunkData, int x, int capY, int z) {
        chunkData.setBlock(x, capY, z, ventCap);
        chunkData.setBlock(x, capY - 1, z, Material.MAGMA_BLOCK);
        chunkData.setBlock(x, capY - 2, z, SULFUR);
    }

    /**
     * A slim chimney with a single lopsided sulfur shoulder
     */
    private void ventChimney(ChunkData chunkData, Random random, int x, int capY, int z) {
        placeCap(chunkData, x, capY, z);
        int[] side = SIDES[random.nextInt(SIDES.length)];
        chunkData.setBlock(x + side[0], capY - 1, z + side[1], SULFUR);
    }

    /**
     * A rounded sulfur mound with the cap poking out of the top
     */
    private void ventMound(ChunkData chunkData, Random random, int x, int capY, int z) {
        placeCap(chunkData, x, capY, z);
        for (int[] side : SIDES) {
            if (random.nextInt(4) < 3) {
                chunkData.setBlock(x + side[0], capY - 1, z + side[1], SULFUR);
            }
            chunkData.setBlock(x + side[0], capY - 2, z + side[1], SULFUR);
        }
        // Ragged diagonal skirt
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dz = -1; dz <= 1; dz += 2) {
                if (random.nextBoolean()) {
                    chunkData.setBlock(x + dx, capY - 2, z + dz, SULFUR);
                }
            }
        }
    }

    /**
     * Two caps at different heights on a shared sulfur outcrop; the deeper cap sits
     * under four water blocks so its geyser erupts higher
     */
    private void ventTwin(ChunkData chunkData, Random random, int x, int capY, int z) {
        placeCap(chunkData, x, capY, z);
        int dx = random.nextBoolean() ? 1 : -1;
        int dz = random.nextBoolean() ? 1 : -1;
        placeCap(chunkData, x + dx, capY - 1, z + dz);
        // Join the two stacks at their corners
        chunkData.setBlock(x + dx, capY - 2, z, SULFUR);
        chunkData.setBlock(x, capY - 2, z + dz, SULFUR);
    }

    /**
     * A craggy vent with sulfur spikes growing from its shoulders
     */
    private void ventCrag(ChunkData chunkData, Random random, int x, int capY, int z) {
        placeCap(chunkData, x, capY, z);
        for (int[] side : SIDES) {
            if (random.nextBoolean()) {
                chunkData.setBlock(x + side[0], capY - 1, z + side[1], SULFUR);
                if (ventSpike != null && random.nextBoolean()) {
                    chunkData.setBlock(x + side[0], capY, z + side[1], ventSpike);
                }
            }
        }
    }

    private void addNoise(@NonNull WorldInfo worldInfo, int chunkX, int chunkZ, @NonNull ChunkData chunkData) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int n = (int)(25 * gen.noise((chunkX << 4) + (double)x, (chunkZ << 4) + (double)z, 0.5, 0.5, true));
                for (int y = worldInfo.getMinHeight(); y < 25 + n; y++) {
                    chunkData.setBlock(x, y, z, rand.nextBoolean() ? floorMats.get(worldInfo.getEnvironment()).top()
                            : floorMats.get(worldInfo.getEnvironment()).base());
                }
            }
        }
        // Make an solid base so sand doesn't fall into the void
        chunkData.setRegion(0, worldInfo.getMinHeight(), 0, 16, worldInfo.getMinHeight() + 1 , 16, Material.BEDROCK);
    }

    @Override
    public boolean shouldGenerateNoise() {
        return false;
    }
    @Override
    public boolean shouldGenerateSurface()  {
        return addon.getSettings().isOceanFloor();
    }
    @Override
    public boolean shouldGenerateCaves()  {
        return addon.getSettings().isMakeCaves();
    }
    @Override
    public boolean shouldGenerateDecorations()  {
        return addon.getSettings().isMakeDecorations();
    }
    @Override
    public boolean shouldGenerateMobs()  {
        return true;
    }
    @Override
    public boolean shouldGenerateStructures()  {
        return addon.getSettings().isMakeStructures();
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
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                setBlock(x, -1, z, Material.BEDROCK);
                makeBedrockLayers(x, z);
                makeNetherrackLayers(x, z);
                makeGlowstoneLayer(x, z);
            }
        }
    }

    /** Layers y=2..4: solid bedrock where noise > 0. */
    private void makeBedrockLayers(int x, int z) {
        for (int y = 2; y < 5; y++) {
            if (gen.noise(x, -y, z, 0.5, 0.5) > 0D) {
                setBlock(x, -y, z, Material.BEDROCK);
            }
        }
    }

    /** Layers y=5..7: netherrack or air based on noise. */
    private void makeNetherrackLayers(int x, int z) {
        for (int y = 5; y < 8; y++) {
            Material m = gen.noise(x, -y, z, 0.5, 0.5) > 0D ? Material.NETHERRACK : Material.AIR;
            setBlock(x, -y, z, m);
        }
    }

    /** Layer y=8: glowstone blobs or air based on noise. */
    private void makeGlowstoneLayer(int x, int z) {
        if (gen.noise(x, -8, z, rand.nextFloat(), rand.nextFloat()) > 0.5D) {
            placeGlowstoneBlob(x, z);
            setBlock(x, -8, z, Material.GLOWSTONE);
        } else {
            setBlock(x, -8, z, Material.AIR);
        }
    }

    private void placeGlowstoneBlob(int x, int z) {
        switch (rand.nextInt(4)) {
            case 1 -> {
                setBlock(x, -8, z, Material.GLOWSTONE);
                if (x < 14 && z < 14) {
                    setBlock(x + 1, -8, z + 1, Material.GLOWSTONE);
                    setBlock(x + 2, -8, z + 2, Material.GLOWSTONE);
                    setBlock(x + 1, -8, z + 2, Material.GLOWSTONE);
                }
            }
            case 2 -> {
                // Stalactite
                for (int i = 0; i < rand.nextInt(10); i++) {
                    setBlock(x, -8 - i, z, Material.GLOWSTONE);
                }
            }
            case 3 -> {
                setBlock(x, -8, z, Material.GLOWSTONE);
                if (x > 3 && z > 3) {
                    for (int xx = 0; xx < 3; xx++) {
                        for (int zz = 0; zz < 3; zz++) {
                            setBlock(x - xx, -8 - rand.nextInt(2), z - zz, Material.GLOWSTONE);
                        }
                    }
                }
            }
            default -> setBlock(x, -8, z, Material.GLOWSTONE);
        }
    }

    private void setBlock(int x, int y, int z, Material m) {
        roofChunk.put(new Vector(x, y, z), m);
    }

}
