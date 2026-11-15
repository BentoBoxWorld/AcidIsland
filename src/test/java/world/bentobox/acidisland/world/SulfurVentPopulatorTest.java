package world.bentobox.acidisland.world;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import world.bentobox.acidisland.AISettings;
import world.bentobox.acidisland.AcidIsland;

/**
 * Tests for {@link SulfurVentPopulator}
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SulfurVentPopulatorTest {

    @Mock
    private AcidIsland addon;
    @Mock
    private WorldInfo worldInfo;
    @Mock
    private LimitedRegion region;

    private SulfurServerMock server;
    private AISettings settings;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock(new SulfurServerMock());
        settings = new AISettings();
        settings.setSulfurVentChance(100);
        when(addon.getSettings()).thenReturn(settings);
        when(worldInfo.getEnvironment()).thenReturn(Environment.NORMAL);
        when(worldInfo.getMinHeight()).thenReturn(-64);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    /**
     * Sulfur vents need Minecraft 26.2 or later; the 26.3 API on this classpath has the blocks.
     */
    @Test
    void testIsSupported() {
        assertTrue(SulfurVentPopulator.isSupported());
    }

    /**
     * At 100% vent chance an overworld chunk gets a dormant potent sulfur cap over magma,
     * VENT_DEPTH (3) blocks below the sea surface.
     */
    @Test
    void testPopulatePlacesVentCap() {
        SulfurVentPopulator pop = new SulfurVentPopulator(addon);
        pop.populate(worldInfo, new Random(42), 0, 0, region);
        int capY = settings.getSeaHeight() - 3;
        verify(region, atLeastOnce()).setBlockData(anyInt(), eq(capY), anyInt(), eq(server.getPotentSulfur()));
        verify(region, atLeastOnce()).setType(anyInt(), eq(capY - 1), anyInt(), eq(Material.MAGMA_BLOCK));
    }

    /**
     * A vent chance of 0 never touches the region.
     */
    @Test
    void testPopulateNoVentAtZeroChance() {
        settings.setSulfurVentChance(0);
        SulfurVentPopulator pop = new SulfurVentPopulator(addon);
        assertDoesNotThrow(() -> pop.populate(worldInfo, new Random(42), 0, 0, region));
        verifyNoInteractions(region);
    }

    /**
     * Non-overworld environments never get vents.
     */
    @Test
    void testPopulateNoVentInNether() {
        when(worldInfo.getEnvironment()).thenReturn(Environment.NETHER);
        SulfurVentPopulator pop = new SulfurVentPopulator(addon);
        assertDoesNotThrow(() -> pop.populate(worldInfo, mock(Random.class), 0, 0, region));
        verifyNoInteractions(region);
    }
}
