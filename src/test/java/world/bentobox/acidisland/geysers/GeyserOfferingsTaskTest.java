package world.bentobox.acidisland.geysers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import world.bentobox.acidisland.AISettings;
import world.bentobox.acidisland.AcidIsland;

/**
 * The offerings mechanic needs Minecraft 26.2's POTENT_SULFUR material, which
 * does not exist on the 1.21 test classpath, so these tests cover the guard
 * paths: the task must stay inert on unsupported servers and when disabled.
 *
 * @author tastybento
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GeyserOfferingsTaskTest {

    @Mock
    private AcidIsland addon;

    private AISettings settings;
    private ServerMock server;
    private MockedStatic<Bukkit> mockedBukkit;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        mockedBukkit = Mockito.mockStatic(Bukkit.class, Mockito.RETURNS_DEEP_STUBS);
        mockedBukkit.when(Bukkit::getMinecraftVersion).thenReturn("1.21.11");
        mockedBukkit.when(Bukkit::getServer).thenReturn(server);
        settings = new AISettings();
        when(addon.getSettings()).thenReturn(settings);
    }

    @AfterEach
    void tearDown() {
        mockedBukkit.closeOnDemand();
        MockBukkit.unmock();
    }

    @Test
    void testInertWithoutPotentSulfur() {
        // Enabled in config, but the material does not exist on this server
        GeyserOfferingsTask task = new GeyserOfferingsTask(addon);
        assertFalse(task.isRunning());
        assertTrue(task.getVents().isEmpty());
    }

    @Test
    void testInertWhenDisabled() {
        settings.setGeyserOfferings(false);
        GeyserOfferingsTask task = new GeyserOfferingsTask(addon);
        assertFalse(task.isRunning());
    }

    @Test
    void testCancelTasksWhenNeverScheduled() {
        GeyserOfferingsTask task = new GeyserOfferingsTask(addon);
        assertDoesNotThrow(task::cancelTasks);
    }
}
