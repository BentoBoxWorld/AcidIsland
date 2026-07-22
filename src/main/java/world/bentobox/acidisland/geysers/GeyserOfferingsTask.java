package world.bentobox.acidisland.geysers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.eclipse.jdt.annotation.Nullable;

import world.bentobox.acidisland.AcidIsland;
import world.bentobox.acidisland.events.GeyserSacrificeEvent;
import world.bentobox.acidisland.events.GeyserTransmuteEvent;

/**
 * The geyser offerings mechanic: items thrown into the water around a sulfur
 * vent are consumed with a sizzle as offerings, and when the vent's vanilla
 * geyser next erupts, the offerings are transmuted into rewards spewed out of
 * the plume. Rewards are rolled from the weighted {@code geyser-loot.yml}
 * table, biased towards the channels of the materials sacrificed.
 * <p>
 * Eruptions are driven entirely by vanilla (Minecraft 26.2+ potent sulfur), so
 * a once-a-second task watches fed vents for the block's
 * {@code potent_sulfur_state} to pass through {@code erupting}; the rewards pay
 * out as the plume settles so the eruption itself cannot fling them away. On
 * older servers the mechanic is inert.
 *
 * @author tastybento
 * @since 2.1.0
 */
public class GeyserOfferingsTask {

    // Only exists on Minecraft 26.2 and later; null on older servers, which disables the mechanic
    private static final Material POTENT_SULFUR = Material.getMaterial("POTENT_SULFUR");
    private static final String ERUPTING_STATE = "potent_sulfur_state=erupting";
    /**
     * Minimum age in ticks before a floating item is consumed, so freshly
     * spewed rewards can clear the pool before the vent eats them back.
     */
    private static final int MIN_AGE_TICKS = 60;
    /** How far below a floating item to look for a vent cap (vent depth + twin vent offset). */
    private static final int POOL_SCAN_DEPTH = 4;
    /**
     * Horizontal radius around a floating item to look for a vent cap.
     * Floating items bob and drift, so requiring them to sit in the exact
     * one-block column above the cap makes offerings nearly impossible to
     * land - anywhere in the pool around the vent counts instead.
     */
    private static final int POOL_SCAN_RADIUS = 3;
    /** Radius around the plume to look for a player to run command rewards for. */
    private static final double COMMAND_RANGE_SQUARED = 24.0 * 24;

    private static final Random RAND = new Random();

    /**
     * Offerings pending at one vent: reward channel bias, the number of items
     * sacrificed, and the eruption state seen last tick.
     */
    static class VentOfferings {
        final Map<String, Integer> bias = new HashMap<>();
        int items;
        boolean erupting;
    }

    private final AcidIsland addon;
    private final @Nullable GeyserLootTable table;
    private final @Nullable BukkitTask task;
    private final Map<Vector, VentOfferings> vents = new HashMap<>();

    /**
     * Runs the repeating task that consumes offerings and pays out rewards.
     * Does nothing if the mechanic is disabled in config, the server predates
     * Minecraft 26.2, or the loot table is empty.
     *
     * @param addon - addon
     */
    public GeyserOfferingsTask(AcidIsland addon) {
        this.addon = addon;
        if (!addon.getSettings().isGeyserOfferings()) {
            table = null;
            task = null;
            return;
        }
        if (POTENT_SULFUR == null) {
            addon.log("Geyser offerings require Minecraft 26.2 or later - mechanic disabled.");
            table = null;
            task = null;
            return;
        }
        table = loadTable();
        if (table.isEmpty()) {
            addon.logError("geyser-loot.yml has no valid loot entries - geyser offerings disabled.");
            task = null;
            return;
        }
        task = Bukkit.getScheduler().runTaskTimer(addon.getPlugin(), this::tick, 20L, 20L);
        addon.log("Geyser offerings active: " + table.getLoot().size() + " loot entries, max "
                + addon.getSettings().getGeyserMaxRewards() + " rewards per eruption.");
    }

