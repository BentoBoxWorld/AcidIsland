package world.bentobox.acidisland;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import world.bentobox.acidisland.mocks.ServerMocks;
import world.bentobox.acidisland.world.ChunkGeneratorWorld;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.Settings;
import world.bentobox.bentobox.api.addons.AddonDescription;
import world.bentobox.bentobox.api.configuration.Config;
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
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Bukkit.class, BentoBox.class, User.class, Config.class, DatabaseSetup.class, RanksManager.class })
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

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void beforeClass() throws IllegalAccessException, InvocationTargetException, IntrospectionException {
		// This has to be done beforeClass otherwise the tests will interfere with each
		// other
		h = mock(AbstractDatabaseHandler.class);
		// Database
		PowerMockito.mockStatic(DatabaseSetup.class);
		DatabaseSetup dbSetup = mock(DatabaseSetup.class);
		when(DatabaseSetup.getDatabase()).thenReturn(dbSetup);
		when(dbSetup.getHandler(any())).thenReturn(h);
		when(h.saveObject(any())).thenReturn(CompletableFuture.completedFuture(true));
        ServerMocks.newServer();
	}

	@After
	public void tearDown() throws IOException {
		User.clearUsers();
		Mockito.framework().clearInlineMocks();
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

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// Set up plugin
		Whitebox.setInternalState(BentoBox.class, "instance", plugin);
		when(plugin.getLogger()).thenReturn(Logger.getAnonymousLogger());

        Whitebox.setInternalState(RanksManager.class, "instance", rm);
		// Command manager
		CommandsManager cm = mock(CommandsManager.class);
		when(plugin.getCommandsManager()).thenReturn(cm);

		// Player
		Player p = mock(Player.class);
		// Sometimes use Mockito.withSettings().verboseLogging()
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
		// Return the reference (USE THIS IN THE FUTURE)
		when(user.getTranslation(Mockito.anyString()))
				.thenAnswer((Answer<String>) invocation -> invocation.getArgument(0, String.class));

		// Server
		PowerMockito.mockStatic(Bukkit.class, Mockito.RETURNS_MOCKS);
		Server server = mock(Server.class);
		when(Bukkit.getServer()).thenReturn(server);
		when(Bukkit.getLogger()).thenReturn(Logger.getAnonymousLogger());
		when(Bukkit.getPluginManager()).thenReturn(mock(PluginManager.class));
		when(Bukkit.getWorld(anyString())).thenReturn(null);

		// Addon
		addon = new AcidIsland();
		File jFile = new File("addon.jar");
		List<String> lines = Arrays.asList("# AcidIsland Configuration", "uniqueId: config");
		Path path = Paths.get("config.yml");
		Files.write(path, lines, Charset.forName("UTF-8"));
		try (JarOutputStream tempJarOutputStream = new JarOutputStream(new FileOutputStream(jFile))) {
			// Added the new files to the jar.
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
	 * Test method for
	 * {@link world.bentobox.acidisland.AcidIsland#getWorldSettings()}.
	 */
	@Test
	public void testGetWorldSettings() {
		addon.onLoad();
		assertEquals(addon.getSettings(), addon.getWorldSettings());
	}

	/**
	 * Test method for
	 * {@link world.bentobox.acidisland.AcidIsland#getDefaultWorldGenerator(java.lang.String, java.lang.String)}.
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
	 * Test method for
	 * {@link world.bentobox.acidisland.AcidIsland#saveWorldSettings()}.
	 */
	@Test
	public void testSaveWorldSettings() {
		addon.saveWorldSettings();
	}

}
