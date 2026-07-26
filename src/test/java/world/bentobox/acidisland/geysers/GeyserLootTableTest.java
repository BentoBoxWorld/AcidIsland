package world.bentobox.acidisland.geysers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import world.bentobox.acidisland.AcidIsland;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.managers.AddonsManager;

/**
 * @author tastybento
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GeyserLootTableTest {

    @Mock
    private AcidIsland addon;
    @Mock
    private BentoBox plugin;
    @Mock
    private AddonsManager addonsManager;

    @TempDir
    File dataFolder;

    private ServerMock server;
    private MockedStatic<Bukkit> mockedBukkit;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        mockedBukkit = Mockito.mockStatic(Bukkit.class, Mockito.RETURNS_DEEP_STUBS);
        mockedBukkit.when(Bukkit::getMinecraftVersion).thenReturn("1.21.11");
        mockedBukkit.when(Bukkit::getServer).thenReturn(server);
        when(addon.getDataFolder()).thenReturn(dataFolder);
        when(addon.getPlugin()).thenReturn(plugin);
        when(plugin.getAddonsManager()).thenReturn(addonsManager);
        when(addonsManager.getAddonByName("Level")).thenReturn(Optional.empty());
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

    private static final String RECIPE_YAML = """
            loot:
              - {item: OBSIDIAN, weight: 5, from: [MAGMA_BLOCK, BASALT]}
              - {item: KELP, weight: 5}
            """;

    private static final String VALUES_YAML = """
            default: 1
            use-level-addon: false
            values:
              DIAMOND: 45
              RAW_IRON: 5
              PRISMARINE_SHARD: 3
            """;

    /**
     * @return worth lookup backed by a values file in a temporary data folder
     */
    private GeyserValues values() throws IOException {
        Files.writeString(new File(dataFolder, "geyser-values.yml").toPath(), VALUES_YAML);
        return new GeyserValues(addon);
    }

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
        // An offering that was nothing but gems pulls its channel the hardest
        assertEquals(2.0 * 4, table.effectiveWeight(gems, Map.of("gems", 4)));
        // Half the worth in gems pulls half as hard
        assertEquals(2.0 * 2.5, table.effectiveWeight(gems, Map.of("gems", 10, "mineral", 10)));
        // Bias on another channel does nothing
        assertEquals(2.0, table.effectiveWeight(gems, Map.of("mineral", 4)));
    }

    @Test
    void testEffectiveWeightBiasIsShareNotSize() throws InvalidConfigurationException {
        GeyserLootTable table = load(YAML);
        GeyserLootEntry gems = table.getLoot().get(1);
        // One diamond's worth of gems pulls as hard as a hoard of it - what
        // matters is that the offering was all gems, not how big it was
        assertEquals(table.effectiveWeight(gems, Map.of("gems", 45)),
                table.effectiveWeight(gems, Map.of("gems", 4500)));
    }

    @Test
    void testRollHonorsBias() throws InvalidConfigurationException {
        GeyserLootTable table = load(YAML);
        Random random = new Random(42);
        // Gems are 2 of 20 base weight in this table; an all-gems offering
        // quadruples that, so they should turn up far more often than 10%
        int gems = 0;
        for (int i = 0; i < 1000; i++) {
            GeyserLootEntry entry = table.roll(random, Map.of("gems", 1000));
            if ("gems".equals(entry.channel())) {
                gems++;
            }
        }
        assertTrue(gems > 250, "Expected an all-gems offering to pull gems hard, but rolled only " + gems + "/1000");
    }

    @Test
    void testEffectiveWeightCeilingBlocksRichRewards() throws InvalidConfigurationException, IOException {
        GeyserLootTable table = load(YAML);
        GeyserValues values = values();
        GeyserLootEntry diamond = table.getLoot().get(1);
        // Plenty of worth offered, but nothing rich enough to justify a diamond
        assertEquals(0.0, table.effectiveWeight(diamond, new GeyserOffer(Map.of(), Set.of(), 500, 8), values));
        assertTrue(table.effectiveWeight(diamond, new GeyserOffer(Map.of(), Set.of(), 500, 360), values) > 0);
    }

    @Test
    void testTransmutationIgnoresTheCeiling() throws InvalidConfigurationException, IOException {
        GeyserLootTable table = load(RECIPE_YAML);
        GeyserValues values = values();
        GeyserLootEntry obsidian = table.getLoot().get(0);
        // Obsidian is not in the test values file, so it falls back to 1 - make
        // the ceiling bite by asking about an entry with an explicit value
        GeyserLootEntry rich = new GeyserLootEntry(Material.OBSIDIAN, null, 5, null, 1, 1, 500,
                Set.of(Material.MAGMA_BLOCK));
        GeyserOffer offer = new GeyserOffer(Map.of(), Set.of(Material.MAGMA_BLOCK), 1000, 8);
        assertTrue(table.effectiveWeight(rich, offer, values) > 0, "A named transmutation must ignore the ceiling");
        assertTrue(table.effectiveWeight(obsidian, offer, values) > 0);
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
    void testEffectiveWeightRecipeBoost() throws InvalidConfigurationException {
        GeyserLootTable table = load(RECIPE_YAML);
        GeyserLootEntry obsidian = table.getLoot().get(0);
        // Nothing offered that transmutes into it
        assertEquals(5.0, table.effectiveWeight(obsidian, GeyserOffer.of(Map.of()), null));
        // Offering magma makes obsidian eight times as likely
        GeyserOffer magma = new GeyserOffer(Map.of(), Set.of(Material.MAGMA_BLOCK), GeyserOffer.UNLIMITED);
        assertEquals(40.0, table.effectiveWeight(obsidian, magma, null));
    }

    @Test
    void testEffectiveWeightUnaffordableIsNeverRolled() throws InvalidConfigurationException, IOException {
        GeyserLootTable table = load(YAML);
        GeyserValues values = values();
        GeyserLootEntry diamond = table.getLoot().get(1);
        // A vent that owes 10 cannot pay out a diamond worth 45
        assertEquals(0.0, table.effectiveWeight(diamond, new GeyserOffer(Map.of(), Set.of(), 10), values));
        // One that owes 45 can, and is not damped because it is not a pittance
        assertEquals(2.0, table.effectiveWeight(diamond, new GeyserOffer(Map.of(), Set.of(), 45), values));
    }

    @Test
    void testEffectiveWeightPittanceIsDamped() throws InvalidConfigurationException, IOException {
        GeyserLootTable table = load(YAML);
        // Raw iron is worth 5 against a budget of 450 - the vent leans away from it
        GeyserLootEntry iron = table.getLoot().get(2);
        assertEquals(8.0 * 0.25, table.effectiveWeight(iron, new GeyserOffer(Map.of(), Set.of(), 450), values()));
    }

    @Test
    void testRollReturnsNullWhenNothingIsAffordable() throws InvalidConfigurationException, IOException {
        GeyserLootTable table = load(YAML);
        // A budget of zero can buy nothing in this table
        assertNull(table.roll(new Random(42), new GeyserOffer(Map.of(), Set.of(), 0), values()));
    }

    @Test
    void testRollStaysWithinBudget() throws InvalidConfigurationException, IOException {
        GeyserLootTable table = load(YAML);
        GeyserValues values = values();
        Random random = new Random(42);
        for (int i = 0; i < 100; i++) {
            GeyserLootEntry entry = table.roll(random, new GeyserOffer(Map.of(), Set.of(), 6), values);
            assertTrue(values.unitValue(entry) <= 6, "Rolled " + entry.material() + " the vent cannot afford");
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
