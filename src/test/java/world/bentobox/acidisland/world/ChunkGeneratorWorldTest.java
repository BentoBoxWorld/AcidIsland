package world.bentobox.acidisland.world;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import world.bentobox.acidisland.AISettings;
import world.bentobox.acidisland.AcidIsland;
import world.bentobox.acidisland.mocks.ServerMocks;

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

    private AISettings settings;
    @Mock
    private ChunkData data;

    @BeforeClass
    public static void beforeClass() {
        ServerMocks.newServer();
    }

    @Before
    public void setUp() {
        // World
        when(world.getEnvironment()).thenReturn(World.Environment.NORMAL);
        when(world.getMaxHeight()).thenReturn(256);
        // Settings
        settings = new AISettings();
        when(addon.getSettings()).thenReturn(settings);
    }

    /**
     * Test method for {@link world.bentobox.bskyblock.generators.ChunkGeneratorWorld#canSpawn(org.bukkit.World, int, int)}.
     */
    @Test
    public void testCanSpawnWorldIntInt() {
        // Instance
        cg = new ChunkGeneratorWorld(addon);
        assertTrue(cg.canSpawn(mock(World.class), 0, 1));
    }

    /**
     * Test method for {@link world.bentobox.bskyblock.generators.ChunkGeneratorWorld#getDefaultPopulators(org.bukkit.World)}.
     */
    @Test
    public void testGetDefaultPopulatorsWorld() {
        // Instance
        cg = new ChunkGeneratorWorld(addon);
        assertTrue(cg.getDefaultPopulators(mock(World.class)).isEmpty());
    }

}
