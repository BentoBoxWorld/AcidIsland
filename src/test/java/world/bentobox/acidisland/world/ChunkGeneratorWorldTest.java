package world.bentobox.acidisland.world;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.WorldInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import world.bentobox.acidisland.AISettings;
import world.bentobox.acidisland.AcidIsland;

/**
 * @author tastybento
 *
 */
@ExtendWith(MockitoExtension.class)
public class ChunkGeneratorWorldTest {

    @Mock
    private AcidIsland addon;
    private ChunkGeneratorWorld cg;
    @Mock
    private World world;

    private AISettings settings;

    @BeforeEach
    void setUp() {
        MockBukkit.mock(new SulfurServerMock());
        // Settings
        settings = new AISettings();
        when(addon.getSettings()).thenReturn(settings);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.world.ChunkGeneratorWorld#canSpawn(org.bukkit.World, int, int)}.
     */
    @Test
    void testCanSpawnWorldIntInt() {
        cg = new ChunkGeneratorWorld(addon);
        assertTrue(cg.canSpawn(mock(World.class), 0, 1));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.world.ChunkGeneratorWorld#getDefaultPopulators(org.bukkit.World)}.
     * The 26.3 API on this classpath supports sulfur vents, so the vent populator is installed.
     */
    @Test
    void testGetDefaultPopulatorsWorld() {
        cg = new ChunkGeneratorWorld(addon);
        List<BlockPopulator> populators = cg.getDefaultPopulators(mock(World.class));
        assertEquals(1, populators.size());
        assertInstanceOf(SulfurVentPopulator.class, populators.get(0));
    }

    /**
     * Vents are placed by the populator, never by noise generation.
     */
    @Test
    void testGenerateNoiseNoSulfurVent() {
        cg = new ChunkGeneratorWorld(addon);
        WorldInfo worldInfo = mock(WorldInfo.class);
        when(worldInfo.getEnvironment()).thenReturn(Environment.NORMAL);
        when(worldInfo.getMinHeight()).thenReturn(-64);
        ChunkData chunkData = mock(ChunkData.class);
        assertDoesNotThrow(() -> cg.generateNoise(worldInfo, new Random(42), 0, 0, chunkData));
        verify(chunkData, never()).setBlock(anyInt(), anyInt(), anyInt(), eq(Material.MAGMA_BLOCK));
    }

}
