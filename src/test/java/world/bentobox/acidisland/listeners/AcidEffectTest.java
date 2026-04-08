package world.bentobox.acidisland.listeners;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AcidEffectTest {

    @Mock
    private AcidIsland addon;
    @Mock
    private BukkitScheduler scheduler;
    @Mock
    private AISettings settings;
    // DUT
    private AcidEffect ae;
    @Mock
    private Player player;
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
    @Mock
    private Server server;

    private ServerMock mockServer;
    private MockedStatic<Bukkit> mockedBukkit;
    private MockedStatic<Util> mockedUtil;

    @BeforeEach
    public void setUp() {
        mockServer = MockBukkit.mock();
        mockedBukkit = Mockito.mockStatic(Bukkit.class, Mockito.RETURNS_DEEP_STUBS);
        mockedBukkit.when(Bukkit::getMinecraftVersion).thenReturn("1.21.11");
        mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
        mockedBukkit.when(Bukkit::getScheduler).thenReturn(scheduler);
        when(addon.getSettings()).thenReturn(settings);
        when(addon.getOverWorld()).thenReturn(world);

        // Essentials
        mockedBukkit.when(Bukkit::getPluginManager).thenReturn(pim);
        when(pim.getPlugin(eq("Essentials"))).thenReturn(essentials);
        when(essentials.getUser(any(Player.class))).thenReturn(essentialsUser);

        // Player
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(player.getWorld()).thenReturn(world);
        when(player.getLocation()).thenReturn(location);
        when(player.getVelocity()).thenReturn(new Vector(0, 0, 0));
        when(player.getInventory()).thenReturn(inv);
        ItemStack[] armor = { new ItemStack(Material.CHAINMAIL_HELMET) };
        when(inv.getArmorContents()).thenReturn(armor);

        // Location
        when(location.getBlockY()).thenReturn(-66);
        when(location.getWorld()).thenReturn(world);
        when(location.getBlock()).thenReturn(block);

        // Blocks
        when(block.getType()).thenReturn(Material.WATER);
        when(block.getTemperature()).thenReturn(0.5D);
        when(block.getHumidity()).thenReturn(0.3D); // Not dry
        when(block.getRelative(any())).thenReturn(block);
        when(airBlock.getType()).thenReturn(Material.AIR);
        when(solidBlock.getType()).thenReturn(Material.CHISELED_RED_SANDSTONE);

        // Settings
        when(settings.getAcidDestroyItemTime()).thenReturn(0L);
        when(settings.getAcidRainDamage()).thenReturn(10);
        when(settings.getAcidDamage()).thenReturn(10);
        when(settings.getAcidDamageDelay()).thenReturn(60L);

        // Players Manager
        when(addon.getPlayers()).thenReturn(pm);

        // Mock item factory (for itemstacks)
        ItemFactory itemFactory = mock(ItemFactory.class);
        mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
        when(itemFactory.getItemMeta(any())).thenReturn(itemMeta);

        // Util
        mockedUtil = Mockito.mockStatic(Util.class);
        mockedUtil.when(() -> Util.sameWorld(any(), any())).thenReturn(true);

        // World
        when(world.hasStorm()).thenReturn(true);
        when(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(airBlock);
        when(world.getMaxHeight()).thenReturn(5);
        when(world.getMinHeight()).thenReturn(-65);
        when(world.getEnvironment()).thenReturn(Environment.NORMAL);

        // Plugin
        when(addon.getPlugin()).thenReturn(plugin);
        when(plugin.getIWM()).thenReturn(iwm);
        // CUSTOM damage protection
        when(iwm.getIvSettings(any())).thenReturn(Collections.singletonList("CUSTOM"));

        // Island manager
        when(addon.getIslands()).thenReturn(im);
        when(im.userIsOnIsland(any(), any())).thenReturn(true);

        ae = new AcidEffect(addon);
    }

    @AfterEach
    public void tearDown() {
        mockedUtil.closeOnDemand();
        mockedBukkit.closeOnDemand();
        MockBukkit.unmock();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent)}.
     */
    @Test
    public void testOnPlayerDeath() {
        PlayerDeathEvent e = mock(PlayerDeathEvent.class);
        ae.onPlayerDeath(e);
        verify(e, times(2)).getEntity();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onSeaBounce(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnSeaBounce() {
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onSeaBounce(e);
        ArgumentCaptor<Vector> argument = ArgumentCaptor.forClass(Vector.class);
        verify(player).setVelocity(argument.capture());
        assertTrue(argument.getValue().getBlockY() == 1D);
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onSeaBounce(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnSeaBounceCreative() {
        when(player.getGameMode()).thenReturn(GameMode.CREATIVE);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onSeaBounce(e);
        verify(player, never()).setVelocity(any());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onSeaBounce(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnSeaBounceSpectator() {
        when(player.getGameMode()).thenReturn(GameMode.SPECTATOR);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onSeaBounce(e);
        verify(player, never()).setVelocity(any());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onSeaBounce(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnSeaBounceWrongWorld() {
        when(addon.getOverWorld()).thenReturn(mock(World.class));
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onSeaBounce(e);
        verify(player, never()).setVelocity(any());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onSeaBounce(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnSeaBounceNotAtBottom() {
        when(location.getBlockY()).thenReturn(10);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onSeaBounce(e);
        verify(player, never()).setVelocity(any());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveAcidAndRainDamage() {
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings, times(2)).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveVisitorNoAcidAndRainDamage() {
        when(im.userIsOnIsland(any(), any())).thenReturn(false);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings, never()).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveVisitorAcidAndRainDamage() {
        // No protection against CUSTOM damage
        when(iwm.getIvSettings(any())).thenReturn(Collections.emptyList());
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings, times(2)).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveGodModeNoAcidAndRainDamage() {
        when(essentialsUser.isGodModeEnabled()).thenReturn(true);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings, never()).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveDryNoDamage() {
        when(block.getHumidity()).thenReturn(0D);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveSnowDamage() {
        when(block.getTemperature()).thenReturn(0D); // Cold
        when(settings.isAcidDamageSnow()).thenReturn(true);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings, times(2)).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveNoSnowDamage() {
        when(block.getTemperature()).thenReturn(0D); // Cold
        when(settings.isAcidDamageSnow()).thenReturn(false);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveHelmetProtection() {
        when(inv.getHelmet()).thenReturn(new ItemStack(Material.DIAMOND_HELMET));
        when(player.getInventory()).thenReturn(inv);
        when(settings.isHelmetProtection()).thenReturn(true);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveNoHelmetProtection() {
        when(inv.getHelmet()).thenReturn(new ItemStack(Material.CARVED_PUMPKIN));
        when(player.getInventory()).thenReturn(inv);
        when(settings.isHelmetProtection()).thenReturn(true);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings, times(2)).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveUnderObject() {
        when(world.getBlockAt(anyInt(), anyInt(),anyInt())).thenReturn(solidBlock);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveAcidRainWrongWorld() {
        World nether = mock(World.class);
        when(nether.getName()).thenReturn("world_nether");
        when(nether.getEnvironment()).thenReturn(Environment.NETHER);
        when(player.getWorld()).thenReturn(nether);

        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        // 3 times only
        verify(addon, times(3)).getPlugin();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveAcidRainWrongWorldEnd() {
        World end = mock(World.class);
        when(end.getName()).thenReturn("world_end");
        when(end.getEnvironment()).thenReturn(Environment.THE_END);
        when(player.getWorld()).thenReturn(end);

        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        // 3 times only
        verify(addon, times(3)).getPlugin();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveNoAcidRain() {
        when(settings.getAcidRainDamage()).thenReturn(0);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveNoAcidDamage() {
        when(settings.getAcidRainDamage()).thenReturn(0);
        when(settings.getAcidDamage()).thenReturn(0);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings, never()).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveBubbleColumn() {
        when(settings.getAcidRainDamage()).thenReturn(0);
        when(block.getType()).thenReturn(Material.BUBBLE_COLUMN);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveSnow() {
        when(settings.getAcidRainDamage()).thenReturn(0);
        when(block.getType()).thenReturn(Material.SNOW);
        when(settings.isAcidDamageSnow()).thenReturn(true);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveNoSnowAcidDamage() {
        when(settings.getAcidRainDamage()).thenReturn(0);
        when(block.getType()).thenReturn(Material.SNOW);
        when(settings.isAcidDamageSnow()).thenReturn(false);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings, never()).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveInBoat() {
        when(settings.getAcidRainDamage()).thenReturn(0);
        Entity boat = mock(Boat.class);
        when(boat.getType()).thenReturn(EntityType.ACACIA_BOAT);
        when(player.getVehicle()).thenReturn(boat);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings, never()).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveNotInBoat() {
        when(settings.getAcidRainDamage()).thenReturn(0);
        Entity horse = mock(Horse.class);
        when(horse.getType()).thenReturn(EntityType.HORSE);
        when(player.getVehicle()).thenReturn(horse);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveFullArmor() {
        when(settings.getAcidRainDamage()).thenReturn(0);
        when(settings.isFullArmorProtection()).thenReturn(true);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings, never()).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveNotFullArmor() {
        when(settings.getAcidRainDamage()).thenReturn(0);
        when(settings.isFullArmorProtection()).thenReturn(true);
        ItemStack[] partial = { new ItemStack(Material.CHAINMAIL_HELMET), null };
        when(inv.getArmorContents()).thenReturn(partial);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    @Disabled("Cannot be tested because of the PotionEffectType issue")
    public void testOnPlayerMoveActivePotions() {
        Collection<PotionEffect> potions = new ArrayList<>();
        potions.add(new PotionEffect(PotionEffectType.WATER_BREATHING, 0, 0, false, false, false));
        when(player.getActivePotionEffects()).thenReturn(potions);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings, never()).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    @Disabled("Cannot be tested because of the PotionEffectType issue")
    public void testOnPlayerMoveActivePotionsConduit() {
        Collection<PotionEffect> potions = new ArrayList<>();
        potions.add(new PotionEffect(PotionEffectType.CONDUIT_POWER, 0, 0, false, false, false));
        when(player.getActivePotionEffects()).thenReturn(potions);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings, never()).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    @Disabled("Cannot be tested because of the PotionEffectType issue")
    public void testOnPlayerMoveActivePotionsBadOmen() {
        Collection<PotionEffect> potions = new ArrayList<>();
        potions.add(new PotionEffect(PotionEffectType.BAD_OMEN, 0, 0, false, false, false));
        when(player.getActivePotionEffects()).thenReturn(potions);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings, times(2)).getAcidDamageDelay();
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#getDamageReduced(org.bukkit.entity.LivingEntity)}.
     */
    @Test
    public void testGetDamageReducedFullDiamond() {
        AttributeInstance value = mock(AttributeInstance.class);
        when(value.getValue()).thenReturn(20.0);
        when(player.getAttribute(eq(Attribute.ARMOR))).thenReturn(value);
        EntityEquipment equip = mock(EntityEquipment.class);
        when(equip.getBoots()).thenReturn(new ItemStack(Material.DIAMOND_BOOTS));
        when(equip.getHelmet()).thenReturn(new ItemStack(Material.DIAMOND_HELMET));
        when(equip.getLeggings()).thenReturn(new ItemStack(Material.DIAMOND_LEGGINGS));
        when(equip.getChestplate()).thenReturn(new ItemStack(Material.DIAMOND_CHESTPLATE));
        when(player.getEquipment()).thenReturn(equip);
        double a = AcidEffect.getDamageReduced(player);
        assertTrue(a == 0.8);
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#checkForRain(Player)}.
     */
    @Test
    public void testCheckForRain() {
        when(world.hasStorm()).thenReturn(true);
        when(player.isDead()).thenReturn(false);
        when(settings.getAcidRainDamage()).thenReturn(10);
        when(world.getEnvironment()).thenReturn(Environment.NORMAL);
        assertFalse(ae.checkForRain(player));
        when(world.hasStorm()).thenReturn(false);
        when(player.isDead()).thenReturn(false);
        when(settings.getAcidRainDamage()).thenReturn(10);
        when(world.getEnvironment()).thenReturn(Environment.NORMAL);
        assertTrue(ae.checkForRain(player));
        when(world.hasStorm()).thenReturn(true);
        when(player.isDead()).thenReturn(true);
        when(settings.getAcidRainDamage()).thenReturn(10);
        when(world.getEnvironment()).thenReturn(Environment.NORMAL);
        assertTrue(ae.checkForRain(player));
        when(world.hasStorm()).thenReturn(true);
        when(player.isDead()).thenReturn(false);
        when(settings.getAcidRainDamage()).thenReturn(0);
        when(world.getEnvironment()).thenReturn(Environment.NORMAL);
        assertTrue(ae.checkForRain(player));
        when(world.hasStorm()).thenReturn(true);
        when(player.isDead()).thenReturn(false);
        when(settings.getAcidRainDamage()).thenReturn(10);
        when(world.getEnvironment()).thenReturn(Environment.NETHER);
        assertTrue(ae.checkForRain(player));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#checkForRain(Player)}.
     */
    @Test
    public void testCheckForRainWetPlayer() {
        AttributeInstance value = mock(AttributeInstance.class);
        when(value.getValue()).thenReturn(20D);
        // Diamond armor
        when(player.getAttribute(eq(Attribute.ARMOR))).thenReturn(value);
        EntityEquipment equip = mock(EntityEquipment.class);
        when(equip.getBoots()).thenReturn(new ItemStack(Material.DIAMOND_BOOTS));
        when(equip.getHelmet()).thenReturn(new ItemStack(Material.DIAMOND_HELMET));
        when(equip.getLeggings()).thenReturn(new ItemStack(Material.DIAMOND_LEGGINGS));
        when(equip.getChestplate()).thenReturn(new ItemStack(Material.DIAMOND_CHESTPLATE));
        when(player.getEquipment()).thenReturn(equip);

        when(settings.getAcidDamageDelay()).thenReturn(0L);
        when(world.hasStorm()).thenReturn(true);
        when(player.isDead()).thenReturn(false);
        when(settings.getAcidRainDamage()).thenReturn(10);
        when(world.getEnvironment()).thenReturn(Environment.NORMAL);
        testOnPlayerMoveAcidAndRainDamage();

        assertFalse(ae.checkForRain(player));
        verify(player).damage(2.0d); // Reduced due to armor
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#isSafeFromAcid(Player)}.
     */
    @Test
    public void testIsSafeFromAcid() {
        assertFalse(ae.isSafeFromAcid(player));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#isSafeFromAcid(Player)}.
     */
    @Test
    public void testIsSafeFromAcidEssentialGodMode() {
        when(essentialsUser.isGodModeEnabled()).thenReturn(true);
        assertTrue(ae.isSafeFromAcid(player));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#isSafeFromAcid(Player)}.
     */
    @Test
    public void testIsSafeFromAcidBoat() {
        when(player.isInsideVehicle()).thenReturn(true);
        Entity boat = mock(Entity.class);
        when(boat.getType()).thenReturn(EntityType.ACACIA_BOAT);
        when(player.getVehicle()).thenReturn(boat);
        assertTrue(ae.isSafeFromAcid(player));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#isSafeFromAcid(Player)}.
     */
    @Test
    public void testIsSafeFromAcidChestBoat() {
        when(player.isInsideVehicle()).thenReturn(true);
        Entity boat = mock(Entity.class);
        when(boat.getType()).thenReturn(EntityType.ACACIA_CHEST_BOAT);
        when(player.getVehicle()).thenReturn(boat);
        assertTrue(ae.isSafeFromAcid(player));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#isSafeFromAcid(Player)}.
     */
    @Test
    public void testIsSafeFromAcidFullArmor() {
        when(settings.isFullArmorProtection()).thenReturn(true);
        ItemStack[] armor = { new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.CHAINMAIL_HELMET) };
        when(inv.getArmorContents()).thenReturn(armor);
        when(player.getInventory()).thenReturn(inv);
        assertTrue(ae.isSafeFromAcid(player));
    }

    // --- Additional coverage tests ---

    /**
     * Test that dead player is ignored by onPlayerMove.
     */
    @Test
    public void testOnPlayerMoveDeadPlayer() {
        when(player.isDead()).thenReturn(true);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings, never()).getAcidDamageDelay();
    }

    /**
     * Test that player in teleport is ignored by onPlayerMove.
     */
    @Test
    public void testOnPlayerMoveInTeleport() {
        when(pm.isInTeleport(any())).thenReturn(true);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings, never()).getAcidDamageDelay();
    }

    /**
     * Test that player with noburn permission is ignored.
     */
    @Test
    public void testOnPlayerMoveNoburnPermission() {
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("acidisland.mod.noburn")).thenReturn(true);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings, never()).getAcidDamageDelay();
    }

    /**
     * Test that OP player with acidDamageOp=false is ignored.
     */
    @Test
    public void testOnPlayerMoveOpNoDamage() {
        when(player.isOp()).thenReturn(true);
        when(settings.isAcidDamageOp()).thenReturn(false);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings, never()).getAcidDamageDelay();
    }

    /**
     * Test isSafeFromAcid when player is not in water (standing on air).
     */
    @Test
    public void testIsSafeFromAcidNotInWater() {
        when(block.getType()).thenReturn(Material.AIR);
        when(block.getRelative(any())).thenReturn(airBlock);
        assertTrue(ae.isSafeFromAcid(player));
    }

    /**
     * Test isSafeFromAcid when player is in creative mode.
     */
    @Test
    public void testIsSafeFromAcidCreative() {
        when(player.getGameMode()).thenReturn(GameMode.CREATIVE);
        assertTrue(ae.isSafeFromAcid(player));
    }

    /**
     * Test isSafeFromAcid for visitors with CUSTOM protection and not on island.
     */
    @Test
    public void testIsSafeFromAcidVisitorProtection() {
        when(im.userIsOnIsland(any(), any())).thenReturn(false);
        assertTrue(ae.isSafeFromAcid(player));
    }

    /**
     * Test checkForRain returns true (safe) when no storm.
     */
    @Test
    public void testCheckForRainNoStorm() {
        when(world.hasStorm()).thenReturn(false);
        assertTrue(ae.checkForRain(player));
    }

    /**
     * Test checkForRain returns true (safe) when player is dead.
     */
    @Test
    public void testCheckForRainDeadPlayer() {
        when(player.isDead()).thenReturn(true);
        assertTrue(ae.checkForRain(player));
    }

    /**
     * Test checkForRain returns true (safe) when rain damage is zero.
     */
    @Test
    public void testCheckForRainZeroDamage() {
        when(settings.getAcidRainDamage()).thenReturn(0);
        assertTrue(ae.checkForRain(player));
    }

    /**
     * Test getDamageReduced returns 0 when no armor.
     */
    @Test
    public void testGetDamageReducedNoArmor() {
        AttributeInstance value = mock(AttributeInstance.class);
        when(value.getValue()).thenReturn(0D);
        when(player.getAttribute(eq(Attribute.ARMOR))).thenReturn(value);
        EntityEquipment equip = mock(EntityEquipment.class);
        when(player.getEquipment()).thenReturn(equip);
        double a = AcidEffect.getDamageReduced(player);
        assertTrue(a == 0D);
    }

    /**
     * Test getDamageReduced with partial armor (some null slots).
     */
    @Test
    public void testGetDamageReducedPartialArmor() {
        AttributeInstance value = mock(AttributeInstance.class);
        when(value.getValue()).thenReturn(8D); // partial armor
        when(player.getAttribute(eq(Attribute.ARMOR))).thenReturn(value);
        EntityEquipment equip = mock(EntityEquipment.class);
        when(equip.getHelmet()).thenReturn(new ItemStack(Material.IRON_HELMET));
        // boots, chest, pants are null
        when(player.getEquipment()).thenReturn(equip);
        double a = AcidEffect.getDamageReduced(player);
        assertEquals(0.32, a, 0.001);
    }

}
