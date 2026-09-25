package world.bentobox.acidisland.world;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Random;

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

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        AISettings settings = new AISettings();
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
     * Sulfur vents require Minecraft 26.2 or later, and POTENT_SULFUR does not exist
     * on this test classpath
     */
    @Test
    void testIsSupportedOnOldServer() {
        assertFalse(SulfurVentPopulator.isSupported());
    }

    /**
     * On an old server the populator must not touch the region, even at 100% vent chance
     */
    @Test
    void testPopulateNoVentOnOldServer() {
        SulfurVentPopulator pop = new SulfurVentPopulator(addon);
        assertDoesNotThrow(() -> pop.populate(worldInfo, new Random(42), 0, 0, region));
        verifyNoInteractions(region);
    }

    /**
     * Non-overworld environments never get vents
     */
    @Test
    void testPopulateNoVentInNether() {
        when(worldInfo.getEnvironment()).thenReturn(Environment.NETHER);
        SulfurVentPopulator pop = new SulfurVentPopulator(addon);
        assertDoesNotThrow(() -> pop.populate(worldInfo, mock(Random.class), 0, 0, region));
        verifyNoInteractions(region);
    }
}
