package world.bentobox.acidisland.world;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Monster;

import world.bentobox.acidisland.AcidIsland;
import world.bentobox.acidisland.listeners.AcidEffect;

public class AcidTask {
    private final AcidIsland addon;
    private static final List<EntityType> IMMUNE = Arrays.asList(EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN,
            EntityType.SQUID, EntityType.TURTLE, EntityType.POLAR_BEAR, EntityType.DROWNED);
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
     * Start the entity burning task
     */
    private void burnEntities() {
        // This part will kill monsters if they fall into the water because it is acid
        entityBurnTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(addon.getPlugin(), () -> getEntityStream()
                // These entities are immune to acid
                .filter(e -> !IMMUNE.contains(e.getType()))
                // Only burn if the chunk is loaded
                .filter(e -> e.getLocation().getChunk().isLoaded())
                .filter(w -> w.getLocation().getBlock().getType().equals(Material.WATER))
                .forEach(e -> {
                    if ((e instanceof Monster || e instanceof MagmaCube) && addon.getSettings().getAcidDamageMonster() > 0D) {
                        applyDamage((LivingEntity) e, addon.getSettings().getAcidDamageMonster());
                    } else if ((e instanceof Animals) && addon.getSettings().getAcidDamageAnimal() > 0D
                            && (!e.getType().equals(EntityType.CHICKEN) || addon.getSettings().isAcidDamageChickens())) {
                        ((LivingEntity) e).damage(addon.getSettings().getAcidDamageAnimal());
                    }
                }), 0L, 20L);
    }

    private void applyDamage(LivingEntity e, double damage) {
        e.damage(Math.max(0, damage - damage * AcidEffect.getDamageReduced(e)));
    }

    /**
     * @return a stream of all entities in this world and the nether and end if those are island worlds too.
     */
    private Stream<Entity> getEntityStream() {
        Stream<Entity> entityStream = addon.getOverWorld().getEntities().stream();
        // Nether and end
        if (addon.getSettings().isNetherGenerate() && addon.getSettings().isNetherIslands()) {
            entityStream = Stream.concat(entityStream, addon.getNetherWorld().getEntities().stream());
        }
        if (addon.getSettings().isEndGenerate() && addon.getSettings().isEndIslands()) {
            entityStream = Stream.concat(entityStream, addon.getEndWorld().getEntities().stream());
        }
        return entityStream;
    }

    /**
     * Start the item removal in acid task
     */
    private void runAcidItemRemovalTask() {
        if (addon.getSettings().getAcidDestroyItemTime() <= 0) {
            return;
        }
        itemBurnTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(addon.getPlugin(), () -> {
            Set<Entity> newItemsInWater = new HashSet<>();
            getEntityStream()
            .filter(e -> e.getType().equals(EntityType.DROPPED_ITEM))
            .filter(e -> e.getLocation().getBlock().getType().equals(Material.WATER)
                    || (e.getLocation().getY() > 0 && e.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.WATER)))
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