    private GeyserLootTable loadTable() {
        File file = new File(addon.getDataFolder(), "geyser-loot.yml");
        if (!file.exists()) {
            addon.saveResource("geyser-loot.yml", false);
        }
        return GeyserLootTable.parse(YamlConfiguration.loadConfiguration(file));
    }

    /**
     * One tick (1s): consume floating offerings, then watch fed vents for the
     * end of an eruption.
     */
    void tick() {
        World world = addon.getOverWorld();
        if (world == null) {
            return;
        }
        consumeOfferings(world);
        watchVents(world);
    }

    /**
     * @return true if the material is part of a vent's water column. The
     * magma block and bubbling potent sulfur can turn the water above a vent
     * into bubble column blocks, so those count as water here.
     */
    private static boolean isWaterLike(Material type) {
        return type == Material.WATER || type == Material.BUBBLE_COLUMN;
    }

    /**
     * The water block an item is floating in or on. Items bob on the surface,
     * so the item's own block is often the air block just above the water.
     *
     * @param item the floating item
     * @return the water(-like) block to scan down from, or null if the item is
     * not in or on water
     */
    private @Nullable Block waterStart(Item item) {
        Block block = item.getLocation().getBlock();
        if (isWaterLike(block.getType())) {
            return block;
        }
        Block below = block.getRelative(BlockFace.DOWN);
        return isWaterLike(below.getType()) ? below : null;
    }

    /**
     * Consume items floating in the water around a vent cap as offerings.
     */
    private void consumeOfferings(World world) {
        for (Item item : world.getEntitiesByClass(Item.class)) {
            if (item.getTicksLived() >= MIN_AGE_TICKS) {
                Block start = waterStart(item);
                Block vent = start == null ? null : findVentNear(start);
                if (vent != null) {
                    sacrifice(world, item, vent);
                }
            }
        }
    }

