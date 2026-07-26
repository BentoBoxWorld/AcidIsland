package world.bentobox.acidisland.geysers;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.eclipse.jdt.annotation.Nullable;

import world.bentobox.acidisland.AcidIsland;
import world.bentobox.acidisland.events.GeyserSacrificeEvent;
import world.bentobox.acidisland.events.GeyserTransmuteEvent;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.util.Util;

/**
 * The geyser offerings mechanic: items a player throws into the water around a
 * sulfur vent are consumed with a sizzle as offerings, and when the vent erupts
 * the offerings are transmuted into rewards spewed out of the plume. Rewards are
 * rolled from the weighted {@code geyser-loot.yml} table, biased towards the
 * channels of the materials sacrificed, and worth about what was fed in: the
 * vent transmutes rather than destroys.
 * <p>
 * A once-a-second task drives the whole loop. Items floating anywhere near a
 * vent are tugged towards it so a throw does not have to be accurate - the
 * drift is also the only cue a player gets that a vent eats items. A fed vent
 * is then provoked into erupting a few seconds later, so the reward follows the
 * offering while the player is still watching, and the payout lands as the
 * plume settles so the eruption cannot fling it away.
 * <p>
 * All sulfur handling is string-based because the addon compiles against Paper
 * 1.21: on servers older than Minecraft 26.2 the material does not exist and
 * the mechanic is inert.
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
    /** Horizontal radius within which a vent tugs a floating item towards itself. */
    private static final int SUCTION_RADIUS = 5;
    /** How far below a floating item a vent can be and still tug it in. */
    private static final int SUCTION_DEPTH = 6;
    /** Velocity impulse applied once a second to an item being tugged in. */
    private static final double SUCTION_PULL = 0.15;
    /** How far below an item to probe for water before bothering with a suction scan. */
    private static final int WATER_PROBE_DEPTH = 3;
    /**
     * Seconds a fed vent waits before it is provoked into erupting. A thrown
     * stack lands as several items, so this batches them into one eruption.
     */
    private static final int PROVOKE_DELAY = 3;
    /**
     * Seconds a vent may stay erupting before its rewards are paid out anyway,
     * so offerings are never held forever by a plume that does not settle.
     */
    private static final int ERUPTION_TIMEOUT = 30;
    /** Radius around the plume to look for a player to run command rewards for. */
    private static final double COMMAND_RANGE_SQUARED = 24.0 * 24;
    /** Radius around a vent within which its feeders are told what it is doing. */
    private static final double MESSAGE_RANGE_SQUARED = 48.0 * 48;

    /**
     * PDC tag on spewed reward items. Tagged items are never consumed as
     * offerings, so rewards that land back in the pool cannot be recycled into
     * new offerings in a feedback loop. Once a player picks a reward up and
     * rethrows it, it is a fresh untagged entity and sacrifices normally.
     */
    static final NamespacedKey REWARD_KEY = Objects.requireNonNull(NamespacedKey.fromString("acidisland:geyser_reward"));

    private static final Random RAND = new Random();

    /**
     * Offerings pending at one vent: reward channel bias, the number of items
     * sacrificed, who fed it, and where the vent is in its eruption cycle.
     */
    static class VentOfferings {
        final Map<String, Integer> bias = new HashMap<>();
        /** Players who fed this vent, so they can be told when it answers. */
        final Set<UUID> contributors = new HashSet<>();
        /** Materials fed to this vent, for {@code from:} transmutations in the loot table. */
        final Set<Material> materials = EnumSet.noneOf(Material.class);
        int items;
        /** Total worth of everything fed to this vent, which is what it owes back. */
        int value;
        /** Worth of the richest single item fed in, which caps how rich a reward may be. */
        int topValue;
        boolean erupting;
        /** Seconds since the first offering, used to batch a thrown stack before provoking. */
        int age;
        /** Seconds the vent has been erupting, so a plume that sticks still pays out. */
        int eruptingFor;
        /** True once this vent has been provoked, so it is only ever provoked once. */
        boolean provoked;
    }

    private final AcidIsland addon;
    private final @Nullable GeyserLootTable table;
    private final @Nullable GeyserValues values;
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
            values = null;
            task = null;
            return;
        }
        if (POTENT_SULFUR == null) {
            addon.log("Geyser offerings require Minecraft 26.2 or later - mechanic disabled.");
            table = null;
            values = null;
            task = null;
            return;
        }
        table = loadTable();
        if (table.isEmpty()) {
            addon.logError("geyser-loot.yml has no valid loot entries - geyser offerings disabled.");
            values = null;
            task = null;
            return;
        }
        values = new GeyserValues(addon);
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
     * Consume items floating in the water around a vent cap as offerings, and
     * tug the ones that are close but not close enough into the pool.
     */
    private void consumeOfferings(World world) {
        for (Item item : world.getEntitiesByClass(Item.class)) {
            if (!canSacrifice(item)) {
                continue;
            }
            Block start = waterStart(item);
            Block vent = start == null ? null : findVentNear(start);
            if (vent != null) {
                sacrifice(world, item, vent);
            } else {
                tugTowardsVent(item);
            }
        }
    }

    /**
     * @return true if the item is a spewed reward that has not been picked up yet
     */
    private static boolean isReward(Item item) {
        return item.getPersistentDataContainer().has(REWARD_KEY, PersistentDataType.BYTE);
    }

    /**
     * @return true if the item may be offered at all. Only items a player threw
     * count: death drops, block drops and mob drops have no thrower and are left
     * alone, so a vent cannot quietly eat an inventory that washes into its pool.
     * Rewards the vent just spewed are excluded too, which stops them being
     * recycled into fresh offerings.
     */
    static boolean isOffering(Item item) {
        return !isReward(item) && item.getThrower() != null;
    }

    /**
     * @return true if the item is an offering that has settled long enough to be
     * consumed by the floating scan
     */
    private static boolean canSacrifice(Item item) {
        return item.getTicksLived() >= MIN_AGE_TICKS && isOffering(item);
    }

    /**
     * Tug an item floating near a vent towards its pool. Items bob, drift, and
     * get lofted clear of the water by the vent's own bubble column, so without
     * this a player whose throw landed a block or two out just watches it sit
     * there with no hint of what went wrong. The visible drift towards the vent
     * is also the only cue that a vent eats items at all.
     */
    private void tugTowardsVent(Item item) {
        Block block = item.getLocation().getBlock();
        if (!nearWater(block)) {
            return;
        }
        Block vent = findVent(block, SUCTION_RADIUS, 0, SUCTION_DEPTH);
        if (vent == null) {
            return;
        }
        // Aim at the water just above the cap rather than into the block itself
        Vector pull = vent.getLocation().add(0.5, 1, 0.5).toVector().subtract(item.getLocation().toVector());
        double distance = pull.length();
        if (distance > 0.1) {
            item.setVelocity(item.getVelocity().add(pull.multiply(SUCTION_PULL / distance)));
        }
    }

    /**
     * @return true if there is water at or just below the block, so an item
     * there is worth scanning for a nearby vent
     */
    private static boolean nearWater(Block block) {
        for (int dy = 0; dy <= WATER_PROBE_DEPTH; dy++) {
            if (isWaterLike(block.getRelative(0, -dy, 0).getType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Offer an item to a nearby vent on behalf of another consumer. Called by
     * {@link world.bentobox.acidisland.world.AcidTask} when acid is about to
     * destroy a floating item, so acid destruction within a vent's pool counts
     * as a sacrifice instead of a plain loss.
     *
     * @param item the item about to be destroyed
     * @return true if a vent consumed the item as an offering
     */
    public boolean offerToVent(Item item) {
        if (task == null || !isOffering(item)) {
            return false;
        }
        Block start = waterStart(item);
        Block vent = start == null ? null : findVentNear(start);
        return vent != null && sacrifice(item.getWorld(), item, vent);
    }

    /**
     * Look for a potent sulfur cap in the pool below and around a floating
     * item.
     *
     * @param start the water block the floating item is in
     * @return the nearest vent cap block, or null if the item is not in a vent's pool
     */
    private @Nullable Block findVentNear(Block start) {
        return findVent(start, POOL_SCAN_RADIUS, 1, POOL_SCAN_DEPTH);
    }

    /**
     * Look for a potent sulfur cap around and below a block. Scans the
     * shallowest layer first so a twin vent's main cap wins.
     *
     * @param start block to scan from
     * @param radius horizontal radius to scan
     * @param fromDepth first layer to scan, in blocks below the start block
     * @param toDepth last layer to scan, in blocks below the start block
     * @return the nearest vent cap block, or null if there is none in range
     */
    private @Nullable Block findVent(Block start, int radius, int fromDepth, int toDepth) {
        for (int dy = fromDepth; dy <= toDepth; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
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
     *
     * @return true if the item was consumed, false if the event was cancelled
     */
    private boolean sacrifice(World world, Item item, Block vent) {
        String channel = GeyserLootTable.categorize(item.getItemStack().getType());
        GeyserSacrificeEvent event = new GeyserSacrificeEvent(item, vent.getLocation(), channel);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        VentOfferings offerings = vents.computeIfAbsent(vent.getLocation().toVector(), k -> {
            VentOfferings v = new VentOfferings();
            v.erupting = isErupting(vent);
            return v;
        });
        int amount = item.getItemStack().getAmount();
        int worth = values.getValue(item.getItemStack());
        offerings.items += amount;
        offerings.value += worth;
        offerings.topValue = Math.max(offerings.topValue, values.getValue(item.getItemStack().getType()));
        offerings.materials.add(item.getItemStack().getType());
        if (channel != null) {
            // Worth, not item count, is what steers the payout: one diamond
            // should pull as hard as the stack of cobble it is worth
            offerings.bias.merge(channel, Math.max(1, worth), Integer::sum);
        }
        UUID thrower = item.getThrower();
        if (thrower != null) {
            offerings.contributors.add(thrower);
            tellThrower(thrower, item.getItemStack().getType(), channel);
        }
        // Consumed either way - the geyser is not a storage unit
        item.remove();
        world.playSound(item.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.8F, 1.2F);
        world.spawnParticle(Particle.LARGE_SMOKE, item.getLocation(), 8, 0.2, 0.2, 0.2, 0.02);
        return true;
    }

    /**
     * Tell the player whose offering was just swallowed what the vent took and,
     * when the material has one, which reward channel it leans on. Naming the
     * channel here is the only way a player discovers that what they feed a
     * vent steers what it gives back.
     */
    private void tellThrower(UUID uuid, Material material, @Nullable String channel) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        User user = User.getInstance(player);
        String item = Util.prettifyText(material.name());
        if (channel == null) {
            user.sendMessage("acidisland.geyser.offering", "[item]", item);
        } else {
            user.sendMessage("acidisland.geyser.offering-channel", "[item]", item, "[channel]",
                    channelName(user, channel));
        }
    }

    /**
     * @return the translated name of a reward channel, falling back to the raw
     * channel name so channels added to geyser-loot.yml still read sensibly
     */
    private static String channelName(User user, String channel) {
        String name = user.getTranslationOrNothing("acidisland.geyser.channels." + channel);
        return name.isEmpty() ? channel : name;
    }

    /**
     * Tell the players who fed a vent what it is doing, if they are still near
     * enough to see it happen.
     *
     * @param where the vent's plume
     * @param offerings the vent's pending offerings
     * @param reference locale reference
     * @param channel channel to name in the message, or null if it takes none
     */
    private void announce(Location where, VentOfferings offerings, String reference, @Nullable String channel) {
        for (UUID uuid : offerings.contributors) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.getWorld().equals(where.getWorld())
                    || player.getLocation().distanceSquared(where) > MESSAGE_RANGE_SQUARED) {
                continue;
            }
            User user = User.getInstance(player);
            if (channel == null) {
                user.sendMessage(reference);
            } else {
                user.sendMessage(reference, "[channel]", channelName(user, channel));
            }
        }
    }

    /**
     * @return the channel the vent was fed most of, or null if nothing offered
     * carried a channel
     */
    static @Nullable String dominantChannel(VentOfferings offerings) {
        return offerings.bias.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
    }

    /**
     * Watch fed vents: provoke them into erupting, and when a vent that was
     * erupting settles again, spew the transmuted rewards. Vents that have been
     * mined out forfeit their offerings.
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
        offerings.age++;
        boolean erupting = isErupting(vent);
        if (offerings.erupting && !erupting) {
            // The plume has settled - pay out now so the eruption cannot
            // fling the rewards far from the vent
            spew(world, vent, offerings);
            return true;
        }
        if (erupting) {
            offerings.eruptingFor++;
            if (offerings.eruptingFor >= ERUPTION_TIMEOUT) {
                // The plume never settled - unstick the vent and pay out rather
                // than hold the offerings forever
                vent.tick();
                spew(world, vent, offerings);
                return true;
            }
        } else if (!offerings.provoked && offerings.age >= PROVOKE_DELAY && provoke(vent, offerings)) {
            // Treat it as erupting from here so the payout fires as the plume settles
            offerings.erupting = true;
            return false;
        }
        offerings.erupting = erupting;
        return false;
    }

    private boolean isErupting(Block vent) {
        return vent.getBlockData().getAsString().contains(ERUPTING_STATE);
    }

    /**
     * Provoke a fed vent into erupting now instead of waiting out the vanilla
     * cycle. Without this the payout can arrive minutes after the offering,
     * long after the player has sailed off, so nothing connects the two.
     *
     * @param vent the vent cap
     * @param offerings the vent's pending offerings
     * @return true if the vent was set erupting
     */
    private boolean provoke(Block vent, VentOfferings offerings) {
        offerings.provoked = true;
        if (!addon.getSettings().isGeyserEruptOnOffering()) {
            return false;
        }
        String data = vent.getBlockData().getAsString().replaceAll("potent_sulfur_state=\\w+", ERUPTING_STATE);
        try {
            vent.setBlockData(Bukkit.createBlockData(data), true);
        } catch (IllegalArgumentException e) {
            // Unknown state on this server - leave the eruption to vanilla
            addon.logWarning("Could not provoke a sulfur vent into erupting: " + e.getMessage());
            return false;
        }
        vent.getWorld().playSound(vent.getLocation(), Sound.BLOCK_LAVA_AMBIENT, 1F, 0.6F);
        announce(vent.getLocation(), offerings, "acidisland.geyser.churning", null);
        return true;
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
        List<Item> rewards = payOut(world, spawn, offerings);
        world.playSound(spawn, Sound.ENTITY_GENERIC_SPLASH, 1F, 1F);
        world.spawnParticle(Particle.SPLASH, spawn, 40, 0.4, 0.6, 0.4, 0.2);
        String channel = dominantChannel(offerings);
        announce(spawn, offerings, channel == null ? "acidisland.geyser.payout" : "acidisland.geyser.payout-channel",
                channel);
        Bukkit.getPluginManager().callEvent(new GeyserTransmuteEvent(vent.getLocation(), rewards, offerings.items));
    }

    /**
     * Roll and hand out a vent's rewards.
     * <p>
     * With worth matching on, the vent owes back what it was fed times the
     * exchange rate, and keeps rolling rewards it can still afford until that
     * worth is spent - so a diamond comes back as gems rather than as one
     * random trinket, and a stack of cobble comes back as cobble-grade tat.
     * Otherwise it falls back to one roll per item offered.
     *
     * @return the reward items spewed, which excludes command rewards
     */
    private List<Item> payOut(World world, Location spawn, VentOfferings offerings) {
        int max = Math.max(1, addon.getSettings().getGeyserMaxRewards());
        boolean budgeted = addon.getSettings().isGeyserMatchValue();
        int budget = budgeted ? (int) Math.round(offerings.value * addon.getSettings().getGeyserExchangeRate())
                : GeyserOffer.UNLIMITED;
        int rolls = budgeted ? max : Math.clamp(offerings.items, 1, max);
        GeyserOffer offer = new GeyserOffer(offerings.bias, offerings.materials, budget, ceiling(offerings));
        List<Item> rewards = new ArrayList<>();
        for (GeyserLootTable.Award award : table.plan(RAND, offer, budgeted ? values : null, rolls)) {
            if (award.entry().isCommand()) {
                executeReward(world, spawn, award.entry());
            } else {
                rewards.add(launchReward(world, spawn, award.entry().toItemStack(award.amount())));
            }
        }
        return rewards;
    }

    /**
     * The most a single reward may be worth: what the richest thing fed in was
     * worth, times the configured multiplier. Without this a stack of cobble -
     * worth an emerald, and infinite on an island with a generator - would buy
     * gems. Named {@code from:} transmutations ignore it.
     *
     * @param offerings the vent's offerings
     * @return the ceiling, or {@link GeyserOffer#NO_CEILING} if it is switched off
     */
    private int ceiling(VentOfferings offerings) {
        double multiplier = addon.getSettings().getGeyserRewardCeiling();
        if (multiplier <= 0) {
            return GeyserOffer.NO_CEILING;
        }
        return Math.max(1, (int) Math.round(offerings.topValue * multiplier));
    }

    /**
     * Spawn one reward item at the plume top with a radial launch: guaranteed
     * outward motion so the rewards scatter around the vent instead of falling
     * straight back into the pool.
     */
    private Item launchReward(World world, Location spawn, ItemStack stack) {
        Item item = world.dropItem(spawn, stack);
        item.getPersistentDataContainer().set(REWARD_KEY, PersistentDataType.BYTE, (byte) 1);
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
