package world.bentobox.acidisland.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Monster;
import org.bukkit.entity.WaterMob;
import org.bukkit.scheduler.BukkitTask;

import world.bentobox.acidisland.AcidIsland;
import world.bentobox.acidisland.events.EntityDamageByAcidEvent;
import world.bentobox.acidisland.events.EntityDamageByAcidEvent.Acid;
import world.bentobox.acidisland.events.ItemDestroyByAcidEvent;
import world.bentobox.acidisland.listeners.AcidEffect;

public class AcidTask {
    private final AcidIsland addon;
    private static final List<EntityType> IMMUNE = Arrays.asList(EntityType.TURTLE, EntityType.POLAR_BEAR, EntityType.DROWNED, EntityType.AXOLOTL);
    private Map<Entity, Long> itemsInWater = new ConcurrentHashMap<>();
    private final BukkitTask findMobsTask;

    /**
     * Runs repeating tasks to deliver acid damage to mobs, etc.
     * @param addon - addon
     */
    public AcidTask(AcidIsland addon) {
        this.addon = addon;
        findMobsTask = Bukkit.getScheduler().runTaskTimer(addon.getPlugin(), this::findEntities, 0L, 20L);
    }

    void findEntities() {
        Map<Entity, Long> burnList = new WeakHashMap<>();
        for (Entity e : getEntityStream()) {
            if (e instanceof Item || (!IMMUNE.contains(e.getType()) && !(e instanceof WaterMob))) {
                int x = e.getLocation().getBlockX() >> 4;
                int z = e.getLocation().getBlockZ() >> 4;
                if (e.getWorld().isChunkLoaded(x,z)) {
                    if (e.getLocation().getBlock().getType().equals(Material.WATER)) {
                        if ((e instanceof Monster || e instanceof MagmaCube) && addon.getSettings().getAcidDamageMonster() > 0D) {
                            burnList.put(e, (long)addon.getSettings().getAcidDamageMonster());

                        } else if ((e instanceof Animals) && addon.getSettings().getAcidDamageAnimal() > 0D
                                && (!e.getType().equals(EntityType.CHICKEN) || addon.getSettings().isAcidDamageChickens())) {
                            burnList.put(e, (long)addon.getSettings().getAcidDamageAnimal());
                        } else if (addon.getSettings().getAcidDestroyItemTime() > 0 && e instanceof Item) {
                            burnList.put(e, System.currentTimeMillis());
                        }
                    }
                }
            }
        }
        // Remove any entities not on the burn list
        itemsInWater.keySet().removeIf(i -> !burnList.containsKey(i));

        if (!burnList.isEmpty()) {
            Bukkit.getScheduler().runTask(addon.getPlugin(), () ->
            // Burn everything
            burnList.forEach(this::applyDamage));
        }
    }

    void applyDamage(Entity e, long damage) {
        if (e instanceof LivingEntity) {
            double actualDamage = Math.max(0, damage - damage * AcidEffect.getDamageReduced((LivingEntity)e));
            ((LivingEntity)e).damage(actualDamage);
            EntityDamageByAcidEvent event = new EntityDamageByAcidEvent(e, actualDamage, Acid.WATER);
            // Fire event
            Bukkit.getPluginManager().callEvent(event);
        } else if (addon.getSettings().getAcidDestroyItemTime() > 0 && e instanceof Item){
            // Item
            if (e.getLocation().getBlock().getType().equals(Material.WATER)) {
                itemsInWater.putIfAbsent(e, damage + addon.getSettings().getAcidDestroyItemTime() * 1000);
                if (System.currentTimeMillis() > itemsInWater.get(e)) {
                    e.getWorld().playSound(e.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 3F, 3F);
                    e.remove();
                    itemsInWater.remove(e);
                    // Fire event
                    Bukkit.getPluginManager().callEvent(new ItemDestroyByAcidEvent((Item)e));
                }
            } else {
                itemsInWater.remove(e);
            }
        }
    }

    /**
     * @return a stream of all entities in this world and the nether and end if those are island worlds too.
     */
    List<Entity> getEntityStream() {
        List<Entity> entityStream = new ArrayList<>(addon.getOverWorld().getEntities());
        // Nether and end
        if (addon.getSettings().isNetherGenerate() && addon.getSettings().isNetherIslands()) {
            entityStream.addAll(Objects.requireNonNull(addon.getNetherWorld()).getEntities());
        }
        if (addon.getSettings().isEndGenerate() && addon.getSettings().isEndIslands()) {
            entityStream.addAll(Objects.requireNonNull(addon.getEndWorld()).getEntities());
        }
        return entityStream;
    }

    /**
     * Cancel tasks running
     */
    public void cancelTasks() {
        if (findMobsTask != null) findMobsTask.cancel();
    }

    /**
     * @return the itemsInWater
     */
    Map<Entity, Long> getItemsInWater() {
        return itemsInWater;
    }

    /**
     * @param itemsInWater the itemsInWater to set
     */
    void setItemsInWater(Map<Entity, Long> itemsInWater) {
        this.itemsInWater = itemsInWater;
    }
}
