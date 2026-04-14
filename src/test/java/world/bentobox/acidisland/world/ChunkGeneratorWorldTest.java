package world.bentobox.acidisland.world;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        MockBukkit.mock();
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
     */
    @Test
    void testGetDefaultPopulatorsWorld() {
        cg = new ChunkGeneratorWorld(addon);
        assertTrue(cg.getDefaultPopulators(mock(World.class)).isEmpty());
    }

}
