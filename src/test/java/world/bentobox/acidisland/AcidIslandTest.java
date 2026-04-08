package world.bentobox.acidisland;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import world.bentobox.acidisland.world.ChunkGeneratorWorld;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.Settings;
import world.bentobox.bentobox.api.addons.AddonDescription;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.AbstractDatabaseHandler;
import world.bentobox.bentobox.database.DatabaseSetup;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.AddonsManager;
import world.bentobox.bentobox.managers.CommandsManager;
import world.bentobox.bentobox.managers.FlagsManager;
import world.bentobox.bentobox.managers.IslandWorldManager;
import world.bentobox.bentobox.managers.IslandsManager;
import world.bentobox.bentobox.managers.RanksManager;

/**
 * @author tastybento
 *
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AcidIslandTest {

    /**
     * Class under test
     */
    private AcidIsland addon;

    @Mock
    private User user;
    @Mock
    private IslandsManager im;
    @Mock
    private Island island;
    @Mock
    private BentoBox plugin;
    @Mock
    private FlagsManager fm;
    @Mock
    private Settings settings;
    @Mock
    private RanksManager rm;

    private static AbstractDatabaseHandler<Object> h;
    private static MockedStatic<DatabaseSetup> mockedDbSetup;

    private ServerMock server;
    private MockedStatic<Bukkit> mockedBukkit;

    @SuppressWarnings("unchecked")
    @BeforeAll
    public static void beforeAll() throws Exception {
        // This has to be done beforeAll otherwise the tests will interfere with each other
        h = mock(AbstractDatabaseHandler.class);
        // Database
        mockedDbSetup = Mockito.mockStatic(DatabaseSetup.class);
        DatabaseSetup dbSetup = mock(DatabaseSetup.class);
        mockedDbSetup.when(DatabaseSetup::getDatabase).thenReturn(dbSetup);
        when(dbSetup.getHandler(any())).thenReturn(h);
        when(h.saveObject(any())).thenReturn(CompletableFuture.completedFuture(true));
    }

    @AfterAll
    public static void afterAll() {
        mockedDbSetup.close();
    }

    @AfterEach
    public void tearDown() throws IOException {
        User.clearUsers();
        if (mockedBukkit != null) {
            mockedBukkit.closeOnDemand();
        }
        MockBukkit.unmock();
        deleteAll(new File("database"));
        deleteAll(new File("database_backup"));
        deleteAll(new File("addon.jar"));
        deleteAll(new File("config.yml"));
        deleteAll(new File("addons"));
    }

    private void deleteAll(File file) throws IOException {
        if (file.exists()) {
            Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        // Set up plugin via reflection
        Field instanceField = BentoBox.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, plugin);
        when(plugin.getLogger()).thenReturn(Logger.getAnonymousLogger());

        Field rmField = RanksManager.class.getDeclaredField("instance");
        rmField.setAccessible(true);
        rmField.set(null, rm);

        // Command manager
        CommandsManager cm = mock(CommandsManager.class);
        when(plugin.getCommandsManager()).thenReturn(cm);

        // Player
        Player p = mock(Player.class);
        when(user.isOp()).thenReturn(false);
        UUID uuid = UUID.randomUUID();
        when(user.getUniqueId()).thenReturn(uuid);
        when(user.getPlayer()).thenReturn(p);
        when(user.getName()).thenReturn("tastybento");
        User.setPlugin(plugin);

        // Island World Manager
        IslandWorldManager iwm = mock(IslandWorldManager.class);
        when(plugin.getIWM()).thenReturn(iwm);

        // Player has island to begin with
        island = mock(Island.class);
        when(im.getIsland(Mockito.any(), Mockito.any(UUID.class))).thenReturn(island);
        when(plugin.getIslands()).thenReturn(im);

        // Locales
        when(user.getTranslation(Mockito.anyString()))
                .thenAnswer((Answer<String>) invocation -> invocation.getArgument(0, String.class));

        // Server
        server = MockBukkit.mock();
        mockedBukkit = Mockito.mockStatic(Bukkit.class, Mockito.RETURNS_DEEP_STUBS);
        mockedBukkit.when(Bukkit::getMinecraftVersion).thenReturn("1.21.11");
        mockedBukkit.when(Bukkit::getServer).thenReturn(server);
        mockedBukkit.when(Bukkit::getLogger).thenReturn(Logger.getAnonymousLogger());
        mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mock(PluginManager.class));
        mockedBukkit.when(() -> Bukkit.getWorld(anyString())).thenReturn(null);

        // Addon
        addon = new AcidIsland();
        File jFile = new File("addon.jar");
        List<String> lines = Arrays.asList("# AcidIsland Configuration", "uniqueId: config");
        Path path = Paths.get("config.yml");
        Files.write(path, lines, Charset.forName("UTF-8"));
        try (JarOutputStream tempJarOutputStream = new JarOutputStream(new FileOutputStream(jFile))) {
            try (FileInputStream fis = new FileInputStream(path.toFile())) {
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                JarEntry entry = new JarEntry(path.toString());
                tempJarOutputStream.putNextEntry(entry);
                while ((bytesRead = fis.read(buffer)) != -1) {
                    tempJarOutputStream.write(buffer, 0, bytesRead);
                }
            }
        }
        File dataFolder = new File("addons/AcidIsland");
        addon.setDataFolder(dataFolder);
        addon.setFile(jFile);
        AddonDescription desc = new AddonDescription.Builder("bentobox", "AcidIsland", "1.3").description("test")
                .authors("tasty").build();
        addon.setDescription(desc);
        // Addons manager
        AddonsManager am = mock(AddonsManager.class);
        when(plugin.getAddonsManager()).thenReturn(am);

        // Flags manager
        when(plugin.getFlagsManager()).thenReturn(fm);
        when(fm.getFlags()).thenReturn(Collections.emptyList());

        // Settings
        when(plugin.getSettings()).thenReturn(settings);
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AcidIsland#onLoad()}.
     */
    @Test
    public void testOnLoad() {
        addon.onLoad();
        // Check that config.yml file has been saved
        File check = new File("addons/AcidIsland", "config.yml");
        assertTrue(check.exists());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AcidIsland#onEnable()}.
     */
    @Test
    public void testOnEnable() {
        testOnLoad();
        addon.onEnable();
        assertTrue(addon.getPlayerCommand().isPresent());
        assertTrue(addon.getAdminCommand().isPresent());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AcidIsland#onReload()}.
     */
    @Test
    public void testOnReload() {
        addon.onReload();
        // Check that config.yml file has been saved
        File check = new File("addons/AcidIsland", "config.yml");
        assertTrue(check.exists());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AcidIsland#createWorlds()}.
     */
    @Test
    public void testCreateWorlds() {
        addon.onLoad();
        addon.createWorlds();
        Mockito.verify(plugin).log("[AcidIsland] Creating AcidIsland...");
        Mockito.verify(plugin).log("[AcidIsland] Creating AcidIsland's Nether...");
        Mockito.verify(plugin).log("[AcidIsland] Creating AcidIsland's End World...");
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AcidIsland#getSettings()}.
     */
    @Test
    public void testGetSettings() {
        addon.onLoad();
        assertNotNull(addon.getSettings());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AcidIsland#getWorldSettings()}.
     */
    @Test
    public void testGetWorldSettings() {
        addon.onLoad();
        assertEquals(addon.getSettings(), addon.getWorldSettings());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AcidIsland#getDefaultWorldGenerator(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testGetDefaultWorldGeneratorStringString() {
        assertNull(addon.getDefaultWorldGenerator("", ""));
        addon.onLoad();
        addon.createWorlds();
        assertNotNull(addon.getDefaultWorldGenerator("", ""));
        assertTrue(addon.getDefaultWorldGenerator("", "") instanceof ChunkGeneratorWorld);
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AcidIsland#allLoaded()}.
     */
    @Test
    public void testAllLoaded() {
        addon.allLoaded();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AcidIsland#saveWorldSettings()}.
     */
    @Test
    public void testSaveWorldSettings() {
        addon.saveWorldSettings();
    }

    /**
     * Test onDisable cancels acidTask.
     */
    @Test
    public void testOnDisable() {
        testOnEnable();
        addon.onDisable();
        // Should not throw - acidTask.cancelTasks() is called
    }

    /**
     * Test onDisable when acidTask is null (no prior onEnable).
     */
    @Test
    public void testOnDisableNoTask() {
        // No onEnable called, acidTask is null
        addon.onDisable();
        // Should not throw
    }

    /**
     * Test onEnable does nothing when settings is null.
     */
    @Test
    public void testOnEnableNullSettings() {
        // Don't call onLoad, so settings is null
        addon.onEnable();
        // Should return early without registering listeners
        assertFalse(addon.getPlayerCommand().isPresent());
    }

}
