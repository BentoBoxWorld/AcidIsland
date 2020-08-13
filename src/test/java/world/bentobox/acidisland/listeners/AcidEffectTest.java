package world.bentobox.acidisland.listeners;

import static org.junit.Assert.assertTrue;
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

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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
import world.bentobox.bentobox.managers.PlayersManager;
import world.bentobox.bentobox.util.Util;

/**
 * @author tastybento
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class, Util.class})
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
    

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Bukkit.class, Mockito.RETURNS_MOCKS);
        when(Bukkit.getScheduler()).thenReturn(scheduler);
        when(addon.getSettings()).thenReturn(settings);
        when(addon.getOverWorld()).thenReturn(world);
        
        // Essentials
        when(Bukkit.getPluginManager()).thenReturn(pim);
        when(pim.getPlugin(eq("Essentials"))).thenReturn(essentials);
        when(essentials.getUser(any(Player.class))).thenReturn(essentialsUser);
        
        // Player
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(player.getWorld()).thenReturn(world);
        when(player.getLocation()).thenReturn(location);
        when(player.getVelocity()).thenReturn(new Vector(0,0,0));
        when(player.getInventory()).thenReturn(inv);
        ItemStack[] armor = { new ItemStack(Material.CHAINMAIL_HELMET) };
        when(inv.getArmorContents()).thenReturn(armor);
        
        // Location
        when(location.getBlockY()).thenReturn(0);
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
        when(Bukkit.getItemFactory()).thenReturn(itemFactory);
        when(itemFactory.getItemMeta(any())).thenReturn(itemMeta);
        
        // Util
        PowerMockito.mockStatic(Util.class);
        when(Util.sameWorld(any(), any())).thenReturn(true);
        
        // World
        when(world.hasStorm()).thenReturn(true);
        when(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(airBlock);
        when(world.getMaxHeight()).thenReturn(5);
        when(world.getEnvironment()).thenReturn(Environment.NORMAL);
        
        ae = new AcidEffect(addon);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
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
        // 2 times only
        verify(addon, times(2)).getPlugin();
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
        // 2 times only
        verify(addon, times(2)).getPlugin();
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
        when(boat.getType()).thenReturn(EntityType.BOAT);
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
        ItemStack[] partial = { new ItemStack(Material.CHAINMAIL_HELMET), new ItemStack(Material.AIR) };
        when(inv.getArmorContents()).thenReturn(partial);
        PlayerMoveEvent e = new PlayerMoveEvent(player, from, to);
        ae.onPlayerMove(e);
        verify(settings).getAcidDamageDelay();
    }
    
    /**
     * Test method for {@link world.bentobox.acidisland.listeners.AcidEffect#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
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
        when(value.getValue()).thenReturn(20D);
        // Diamond armor
        when(player.getAttribute(eq(Attribute.GENERIC_ARMOR))).thenReturn(value);
        EntityEquipment equip = mock(EntityEquipment.class);
        when(equip.getBoots()).thenReturn(new ItemStack(Material.DIAMOND_BOOTS));
        when(equip.getHelmet()).thenReturn(new ItemStack(Material.DIAMOND_HELMET));
        when(equip.getLeggings()).thenReturn(new ItemStack(Material.DIAMOND_LEGGINGS));
        when(equip.getChestplate()).thenReturn(new ItemStack(Material.DIAMOND_CHESTPLATE));
        when(player.getEquipment()).thenReturn(equip);
        double a = AcidEffect.getDamageReduced(player);
        assertTrue(a == 0.8);
        
    }

}
