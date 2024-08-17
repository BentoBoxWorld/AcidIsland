package world.bentobox.acidisland.listeners;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World.Environment;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.earth2me.essentials.Essentials;

import world.bentobox.acidisland.AcidIsland;
import world.bentobox.acidisland.events.AcidEvent;
import world.bentobox.acidisland.events.AcidRainEvent;
import world.bentobox.acidisland.events.EntityDamageByAcidEvent;
import world.bentobox.acidisland.events.EntityDamageByAcidEvent.Acid;
import world.bentobox.acidisland.world.AcidTask;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.util.Util;

/**
 * Applies the acid effect to players
 *
 * @author tastybento
 */
public class AcidEffect implements Listener {

    private final AcidIsland addon;
    private final Map<Player, Long> burningPlayers;
    private final Map<Player, Long> wetPlayers;
    private Essentials essentials;
    private boolean essentialsCheck;
    private static final List<PotionEffectType> EFFECTS;
    static {
        if (!inTest()) {
            EFFECTS = List.of(PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.HUNGER,
                    PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.WEAKNESS,
                    PotionEffectType.POISON);
        } else {
            EFFECTS = List.of();
        }
    }

    private static final List<PotionEffectType> IMMUNE_EFFECTS;
    static {
        if (!inTest()) {
            IMMUNE_EFFECTS = List.of(PotionEffectType.WATER_BREATHING, PotionEffectType.CONDUIT_POWER);
        } else {
            IMMUNE_EFFECTS = List.of();
        }
    }

    /**
     * This checks the stack trace for @Test to determine if a test is calling the code and skips.
     * TODO: when we find a way to mock Enchantment, remove this.
     * @return true if it's a test.
     */
    private static boolean inTest() {
        return Arrays.stream(Thread.currentThread().getStackTrace()).anyMatch(e -> e.getClassName().endsWith("Test"));
    }

    public AcidEffect(AcidIsland addon) {
        this.addon = addon;
        burningPlayers = new HashMap<>();
        wetPlayers = new HashMap<>();
        // Burn monsters or animals that fall into the acid
        new AcidTask(addon);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent e) {
        burningPlayers.remove(e.getEntity());
        wetPlayers.remove(e.getEntity());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSeaBounce(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (!player.getGameMode().equals(GameMode.CREATIVE) && !player.getGameMode().equals(GameMode.SPECTATOR)
                && player.getWorld().equals(addon.getOverWorld())
                && player.getLocation().getBlockY() < player.getWorld().getMinHeight()) {
            player.setVelocity(new Vector(player.getVelocity().getX(), 1D, player.getVelocity().getZ()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        // Fast checks
        if ((addon.getSettings().getAcidRainDamage() == 0 && addon.getSettings().getAcidDamage() == 0)
                || player.isDead() || player.getGameMode().equals(GameMode.CREATIVE)
                || player.getGameMode().equals(GameMode.SPECTATOR)
                || addon.getPlayers().isInTeleport(player.getUniqueId())
                || !Util.sameWorld(addon.getOverWorld(), player.getWorld())
                || (!player.isOp() && player.hasPermission("acidisland.mod.noburn"))
                || (player.isOp() && !addon.getSettings().isAcidDamageOp())) {
            return;
        }
        // Slow checks
        // Check for acid rain
        if (addon.getSettings().getAcidRainDamage() > 0D && addon.getOverWorld().hasStorm()) {
            if (isSafeFromRain(player)) {
                wetPlayers.remove(player);
            } else if (!wetPlayers.containsKey(player)) {
                // Start hurting them
                // Add to the list
                wetPlayers.put(player, System.currentTimeMillis() + addon.getSettings().getAcidDamageDelay() * 1000);
                // This runnable continuously hurts the player even if
                // they are not
                // moving but are in acid rain.

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Check if it is still raining or player is safe or dead or there is no damage
                        if (checkForRain(player)) {
                            this.cancel();
                        }

                    }
                }.runTaskTimer(addon.getPlugin(), 0L, 20L);
            }

        }
        // If they are already burning in acid then return
        if (burningPlayers.containsKey(player) || isSafeFromAcid(player)) {
            return;
        }
        // ACID!
        // Put the player into the acid list
        burningPlayers.put(player, System.currentTimeMillis() + addon.getSettings().getAcidDamageDelay() * 1000);
        // This runnable continuously hurts the player even if they are not
        // moving but are in acid.
        new BukkitRunnable() {
            @Override
            public void run() {
                if (continuouslyHurtPlayer(player)) {
                    this.cancel();
                }

            }
        }.runTaskTimer(addon.getPlugin(), 0L, 20L);
    }

    /**
     * Check if it is still raining or player is safe or dead or there is no damage
     * @param player player
     * @return true if the acid raid damage should stop
     */
    protected boolean checkForRain(Player player) {
        if (!addon.getOverWorld().hasStorm() || player.isDead() || isSafeFromRain(player)
                || addon.getSettings().getAcidRainDamage() <= 0D) {
            wetPlayers.remove(player);
            return true;
            // Check they are still in this world
        } else if (wetPlayers.containsKey(player) && wetPlayers.get(player) < System.currentTimeMillis()) {
            double protection = addon.getSettings().getAcidRainDamage() * getDamageReduced(player);

            User user = User.getInstance(player);
            // Get the percentage reduction and ensure the value is between 0 and 100
            double percent = (100
                    - Math.max(0, Math.min(100, user.getPermissionValue("acidisland.protection.rain", 0)))) / 100D;

            double totalDamage = Math.max(0, addon.getSettings().getAcidRainDamage() - protection) * percent;

            AcidRainEvent event = new AcidRainEvent(player, totalDamage, protection,
                    addon.getSettings().getAcidRainEffects());
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                event.getPotionEffects().stream().filter(EFFECTS::contains).forEach(t -> player
                        .addPotionEffect(new PotionEffect(t, addon.getSettings().getRainEffectDuation() * 20, 1)));
                // Apply damage if there is any
                if (event.getRainDamage() > 0D) {
                    EntityDamageByAcidEvent e = new EntityDamageByAcidEvent(player, event.getRainDamage(), Acid.RAIN);
                    // Fire event
                    Bukkit.getPluginManager().callEvent(e);
                    if (!e.isCancelled()) {
                        player.damage(event.getRainDamage());
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 3F, 3F);
                    }
                }
            }
        }
        return false;
    }

