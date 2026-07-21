package world.bentobox.acidisland.geysers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * @author tastybento
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GeyserLootEntryTest {

    private ServerMock server;
    private MockedStatic<Bukkit> mockedBukkit;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        mockedBukkit = Mockito.mockStatic(Bukkit.class, Mockito.RETURNS_DEEP_STUBS);
        mockedBukkit.when(Bukkit::getMinecraftVersion).thenReturn("1.21.11");
        mockedBukkit.when(Bukkit::getServer).thenReturn(server);
    }

    @AfterEach
    void tearDown() {
        mockedBukkit.closeOnDemand();
        MockBukkit.unmock();
    }

    @Test
    void testParseItemEntry() {
        GeyserLootEntry entry = GeyserLootEntry.parse(
                Map.of("item", "RAW_IRON", "weight", 30, "channel", "MINERAL", "amount", Map.of("min", 1, "max", 3)));
        assertEquals(Material.RAW_IRON, entry.material());
        assertNull(entry.command());
        assertEquals(30, entry.weight());
        assertEquals("mineral", entry.channel());
        assertEquals(1, entry.min());
        assertEquals(3, entry.max());
        assertFalse(entry.isCommand());
    }

    @Test
    void testParseFixedAmount() {
        GeyserLootEntry entry = GeyserLootEntry.parse(Map.of("item", "KELP", "amount", 4));
        assertEquals(4, entry.min());
        assertEquals(4, entry.max());
        assertEquals(1, entry.weight());
        assertNull(entry.channel());
    }

    @Test
    void testParseCommandEntry() {
        GeyserLootEntry entry = GeyserLootEntry.parse(Map.of("command", "give %player% cod 1", "weight", 2));
        assertTrue(entry.isCommand());
        assertEquals("give %player% cod 1", entry.command());
        assertNull(entry.material());
        assertEquals(2, entry.weight());
    }

    @Test
    void testParseUnknownMaterial() {
        assertNull(GeyserLootEntry.parse(Map.of("item", "NOT_A_MATERIAL", "weight", 5)));
    }

    @Test
    void testParseMissingItemAndCommand() {
        assertNull(GeyserLootEntry.parse(Map.of("weight", 5)));
    }

    @Test
    void testParseBlankCommand() {
        assertNull(GeyserLootEntry.parse(Map.of("command", " ")));
    }

    @Test
    void testToItemStackRange() {
        GeyserLootEntry entry = new GeyserLootEntry(Material.IRON_NUGGET, null, 1, null, 2, 5);
        Random random = new Random(42);
        for (int i = 0; i < 50; i++) {
            ItemStack stack = entry.toItemStack(random);
            assertEquals(Material.IRON_NUGGET, stack.getType());
            assertTrue(stack.getAmount() >= 2 && stack.getAmount() <= 5,
                    "Amount out of range: " + stack.getAmount());
        }
    }

    @Test
    void testToItemStackFixedAmount() {
        GeyserLootEntry entry = new GeyserLootEntry(Material.DIAMOND, null, 1, "gems", 1, 1);
        assertEquals(1, entry.toItemStack(new Random(42)).getAmount());
    }
}
