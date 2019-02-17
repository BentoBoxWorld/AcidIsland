package world.bentobox.acidisland.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
    private final List<Player> burningPlayers = new ArrayList<>();
    private final List<Player> wetPlayers = new ArrayList<>();
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
            } else if (!wetPlayers.contains(player)) {
                // Start hurting them
                // Add to the list
                wetPlayers.add(player);
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
                        } else {
                            double protection = addon.getSettings().getAcidRainDamage() * getDamageReduced(player);
                            double totalDamage = Math.max(0, addon.getSettings().getAcidRainDamage() - protection);
                            AcidRainEvent e = new AcidRainEvent(player, totalDamage, protection);
                            addon.getServer().getPluginManager().callEvent(e);
                            if (!e.isCancelled()) {
                                player.damage(e.getRainDamage());
                                player.getWorld().playSound(playerLoc, Sound.ENTITY_CREEPER_PRIMED, 3F, 3F);
                            }
                        }
                    }
                }.runTaskTimer(addon.getPlugin(), 0L, 20L);
            }


        }
        // If they are already burning in acid then return
        if (burningPlayers.contains(player)) {
            return;
        }
        if (isSafeFromAcid(player)) {
            return;
        }
        // ACID!
        // Put the player into the acid list
        burningPlayers.add(player);
        // This runnable continuously hurts the player even if they are not
        // moving but are in acid.
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isDead() || isSafeFromAcid(player)) {
                    burningPlayers.remove(player);
                    this.cancel();
                } else {
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
        if (addon.getSettings().isHelmetProtection() && (player.getInventory().getHelmet() != null
                && player.getInventory().getHelmet().getType().name().contains("HELMET"))
                || player.getLocation().getBlock().getTemperature() < 0.1 // snow falls
                || player.getLocation().getBlock().getHumidity() == 0 // dry
                || (player.getActivePotionEffects().stream().map(PotionEffect::getType).anyMatch(IMMUNE_EFFECTS::contains))) {
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
        // In liquid
        if (!player.getLocation().getBlock().getType().equals(Material.WATER)
                && !player.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.WATER)) {
            return true;
        }
        // Check if player is in a boat
        if (player.getVehicle() != null && player.getVehicle().getType().equals(EntityType.BOAT)) {
            // I'M ON A BOAT! I'M ON A BOAT! A %^&&* BOAT! SNL Sketch.
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
     * @param player - player
     * @return A double between 0.0 and 0.80 that reflects how much armor the
     *         player has on. The higher the value, the more protection they
     *         have.
     */
    private double getDamageReduced(Player player) {
        org.bukkit.inventory.PlayerInventory inv = player.getInventory();
        ItemStack boots = inv.getBoots();
        ItemStack helmet = inv.getHelmet();
        ItemStack chest = inv.getChestplate();
        ItemStack pants = inv.getLeggings();
        double red = 0.0;
        if (helmet != null) {
            switch (helmet.getType()) {
            case LEATHER_HELMET:
                red += 0.04;
                break;
            case GOLDEN_HELMET:
                red += 0.08;
                break;
            case CHAINMAIL_HELMET:
                red += 0.08;
                break;
            case IRON_HELMET:
                red += 0.08;
                break;
            case DIAMOND_HELMET:
                red += 0.12;
                break;
            default:
                break;
            }
            // Check respiration (Bukkit name OXYGEN) enchantment
            // Each level gives the same protection as a diamond helmet
            red += helmet.getEnchantments().getOrDefault(Enchantment.OXYGEN, 0) * 0.12;
            // Damage
            damage(helmet);
        }
        if (boots != null) {
            switch (boots.getType()) {
            case LEATHER_BOOTS:
                red = red + 0.04;
                break;
            case GOLDEN_BOOTS:
                red = red + 0.04;
                break;
            case CHAINMAIL_BOOTS:
                red = red + 0.04;
                break;
            case IRON_BOOTS:
                red = red + 0.08;
                break;
            case DIAMOND_BOOTS:
                red = red + 0.12;
                break;
            default:
                break;
            }
            // Damage
            damage(boots);
        }
        // Pants
        if (pants != null) {
            switch (pants.getType()) {
            case LEATHER_LEGGINGS:
                red = red + 0.08;
                break;
            case GOLDEN_LEGGINGS:
                red = red + 0.12;
                break;
            case CHAINMAIL_LEGGINGS:
                red = red + 0.16;
                break;
            case IRON_LEGGINGS:
                red = red + 0.20;
                break;
            case DIAMOND_LEGGINGS:
                red = red + 0.24;
                break;
            default:
                break;
            }
            // Damage
            damage(pants);
        }
        // Chest plate
        if (chest != null) {
            switch (chest.getType()) {
            case LEATHER_CHESTPLATE:
                red = red + 0.12;
                break;
            case GOLDEN_CHESTPLATE:
                red = red + 0.20;
                break;
            case CHAINMAIL_CHESTPLATE:
                red = red + 0.20;
                break;
            case IRON_CHESTPLATE:
                red = red + 0.24;
                break;
            case DIAMOND_CHESTPLATE:
                red = red + 0.32;
                break;
            default:
                break;
            }
            // Damage
            damage(chest);
        }
        return red;
    }

    private void damage(ItemStack item) {
        ItemMeta im = item.getItemMeta();
        if (im instanceof Damageable) {
            Damageable d = ((Damageable)item.getItemMeta());
            d.setDamage(d.getDamage() + 1);
            item.setItemMeta((ItemMeta) d);
        }
    }
}