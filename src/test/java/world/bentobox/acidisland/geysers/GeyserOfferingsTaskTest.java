package world.bentobox.acidisland.geysers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.persistence.PersistentDataContainer;
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
class GeyserOfferingsTaskTest {

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

    @Test
    void testOfferToVentWhenInert() {
        // An inert task must refuse offers without touching the item
        GeyserOfferingsTask task = new GeyserOfferingsTask(addon);
        assertFalse(task.offerToVent(thrownItem(UUID.randomUUID(), false)));
    }

    @Test
    void testIsOfferingThrownByPlayer() {
        assertTrue(GeyserOfferingsTask.isOffering(thrownItem(UUID.randomUUID(), false)));
    }

    @Test
    void testIsOfferingIgnoresDeathDrops() {
        // Death drops, block drops and mob drops have no thrower and must be left alone
        assertFalse(GeyserOfferingsTask.isOffering(thrownItem(null, false)));
    }

    @Test
    void testIsOfferingIgnoresRewards() {
        assertFalse(GeyserOfferingsTask.isOffering(thrownItem(UUID.randomUUID(), true)));
    }

    @Test
    void testDominantChannelOfNothing() {
        assertNull(GeyserOfferingsTask.dominantChannel(new GeyserOfferingsTask.VentOfferings()));
    }

    @Test
    void testDominantChannelIsTheMostFed() {
        GeyserOfferingsTask.VentOfferings offerings = new GeyserOfferingsTask.VentOfferings();
        offerings.bias.put("gems", 2);
        offerings.bias.put("mineral", 7);
        offerings.bias.put("forestry", 5);
        assertEquals("mineral", GeyserOfferingsTask.dominantChannel(offerings));
    }

    /**
     * @param thrower who threw the item, or null for a death or block drop
     * @param reward true to tag the item as one the vent just spewed
     * @return a mocked item entity
     */
    private Item thrownItem(UUID thrower, boolean reward) {
        Item item = Mockito.mock(Item.class);
        PersistentDataContainer pdc = Mockito.mock(PersistentDataContainer.class);
        // Generic type erasure means the PDC key and type must be matched loosely
        when(pdc.has(any(), any())).thenReturn(reward);
        when(item.getPersistentDataContainer()).thenReturn(pdc);
        when(item.getThrower()).thenReturn(thrower);
        return item;
    }
}
