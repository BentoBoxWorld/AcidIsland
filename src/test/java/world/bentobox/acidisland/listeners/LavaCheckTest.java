package world.bentobox.acidisland.listeners;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import world.bentobox.acidisland.AISettings;
import world.bentobox.acidisland.AcidIsland;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.managers.IslandWorldManager;
import world.bentobox.bentobox.managers.IslandsManager;
import world.bentobox.bentobox.managers.PlayersManager;
import world.bentobox.bentobox.util.Util;

/**
 * @author tastybento
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class, Util.class})
public class LavaCheckTest {
    @Mock
    private AcidIsland addon;
    @Mock
    private BukkitScheduler scheduler;
    private AISettings settings;
    @Mock
    private Location from;
    @Mock
    private Location to;
    @Mock
    private World world;
    @Mock
    private Location location;
    @Mock
    private PlayersManager pm;
    @Mock
    private Block block;
    @Mock
    private Block airBlock;
    @Mock
    private Block solidBlock;
    @Mock
    private PlayerInventory inv;
    @Mock
    private ItemMeta itemMeta;
    @Mock
    private PluginManager pim;
    @Mock
    private Essentials essentials;
    @Mock
    private User essentialsUser;
    @Mock
    private BentoBox plugin;
    @Mock
    private IslandWorldManager iwm;
    @Mock
    private IslandsManager im;
    
    private LavaCheck lc;
    
    /**
     */
    @Before
    public void setUp() {
        PowerMockito.mockStatic(Bukkit.class, Mockito.RETURNS_MOCKS);
        when(Bukkit.getScheduler()).thenReturn(scheduler);
        settings = new AISettings();
        when(addon.getSettings()).thenReturn(settings);
        when(addon.getOverWorld()).thenReturn(world);
        // Blocks
        when(block.getType()).thenReturn(Material.WATER);
        when(block.getWorld()).thenReturn(world);
        when(block.getLocation()).thenReturn(location);

        // CUT
        lc = new LavaCheck(addon);
    }

    /**
     */
    @After
    public void tearDown() {
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.LavaCheck#onCleanstoneGen(org.bukkit.event.block.BlockFromToEvent)}.
     */
    @Test
    public void testOnCleanstoneGen() {
        ArgumentCaptor<Runnable> argument = ArgumentCaptor.forClass(Runnable.class);
        
        BlockFromToEvent e = new BlockFromToEvent(airBlock, block);
        lc.onCleanstoneGen(e);
        
        verify(scheduler).runTask(any(), argument.capture());
        // make block now be stone
        when(block.getType()).thenReturn(Material.STONE);
        // Run runnable
        argument.getValue().run();
        verify(block).setType(eq(Material.WATER));
        verify(world).playSound(eq(location), eq(Sound.ENTITY_CREEPER_PRIMED), eq(1F), eq(2F));
    }
    
    /**
     * Test method for {@link world.bentobox.acidisland.listeners.LavaCheck#onCleanstoneGen(org.bukkit.event.block.BlockFromToEvent)}.
     */
    @Test
    public void testOnCleanstoneGenNoStone() {
        ArgumentCaptor<Runnable> argument = ArgumentCaptor.forClass(Runnable.class);
        
        BlockFromToEvent e = new BlockFromToEvent(airBlock, block);
        lc.onCleanstoneGen(e);
        
        verify(scheduler).runTask(any(), argument.capture());
        // make block now be obsidian
        when(block.getType()).thenReturn(Material.OBSIDIAN);
        // Run runnable
        argument.getValue().run();
        verify(block, never()).setType(any());
        verify(world, never()).playSound(any(Location.class), any(Sound.class), anyFloat(),anyFloat());
    }
    
    /**
     * Test method for {@link world.bentobox.acidisland.listeners.LavaCheck#onCleanstoneGen(org.bukkit.event.block.BlockFromToEvent)}.
     */
    @Test
    public void testOnCleanstoneGenWrongWorld() {
        when(block.getWorld()).thenReturn(Mockito.mock(World.class));
        
        BlockFromToEvent e = new BlockFromToEvent(airBlock, block);
        lc.onCleanstoneGen(e);
        verify(block, never()).setType(any());
        verify(world, never()).playSound(any(Location.class), any(Sound.class), anyFloat(),anyFloat());
    }
    
    /**
     * Test method for {@link world.bentobox.acidisland.listeners.LavaCheck#onCleanstoneGen(org.bukkit.event.block.BlockFromToEvent)}.
     */
    @Test
    public void testOnCleanstoneGenNotWater() {
        when(block.getType()).thenReturn(Material.LAVA);
        
        BlockFromToEvent e = new BlockFromToEvent(airBlock, block);
        lc.onCleanstoneGen(e);
        verify(block, never()).setType(any());
        verify(world, never()).playSound(any(Location.class), any(Sound.class), anyFloat(),anyFloat());
    }
    
    /**
     * Test method for {@link world.bentobox.acidisland.listeners.LavaCheck#onCleanstoneGen(org.bukkit.event.block.BlockFromToEvent)}.
     */
    @Test
    public void testOnCleanstoneGenNoAcid() {
        // No acid damage
        settings.setAcidDamage(0);

        BlockFromToEvent e = new BlockFromToEvent(airBlock, block);
        lc.onCleanstoneGen(e);

        verify(block, never()).setType(any());
        verify(world, never()).playSound(any(Location.class), any(Sound.class), anyFloat(),anyFloat());
    }
    

}
