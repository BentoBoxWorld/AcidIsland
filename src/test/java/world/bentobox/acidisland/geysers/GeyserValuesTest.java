package world.bentobox.acidisland.geysers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
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
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.managers.AddonsManager;

/**
 * @author tastybento
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GeyserValuesTest {

    private static final String YAML = """
            default: 2
            use-level-addon: false
            values:
              DIAMOND: 45
              EMERALD: 15
              COBBLESTONE: 1
              NOT_A_MATERIAL: 99
              NEGATIVE: -5
            """;

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

    private GeyserValues load(String yaml) throws IOException {
        Files.writeString(new File(dataFolder, "geyser-values.yml").toPath(), yaml);
        return new GeyserValues(addon);
    }

    @Test
    void testConfiguredValueWins() throws IOException {
        assertEquals(45, load(YAML).getValue(Material.DIAMOND));
    }

    @Test
    void testUnlistedMaterialFallsBackToDefault() throws IOException {
        GeyserValues values = load(YAML);
        assertEquals(2, values.getDefaultValue());
        assertEquals(2, values.getValue(Material.PUMPKIN));
    }

    @Test
    void testNegativeValuesAreFloored() throws IOException {
        // A negative worth would let a vent pay out for free
        assertEquals(0, load("values:\n  DIAMOND: -5\n").getValue(Material.DIAMOND));
    }

    @Test
    void testMissingFileUsesDefaults() {
        // No file on disk and nothing to save from the jar in a test - still usable
        GeyserValues values = new GeyserValues(addon);
        assertEquals(1, values.getDefaultValue());
        assertEquals(1, values.getValue(Material.DIAMOND));
    }

    @Test
    void testBrokenLevelHookFallsBackQuietly() throws IOException {
        // An addon named Level that is not the Level we know: the hook must
        // fail over to the values file rather than take the vent down
        when(addonsManager.getAddonByName("Level")).thenReturn(Optional.of(Mockito.mock(Addon.class)));
        GeyserValues values = load("default: 3\nvalues:\n  DIAMOND: 45\n");
        assertEquals(45, values.getValue(Material.DIAMOND));
        assertEquals(3, values.getValue(Material.PUMPKIN));
    }

    @Test
    void testStackValueIsPerItem() throws IOException {
        assertEquals(45, load(YAML).getValue(new ItemStack(Material.EMERALD, 3)));
    }

    @Test
    void testEntryValueOverridesMaterial() throws IOException {
        GeyserLootEntry entry = new GeyserLootEntry(Material.DIAMOND, null, 1, null, 1, 1, 7, Set.of());
        assertEquals(7, load(YAML).unitValue(entry));
    }

    @Test
    void testEntryWithoutValueUsesMaterial() throws IOException {
        GeyserLootEntry entry = new GeyserLootEntry(Material.DIAMOND, null, 1, null, 1, 1);
        assertEquals(45, load(YAML).unitValue(entry));
    }

    @Test
    void testCommandEntryIsFreeUnlessValued() throws IOException {
        GeyserLootEntry entry = new GeyserLootEntry(null, "say hi", 1, null, 1, 1);
        assertEquals(0, load(YAML).unitValue(entry));
    }
}
