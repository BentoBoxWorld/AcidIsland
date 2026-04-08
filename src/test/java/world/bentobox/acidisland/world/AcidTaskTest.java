package world.bentobox.acidisland.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
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
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Cod;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Squid;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import world.bentobox.acidisland.AISettings;
import world.bentobox.acidisland.AcidIsland;
import world.bentobox.acidisland.events.EntityDamageByAcidEvent;

/**
 * @author tastybento
 *
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

    private ServerMock server;
    private MockedStatic<Bukkit> mockedBukkit;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        mockedBukkit = Mockito.mockStatic(Bukkit.class, Mockito.RETURNS_DEEP_STUBS);
        mockedBukkit.when(Bukkit::getMinecraftVersion).thenReturn("1.21.11");
        mockedBukkit.when(Bukkit::getServer).thenReturn(server);
        mockedBukkit.when(Bukkit::getScheduler).thenReturn(scheduler);
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
        when(i.getType()).thenReturn(EntityType.ITEM);
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

    @AfterEach
    public void tearDown() {
        mockedBukkit.closeOnDemand();
        MockBukkit.unmock();
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

        verify(world, never()).playSound(any(Location.class), any(Sound.class), anyFloat(), anyFloat());
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

        verify(world, never()).playSound(any(Location.class), any(Sound.class), anyFloat(), anyFloat());
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

    // --- Additional coverage tests ---

    /**
     * Test getEntityStream with nether and end disabled.
     */
    @Test
    public void testGetEntityStreamOverworldOnly() {
        settings.setNetherGenerate(false);
        settings.setEndGenerate(false);
        // Recreate to use updated settings
        at = new AcidTask(addon);
        List<Entity> es = at.getEntityStream();
        // Only overworld entities (4)
        assertEquals(4, es.size());
    }

    /**
     * Test getEntityStream with nether enabled but netherIslands disabled.
     */
    @Test
    public void testGetEntityStreamNetherNoIslands() {
        settings.setNetherIslands(false);
        settings.setEndGenerate(false);
        at = new AcidTask(addon);
        List<Entity> es = at.getEntityStream();
        // Only overworld entities (4)
        assertEquals(4, es.size());
    }

    /**
     * Test that applyDamage on LivingEntity with cancelled event does no damage.
     */
    @Test
    public void testApplyDamageLivingEntityCancelled() {
        Skeleton s = mock(Skeleton.class);
        when(s.getLocation()).thenReturn(l);
        when(s.getWorld()).thenReturn(world);
        AttributeInstance attr = mock(AttributeInstance.class);
        when(attr.getValue()).thenReturn(0D);
        when(s.getAttribute(any())).thenReturn(attr);
        EntityEquipment equip = mock(EntityEquipment.class);
        when(s.getEquipment()).thenReturn(equip);

        // Cancel the event via plugin manager
        mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mock(org.bukkit.plugin.PluginManager.class));
        org.bukkit.plugin.PluginManager pm = Bukkit.getPluginManager();
        Mockito.doAnswer(inv -> {
            Object event = inv.getArgument(0);
            if (event instanceof EntityDamageByAcidEvent) {
                ((EntityDamageByAcidEvent) event).setCancelled(true);
            }
            return null;
        }).when(pm).callEvent(any());

        at.applyDamage(s, 10);
        verify(s, never()).damage(anyDouble());
    }

    /**
     * Test that applyDamage removes item from tracking when not in water.
     */
    @Test
    public void testApplyDamageItemNotInWaterAnymore() {
        Item e = mock(Item.class);
        Location loc = mock(Location.class);
        Block b = mock(Block.class);
        when(b.getType()).thenReturn(Material.AIR);
        when(loc.getBlock()).thenReturn(b);
        when(e.getLocation()).thenReturn(loc);
        when(e.getWorld()).thenReturn(world);

        Map<Entity, Long> map = new HashMap<>();
        map.put(e, 0L);
        at.setItemsInWater(map);
        at.applyDamage(e, System.currentTimeMillis());

        // Item should be removed from tracking since it's not in water
        assertTrue(at.getItemsInWater().isEmpty());
        verify(e, never()).remove();
    }

}
