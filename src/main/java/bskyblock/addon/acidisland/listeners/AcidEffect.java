/*******************************************************************************
 * This file is part of ASkyBlock.
 *
 *     ASkyBlock is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ASkyBlock is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with ASkyBlock.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

package bskyblock.addon.acidisland.listeners;

import java.util.ArrayList;
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

import bskyblock.addon.acidisland.AcidIsland;

/**
 * Applies the acid effect to players
 * 
 * @author tastybento
 */
public class AcidEffect implements Listener {

    private final AcidIsland addon;
    private List<Player> burningPlayers = new ArrayList<Player>();
    private boolean isRaining = false;
    private List<Player> wetPlayers = new ArrayList<Player>();

    public AcidEffect(AcidIsland addon) {
        this.addon = addon;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent e) {
        burningPlayers.remove((Player) e.getEntity());
        wetPlayers.remove((Player) e.getEntity());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e) {
        // Fast return if acid isn't being used
        if (addon.getSettings().getAcidRainDamage() == 0 && addon.getSettings().getAcidDamage() == 0) {
            return;
        }
        final Player player = e.getPlayer();
        // Fast checks
        if (player.isDead() || player.getGameMode().toString().startsWith("SPECTATOR")) {
            return;
        }
        // Check if in teleport
        if (addon.getPlayers().isInTeleport(player.getUniqueId())) {
            return;
        }
        // Check that they are in the ASkyBlock world
        if (!player.getWorld().equals(addon.getAiw().getOverWorld())) {
            return;
        }        
        // Return if players are immune
        if (player.isOp()) {
            if (!addon.getSettings().getDamageOps()) {
                return;
            }
        } else if (player.hasPermission("acidisland.mod.noburn") || player.hasPermission("admin.noburn")) {
            return;
        }

        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }

        // Slow checks
        Location playerLoc = player.getLocation();

