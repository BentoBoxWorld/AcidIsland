package world.bentobox.acidisland.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
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
    private boolean isRaining = false;
    private final List<Player> wetPlayers = new ArrayList<>();
    private final static List<PotionEffectType> EFFECTS = Arrays.asList(
            PotionEffectType.BLINDNESS,
            PotionEffectType.CONFUSION,
            PotionEffectType.HUNGER,
            PotionEffectType.SLOW,
            PotionEffectType.SLOW_DIGGING,
            PotionEffectType.WEAKNESS);

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
                && player.getWorld().equals(addon.getIslandWorld()) && player.getLocation().getBlockY() < 1) {
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
                || !Util.sameWorld(addon.getIslandWorld(), player.getWorld())
                || (!player.isOp() && player.hasPermission("acidisland.mod.noburn"))
                || (!player.isOp() && player.hasPermission("admin.noburn"))
                || (player.isOp() && !addon.getSettings().isAcidDamageOp())) {
            return;
        }

        // Slow checks
        Location playerLoc = player.getLocation();
        Biome biome = playerLoc.getBlock().getBiome();
        // Check for acid rain
        if (addon.getSettings().getAcidRainDamage() > 0D && isRaining
                && !biome.name().contains("DESERT")
                && !biome.equals(Biome.NETHER)
                && !biome.name().contains("SAVANNA")) {
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
                        if (!isRaining || player.isDead() || isSafeFromRain(player) || addon.getSettings().getAcidRainDamage() <= 0D) {
                            wetPlayers.remove(player);
                            this.cancel();
                            // Check they are still in this world
                        } else {
                            double protection = addon.getSettings().getAcidRainDamage() * getDamageReduced(player);
                            double totalDamage = (addon.getSettings().getAcidRainDamage() - protection);
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
                    double totalDamage = addon.getSettings().getAcidDamage() - protection;
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
                && player.getInventory().getHelmet().getType().name().contains("HELMET"))) {
            return true;
        }
        // Check potions
        for (PotionEffect s : player.getActivePotionEffects()) {
            if (s.getType().equals(PotionEffectType.WATER_BREATHING)) {
                // Safe!
                return true;
            }
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
     * @return true if player is not safe
     */
    private boolean isSafeFromAcid(Player player) {
        // In liquid
        Material bodyMat = player.getLocation().getBlock().getType();
        Material headMat = player.getLocation().getBlock().getRelative(BlockFace.UP).getType();
        // TODO: remove backwards compatibility hack
        if (bodyMat.name().contains("WATER"))
            bodyMat = Material.WATER;
        if (headMat.name().contains("WATER"))
            headMat = Material.WATER;
        if (bodyMat != Material.WATER && headMat != Material.WATER) {
            return true;
        }
        // Check if player is in a boat
        Entity playersVehicle = player.getVehicle();
        if (playersVehicle != null && playersVehicle.getType().equals(EntityType.BOAT)) {
            // I'M ON A BOAT! I'M ON A BOAT! A %^&&* BOAT!
            return true;
        }
        // Check if full armor protects
        if (addon.getSettings().isFullArmorProtection()) {
            boolean fullArmor = true;
            for (ItemStack item : player.getInventory().getArmorContents()) {
                if (item == null || item.getType().equals(Material.AIR)) {
                    fullArmor = false;
                    break;
                }
            }
            if (fullArmor) {
                return true;
            }
        }
        // Check if player has an active water potion or not
        Collection<PotionEffect> activePotions = player.getActivePotionEffects();
        for (PotionEffect s : activePotions) {
            if (s.getType().equals(PotionEffectType.WATER_BREATHING)) {
                // Safe!
                return true;
            }
        }
        return false;
    }

    /**
     * @param player - player
     * @return A double between 0.0 and 0.80 that reflects how much armor the
     *         player has on. The higher the value, the more protection they
     *         have.
     */
    private static double getDamageReduced(Player player) {
        org.bukkit.inventory.PlayerInventory inv = player.getInventory();
        ItemStack boots = inv.getBoots();
        ItemStack helmet = inv.getHelmet();
        ItemStack chest = inv.getChestplate();
        ItemStack pants = inv.getLeggings();
        double red = 0.0;
        if (helmet != null) {
            switch (helmet.getType()) {
            case LEATHER_HELMET:
                red = red + 0.04;
                break;
            case GOLDEN_HELMET:
                red = red + 0.08;
                break;
            case CHAINMAIL_HELMET:
                red = red + 0.08;
                break;
            case IRON_HELMET:
                red = red + 0.08;
                break;
            case DIAMOND_HELMET:
                red = red + 0.12;
                break;
            default:
                break;
            }
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
        }
        return red;
    }

    /**
     * Tracks weather changes and acid rain
     *
     * @param e - event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onWeatherChange(final WeatherChangeEvent e) {
        if (e.getWorld().equals(addon.getIslandWorld())) {
            this.isRaining = e.toWeatherState();
        }
    }

}