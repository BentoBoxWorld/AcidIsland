package world.bentobox.acidisland.geysers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
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
import world.bentobox.acidisland.geysers.GeyserLootTable.Award;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.managers.AddonsManager;

/**
 * The payout has to be a fair trade with the files AcidIsland actually ships,
 * not just with a toy table: a diamond must come back in gems rather than in a
 * heap of kelp, and a stack of cobble must not come back as a diamond.
 * <p>
 * SULFUR and CINNABAR do not exist on the 1.21 test classpath, so the shipped
 * table loses those entries here - which only makes the trade harder to
 * balance, not easier.
 *
 * @author tastybento
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GeyserPayoutTest {

    /** How many rewards a single eruption may spew, matching config.yml. */
    private static final int MAX_REWARDS = 12;
    /** How rich a single reward may be next to the richest item offered, matching config.yml. */
    private static final int CEILING_MULTIPLIER = 8;

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
    private GeyserLootTable table;
    private GeyserValues values;

    @BeforeEach
    void setUp() throws IOException, InvalidConfigurationException {
        server = MockBukkit.mock();
        mockedBukkit = Mockito.mockStatic(Bukkit.class, Mockito.RETURNS_DEEP_STUBS);
        mockedBukkit.when(Bukkit::getMinecraftVersion).thenReturn("1.21.11");
        mockedBukkit.when(Bukkit::getServer).thenReturn(server);
        when(addon.getDataFolder()).thenReturn(dataFolder);
        when(addon.getPlugin()).thenReturn(plugin);
        when(plugin.getAddonsManager()).thenReturn(addonsManager);
        when(addonsManager.getAddonByName("Level")).thenReturn(Optional.empty());
        Files.writeString(new File(dataFolder, "geyser-values.yml").toPath(), shipped("geyser-values.yml"));
        values = new GeyserValues(addon);
        YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(shipped("geyser-loot.yml"));
        table = GeyserLootTable.parse(config);
    }

    @AfterEach
    void tearDown() {
        mockedBukkit.closeOnDemand();
        MockBukkit.unmock();
    }

    /**
     * @param name resource in src/main/resources
     * @return its contents
     */
    private String shipped(String name) throws IOException {
        try (InputStream in = GeyserPayoutTest.class.getClassLoader().getResourceAsStream(name)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Build the offer a vent would make of an offering, the way the task does:
     * worth-weighted channel bias, total worth as the budget, and a ceiling off
     * the richest single item.
     *
     * @param offering material to amount thrown in
     * @return the offer
     */
    private GeyserOffer offer(Map<Material, Integer> offering) {
        int budget = 0;
        int top = 0;
        Map<String, Integer> bias = new HashMap<>();
        for (Map.Entry<Material, Integer> e : offering.entrySet()) {
            int worth = values.getValue(e.getKey()) * e.getValue();
            budget += worth;
            top = Math.max(top, values.getValue(e.getKey()));
            String channel = GeyserLootTable.categorize(e.getKey());
            if (channel != null) {
                bias.merge(channel, worth, Integer::sum);
            }
        }
        return new GeyserOffer(bias, offering.keySet(), budget, Math.max(1, top * CEILING_MULTIPLIER));
    }

    /**
     * @param offer what the vent was fed
     * @return total worth of one payout
     */
    private int payoutValue(GeyserOffer offer, Random random) {
        int total = 0;
        for (Award award : table.plan(random, offer, values, MAX_REWARDS)) {
            total += values.unitValue(award.entry()) * award.amount();
        }
        return total;
    }

    @Test
    void testShippedFilesLoad() {
        assertFalse(table.isEmpty());
        assertEquals(45, values.getValue(Material.DIAMOND));
    }

    @Test
    void testDiamondIsAFairTrade() {
        Random random = new Random(42);
        GeyserOffer diamond = offer(Map.of(Material.DIAMOND, 1));
        for (int i = 0; i < 200; i++) {
            int paid = payoutValue(diamond, random);
            // Never more than was offered, and never a derisory handful
            assertTrue(paid <= diamond.budget(), "Paid out " + paid + " for a diamond worth " + diamond.budget());
            assertTrue(paid >= diamond.budget() / 2, "Paid out only " + paid + " for a diamond");
        }
    }

    @Test
    void testDiamondIsAnsweredInGems() {
        Random random = new Random(42);
        GeyserOffer diamond = offer(Map.of(Material.DIAMOND, 1));
        int gems = 0;
        for (int i = 0; i < 200; i++) {
            if (table.plan(random, diamond, values, MAX_REWARDS).stream()
                    .anyMatch(a -> "gems".equals(a.entry().channel()))) {
                gems++;
            }
        }
        assertTrue(gems > 100, "A diamond should mostly come back in gems, but only " + gems + " of 200 payouts did");
    }

    @Test
    void testCobbleCannotBuyGems() {
        Random random = new Random(42);
        // A cobble generator is infinite, so a stack of it must not print gems
        GeyserOffer cobble = offer(Map.of(Material.COBBLESTONE, 64));
        for (int i = 0; i < 200; i++) {
            for (Award award : table.plan(random, cobble, values, MAX_REWARDS)) {
                assertTrue(values.unitValue(award.entry()) <= cobble.ceiling(),
                        "A stack of cobble bought a " + award.entry().material());
            }
        }
    }

    @Test
    void testPayoutNeverExceedsTheOffering() {
        Random random = new Random(7);
        for (int budget = 1; budget <= 500; budget += 7) {
            GeyserOffer any = new GeyserOffer(Map.of(), Set.of(), budget);
            assertTrue(payoutValue(any, random) <= budget, "Overpaid on a budget of " + budget);
        }
    }

    @Test
    void testBigOfferingIsNotWasted() {
        Random random = new Random(7);
        // A diamond chestplate is worth far more than 12 average rewards, so the
        // vent has to scale amounts up rather than pocket the difference
        GeyserOffer gear = offer(Map.of(Material.DIAMOND_CHESTPLATE, 1));
        for (int i = 0; i < 100; i++) {
            int paid = payoutValue(gear, random);
            assertTrue(paid >= gear.budget() * 0.75, "Paid out only " + paid + " of " + gear.budget());
        }
    }

    @Test
    void testTinyOfferingStillPaysSomething() {
        Random random = new Random(7);
        // One kelp is worth 1 - the vent must still find something to give back
        assertFalse(table.plan(random, offer(Map.of(Material.KELP, 1)), values, MAX_REWARDS).isEmpty());
    }

    @Test
    void testMagmaTransmutesIntoObsidian() {
        Random random = new Random(42);
        GeyserOffer magma = offer(Map.of(Material.MAGMA_BLOCK, 16));
        int obsidian = 0;
        for (int i = 0; i < 100; i++) {
            List<Award> awards = table.plan(random, magma, values, MAX_REWARDS);
            if (awards.stream().anyMatch(a -> a.entry().material() == Material.OBSIDIAN)) {
                obsidian++;
            }
        }
        assertTrue(obsidian > 50, "Magma should reliably make obsidian, but only " + obsidian + " of 100 payouts did");
    }

    @Test
    void testBonesAndGunpowderTransmuteIntoRecords() {
        Random random = new Random(42);
        // A hopper full of skeleton and creeper drops, offered to the vent.
        // A record is worth 40 against a ceiling of 32, so only the written-down
        // transmutation can produce one - which is the point of from:
        GeyserOffer drops = offer(Map.of(Material.BONE, 32, Material.GUNPOWDER, 16));
        int discs = 0;
        for (int i = 0; i < 100; i++) {
            List<Award> awards = table.plan(random, drops, values, MAX_REWARDS);
            if (awards.stream().anyMatch(a -> a.entry().material().name().startsWith("MUSIC_DISC"))) {
                discs++;
            }
        }
        assertTrue(discs > 20, "Bones and gunpowder should sometimes make a record, but only " + discs + " of 100 did");
    }

    @Test
    void testUnmatchedPayoutIgnoresWorth() {
        // With match-value off the vent rolls once per item, whatever it is worth
        Random random = new Random(42);
        List<Award> awards = table.plan(random, GeyserOffer.of(Map.of()), null, 5);
        assertEquals(5, awards.size());
    }
}