        // Check for acid rain
        if (addon.getSettings().getAcidRainDamage() > 0D && isRaining) {
            // Only check if they are in a non-dry biome
            Biome biome = playerLoc.getBlock().getBiome();
            if (biome != Biome.DESERT && biome != Biome.DESERT_HILLS 
                    && biome != Biome.SAVANNA && biome != Biome.MESA && biome != Biome.HELL) {
                if (isSafeFromRain(player)) {
                    wetPlayers.remove(player);
                } else {
                    if (!wetPlayers.contains(player)) {
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
                                    player.damage((addon.getSettings().getAcidRainDamage() - addon.getSettings().getAcidRainDamage() * getDamageReduced(player)));
                                    if (addon.getServer().getVersion().contains("(MC: 1.8") || addon.getServer().getVersion().contains("(MC: 1.7")) {
                                        player.getWorld().playSound(playerLoc, Sound.valueOf("FIZZ"), 3F, 3F);
                                    } else {
                                        player.getWorld().playSound(playerLoc, Sound.ENTITY_CREEPER_PRIMED, 3F, 3F);
                                    }
                                }
                            }
                        }.runTaskTimer(addon.getBSkyBlock(), 0L, 20L);
                    }
                }
            }
        }

        // Find out if they are at the bottom of the sea and if so bounce them
        // back up
        if (playerLoc.getBlockY() < 1) {
            final Vector v = new Vector(player.getVelocity().getX(), 1D, player.getVelocity().getZ());
            player.setVelocity(v);
        }
        // If they are already burning in acid then return
        if (burningPlayers.contains(player)) {
            // plugin.getLogger().info("DEBUG: no acid water is false");
            if (isSafeFromAcid(player)) {
                return;
            }
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
                    if (!addon.getSettings().getAcidDamageType().isEmpty()) {
                        for (PotionEffectType t : addon.getSettings().getAcidDamageType()) {
                            if (t.equals(PotionEffectType.BLINDNESS) || t.equals(PotionEffectType.CONFUSION) || t.equals(PotionEffectType.HUNGER)
                                    || t.equals(PotionEffectType.SLOW) || t.equals(PotionEffectType.SLOW_DIGGING) || t.equals(PotionEffectType.WEAKNESS)) {
                                player.addPotionEffect(new PotionEffect(t, 600, 1));
                            } else {
                                // Poison
                                player.addPotionEffect(new PotionEffect(t, 200, 1));
                            }
                        }
                    }
                    // Apply damage if there is any
                    if (addon.getSettings().getAcidDamage() > 0D) {
                        player.damage((addon.getSettings().getAcidDamage() - addon.getSettings().getAcidDamage() * getDamageReduced(player)));
                        if (addon.getServer().getVersion().contains("(MC: 1.8") || addon.getServer().getVersion().contains("(MC: 1.7")) {
                            player.getWorld().playSound(playerLoc, Sound.valueOf("FIZZ"), 3F, 3F);
                        } else {
                            player.getWorld().playSound(playerLoc, Sound.ENTITY_CREEPER_PRIMED, 3F, 3F);
                        }
                    }

                }
            }
        }.runTaskTimer(addon.getBSkyBlock(), 0L, 20L);
    }

    /**
     * Check if player is safe from rain
     * @param player
     * @return true if they are safe
     */
    private boolean isSafeFromRain(Player player) {
        if (!player.getWorld().equals(addon.getIslandWorld())) {
            return true;
        }
        // Check if player has a helmet on and helmet protection is true
        if (addon.getSettings().getHelmetProtection() && (player.getInventory().getHelmet() != null 
                && player.getInventory().getHelmet().getType().name().contains("HELMET"))) {
            return true;
        }
        // Check potions
        Collection<PotionEffect> activePotions = player.getActivePotionEffects();
        for (PotionEffect s : activePotions) {
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
     * @param player
     * @return true if player is not safe
     */
    private boolean isSafeFromAcid(Player player) {
        if (!player.getWorld().equals(addon.getIslandWorld())) {
            return true;
        }
        // In liquid
        Material bodyMat = player.getLocation().getBlock().getType();
        Material headMat = player.getLocation().getBlock().getRelative(BlockFace.UP).getType();
        if (bodyMat.equals(Material.STATIONARY_WATER))
            bodyMat = Material.WATER;
        if (headMat.equals(Material.STATIONARY_WATER))
            headMat = Material.WATER;
        if (bodyMat != Material.WATER && headMat != Material.WATER) {
            return true;
        }
        // Check if player is in a boat
        Entity playersVehicle = player.getVehicle();
        if (playersVehicle != null) {
            // They are in a Vehicle
            if (playersVehicle.getType().equals(EntityType.BOAT)) {
                // I'M ON A BOAT! I'M ON A BOAT! A %^&&* BOAT!
                return true;
            }
        }
        // Check if full armor protects
        if (addon.getSettings().getFullArmorProtection()) {
            boolean fullArmor = true;
            for (ItemStack item : player.getInventory().getArmorContents()) {
                if (item == null || (item != null && item.getType().equals(Material.AIR))) {
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
            // plugin.getLogger().info("Potion is : " +
            // s.getType().toString());
            if (s.getType().equals(PotionEffectType.WATER_BREATHING)) {
                // Safe!
                return true;
            }
        }
        return false;
    }

    /**
     * @param player
     * @return A double between 0.0 and 0.80 that reflects how much armor the
     *         player has on. The higher the value, the more protection they
     *         have.
     */
    static public double getDamageReduced(Player player) {
        org.bukkit.inventory.PlayerInventory inv = player.getInventory();
        ItemStack boots = inv.getBoots();
        ItemStack helmet = inv.getHelmet();
        ItemStack chest = inv.getChestplate();
        ItemStack pants = inv.getLeggings();
        double red = 0.0;
        if (helmet != null) {
            if (helmet.getType() == Material.LEATHER_HELMET)
                red = red + 0.04;
            else if (helmet.getType() == Material.GOLD_HELMET)
                red = red + 0.08;
            else if (helmet.getType() == Material.CHAINMAIL_HELMET)
                red = red + 0.08;
            else if (helmet.getType() == Material.IRON_HELMET)
                red = red + 0.08;
            else if (helmet.getType() == Material.DIAMOND_HELMET)
                red = red + 0.12;
        }
        if (boots != null) {
            if (boots.getType() == Material.LEATHER_BOOTS)
                red = red + 0.04;
            else if (boots.getType() == Material.GOLD_BOOTS)
                red = red + 0.04;
            else if (boots.getType() == Material.CHAINMAIL_BOOTS)
                red = red + 0.04;
            else if (boots.getType() == Material.IRON_BOOTS)
                red = red + 0.08;
            else if (boots.getType() == Material.DIAMOND_BOOTS)
                red = red + 0.12;
        }
        // Pants
        if (pants != null) {
            if (pants.getType() == Material.LEATHER_LEGGINGS)
                red = red + 0.08;
            else if (pants.getType() == Material.GOLD_LEGGINGS)
                red = red + 0.12;
            else if (pants.getType() == Material.CHAINMAIL_LEGGINGS)
                red = red + 0.16;
            else if (pants.getType() == Material.IRON_LEGGINGS)
                red = red + 0.20;
            else if (pants.getType() == Material.DIAMOND_LEGGINGS)
                red = red + 0.24;
        }
        // Chest plate
        if (chest != null) {
            if (chest.getType() == Material.LEATHER_CHESTPLATE)
                red = red + 0.12;
            else if (chest.getType() == Material.GOLD_CHESTPLATE)
                red = red + 0.20;
            else if (chest.getType() == Material.CHAINMAIL_CHESTPLATE)
                red = red + 0.20;
            else if (chest.getType() == Material.IRON_CHESTPLATE)
                red = red + 0.24;
            else if (chest.getType() == Material.DIAMOND_CHESTPLATE)
                red = red + 0.32;
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