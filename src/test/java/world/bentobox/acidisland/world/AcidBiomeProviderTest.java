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
    public void setUp() {
        MockBukkit.mock();
        settings = new AISettings();
        when(addon.getSettings()).thenReturn(settings);
        provider = new AcidBiomeProvider(addon);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testGetBiomeNormal() {
        when(worldInfo.getEnvironment()).thenReturn(Environment.NORMAL);
        assertEquals(Biome.WARM_OCEAN, provider.getBiome(worldInfo, 0, 0, 0));
    }

    @Test
    public void testGetBiomeNether() {
        when(worldInfo.getEnvironment()).thenReturn(Environment.NETHER);
        assertEquals(Biome.NETHER_WASTES, provider.getBiome(worldInfo, 0, 0, 0));
    }

    @Test
    public void testGetBiomeEnd() {
        when(worldInfo.getEnvironment()).thenReturn(Environment.THE_END);
        assertEquals(Biome.THE_END, provider.getBiome(worldInfo, 0, 0, 0));
    }

    @Test
    public void testGetBiomeCustom() {
        settings.setDefaultBiome(Biome.DEEP_OCEAN);
        when(worldInfo.getEnvironment()).thenReturn(Environment.NORMAL);
        assertEquals(Biome.DEEP_OCEAN, provider.getBiome(worldInfo, 0, 0, 0));
    }

    @Test
    public void testGetBiomeCustomNether() {
        settings.setDefaultNetherBiome(Biome.SOUL_SAND_VALLEY);
        when(worldInfo.getEnvironment()).thenReturn(Environment.NETHER);
        assertEquals(Biome.SOUL_SAND_VALLEY, provider.getBiome(worldInfo, 0, 0, 0));
    }
}
