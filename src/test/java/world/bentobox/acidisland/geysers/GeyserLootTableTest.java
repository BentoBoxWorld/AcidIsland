package world.bentobox.acidisland.geysers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
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
class GeyserLootTableTest {

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

    private static final String YAML = """
            loot:
              - {item: PRISMARINE_SHARD, weight: 10}
              - {item: DIAMOND, weight: 2, channel: gems}
              - {item: RAW_IRON, weight: 8, channel: mineral, amount: {min: 1, max: 3}}
              - {item: NOT_A_MATERIAL, weight: 100}
            """;

    private GeyserLootTable load(String yaml) throws InvalidConfigurationException {
        YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(yaml);
        return GeyserLootTable.parse(config);
    }

    @Test
    void testParseDropsInvalidEntries() throws InvalidConfigurationException {
        GeyserLootTable table = load(YAML);
        assertFalse(table.isEmpty());
        assertEquals(3, table.getLoot().size());
    }

    @Test
    void testParseEmpty() throws InvalidConfigurationException {
        GeyserLootTable table = load("loot: []");
        assertTrue(table.isEmpty());
        assertNull(table.roll(new Random(42), Map.of()));
    }

    @Test
    void testEffectiveWeightNoChannel() throws InvalidConfigurationException {
        GeyserLootTable table = load(YAML);
        GeyserLootEntry plain = table.getLoot().get(0);
        // Bias never changes un-channeled entries
        assertEquals(10.0, table.effectiveWeight(plain, Map.of("gems", 100)));
    }

    @Test
    void testEffectiveWeightBias() throws InvalidConfigurationException {
        GeyserLootTable table = load(YAML);
        GeyserLootEntry gems = table.getLoot().get(1);
        assertEquals(2.0, table.effectiveWeight(gems, Map.of()));
        // Each offering point adds +50% of the base weight
        assertEquals(2.0 * (1 + 0.5 * 4), table.effectiveWeight(gems, Map.of("gems", 4)));
        // Bias on another channel does nothing
        assertEquals(2.0, table.effectiveWeight(gems, Map.of("mineral", 4)));
    }

    @Test
    void testRollHonorsBias() throws InvalidConfigurationException {
        GeyserLootTable table = load(YAML);
        Random random = new Random(42);
        // Overwhelming gems bias means gems should dominate the rolls
        int gems = 0;
        for (int i = 0; i < 100; i++) {
            GeyserLootEntry entry = table.roll(random, Map.of("gems", 1000));
            if ("gems".equals(entry.channel())) {
                gems++;
            }
        }
        assertTrue(gems > 90, "Expected gems to dominate but rolled only " + gems);
    }

    @Test
    void testRollUnbiased() throws InvalidConfigurationException {
        GeyserLootTable table = load(YAML);
        Random random = new Random(42);
        for (int i = 0; i < 100; i++) {
            assertTrue(table.getLoot().contains(table.roll(random, Map.of())));
        }
    }

    @Test
    void testCategorizeGems() {
        assertEquals("gems", GeyserLootTable.categorize(Material.DIAMOND));
        assertEquals("gems", GeyserLootTable.categorize(Material.EMERALD_BLOCK));
        assertEquals("gems", GeyserLootTable.categorize(Material.AMETHYST_SHARD));
        assertEquals("gems", GeyserLootTable.categorize(Material.ENDER_PEARL));
    }

    @Test
    void testCategorizeNether() {
        assertEquals("nether", GeyserLootTable.categorize(Material.NETHERRACK));
        assertEquals("nether", GeyserLootTable.categorize(Material.QUARTZ));
        assertEquals("nether", GeyserLootTable.categorize(Material.BLAZE_ROD));
        assertEquals("nether", GeyserLootTable.categorize(Material.MAGMA_BLOCK));
    }

    @Test
    void testCategorizeMineral() {
        assertEquals("mineral", GeyserLootTable.categorize(Material.IRON_ORE));
        assertEquals("mineral", GeyserLootTable.categorize(Material.RAW_COPPER));
        assertEquals("mineral", GeyserLootTable.categorize(Material.GOLD_INGOT));
        assertEquals("mineral", GeyserLootTable.categorize(Material.STONE));
        assertEquals("mineral", GeyserLootTable.categorize(Material.COAL));
    }

    @Test
    void testCategorizeForestry() {
        assertEquals("forestry", GeyserLootTable.categorize(Material.OAK_LOG));
        assertEquals("forestry", GeyserLootTable.categorize(Material.SPRUCE_PLANKS));
        assertEquals("forestry", GeyserLootTable.categorize(Material.BAMBOO));
        assertEquals("forestry", GeyserLootTable.categorize(Material.KELP));
    }

    @Test
    void testCategorizeHusbandry() {
        assertEquals("husbandry", GeyserLootTable.categorize(Material.WHITE_WOOL));
        assertEquals("husbandry", GeyserLootTable.categorize(Material.LEATHER));
        assertEquals("husbandry", GeyserLootTable.categorize(Material.STRING));
        assertEquals("husbandry", GeyserLootTable.categorize(Material.COOKED_COD));
    }

    @Test
    void testCategorizeNone() {
        assertNull(GeyserLootTable.categorize(Material.TNT));
        assertNull(GeyserLootTable.categorize(Material.GLASS));
        assertNull(GeyserLootTable.categorize(Material.DIRT));
    }
}
