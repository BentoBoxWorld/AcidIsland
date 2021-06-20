package world.bentobox.acidisland.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Cod;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Squid;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import world.bentobox.acidisland.AISettings;
import world.bentobox.acidisland.AcidIsland;

/**
 * @author tastybento
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class})
public class AcidTaskTest {

    @Mock
    private AcidIsland addon;

    // CUT
    private AcidTask at;

    private @Nullable AISettings settings;

    @Mock
    private World world;

    @Mock
    private @Nullable World nether;
    @Mock
    private @Nullable World end;

    @Mock
    private BukkitScheduler scheduler;

    @Mock
    private BukkitTask task;
    @Mock
    private Location l;


    /**
     */
    @Before
    public void setUp() {
        PowerMockito.mockStatic(Bukkit.class, Mockito.RETURNS_MOCKS);
        when(Bukkit.getScheduler()).thenReturn(scheduler);
        when(scheduler.runTaskTimer(any(), any(Runnable.class), anyLong(), anyLong())).thenReturn(task);

        when(addon.getOverWorld()).thenReturn(world);
        when(addon.getNetherWorld()).thenReturn(nether);
        when(addon.getEndWorld()).thenReturn(end);

        when(world.isChunkLoaded(anyInt(), anyInt())).thenReturn(true);

        Block block = mock(Block.class);
        when(block.getType()).thenReturn(Material.WATER);
        when(l.getBlock()).thenReturn(block);


        // Default squid
        List<Entity> mob = new ArrayList<>();
        Squid squid = mock(Squid.class);
        when(squid.getType()).thenReturn(EntityType.SQUID);
        when(squid.getLocation()).thenReturn(l);
        when(squid.getWorld()).thenReturn(world);
        mob.add(squid);
        Skeleton s = mock(Skeleton.class);
        when(s.getLocation()).thenReturn(l);
        when(s.getType()).thenReturn(EntityType.SKELETON);
        when(s.getWorld()).thenReturn(world);
        mob.add(s);
        Cod c = mock(Cod.class);
        when(c.getLocation()).thenReturn(l);
        when(c.getType()).thenReturn(EntityType.COD);
        when(c.getWorld()).thenReturn(world);
        mob.add(mock(Cod.class));
        Item i = mock(Item.class);
        when(i.getLocation()).thenReturn(l);
        when(i.getType()).thenReturn(EntityType.DROPPED_ITEM);
        when(i.getWorld()).thenReturn(world);
        mob.add(i);
        when(world.getEntities()).thenReturn(mob);
        when(nether.getEntities()).thenReturn(mob);
        when(end.getEntities()).thenReturn(mob);

        settings = new AISettings();
        settings.setNetherGenerate(true);
        settings.setEndGenerate(true);
        settings.setEndIslands(true);
        settings.setNetherIslands(true);
        settings.setAcidDestroyItemTime(1);
        settings.setAcidDamageAnimal(10);
        settings.setAcidDamageMonster(10);
        settings.setAcidDamageChickens(true);
        when(addon.getSettings()).thenReturn(settings);

        at = new AcidTask(addon);
    }

    /**
     */
    @After
    public void tearDown() {
    }

    /**
     * Test method for {@link world.bentobox.acidisland.world.AcidTask#AcidTask(world.bentobox.acidisland.AcidIsland)}.
     */
    @Test
    public void testAcidTask() {
        verify(scheduler).runTaskTimer(eq(null), any(Runnable.class), eq(0L), eq(20L));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.world.AcidTask#findEntities()}.
     */
    @Test
    public void testFindEntities() {

        at.findEntities();
        verify(scheduler).runTask(eq(null), any(Runnable.class));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.world.AcidTask#applyDamage(org.bukkit.entity.Entity, long)}.
     */
    @Test
    public void testApplyDamageRemoveItems() {
        Item e = mock(Item.class);
        when(e.getLocation()).thenReturn(l);
        when(e.getWorld()).thenReturn(world);
        // Put the item in the water

        Map<Entity, Long> map = new HashMap<>();
        map.put(e, 0L);
        at.setItemsInWater(map);
        at.applyDamage(e, 0);

        verify(world).playSound(eq(l), any(Sound.class), anyFloat(), anyFloat());
        verify(e).remove();
        assertTrue(map.isEmpty());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.world.AcidTask#applyDamage(org.bukkit.entity.Entity, long)}.
     */
    @Test
    public void testApplyDamageNoItemDamage() {
        settings.setAcidDestroyItemTime(0L);
        Item e = mock(Item.class);
        at.applyDamage(e, 0);

        verify(world, never()).playSound(any(), any(Sound.class), anyFloat(), anyFloat());
        verify(e, never()).remove();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.world.AcidTask#applyDamage(org.bukkit.entity.Entity, long)}.
     */
    @Test
    public void testApplyDamageKeepItems() {
        Item e = mock(Item.class);
        Location l = mock(Location.class);
        Block block = mock(Block.class);
        when(block.getType()).thenReturn(Material.AIR);
        when(l.getBlock()).thenReturn(block);
        when(e.getLocation()).thenReturn(l);
        when(e.getWorld()).thenReturn(world);
        // Put the item in the water

        Map<Entity, Long> map = new HashMap<>();
        map.put(e, 0L);
        at.setItemsInWater(map);
        at.applyDamage(e, 0);

        verify(world, never()).playSound(any(), any(Sound.class), anyFloat(), anyFloat());
        verify(e, never()).remove();
        assertTrue(map.isEmpty());
    }
    /**
     * Test method for {@link world.bentobox.acidisland.world.AcidTask#getEntityStream()}.
     */
    @Test
    public void testGetEntityStream() {
        List<Entity> es = at.getEntityStream();
        assertEquals(12, es.size());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.world.AcidTask#cancelTasks()}.
     */
    @Test
    public void testCancelTasks() {
        at.cancelTasks();
        verify(task).cancel();
    }

}
