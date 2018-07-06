package bskyblock.addon.acidisland.world;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Squid;

import bskyblock.addon.acidisland.AcidIsland;

public class AcidTask {
    private final AcidIsland addon;
    private Set<Entity> itemsInWater = Collections.newSetFromMap(new WeakHashMap<Entity, Boolean>());
    private int entityBurnTask = -1;
    private int itemBurnTask = -1;

    /**
     * Runs repeating tasks to deliver acid damage to mobs, etc.
     * @param addon - addon
     */
    public AcidTask(AcidIsland addon) {
        this.addon = addon;
        burnEntities();
        runAcidItemRemovalTask();
    }

    /**
     * Start the entity buring task
     */
    private void burnEntities() {
        // This part will kill monsters if they fall into the water because it is acid
        entityBurnTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(addon.getBSkyBlock(), () -> addon.getIslandWorld().getEntities().stream()
                .filter(e -> !(e instanceof Guardian || e instanceof Squid))
                .filter(w -> w.getLocation().getBlock().getType().equals(Material.WATER) || w.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER))
                .forEach(e -> {
                    if ((e instanceof Monster) && addon.getSettings().getAcidDamageMonster() > 0D) {
                        ((Monster) e).damage(addon.getSettings().getAcidDamageMonster());
                    } else if ((e instanceof Animals) && addon.getSettings().getAcidDamageAnimal() > 0D
                            && (!e.getType().equals(EntityType.CHICKEN) || addon.getSettings().isAcidDamageChickens())) {
                        ((Animals) e).damage(addon.getSettings().getAcidDamageMonster());
                    }
                }), 0L, 20L);
    }

    /**
     * Start the item removal in acid task
     */
    private void runAcidItemRemovalTask() {
        if (addon.getSettings().getAcidDestroyItemTime() <= 0) {
            return;
        }
        itemBurnTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(addon.getBSkyBlock(), () -> {
            Set<Entity> newItemsInWater = new HashSet<>();
            addon.getIslandWorld().getEntities().stream()
            .filter(e -> e.getType().equals(EntityType.DROPPED_ITEM)
                    && (e.getLocation().getBlock().getType().equals(Material.WATER)
                            || e.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER))
                    )
            .forEach(e -> {
                if (itemsInWater.contains(e)) {
                    e.getWorld().playSound(e.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 3F, 3F);
                    e.remove();
                } else {
                    newItemsInWater.add(e);
                }
            });
            itemsInWater = newItemsInWater;
        }, addon.getSettings().getAcidDestroyItemTime() * 20L, addon.getSettings().getAcidDestroyItemTime() * 20L);
    }

    /**
     * Cancel tasks running
     */
    public void cancelTasks() {
        if (entityBurnTask >= 0) {
            Bukkit.getScheduler().cancelTask(entityBurnTask);
        }
        if (itemBurnTask >= 0) {
            Bukkit.getScheduler().cancelTask(itemBurnTask);
        }
    }
}
