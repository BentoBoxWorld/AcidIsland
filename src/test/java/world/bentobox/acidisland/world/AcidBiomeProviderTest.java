package world.bentobox.acidisland.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.generator.WorldInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import world.bentobox.acidisland.AISettings;
import world.bentobox.acidisland.AcidIsland;

@ExtendWith(MockitoExtension.class)
public class AcidBiomeProviderTest {

    @Mock
    private AcidIsland addon;
    @Mock
    private WorldInfo worldInfo;

    private AISettings settings;
    private AcidBiomeProvider provider;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        settings = new AISettings();
        when(addon.getSettings()).thenReturn(settings);
        provider = new AcidBiomeProvider(addon);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void testGetBiomeNormal() {
        when(worldInfo.getEnvironment()).thenReturn(Environment.NORMAL);
        assertEquals(Biome.WARM_OCEAN, provider.getBiome(worldInfo, 0, 0, 0));
    }

    @Test
    void testGetBiomeNether() {
        when(worldInfo.getEnvironment()).thenReturn(Environment.NETHER);
        assertEquals(Biome.NETHER_WASTES, provider.getBiome(worldInfo, 0, 0, 0));
    }

    @Test
    void testGetBiomeEnd() {
        when(worldInfo.getEnvironment()).thenReturn(Environment.THE_END);
        assertEquals(Biome.THE_END, provider.getBiome(worldInfo, 0, 0, 0));
    }

    @Test
    void testGetBiomeCustom() {
        settings.setDefaultBiome("DEEP_OCEAN");
        when(worldInfo.getEnvironment()).thenReturn(Environment.NORMAL);
        assertEquals(Biome.DEEP_OCEAN, provider.getBiome(worldInfo, 0, 0, 0));
    }

    @Test
    void testGetBiomeCustomNamespaced() {
        settings.setDefaultBiome("minecraft:deep_ocean");
        when(worldInfo.getEnvironment()).thenReturn(Environment.NORMAL);
        assertEquals(Biome.DEEP_OCEAN, provider.getBiome(worldInfo, 0, 0, 0));
    }

    @Test
    void testGetBiomeCustomNether() {
        settings.setDefaultNetherBiome("SOUL_SAND_VALLEY");
        when(worldInfo.getEnvironment()).thenReturn(Environment.NETHER);
        assertEquals(Biome.SOUL_SAND_VALLEY, provider.getBiome(worldInfo, 0, 0, 0));
    }

    /**
     * A biome name that does not exist on this server version, e.g. SULFUR_CAVES on
     * servers older than Minecraft 26.2, must fall back to a vanilla biome
     */
    @Test
    void testGetBiomeUnknownFallsBackNormal() {
        settings.setDefaultBiome("SULFUR_CAVES");
        when(worldInfo.getEnvironment()).thenReturn(Environment.NORMAL);
        assertEquals(Biome.WARM_OCEAN, provider.getBiome(worldInfo, 0, 0, 0));
    }

    @Test
    void testGetBiomeNullFallsBackNormal() {
        settings.setDefaultBiome(null);
        when(worldInfo.getEnvironment()).thenReturn(Environment.NORMAL);
        assertEquals(Biome.WARM_OCEAN, provider.getBiome(worldInfo, 0, 0, 0));
    }

    @Test
    void testGetBiomeNullFallsBackNether() {
        settings.setDefaultNetherBiome(null);
        when(worldInfo.getEnvironment()).thenReturn(Environment.NETHER);
        assertEquals(Biome.NETHER_WASTES, provider.getBiome(worldInfo, 0, 0, 0));
    }

    @Test
    void testGetBiomeNullFallsBackEnd() {
        settings.setDefaultEndBiome(null);
        when(worldInfo.getEnvironment()).thenReturn(Environment.THE_END);
        assertEquals(Biome.THE_END, provider.getBiome(worldInfo, 0, 0, 0));
    }
}
