package world.bentobox.acidisland.listeners;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.PointedDripstone;
import org.bukkit.entity.Player;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent.ChangeReason;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import world.bentobox.acidisland.AISettings;
import world.bentobox.acidisland.AcidIsland;
import world.bentobox.acidisland.events.ItemFillWithAcidEvent;
import world.bentobox.acidisland.events.PlayerDrinkPurifiedWaterEvent;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.IslandsManager;
import world.bentobox.bentobox.managers.LocalesManager;

/**
 * @author tastybento
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PurifiedWaterListenerTest {

    @Mock
    private AcidIsland addon;
    @Mock
    private AISettings settings;
    @Mock
    private Player player;
    @Mock
    private World world;
    @Mock
    private Location location;
    @Mock
    private Block block;
    @Mock
    private PlayerInventory inventory;
    @Mock
    private PluginManager pluginManager;
    @Mock
    private BukkitScheduler scheduler;
    @Mock
    private IslandsManager islandsManager;
    @Mock
    private BentoBox plugin;
    @Mock
    private LocalesManager localesManager;

    private ServerMock server;
    private MockedStatic<Bukkit> mockedBukkit;

    // Class under test
    private PurifiedWaterListener listener;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        mockedBukkit = Mockito.mockStatic(Bukkit.class, Mockito.RETURNS_DEEP_STUBS);
        mockedBukkit.when(Bukkit::getMinecraftVersion).thenReturn("1.21.11");
        mockedBukkit.when(Bukkit::getServer).thenReturn(server);
        mockedBukkit.when(Bukkit::getPluginManager).thenReturn(pluginManager);
        mockedBukkit.when(Bukkit::getScheduler).thenReturn(scheduler);

        when(addon.getSettings()).thenReturn(settings);
        when(addon.getOverWorld()).thenReturn(world);
        when(addon.getPlugin()).thenReturn(plugin);
        when(addon.getIslands()).thenReturn(islandsManager);
        when(plugin.getLocalesManager()).thenReturn(localesManager);
        when(localesManager.getOrDefault(any(), any())).thenAnswer(inv -> inv.getArgument(1));

        when(settings.isPurifiedWaterEnabled()).thenReturn(true);
        when(settings.isPurifiedBucketFurnaceEnabled()).thenReturn(true);
        when(settings.getPurifiedWaterHeal()).thenReturn(4.0);

        when(player.getWorld()).thenReturn(world);
        when(player.getLocation()).thenReturn(location);
        when(player.getInventory()).thenReturn(inventory);

        when(block.getWorld()).thenReturn(world);
        when(block.getLocation()).thenReturn(location);
        // Stub blocks above cauldron as AIR by default (no dripstone = acid from rain)
        Block airBlock = mock(Block.class);
        when(airBlock.getType()).thenReturn(Material.AIR);
        when(block.getRelative(any(BlockFace.class), anyInt())).thenReturn(airBlock);

        when(islandsManager.getIslandAt(any(Location.class))).thenReturn(Optional.empty());

        // Inventory defaults: no items, standard 41-slot size
        when(inventory.getSize()).thenReturn(41);
        when(inventory.getItem(anyInt())).thenReturn(null);

        listener = new PurifiedWaterListener(addon);
    }

    @AfterEach
    void tearDown() {
        mockedBukkit.closeOnDemand();
        MockBukkit.unmock();
    }

    // -----------------------------------------------------------------------
    // Static helper: isPurified
    // -----------------------------------------------------------------------

    @Test
    void testIsPurifiedWithNullItem() {
        assertFalse(PurifiedWaterListener.isPurified(null));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testIsPurifiedWithPurifiedTag() {
        ItemStack item = mock(ItemStack.class);
        ItemMeta meta = mock(ItemMeta.class);
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(item.getItemMeta()).thenReturn(meta);
        when(meta.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.get(any(), any())).thenReturn(PurifiedWaterListener.PURIFIED);
        assertTrue(PurifiedWaterListener.isPurified(item));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testIsPurifiedWithAcidTag() {
        ItemStack item = mock(ItemStack.class);
        ItemMeta meta = mock(ItemMeta.class);
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(item.getItemMeta()).thenReturn(meta);
        when(meta.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.get(any(), any())).thenReturn(PurifiedWaterListener.ACID);
        assertFalse(PurifiedWaterListener.isPurified(item));
    }

    // -----------------------------------------------------------------------
    // Static helper: isWaterBottle
    // -----------------------------------------------------------------------

    @Test
    void testIsWaterBottleWithNullItem() {
        assertFalse(PurifiedWaterListener.isWaterBottle(null));
    }

    @Test
    void testIsWaterBottleWithNonPotion() {
        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.GLASS_BOTTLE);
        assertFalse(PurifiedWaterListener.isWaterBottle(item));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testIsWaterBottleWithAcidTag() {
        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.POTION);
        PotionMeta meta = mock(PotionMeta.class);
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(item.getItemMeta()).thenReturn(meta);
        when(meta.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.get(any(), any())).thenReturn(PurifiedWaterListener.ACID);
        assertTrue(PurifiedWaterListener.isWaterBottle(item));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testIsWaterBottleWithPurifiedTag() {
        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.POTION);
        PotionMeta meta = mock(PotionMeta.class);
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(item.getItemMeta()).thenReturn(meta);
        when(meta.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.get(any(), any())).thenReturn(PurifiedWaterListener.PURIFIED);
        assertFalse(PurifiedWaterListener.isWaterBottle(item));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testIsWaterBottlePlainWaterPotion() {
        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.POTION);
        PotionMeta meta = mock(PotionMeta.class);
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(item.getItemMeta()).thenReturn(meta);
        when(meta.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.get(any(), any())).thenReturn(null);  // no custom tag
        when(meta.getBasePotionType()).thenReturn(PotionType.WATER);
        when(meta.getCustomEffects()).thenReturn(java.util.List.of());
        assertTrue(PurifiedWaterListener.isWaterBottle(item));
    }

    // -----------------------------------------------------------------------
    // CauldronLevelChangeEvent
    // -----------------------------------------------------------------------

    @Test
    void testCauldronNaturalFillMarkedAcid() {
        CauldronLevelChangeEvent e = mock(CauldronLevelChangeEvent.class);
        when(e.getBlock()).thenReturn(block);
        when(e.getReason()).thenReturn(ChangeReason.NATURAL_FILL);
        when(e.getNewLevel()).thenReturn(1);

        listener.onCauldronChange(e);

        assertFalse(listener.getCauldronPurity().getOrDefault(location, true));
    }

    @Test
    void testCauldronBucketFillDoesNotMarkPurity() {
        // BUCKET_FILL = player took water from cauldron into bucket (cauldron being emptied, not filled)
        // It doesn't change the purity map entry (handled by level-0 removal if fully emptied)
        listener.getCauldronPurity().put(location, true); // pre-existing purified
        CauldronLevelChangeEvent e = mock(CauldronLevelChangeEvent.class);
        when(e.getBlock()).thenReturn(block);
        when(e.getReason()).thenReturn(ChangeReason.BUCKET_FILL);
        when(e.getNewLevel()).thenReturn(2); // still has some water

        listener.onCauldronChange(e);

        // purity should remain unchanged (still purified)
        assertTrue(listener.getCauldronPurity().getOrDefault(location, false));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testCauldronBucketEmptyWithPurifiedBucketMarkedPurified() {
        // BUCKET_EMPTY = player poured a purified water bucket into the cauldron
        ItemStack purifiedBucket = mock(ItemStack.class);
        ItemMeta bucketMeta = mock(ItemMeta.class);
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(purifiedBucket.getItemMeta()).thenReturn(bucketMeta);
        when(bucketMeta.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.get(any(), any())).thenReturn(PurifiedWaterListener.PURIFIED);
        when(inventory.getItemInMainHand()).thenReturn(purifiedBucket);

        CauldronLevelChangeEvent e = mock(CauldronLevelChangeEvent.class);
        when(e.getBlock()).thenReturn(block);
        when(e.getReason()).thenReturn(ChangeReason.BUCKET_EMPTY);
        when(e.getNewLevel()).thenReturn(3);
        when(e.getEntity()).thenReturn(player);

        listener.onCauldronChange(e);

        assertTrue(listener.getCauldronPurity().getOrDefault(location, false));
    }

    @Test
    void testCauldronBucketEmptyWithNormalBucketMarkedAcid() {
        // BUCKET_EMPTY = player poured a plain water bucket into the cauldron
        ItemStack waterBucket = mock(ItemStack.class);
        when(waterBucket.getItemMeta()).thenReturn(null);
        when(inventory.getItemInMainHand()).thenReturn(waterBucket);

        CauldronLevelChangeEvent e = mock(CauldronLevelChangeEvent.class);
        when(e.getBlock()).thenReturn(block);
        when(e.getReason()).thenReturn(ChangeReason.BUCKET_EMPTY);
        when(e.getNewLevel()).thenReturn(3);
        when(e.getEntity()).thenReturn(player);

        listener.onCauldronChange(e);

        assertFalse(listener.getCauldronPurity().getOrDefault(location, true));
    }

    @Test
    void testCauldronUnknownMarkedAcid() {
        // UNKNOWN reason → acid (no special meaning for dripstone in this API)
        CauldronLevelChangeEvent e = mock(CauldronLevelChangeEvent.class);
        when(e.getBlock()).thenReturn(block);
        when(e.getReason()).thenReturn(ChangeReason.UNKNOWN);
        when(e.getNewLevel()).thenReturn(1);

        listener.onCauldronChange(e);

        assertFalse(listener.getCauldronPurity().getOrDefault(location, true));
    }

    @Test
    void testCauldronDripstoneNaturalFillMarkedPurified() {
        // NATURAL_FILL with a dripstone stalactite tip directly above → purified
        Block dripstoneTip = mock(Block.class);
        when(dripstoneTip.getType()).thenReturn(Material.POINTED_DRIPSTONE);
        PointedDripstone ds = mock(PointedDripstone.class);
        when(dripstoneTip.getBlockData()).thenReturn(ds);
        when(ds.getVerticalDirection()).thenReturn(BlockFace.DOWN);
        when(block.getRelative(BlockFace.UP, 1)).thenReturn(dripstoneTip);

        CauldronLevelChangeEvent e = mock(CauldronLevelChangeEvent.class);
        when(e.getBlock()).thenReturn(block);
        when(e.getReason()).thenReturn(ChangeReason.NATURAL_FILL);
        when(e.getNewLevel()).thenReturn(1);

        listener.onCauldronChange(e);

        assertTrue(listener.getCauldronPurity().getOrDefault(location, false));
    }

    @Test
    void testCauldronEmptiedRemovesFromMap() {
        // Put something in the map first
        listener.getCauldronPurity().put(location, false);

        CauldronLevelChangeEvent e = mock(CauldronLevelChangeEvent.class);
        when(e.getBlock()).thenReturn(block);
        when(e.getReason()).thenReturn(ChangeReason.EVAPORATE);
        when(e.getNewLevel()).thenReturn(0);

        listener.onCauldronChange(e);

        assertFalse(listener.getCauldronPurity().containsKey(location));
    }

    @Test
    void testCauldronChangeIgnoredWhenDisabled() {
        when(settings.isPurifiedWaterEnabled()).thenReturn(false);

        CauldronLevelChangeEvent e = mock(CauldronLevelChangeEvent.class);
        when(e.getBlock()).thenReturn(block);
        when(e.getReason()).thenReturn(ChangeReason.NATURAL_FILL);
        when(e.getNewLevel()).thenReturn(1);

        listener.onCauldronChange(e);

        assertFalse(listener.getCauldronPurity().containsKey(location));
    }

    @Test
    void testCauldronChangeIgnoredInOtherWorld() {
        World otherWorld = mock(World.class);
        Block otherBlock = mock(Block.class);
        when(otherBlock.getWorld()).thenReturn(otherWorld);
        when(otherBlock.getLocation()).thenReturn(location);

        CauldronLevelChangeEvent e = mock(CauldronLevelChangeEvent.class);
        when(e.getBlock()).thenReturn(otherBlock);
        when(e.getReason()).thenReturn(ChangeReason.NATURAL_FILL);
        when(e.getNewLevel()).thenReturn(1);

        listener.onCauldronChange(e);

        assertFalse(listener.getCauldronPurity().containsKey(location));
    }

    // -----------------------------------------------------------------------
    // PlayerInteractEvent — bottle fill from water block
    // -----------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Test
    void testBottleFillFromWaterFiresItemFillWithAcidEvent() {
        ItemStack glassBottle = mock(ItemStack.class);
        when(glassBottle.getType()).thenReturn(Material.GLASS_BOTTLE);
        when(inventory.getItemInMainHand()).thenReturn(glassBottle);

        when(block.getType()).thenReturn(Material.WATER);

        PlayerInteractEvent e = mock(PlayerInteractEvent.class);
        when(e.getClickedBlock()).thenReturn(block);
        when(e.getPlayer()).thenReturn(player);
        when(e.getItem()).thenReturn(glassBottle);

        listener.onBottleFill(e);

        // Post-hoc: event is NOT cancelled — vanilla fill is allowed to run
        verify(e, never()).setCancelled(true);

        // Capture the scheduled task
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).runTask(any(), runnableCaptor.capture());

        // Simulate vanilla placing a plain water bottle in slot 0
        ItemStack vanillaWaterBottle = plainWaterBottleMock();
        when(inventory.getItem(0)).thenReturn(vanillaWaterBottle);

        runnableCaptor.getValue().run();

        // Acid event was fired and slot 0 was replaced with the acid bottle
        ArgumentCaptor<ItemFillWithAcidEvent> captor = ArgumentCaptor.forClass(ItemFillWithAcidEvent.class);
        verify(pluginManager).callEvent(captor.capture());
        assertFalse(captor.getValue().isCancelled());
        verify(inventory).setItem(anyInt(), any(ItemStack.class));
    }

    @Test
    void testBottleFillIgnoredWhenFeatureDisabled() {
        when(settings.isPurifiedWaterEnabled()).thenReturn(false);

        PlayerInteractEvent e = mock(PlayerInteractEvent.class);
        when(e.getClickedBlock()).thenReturn(block);
        when(e.getPlayer()).thenReturn(player);

        listener.onBottleFill(e);

        verify(e, never()).setCancelled(true);
    }

    @Test
    void testBottleFillIgnoredInOtherWorld() {
        World otherWorld = mock(World.class);
        when(player.getWorld()).thenReturn(otherWorld);

        ItemStack glassBottle = mock(ItemStack.class);
        when(glassBottle.getType()).thenReturn(Material.GLASS_BOTTLE);

        PlayerInteractEvent e = mock(PlayerInteractEvent.class);
        when(e.getClickedBlock()).thenReturn(block);
        when(e.getPlayer()).thenReturn(player);
        when(e.getItem()).thenReturn(glassBottle);

        listener.onBottleFill(e);

        verify(e, never()).setCancelled(true);
    }

    @Test
    void testBottleFillWithNonBottleIgnored() {
        ItemStack sword = mock(ItemStack.class);
        when(sword.getType()).thenReturn(Material.IRON_SWORD);

        PlayerInteractEvent e = mock(PlayerInteractEvent.class);
        when(e.getClickedBlock()).thenReturn(block);
        when(e.getPlayer()).thenReturn(player);
        when(e.getItem()).thenReturn(sword);

        listener.onBottleFill(e);

        verify(e, never()).setCancelled(true);
    }

    @Test
    void testBottleFillFromAcidCauldronGivesAcidBottle() {
        // Cauldron marked as acid
        listener.getCauldronPurity().put(location, false);

        ItemStack glassBottle = mock(ItemStack.class);
        when(glassBottle.getType()).thenReturn(Material.GLASS_BOTTLE);
        when(glassBottle.getAmount()).thenReturn(1);
        when(inventory.getItemInMainHand()).thenReturn(glassBottle);

        when(block.getType()).thenReturn(Material.WATER_CAULDRON);
        Levelled levelled = mock(Levelled.class);
        when(levelled.getLevel()).thenReturn(2);
        when(block.getBlockData()).thenReturn(levelled);

        PlayerInteractEvent e = mock(PlayerInteractEvent.class);
        when(e.getClickedBlock()).thenReturn(block);
        when(e.getPlayer()).thenReturn(player);
        when(e.getItem()).thenReturn(glassBottle);

        ArgumentCaptor<ItemFillWithAcidEvent> captor = ArgumentCaptor.forClass(ItemFillWithAcidEvent.class);

        listener.onBottleFill(e);

        verify(pluginManager).callEvent(captor.capture());
        assertFalse(captor.getValue().isCancelled());
        verify(e).setCancelled(true);
    }

    @Test
    void testBottleFillFromPurifiedCauldronNoAcidEvent() {
        // Cauldron marked as purified
        listener.getCauldronPurity().put(location, true);

        ItemStack glassBottle = mock(ItemStack.class);
        when(glassBottle.getType()).thenReturn(Material.GLASS_BOTTLE);
        when(glassBottle.getAmount()).thenReturn(1);
        when(inventory.getItemInMainHand()).thenReturn(glassBottle);

        when(block.getType()).thenReturn(Material.WATER_CAULDRON);
        Levelled levelled = mock(Levelled.class);
        when(levelled.getLevel()).thenReturn(2);
        when(block.getBlockData()).thenReturn(levelled);

        PlayerInteractEvent e = mock(PlayerInteractEvent.class);
        when(e.getClickedBlock()).thenReturn(block);
        when(e.getPlayer()).thenReturn(player);
        when(e.getItem()).thenReturn(glassBottle);

        listener.onBottleFill(e);

        // No ItemFillWithAcidEvent for purified; event is still cancelled to swap item
        verify(pluginManager, never()).callEvent(any(ItemFillWithAcidEvent.class));
        verify(e).setCancelled(true);
    }

    @Test
    void testBottleFillCancelledWhenItemFillEventCancelled() {
        ItemStack glassBottle = mock(ItemStack.class);
        when(glassBottle.getType()).thenReturn(Material.GLASS_BOTTLE);
        when(block.getType()).thenReturn(Material.WATER);

        PlayerInteractEvent e = mock(PlayerInteractEvent.class);
        when(e.getClickedBlock()).thenReturn(block);
        when(e.getPlayer()).thenReturn(player);
        when(e.getItem()).thenReturn(glassBottle);

        // Cancel the ItemFillWithAcidEvent
        Mockito.doAnswer(inv -> {
            ItemFillWithAcidEvent event = inv.getArgument(0);
            event.setCancelled(true);
            return null;
        }).when(pluginManager).callEvent(any(ItemFillWithAcidEvent.class));

        listener.onBottleFill(e);

        // Capture and run the scheduled task
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).runTask(any(), runnableCaptor.capture());

        // Simulate vanilla giving a plain water bottle in slot 0
        ItemStack vanillaWaterBottle = plainWaterBottleMock();
        when(inventory.getItem(0)).thenReturn(vanillaWaterBottle);

        runnableCaptor.getValue().run();

        // Fill event was cancelled — player should not receive an acid bottle
        verify(inventory, never()).setItem(anyInt(), any(ItemStack.class));
    }

    // -----------------------------------------------------------------------
    // PlayerBucketFillEvent
    // -----------------------------------------------------------------------

    @Test
    void testBucketFillFromOceanGivesAcidBucket() {
        ItemStack waterBucket = mock(ItemStack.class);
        when(waterBucket.getType()).thenReturn(Material.WATER_BUCKET);
        when(block.getType()).thenReturn(Material.WATER);

        PlayerBucketFillEvent e = mock(PlayerBucketFillEvent.class);
        when(e.getPlayer()).thenReturn(player);
        when(e.getItemStack()).thenReturn(waterBucket);
        when(e.getBlock()).thenReturn(block);

        listener.onBucketFill(e);

        verify(pluginManager).callEvent(any(ItemFillWithAcidEvent.class));
        verify(e).setItemStack(any(ItemStack.class));
    }

    @Test
    void testBucketFillFromPurifiedCauldronGivesPurifiedBucket() {
        listener.getCauldronPurity().put(location, true);

        ItemStack waterBucket = mock(ItemStack.class);
        when(waterBucket.getType()).thenReturn(Material.WATER_BUCKET);
        when(block.getType()).thenReturn(Material.WATER_CAULDRON);

        PlayerBucketFillEvent e = mock(PlayerBucketFillEvent.class);
        when(e.getPlayer()).thenReturn(player);
        when(e.getItemStack()).thenReturn(waterBucket);
        when(e.getBlock()).thenReturn(block);

        listener.onBucketFill(e);

        verify(pluginManager, never()).callEvent(any(ItemFillWithAcidEvent.class));
        verify(e).setItemStack(any(ItemStack.class));
    }

    @Test
    void testBucketFillFromAcidCauldronGivesAcidBucket() {
        listener.getCauldronPurity().put(location, false);

        ItemStack waterBucket = mock(ItemStack.class);
        when(waterBucket.getType()).thenReturn(Material.WATER_BUCKET);
        when(block.getType()).thenReturn(Material.WATER_CAULDRON);

        PlayerBucketFillEvent e = mock(PlayerBucketFillEvent.class);
        when(e.getPlayer()).thenReturn(player);
        when(e.getItemStack()).thenReturn(waterBucket);
        when(e.getBlock()).thenReturn(block);

        listener.onBucketFill(e);

        verify(pluginManager).callEvent(any(ItemFillWithAcidEvent.class));
        verify(e).setItemStack(any(ItemStack.class));
    }

    @Test
    void testBucketFillIgnoredWhenFeatureDisabled() {
        when(settings.isPurifiedWaterEnabled()).thenReturn(false);

        PlayerBucketFillEvent e = mock(PlayerBucketFillEvent.class);
        when(e.getPlayer()).thenReturn(player);

        listener.onBucketFill(e);

        verify(e, never()).setItemStack(any());
    }

    @Test
    void testBucketFillIgnoredInOtherWorld() {
        World otherWorld = mock(World.class);
        when(player.getWorld()).thenReturn(otherWorld);

        PlayerBucketFillEvent e = mock(PlayerBucketFillEvent.class);
        when(e.getPlayer()).thenReturn(player);

        listener.onBucketFill(e);

        verify(e, never()).setItemStack(any());
    }

    @Test
    void testBucketFillIgnoredForNonWaterBucket() {
        ItemStack lavaBucket = mock(ItemStack.class);
        when(lavaBucket.getType()).thenReturn(Material.LAVA_BUCKET);

        PlayerBucketFillEvent e = mock(PlayerBucketFillEvent.class);
        when(e.getPlayer()).thenReturn(player);
        when(e.getItemStack()).thenReturn(lavaBucket);

        listener.onBucketFill(e);

        verify(e, never()).setItemStack(any());
    }

    @Test
    void testBucketFillCancelledWhenItemFillEventCancelled() {
        ItemStack waterBucket = mock(ItemStack.class);
        when(waterBucket.getType()).thenReturn(Material.WATER_BUCKET);
        when(waterBucket.getItemMeta()).thenReturn(null);

        Mockito.doAnswer(inv -> {
            ItemFillWithAcidEvent event = inv.getArgument(0);
            event.setCancelled(true);
            return null;
        }).when(pluginManager).callEvent(any(ItemFillWithAcidEvent.class));

        when(block.getType()).thenReturn(Material.WATER);

        PlayerBucketFillEvent e = mock(PlayerBucketFillEvent.class);
        when(e.getPlayer()).thenReturn(player);
        when(e.getItemStack()).thenReturn(waterBucket);
        when(e.getBlock()).thenReturn(block);

        listener.onBucketFill(e);

        verify(e, never()).setItemStack(any());
    }

    // -----------------------------------------------------------------------
    // PlayerItemConsumeEvent — drinking purified water
    // -----------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Test
    void testDrinkPurifiedBottleHealsPlayer() {
        ItemStack purifiedBottle = mock(ItemStack.class);
        when(purifiedBottle.getType()).thenReturn(Material.POTION);
        ItemMeta meta = mock(ItemMeta.class);
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(purifiedBottle.getItemMeta()).thenReturn(meta);
        when(meta.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.get(any(), any())).thenReturn(PurifiedWaterListener.PURIFIED);

        AttributeInstance maxHealthAttr = mock(AttributeInstance.class);
        when(maxHealthAttr.getValue()).thenReturn(20.0);
        when(player.getAttribute(Attribute.MAX_HEALTH)).thenReturn(maxHealthAttr);
        when(player.getHealth()).thenReturn(10.0);
        when(settings.getPurifiedWaterHeal()).thenReturn(4.0);

        PlayerItemConsumeEvent e = mock(PlayerItemConsumeEvent.class);
        when(e.getItem()).thenReturn(purifiedBottle);
        when(e.getPlayer()).thenReturn(player);

        listener.onDrinkPurified(e);

        verify(pluginManager).callEvent(any(PlayerDrinkPurifiedWaterEvent.class));
        verify(player).setHealth(14.0); // 10 + 4
    }

    @SuppressWarnings("unchecked")
    @Test
    void testDrinkPurifiedBottleCapsAtMaxHealth() {
        ItemStack purifiedBottle = mock(ItemStack.class);
        when(purifiedBottle.getType()).thenReturn(Material.POTION);
        ItemMeta meta = mock(ItemMeta.class);
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(purifiedBottle.getItemMeta()).thenReturn(meta);
        when(meta.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.get(any(), any())).thenReturn(PurifiedWaterListener.PURIFIED);

        AttributeInstance maxHealthAttr = mock(AttributeInstance.class);
        when(maxHealthAttr.getValue()).thenReturn(20.0);
        when(player.getAttribute(Attribute.MAX_HEALTH)).thenReturn(maxHealthAttr);
        when(player.getHealth()).thenReturn(18.0);
        when(settings.getPurifiedWaterHeal()).thenReturn(4.0);

        PlayerItemConsumeEvent e = mock(PlayerItemConsumeEvent.class);
        when(e.getItem()).thenReturn(purifiedBottle);
        when(e.getPlayer()).thenReturn(player);

        listener.onDrinkPurified(e);

        verify(player).setHealth(20.0); // capped at max
    }

    @SuppressWarnings("unchecked")
    @Test
    void testDrinkPurifiedEventCancellable() {
        ItemStack purifiedBottle = mock(ItemStack.class);
        when(purifiedBottle.getType()).thenReturn(Material.POTION);
        ItemMeta meta = mock(ItemMeta.class);
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(purifiedBottle.getItemMeta()).thenReturn(meta);
        when(meta.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.get(any(), any())).thenReturn(PurifiedWaterListener.PURIFIED);

        Mockito.doAnswer(inv -> {
            PlayerDrinkPurifiedWaterEvent event = inv.getArgument(0);
            event.setCancelled(true);
            return null;
        }).when(pluginManager).callEvent(any(PlayerDrinkPurifiedWaterEvent.class));

        PlayerItemConsumeEvent e = mock(PlayerItemConsumeEvent.class);
        when(e.getItem()).thenReturn(purifiedBottle);
        when(e.getPlayer()).thenReturn(player);

        listener.onDrinkPurified(e);

        verify(player, never()).setHealth(any(Double.class));
    }

    @Test
    void testDrinkNonPurifiedBottleIgnored() {
        ItemStack acidBottle = acidBottleMock();

        PlayerItemConsumeEvent e = mock(PlayerItemConsumeEvent.class);
        when(e.getItem()).thenReturn(acidBottle);
        when(e.getPlayer()).thenReturn(player);

        listener.onDrinkPurified(e);

        verify(pluginManager, never()).callEvent(any(PlayerDrinkPurifiedWaterEvent.class));
        verify(player, never()).setHealth(any(Double.class));
    }

    @Test
    void testDrinkPurifiedIgnoredWhenFeatureDisabled() {
        when(settings.isPurifiedWaterEnabled()).thenReturn(false);

        PlayerItemConsumeEvent e = mock(PlayerItemConsumeEvent.class);
        when(e.getPlayer()).thenReturn(player);
        when(e.getItem()).thenReturn(mock(ItemStack.class));

        assertDoesNotThrow(() -> listener.onDrinkPurified(e));
        verify(player, never()).setHealth(any(Double.class));
    }

    // -----------------------------------------------------------------------
    // FurnaceSmeltEvent
    // -----------------------------------------------------------------------

    @Test
    void testFurnaceSmeltWaterBottleResultIsPurified() {
        ItemStack source = acidBottleMock();
        ItemStack result = mock(ItemStack.class);

        FurnaceSmeltEvent e = mock(FurnaceSmeltEvent.class);
        when(e.getSource()).thenReturn(source);
        when(e.getResult()).thenReturn(result);

        assertDoesNotThrow(() -> listener.onFurnaceSmelt(e));
        verify(e).setResult(any(ItemStack.class));
    }

    @Test
    void testFurnaceSmeltNonWaterPotionCancelled() {
        ItemStack source = mock(ItemStack.class);
        when(source.getType()).thenReturn(Material.POTION);
        PotionMeta meta = mock(PotionMeta.class);
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(source.getItemMeta()).thenReturn(meta);
        when(meta.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.get(PurifiedWaterListener.WATER_TYPE_KEY, PersistentDataType.STRING)).thenReturn(null);
        when(meta.getBasePotionType()).thenReturn(PotionType.STRENGTH);  // Not water
        when(meta.getCustomEffects()).thenReturn(java.util.List.of());

        FurnaceSmeltEvent e = mock(FurnaceSmeltEvent.class);
        when(e.getSource()).thenReturn(source);

        listener.onFurnaceSmelt(e);

        verify(e).setCancelled(true);
    }

    @Test
    void testFurnaceSmeltWaterBucketResultIsPurified() {
        ItemStack source = mock(ItemStack.class);
        when(source.getType()).thenReturn(Material.WATER_BUCKET);

        FurnaceSmeltEvent e = mock(FurnaceSmeltEvent.class);
        when(e.getSource()).thenReturn(source);

        assertDoesNotThrow(() -> listener.onFurnaceSmelt(e));
        verify(e).setResult(any(ItemStack.class));
    }

    @Test
    void testFurnaceSmeltWaterBucketDisabledNoChange() {
        when(settings.isPurifiedBucketFurnaceEnabled()).thenReturn(false);

        ItemStack source = mock(ItemStack.class);
        when(source.getType()).thenReturn(Material.WATER_BUCKET);

        FurnaceSmeltEvent e = mock(FurnaceSmeltEvent.class);
        when(e.getSource()).thenReturn(source);

        listener.onFurnaceSmelt(e);

        verify(e, never()).setResult(any());
    }

    @Test
    void testFurnaceSmeltFeatureDisabled() {
        when(settings.isPurifiedWaterEnabled()).thenReturn(false);

        ItemStack source = acidBottleMock();
        FurnaceSmeltEvent e = mock(FurnaceSmeltEvent.class);
        when(e.getSource()).thenReturn(source);

        listener.onFurnaceSmelt(e);

        verify(e, never()).setResult(any());
    }

    // -----------------------------------------------------------------------
    // BrewEvent
    // -----------------------------------------------------------------------

    @Test
    void testBrewWithCoalPurifiesWaterBottles() {
        ItemStack coal = mock(ItemStack.class);
        when(coal.getType()).thenReturn(Material.COAL);

        ItemStack waterBottle = acidBottleMock();

        BrewerInventory inv = mock(BrewerInventory.class);
        when(inv.getIngredient()).thenReturn(coal);
        when(inv.getItem(anyInt())).thenReturn(waterBottle);

        BrewEvent e = mock(BrewEvent.class);
        when(e.getContents()).thenReturn(inv);

        listener.onBrew(e);

        // Each of the 3 slots should have been updated
        verify(inv, Mockito.times(3)).setItem(anyInt(), any(ItemStack.class));
    }

    @Test
    void testBrewWithNonCoalNoChange() {
        ItemStack blazePowder = mock(ItemStack.class);
        when(blazePowder.getType()).thenReturn(Material.BLAZE_POWDER);

        BrewerInventory inv = mock(BrewerInventory.class);
        when(inv.getIngredient()).thenReturn(blazePowder);

        BrewEvent e = mock(BrewEvent.class);
        when(e.getContents()).thenReturn(inv);

        listener.onBrew(e);

        verify(inv, never()).setItem(anyInt(), any(ItemStack.class));
    }

    @Test
    void testBrewFeatureDisabled() {
        when(settings.isPurifiedWaterEnabled()).thenReturn(false);

        BrewEvent e = mock(BrewEvent.class);

        listener.onBrew(e);

        verify(e, never()).getContents();
    }

    @Test
    void testBrewWithNullIngredient() {
        BrewerInventory inv = mock(BrewerInventory.class);
        when(inv.getIngredient()).thenReturn(null);

        BrewEvent e = mock(BrewEvent.class);
        when(e.getContents()).thenReturn(inv);

        assertDoesNotThrow(() -> listener.onBrew(e));
        verify(inv, never()).setItem(anyInt(), any(ItemStack.class));
    }

    // -----------------------------------------------------------------------
    // Cauldron purity map accessor
    // -----------------------------------------------------------------------

    @Test
    void testGetCauldronPurityMapNotNull() {
        assertNotNull(listener.getCauldronPurity());
    }

    // -----------------------------------------------------------------------
    // Private helpers to build mocked items
    // -----------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private ItemStack plainWaterBottleMock() {
        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.POTION);
        PotionMeta meta = mock(PotionMeta.class);
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(item.getItemMeta()).thenReturn(meta);
        when(meta.getPersistentDataContainer()).thenReturn(pdc);
        when(pdc.get(any(), any())).thenReturn(null); // no custom tag
        when(meta.getBasePotionType()).thenReturn(PotionType.WATER);
        when(meta.getCustomEffects()).thenReturn(java.util.List.of());
        return item;
    }

    @SuppressWarnings("unchecked")
    private ItemStack acidBottleMock() {
        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.POTION);
        PotionMeta meta = mock(PotionMeta.class);
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(item.getItemMeta()).thenReturn(meta);
        when(meta.getPersistentDataContainer()).thenReturn(pdc);
        // Use any() to avoid generic type erasure issues with PersistentDataType
        when(pdc.get(any(), any())).thenReturn(PurifiedWaterListener.ACID);
        return item;
    }

}