    /**
     * Look for a potent sulfur cap in the pool below and around a floating
     * item. Scans shallowest layer first so a twin vent's main cap wins.
     *
     * @param start the water block the floating item is in
     * @return the nearest vent cap block, or null if the item is not near a vent
     */
    private @Nullable Block findVentNear(Block start) {
        for (int dy = 1; dy <= POOL_SCAN_DEPTH; dy++) {
            for (int dx = -POOL_SCAN_RADIUS; dx <= POOL_SCAN_RADIUS; dx++) {
                for (int dz = -POOL_SCAN_RADIUS; dz <= POOL_SCAN_RADIUS; dz++) {
                    Block block = start.getRelative(dx, -dy, dz);
                    if (block.getType() == POTENT_SULFUR) {
                        return block;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Consume one offering: sizzle, credit the vent's bias, remove the item.
     */
    private void sacrifice(World world, Item item, Block vent) {
        String channel = GeyserLootTable.categorize(item.getItemStack().getType());
        GeyserSacrificeEvent event = new GeyserSacrificeEvent(item, vent.getLocation(), channel);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        VentOfferings offerings = vents.computeIfAbsent(vent.getLocation().toVector(), k -> {
            VentOfferings v = new VentOfferings();
            v.erupting = isErupting(vent);
            return v;
        });
        int amount = item.getItemStack().getAmount();
        offerings.items += amount;
        if (channel != null) {
            offerings.bias.merge(channel, amount, Integer::sum);
        }
        // Consumed either way - the geyser is not a storage unit
        item.remove();
        world.playSound(item.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.8F, 1.2F);
        world.spawnParticle(Particle.LARGE_SMOKE, item.getLocation(), 8, 0.2, 0.2, 0.2, 0.02);
    }

    /**
     * Watch fed vents: when a vent that was erupting settles again, spew the
     * transmuted rewards. Vents that have been mined out forfeit their
     * offerings.
     */
    private void watchVents(World world) {
        Iterator<Map.Entry<Vector, VentOfferings>> it = vents.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Vector, VentOfferings> entry = it.next();
            if (checkVent(world, entry.getKey(), entry.getValue())) {
                it.remove();
            }
        }
    }

    /**
     * Check one fed vent, spewing its rewards if its eruption has settled.
     *
     * @param world the overworld
     * @param v the vent cap position
     * @param offerings the vent's pending offerings
     * @return true if the vent's tracking entry should be dropped
     */
    private boolean checkVent(World world, Vector v, VentOfferings offerings) {
        if (!world.isChunkLoaded(v.getBlockX() >> 4, v.getBlockZ() >> 4)) {
            return false;
        }
        Block vent = world.getBlockAt(v.getBlockX(), v.getBlockY(), v.getBlockZ());
        if (vent.getType() != POTENT_SULFUR) {
            // Mined out - offerings forfeited
            return true;
        }
        boolean erupting = isErupting(vent);
        if (offerings.erupting && !erupting) {
            // The plume has settled - pay out now so the eruption cannot
            // fling the rewards far from the vent
            spew(world, vent, offerings);
            return true;
        }
        offerings.erupting = erupting;
        return false;
    }

    private boolean isErupting(Block vent) {
        return vent.getBlockData().getAsString().contains(ERUPTING_STATE);
    }

    /**
     * Transmute a vent's offerings into rewards and spew them from the water
     * surface with a radial launch.
     */
    private void spew(World world, Block vent, VentOfferings offerings) {
        // Find the surface above the vent
        Block top = vent.getRelative(BlockFace.UP);
        while (isWaterLike(top.getType()) && top.getY() < world.getMaxHeight()) {
            top = top.getRelative(BlockFace.UP);
        }
        Location spawn = top.getLocation().add(0.5, 0.5, 0.5);
        int rolls = Math.clamp(offerings.items, 1, Math.max(1, addon.getSettings().getGeyserMaxRewards()));
        List<Item> rewards = new ArrayList<>(rolls);
        for (int i = 0; i < rolls; i++) {
            GeyserLootEntry entry = table.roll(RAND, offerings.bias);
            if (entry == null) {
                break;
            }
            if (entry.isCommand()) {
                executeReward(world, spawn, entry);
            } else {
                rewards.add(launchReward(world, spawn, entry));
            }
        }
        world.playSound(spawn, Sound.ENTITY_GENERIC_SPLASH, 1F, 1F);
        world.spawnParticle(Particle.SPLASH, spawn, 40, 0.4, 0.6, 0.4, 0.2);
        Bukkit.getPluginManager().callEvent(new GeyserTransmuteEvent(vent.getLocation(), rewards, offerings.items));
    }

    /**
     * Spawn one reward item at the plume top with a radial launch: guaranteed
     * outward motion so the rewards scatter around the vent instead of falling
     * straight back into the pool.
     */
    private Item launchReward(World world, Location spawn, GeyserLootEntry entry) {
        Item item = world.dropItem(spawn, entry.toItemStack(RAND));
        double angle = RAND.nextDouble() * 2 * Math.PI;
        double horizontal = 0.1 + RAND.nextDouble() * 0.3;
        item.setVelocity(new Vector(Math.cos(angle) * horizontal, 0.6 + RAND.nextDouble() * 0.6,
                Math.sin(angle) * horizontal));
        return item;
    }

    /**
     * Execute a command reward for the nearest player to the vent, if any is
     * close enough to have witnessed the eruption.
     */
    private void executeReward(World world, Location spawn, GeyserLootEntry entry) {
        world.getPlayers().stream()
                .filter(p -> p.getLocation().distanceSquared(spawn) <= COMMAND_RANGE_SQUARED)
                .min((a, b) -> Double.compare(a.getLocation().distanceSquared(spawn),
                        b.getLocation().distanceSquared(spawn)))
                .map(Player::getName)
                .ifPresent(name -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        entry.command().replace("%player%", name)));
    }

    /**
     * Cancel the repeating task, if it is running.
     */
    public void cancelTasks() {
        if (task != null) {
            task.cancel();
        }
    }

    /**
     * @return the pending offerings per vent (package-private for testing)
     */
    Map<Vector, VentOfferings> getVents() {
        return vents;
    }

    /**
     * @return true if the repeating task was scheduled (package-private for testing)
     */
    boolean isRunning() {
        return task != null;
    }
}
