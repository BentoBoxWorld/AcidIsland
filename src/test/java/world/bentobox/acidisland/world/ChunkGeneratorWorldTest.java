package world.bentobox.acidisland.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import world.bentobox.acidisland.AcidIsland;
import world.bentobox.acidisland.AISettings;

/**
 * @author tastybento
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class})
public class ChunkGeneratorWorldTest {

    @Mock
    private AcidIsland addon;
    private ChunkGeneratorWorld cg;
    @Mock
    private World world;
    private final Random random = new Random();
    @Mock
    private BiomeGrid biomeGrid;
    @Mock
    private AISettings settings;
    @Mock
    private ChunkData data;

    /**
     */
    @Before
    public void setUp() {
        // Bukkit
        PowerMockito.mockStatic(Bukkit.class);
        Server server = mock(Server.class);
        when(server.createChunkData(any())).thenReturn(data);
        when(Bukkit.getServer()).thenReturn(server);
        // World
        when(world.getEnvironment()).thenReturn(World.Environment.NORMAL);
        when(world.getMaxHeight()).thenReturn(256);
        // Settings
        when(addon.getSettings()).thenReturn(settings);
        when(settings.getSeaHeight()).thenReturn(0);
        when(settings.isNetherRoof()).thenReturn(true);

        // Instance
        cg = new ChunkGeneratorWorld(addon);
    }

    /**
     */
    @After
    public void tearDown() {
    }

    /**
     * Test method for {@link world.bentobox.bskyblock.generators.ChunkGeneratorWorld#generateChunkData(org.bukkit.World, java.util.Random, int, int, org.bukkit.generator.ChunkGenerator.BiomeGrid)}.
     */
    @Test
    public void testGenerateChunkDataWorldRandomIntIntBiomeGridOverworldVoid() {
        ChunkData cd = cg.generateChunkData(world, random, 0 , 0 , biomeGrid);
        assertEquals(data, cd);
        // Verifications
        // Default biome
        verify(settings).getDefaultBiome();
        verify(biomeGrid, times(1024)).setBiome(anyInt(), anyInt(), anyInt(), any());
        // Sea height
        verify(settings).getSeaHeight();
        // Void
        verify(cd, never()).setRegion(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), any(Material.class));
    }

    /**
     * Test method for {@link world.bentobox.bskyblock.generators.ChunkGeneratorWorld#generateChunkData(org.bukkit.World, java.util.Random, int, int, org.bukkit.generator.ChunkGenerator.BiomeGrid)}.
     */
    @Test
    public void testGenerateChunkDataWorldRandomIntIntBiomeGridOverworldSea() {
        // Set sea height
        when(settings.getSeaHeight()).thenReturn(10);
        // new instance
        cg = new ChunkGeneratorWorld(addon);
        ChunkData cd = cg.generateChunkData(world, random, 0 , 0 , biomeGrid);
        assertEquals(data, cd);
        // Verifications
        // Default biome
        verify(settings).getDefaultBiome();
        verify(biomeGrid, times(1024)).setBiome(anyInt(), anyInt(), anyInt(), any());
        // Sea height
        verify(settings, times(2)).getSeaHeight();
        // Water. Blocks = 16 x 16 x 11 because block 0
        verify(cd).setRegion(0, 0, 0, 16, 11, 16, Material.WATER);
    }

    /**
     * Test method for {@link world.bentobox.bskyblock.generators.ChunkGeneratorWorld#generateChunkData(org.bukkit.World, java.util.Random, int, int, org.bukkit.generator.ChunkGenerator.BiomeGrid)}.
     */
    @Test
    public void testGenerateChunkDataWorldRandomIntIntBiomeGridEnd() {
        when(world.getEnvironment()).thenReturn(World.Environment.THE_END);
        ChunkData cd = cg.generateChunkData(world, random, 0 , 0 , biomeGrid);
        assertEquals(data, cd);
        // Verifications
        // Default biome
        verify(settings).getDefaultEndBiome();
        // Set biome in end
        verify(biomeGrid, times(1024)).setBiome(anyInt(), anyInt(), anyInt(), any());
        // Sea height
        verify(settings).getSeaHeight();
        // Void
        verify(cd, never()).setRegion(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), any(Material.class));
    }

    /**
     * Test method for {@link world.bentobox.bskyblock.generators.ChunkGeneratorWorld#generateChunkData(org.bukkit.World, java.util.Random, int, int, org.bukkit.generator.ChunkGenerator.BiomeGrid)}.
     */
    @Test
    public void testGenerateChunkDataWorldRandomIntIntBiomeGridNetherWithRoof() {
        when(world.getEnvironment()).thenReturn(World.Environment.NETHER);
        ChunkData cd = cg.generateChunkData(world, random, 0 , 0 , biomeGrid);
        assertEquals(data, cd);
        // Verifications
        // Default biome
        verify(settings).getDefaultNetherBiome();
        // Nether roof check
        verify(settings).isNetherRoof();
        // Never set biome in nether
        verify(biomeGrid, times(1024)).setBiome(anyInt(), anyInt(), anyInt(), any());
        // Nether roof - at least bedrock layer
        verify(cd, atLeast(16 * 16)).setBlock(anyInt(), anyInt(), anyInt(), eq(Material.BEDROCK));
    }

    /**
     * Test method for {@link world.bentobox.bskyblock.generators.ChunkGeneratorWorld#generateChunkData(org.bukkit.World, java.util.Random, int, int, org.bukkit.generator.ChunkGenerator.BiomeGrid)}.
     */
    @Test
    public void testGenerateChunkDataWorldRandomIntIntBiomeGridNetherNoRoof() {
        when(settings.isNetherRoof()).thenReturn(false);
        when(world.getEnvironment()).thenReturn(World.Environment.NETHER);
        ChunkData cd = cg.generateChunkData(world, random, 0 , 0 , biomeGrid);
        assertEquals(data, cd);
        // Verifications
        // Nether roof check
        verify(settings).isNetherRoof();
        // Nether roof check
        verify(settings).isNetherRoof();
        // Never set biome in nether
        verify(biomeGrid, times(1024)).setBiome(anyInt(), anyInt(), anyInt(), any());
        // Nether roof - at least bedrock layer
        verify(cd, never()).setBlock(anyInt(), anyInt(), anyInt(), any(Material.class));
    }

    /**
     * Test method for {@link world.bentobox.bskyblock.generators.ChunkGeneratorWorld#canSpawn(org.bukkit.World, int, int)}.
     */
    @Test
    public void testCanSpawnWorldIntInt() {
        assertTrue(cg.canSpawn(mock(World.class), 0, 1));
    }

    /**
     * Test method for {@link world.bentobox.bskyblock.generators.ChunkGeneratorWorld#getDefaultPopulators(org.bukkit.World)}.
     */
    @Test
    public void testGetDefaultPopulatorsWorld() {
        assertTrue(cg.getDefaultPopulators(mock(World.class)).isEmpty());
    }

}