    protected boolean continuouslyHurtPlayer(Player player) {
        if (player.isDead() || isSafeFromAcid(player)) {
            burningPlayers.remove(player);
            return true;
        } else if (burningPlayers.containsKey(player) && burningPlayers.get(player) < System.currentTimeMillis()) {
            double protection = addon.getSettings().getAcidDamage() * getDamageReduced(player);

            User user = User.getInstance(player);
            // Get the percentage reduction and ensure the value is between 0 and 100
            double percent = (100
                    - Math.max(0, Math.min(100, user.getPermissionValue("acidisland.protection.acid", 0)))) / 100D;

            double totalDamage = Math.max(0, addon.getSettings().getAcidDamage() - protection) * percent;

            AcidEvent event = new AcidEvent(player, totalDamage, protection, addon.getSettings().getAcidEffects());
            addon.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                event.getPotionEffects().stream().filter(EFFECTS::contains).forEach(t -> player
                        .addPotionEffect(new PotionEffect(t, addon.getSettings().getAcidEffectDuation() * 20, 1)));
                // Apply damage if there is any
                if (event.getTotalDamage() > 0D) {               
                    EntityDamageByAcidEvent e = new EntityDamageByAcidEvent(player, event.getTotalDamage(), Acid.WATER);
                    // Fire event
                    Bukkit.getPluginManager().callEvent(e);
                    if (!e.isCancelled()) {
                        player.damage(event.getTotalDamage());
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 3F, 3F);
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check if player is safe from rain
     * @param player - player
     * @return true if they are safe
     */
    private boolean isSafeFromRain(Player player) {
        if (isEssentialsGodMode(player) || player.getWorld().getEnvironment().equals(Environment.NETHER)
                || player.getGameMode() != GameMode.SURVIVAL
                || player.getWorld().getEnvironment().equals(Environment.THE_END)
                || (addon.getSettings().isHelmetProtection() && (player.getInventory().getHelmet() != null
                && player.getInventory().getHelmet().getType().name().contains("HELMET")))
                || (!addon.getSettings().isAcidDamageSnow() && player.getLocation().getBlock().getTemperature() < 0.1) // snow falls
                || player.getLocation().getBlock().getHumidity() == 0 // dry
                || (player.getActivePotionEffects().stream().map(PotionEffect::getType)
                        .anyMatch(IMMUNE_EFFECTS::contains))
                // Protect visitors
                || (addon.getPlugin().getIWM().getIvSettings(player.getWorld()).contains(DamageCause.CUSTOM.name())
                        && !addon.getIslands().userIsOnIsland(player.getWorld(), User.getInstance(player)))) {
            return true;
        }
        // Check if all air above player
        for (int y = player.getLocation().getBlockY() + 2; y < player.getLocation().getWorld().getMaxHeight(); y++) {
            if (!player.getLocation().getWorld()
                    .getBlockAt(player.getLocation().getBlockX(), y, player.getLocation().getBlockZ()).getType()
                    .equals(Material.AIR)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if player can be burned by acid
     * @param player - player
     * @return true if player is safe
     */
    boolean isSafeFromAcid(Player player) {
        // Check for GodMode
        if (isEssentialsGodMode(player) || player.getGameMode() != GameMode.SURVIVAL
                // Protect visitors
                || (addon.getPlugin().getIWM().getIvSettings(player.getWorld()).contains(DamageCause.CUSTOM.name())
                        && !addon.getIslands().userIsOnIsland(player.getWorld(), User.getInstance(player)))) {
            return true;
        }
        // Not in liquid or on snow
        if (!player.getLocation().getBlock().getType().equals(Material.WATER)
                && !player.getLocation().getBlock().getType().equals(Material.BUBBLE_COLUMN)
                && (!player.getLocation().getBlock().getType().equals(Material.SNOW)
                        || !addon.getSettings().isAcidDamageSnow())
                && !player.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.WATER)) {
            return true;
        }
        // Check if player is on a boat
        if (player.getVehicle() != null && (player.getVehicle().getType().equals(EntityType.BOAT)
                || player.getVehicle().getType().equals(EntityType.CHEST_BOAT))) {
            // I'M ON A BOAT! I'M ON A BOAT! A %^&&* BOAT! SNL Sketch. https://youtu.be/avaSdC0QOUM.
            return true;
        }
        // Check if full armor protects
        if (addon.getSettings().isFullArmorProtection() && Arrays.stream(player.getInventory().getArmorContents())
                .allMatch(i -> i != null && !i.getType().equals(Material.AIR))) {
            return true;
        }
        // Check if player has an active water potion or not
        return player.getActivePotionEffects().stream().map(PotionEffect::getType).anyMatch(IMMUNE_EFFECTS::contains);
    }

    /**
     * Checks if player has Essentials God Mode enabled.
     * @param player - player
     * @return true if God Mode enabled, false if not or if Essentials plug does not exist
     */
    private boolean isEssentialsGodMode(Player player) {
        if (!essentialsCheck && essentials == null) {
            essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            essentialsCheck = true;
        }
        return essentials != null && essentials.getUser(player).isGodModeEnabled();
    }

    /**
     * Checks what protection armor provides and slightly damages it as a result of the acid
     * @param le - player
     * @return A double that reflects how much armor the
     *         player has on. The higher the value, the more protection they
     *         have.
     */
    public static double getDamageReduced(LivingEntity le) {
        // Full diamond armor value = 20. This normalizes it to a max of 0.8. Enchantments can raise it out further.
        double red = le.getAttribute(Attribute.GENERIC_ARMOR).getValue() * 0.04;
        EntityEquipment inv = le.getEquipment();
        ItemStack boots = inv.getBoots();
        ItemStack helmet = inv.getHelmet();
        ItemStack chest = inv.getChestplate();
        ItemStack pants = inv.getLeggings();
        // Damage if helmet
        if (helmet != null && helmet.getType().name().contains("HELMET") && damage(helmet)) {
            le.getWorld().playSound(le.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
            inv.setHelmet(null);
        }
        if (boots != null && damage(boots)) {
            le.getWorld().playSound(le.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
            inv.setBoots(null);
        }
        // Pants
        if (pants != null && damage(pants)) {
            le.getWorld().playSound(le.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
            inv.setLeggings(null);
        }
        // Chest plate
        if (chest != null && damage(chest)) {
            le.getWorld().playSound(le.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
            inv.setChestplate(null);
        }
        return red;
    }

    private static boolean damage(ItemStack item) {
        ItemMeta im = item.getItemMeta();

        if (im instanceof Damageable d && !im.isUnbreakable()) {
            d.setDamage(d.getDamage() + 1);
            item.setItemMeta(d);
            return d.getDamage() >= item.getType().getMaxDurability();
        }
        return false;
    }
}