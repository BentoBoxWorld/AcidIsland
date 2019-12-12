package world.bentobox.acidisland.listeners;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
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

import world.bentobox.acidisland.AcidIsland;
import world.bentobox.acidisland.events.AcidEvent;
import world.bentobox.acidisland.events.AcidRainEvent;
import world.bentobox.acidisland.world.AcidTask;
import world.bentobox.bentobox.util.Util;

/**
 * Applies the acid effect to players
 *
 * @author tastybento
 */
public class AcidEffect implements Listener {

    private final AcidIsland addon;
    private final Map<Player, Long> burningPlayers = new HashMap<>();
    private final Map<Player, Long> wetPlayers = new HashMap<>();
    private static final List<PotionEffectType> EFFECTS = Arrays.asList(
            PotionEffectType.BLINDNESS,
            PotionEffectType.CONFUSION,
            PotionEffectType.HUNGER,
            PotionEffectType.SLOW,
            PotionEffectType.SLOW_DIGGING,
            PotionEffectType.WEAKNESS);
    private static final List<PotionEffectType> IMMUNE_EFFECTS = Arrays.asList(
            PotionEffectType.WATER_BREATHING,
            PotionEffectType.CONDUIT_POWER);

    public AcidEffect(AcidIsland addon) {
        this.addon = addon;
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
                && player.getWorld().equals(addon.getOverWorld()) && player.getLocation().getBlockY() < 1) {
            player.setVelocity(new Vector(player.getVelocity().getX(), 1D, player.getVelocity().getZ()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        // Fast checks
        if ((addon.getSettings().getAcidRainDamage() == 0 && addon.getSettings().getAcidDamage() == 0)
                || player.isDead()
                || player.getGameMode().equals(GameMode.CREATIVE)
                || player.getGameMode().equals(GameMode.SPECTATOR)
                || addon.getPlayers().isInTeleport(player.getUniqueId())
                || !Util.sameWorld(addon.getOverWorld(), player.getWorld())
                || (!player.isOp() && player.hasPermission("acidisland.mod.noburn"))
                || (!player.isOp() && player.hasPermission("admin.noburn"))
                || (player.isOp() && !addon.getSettings().isAcidDamageOp())) {
            return;
        }
        // Slow checks
        Location playerLoc = player.getLocation();
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
                        if (!addon.getOverWorld().hasStorm() || player.isDead() || isSafeFromRain(player) || addon.getSettings().getAcidRainDamage() <= 0D) {
                            wetPlayers.remove(player);
                            this.cancel();
                            // Check they are still in this world
                        } else if (wetPlayers.containsKey(player) && wetPlayers.get(player) < System.currentTimeMillis()) {
                            double protection = addon.getSettings().getAcidRainDamage() * getDamageReduced(player);
                            double totalDamage = Math.max(0, addon.getSettings().getAcidRainDamage() - protection);
                            AcidRainEvent event = new AcidRainEvent(player, totalDamage, protection, addon.getSettings().getAcidRainEffects());
                            addon.getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                event.getPotionEffects().stream().filter(EFFECTS::contains).forEach(t -> player.addPotionEffect(new PotionEffect(t, 600, 1)));
                                event.getPotionEffects().stream().filter(e -> e.equals(PotionEffectType.POISON)).forEach(t -> player.addPotionEffect(new PotionEffect(t, 200, 1)));
                                // Apply damage if there is any
                                if (event.getRainDamage() > 0D) {
                                    player.damage(event.getRainDamage());
                                    player.getWorld().playSound(playerLoc, Sound.ENTITY_CREEPER_PRIMED, 3F, 3F);
                                }
                            }
                        }
                    }
                }.runTaskTimer(addon.getPlugin(), 0L, 20L);
            }


        }
        // If they are already burning in acid then return
        if (burningPlayers.containsKey(player)) {
            return;
        }
        if (isSafeFromAcid(player)) {
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
                if (player.isDead() || isSafeFromAcid(player)) {
                    burningPlayers.remove(player);
                    this.cancel();
                } else if (burningPlayers.containsKey(player) && burningPlayers.get(player) < System.currentTimeMillis()) {
                    double protection = addon.getSettings().getAcidDamage() * getDamageReduced(player);
                    double totalDamage = Math.max(0, addon.getSettings().getAcidDamage() - protection);
                    AcidEvent acidEvent = new AcidEvent(player, totalDamage, protection, addon.getSettings().getAcidEffects());
                    addon.getServer().getPluginManager().callEvent(acidEvent);
                    if (!acidEvent.isCancelled()) {
                        acidEvent.getPotionEffects().stream().filter(EFFECTS::contains).forEach(t -> player.addPotionEffect(new PotionEffect(t, 600, 1)));
                        acidEvent.getPotionEffects().stream().filter(e -> e.equals(PotionEffectType.POISON)).forEach(t -> player.addPotionEffect(new PotionEffect(t, 200, 1)));
                        // Apply damage if there is any
                        if (acidEvent.getTotalDamage() > 0D) {
                            player.damage(acidEvent.getTotalDamage());
                            player.getWorld().playSound(playerLoc, Sound.ENTITY_CREEPER_PRIMED, 3F, 3F);
                        }
                    }
                }
            }
        }.runTaskTimer(addon.getPlugin(), 0L, 20L);
    }

    /**
     * Check if player is safe from rain
     * @param player - player
     * @return true if they are safe
     */
    private boolean isSafeFromRain(Player player) {
        if (player.getWorld().getEnvironment().equals(Environment.NETHER)
                || player.getWorld().getEnvironment().equals(Environment.THE_END)
                || (addon.getSettings().isHelmetProtection() && (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType().name().contains("HELMET")))
                || (!addon.getSettings().isAcidDamageSnow() && player.getLocation().getBlock().getTemperature() < 0.1) // snow falls
                || player.getLocation().getBlock().getHumidity() == 0 // dry
                || (player.getActivePotionEffects().stream().map(PotionEffect::getType).anyMatch(IMMUNE_EFFECTS::contains))
                ) {
            return true;
        }
        // Check if all air above player
        for (int y = player.getLocation().getBlockY() + 2; y < player.getLocation().getWorld().getMaxHeight(); y++) {
            if (!player.getLocation().getWorld().getBlockAt(player.getLocation().getBlockX(), y, player.getLocation().getBlockZ()).getType().equals(Material.AIR)) {
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
    private boolean isSafeFromAcid(Player player) {
        // Not in liquid or on snow
        if (!player.getLocation().getBlock().getType().equals(Material.WATER)
                && !player.getLocation().getBlock().getType().equals(Material.BUBBLE_COLUMN)
                && (!player.getLocation().getBlock().getType().equals(Material.SNOW) || !addon.getSettings().isAcidDamageSnow())
                && !player.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.WATER)) {
            return true;
        }
        // Check if player is on a boat
        if (player.getVehicle() != null && player.getVehicle().getType().equals(EntityType.BOAT)) {
            // I'M ON A BOAT! I'M ON A BOAT! A %^&&* BOAT! SNL Sketch. https://youtu.be/avaSdC0QOUM.
            return true;
        }
        // Check if full armor protects
        if (addon.getSettings().isFullArmorProtection()
                && Arrays.stream(player.getInventory().getArmorContents()).allMatch(i -> i != null && !i.getType().equals(Material.AIR))) {
            return true;
        }
        // Check if player has an active water potion or not
        return player.getActivePotionEffects().stream().map(PotionEffect::getType).anyMatch(IMMUNE_EFFECTS::contains);
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
        if (helmet != null) {
            // Damage if helmet
            if (helmet.getType().name().contains("HELMET") && damage(helmet)) {
                le.getWorld().playSound(le.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
                inv.setHelmet(null);
            }
        }
        if (boots != null) {
            // Damage
            if (damage(boots)) {
                le.getWorld().playSound(le.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
                inv.setBoots(null);
            }
        }
        // Pants
        if (pants != null) {
            // Damage
            if (damage(pants)) {
                le.getWorld().playSound(le.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
                inv.setLeggings(null);
            }
        }
        // Chest plate
        if (chest != null) {
            // Damage
            if (damage(chest)) {
                le.getWorld().playSound(le.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
                inv.setChestplate(null);
            }
        }
        return red;
    }

    private static boolean damage(ItemStack item) {
        ItemMeta im = item.getItemMeta();

        if (im instanceof Damageable) {
            Damageable d = (Damageable)im;
            d.setDamage(d.getDamage() + 1);
            item.setItemMeta((ItemMeta) d);
            return d.getDamage() >= item.getType().getMaxDurability();
        }
        return false;
    }
}