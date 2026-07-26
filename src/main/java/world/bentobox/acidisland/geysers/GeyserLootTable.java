package world.bentobox.acidisland.geysers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * The weighted geyser reward table, loaded from {@code geyser-loot.yml}.
 * Entries with a channel are boosted by the share of the offering's worth that
 * went into that channel:
 * <pre>
 * effective = weight × (1 + 3 × channelWorth / totalChannelWorth)
 * </pre>
 * so a vent fed nothing but diamonds leans hard towards gems, a vent fed logs
 * towards forestry, and a vent fed a bit of everything leans nowhere. Worth
 * rather than item count is what pulls, so one diamond steers the table as
 * firmly as the stack of cobble it is worth.
 * <p>
 * An entry that names the offered material in its {@code from:} list
 * is a direct transmutation - magma into obsidian - and is boosted far harder,
 * so feeding a vent a specific thing reliably yields the thing it makes.
 * <p>
 * When the payout is budgeted by worth, entries the vent cannot afford are not
 * rolled at all, and entries worth a pittance next to the budget are damped, so
 * a diamond is answered in gems rather than in a heap of kelp.
 *
 * @author tastybento
 * @since 2.1.0
 */
public class GeyserLootTable {

    /**
     * How hard the channel a vent was fed pulls the table towards itself: an
     * entry whose channel took the whole offering is this much more likely.
     */
    private static final double CHANNEL_PULL = 3;
    /** Weight multiplier for an entry that directly transmutes an offered material. */
    private static final double RECIPE_BOOST = 8;
    /**
     * An entry worth less than this fraction of the budget is damped, so big
     * offerings are answered with big rewards.
     */
    private static final double PITTANCE_FRACTION = 0.125;
    /** Weight multiplier applied to entries worth a pittance next to the budget. */
    private static final double PITTANCE_DAMPING = 0.25;

    private final List<GeyserLootEntry> loot;

    public GeyserLootTable(List<GeyserLootEntry> loot) {
        this.loot = loot;
    }

    /**
     * Load a table from YAML: a list of entries under the {@code loot:} key.
     *
     * @param config loaded YAML
     * @return the table; empty if no valid entries were found
     */
    @NonNull
    public static GeyserLootTable parse(YamlConfiguration config) {
        List<GeyserLootEntry> loot = new ArrayList<>();
        for (Map<?, ?> map : config.getMapList("loot")) {
            GeyserLootEntry entry = GeyserLootEntry.parse(map);
            if (entry != null) {
                loot.add(entry);
            }
        }
        return new GeyserLootTable(loot);
    }

    /**
     * @return true if the table has no entries
     */
    public boolean isEmpty() {
        return loot.isEmpty();
    }

    /**
     * @return the raw loot table (do not mutate)
     */
    @NonNull
    public List<GeyserLootEntry> getLoot() {
        return loot;
    }

    /**
     * One reward a vent has decided to hand over.
     *
     * @param entry the entry rolled
     * @param amount how many to give - ignored for command entries
     */
    public record Award(GeyserLootEntry entry, int amount) {
    }

    /**
     * Work out everything a vent hands over for one eruption.
     * <p>
     * When the offer has a budget, the vent keeps rolling rewards it can still
     * afford and subtracting their worth until the worth it owes is spent, so
     * the payout is worth about what was fed in. Its final roll gives as many
     * as the leftover worth allows rather than pocketing the remainder.
     * Otherwise it simply rolls {@code maxRewards} times.
     *
     * @param random random source
     * @param offer what the vent was fed and owes
     * @param values worth of materials, or null if worth is not being matched
     * @param maxRewards hard cap on how many rewards may be handed over
     * @return the rewards to hand over, in the order they were rolled
     */
    @NonNull
    public List<Award> plan(Random random, @NonNull GeyserOffer offer, @Nullable GeyserValues values, int maxRewards) {
        boolean budgeted = offer.isBudgeted() && values != null;
        int budget = offer.budget();
        int rolls = Math.max(1, maxRewards);
        List<Award> awards = new ArrayList<>();
        for (int paid = 0; paid < rolls; paid++) {
            boolean last = paid == rolls - 1;
            GeyserOffer current = budgeted
                    ? new GeyserOffer(offer.bias(), offer.materials(), budget, offer.ceiling())
                    : offer;
            // On the last roll, first look for something that can soak up what
            // is left; if nothing can, take whatever the table offers
            GeyserLootEntry entry = last ? roll(random, current, budgeted ? values : null, true) : null;
            if (entry == null) {
                entry = roll(random, current, budgeted ? values : null, false);
            }
            if (entry == null) {
                // Nothing left in the table the vent can afford
                break;
            }
            int amount = amountFor(entry, values, budget, budgeted, last, random);
            awards.add(new Award(entry, amount));
            if (budgeted) {
                budget -= values.unitValue(entry) * (entry.isCommand() ? 1 : amount);
                if (budget <= 0) {
                    break;
                }
            }
        }
        return awards;
    }

    /**
     * How many of a reward to give: its own rolled amount, capped by what the
     * vent can still afford and by the stack size. On the vent's last roll it
     * gives as many as the budget allows, so leftover worth is paid out.
     */
    private int amountFor(GeyserLootEntry entry, @Nullable GeyserValues values, int budget, boolean budgeted,
            boolean last, Random random) {
        int rolled = entry.rollAmount(random);
        if (!budgeted || entry.isCommand()) {
            return rolled;
        }
        int unit = values.unitValue(entry);
        int affordable = unit > 0 ? budget / unit : Integer.MAX_VALUE;
        int cap = Math.max(1, Math.min(affordable, entry.material().getMaxStackSize()));
        return last ? cap : Math.clamp(rolled, 1, cap);
    }

    /**
     * Weighted roll honoring the vent's offering bias, with no worth budget.
     *
     * @param random random source
     * @param bias channel name to offering bias points
     * @return the rolled entry, or null if the table is empty
     */
    @Nullable
    public GeyserLootEntry roll(Random random, @NonNull Map<String, Integer> bias) {
        return roll(random, GeyserOffer.of(bias), null);
    }

    /**
     * Weighted roll honoring the vent's offering bias, its transmutations, and
     * what worth it has left to pay out.
     *
     * @param random random source
     * @param offer what the vent was fed and still owes
     * @param values worth of materials, or null if worth is not being matched
     * @return the rolled entry, or null if the table is empty or the vent can
     * afford nothing in it
     */
    @Nullable
    public GeyserLootEntry roll(Random random, @NonNull GeyserOffer offer, @Nullable GeyserValues values) {
        return roll(random, offer, values, false);
    }

    /**
     * Weighted roll, optionally as the vent's final roll of an eruption.
     *
     * @param random random source
     * @param offer what the vent was fed and still owes
     * @param values worth of materials, or null if worth is not being matched
     * @param lastRoll true to skip entries too cheap to spend the rest of the budget on
     * @return the rolled entry, or null if nothing qualifies
     */
    @Nullable
    private GeyserLootEntry roll(Random random, @NonNull GeyserOffer offer, @Nullable GeyserValues values,
            boolean lastRoll) {
        double total = 0;
        double[] weights = new double[loot.size()];
        for (int i = 0; i < loot.size(); i++) {
            weights[i] = effectiveWeight(loot.get(i), offer, values, lastRoll);
            total += weights[i];
        }
        if (total <= 0) {
            return null;
        }
        double roll = random.nextDouble() * total;
        for (int i = 0; i < loot.size(); i++) {
            roll -= weights[i];
            if (roll < 0) {
                return loot.get(i);
            }
        }
        return loot.get(loot.size() - 1);
    }

    /**
     * Effective weight of one entry given the vent's offering bias.
     */
    double effectiveWeight(GeyserLootEntry entry, @NonNull Map<String, Integer> bias) {
        return effectiveWeight(entry, GeyserOffer.of(bias), null);
    }

    /**
     * Effective weight of one entry given what the vent was fed and can afford.
     */
    double effectiveWeight(GeyserLootEntry entry, @NonNull GeyserOffer offer, @Nullable GeyserValues values) {
        return effectiveWeight(entry, offer, values, false);
    }

    /**
     * Effective weight of one entry given what the vent was fed and can afford.
     * An entry the vent cannot pay for, or that is far richer than anything it
     * was fed, weighs nothing and is never rolled.
     *
     * @param entry the entry
     * @param offer what the vent was fed and owes
     * @param values worth of materials, or null if worth is not being matched
     * @param lastRoll true if this is the vent's final roll, where entries too
     * cheap to spend the remaining worth on are skipped rather than leaving it
     * unpaid
     */
    double effectiveWeight(GeyserLootEntry entry, @NonNull GeyserOffer offer, @Nullable GeyserValues values,
            boolean lastRoll) {
        double weight = entry.weight() * channelPull(entry, offer);
        boolean transmutes = !entry.from().isEmpty() && !Collections.disjoint(entry.from(), offer.materials());
        if (transmutes) {
            weight *= RECIPE_BOOST;
        }
        if (values == null || !offer.isBudgeted()) {
            return weight;
        }
        int cost = values.unitValue(entry);
        if (cost > offer.budget()) {
            return 0;
        }
        // A named transmutation may be as rich as the admin likes; anything
        // else has to stay in the league of what was thrown in
        if (!transmutes && offer.hasCeiling() && cost > offer.ceiling()) {
            return 0;
        }
        if (lastRoll && cost > 0 && cost * stackSize(entry) < offer.budget()) {
            // Too cheap to spend what is left, even a full stack of it
            return 0;
        }
        if (cost > 0 && cost < offer.budget() * PITTANCE_FRACTION) {
            weight *= PITTANCE_DAMPING;
        }
        return weight;
    }

    /**
     * @return how much the vent's offering pulls the table towards this entry's
     * channel, from 1 (nothing of that channel was fed in) up to
     * 1 + {@link #CHANNEL_PULL} (the offering was nothing else)
     */
    private static double channelPull(GeyserLootEntry entry, @NonNull GeyserOffer offer) {
        if (entry.channel() == null || offer.bias().isEmpty()) {
            return 1;
        }
        double total = 0;
        for (int points : offer.bias().values()) {
            total += points;
        }
        if (total <= 0) {
            return 1;
        }
        return 1 + CHANNEL_PULL * (offer.bias().getOrDefault(entry.channel(), 0) / total);
    }

    private static int stackSize(GeyserLootEntry entry) {
        return entry.material() == null ? 1 : entry.material().getMaxStackSize();
    }

    /**
     * Map an offered material to a reward channel. Items with no channel still
     * count towards the reward count but do not bias the table.
     *
     * @param material the offered material
     * @return channel name or null
     */
    @Nullable
    public static String categorize(Material material) {
        String name = material.name().toUpperCase(Locale.ROOT);
        // Gems first - more specific than ores
        if (name.contains("DIAMOND") || name.contains("EMERALD") || name.contains("AMETHYST")
                || name.contains("LAPIS") || name.contains("ENDER_PEARL")) {
            return "gems";
        }
        if (name.contains("NETHER") || name.contains("SOUL_") || name.contains("MAGMA") || name.contains("BLAZE")
                || name.contains("QUARTZ") || name.contains("GHAST") || name.contains("BASALT")
                || name.contains("CRIMSON") || name.contains("WARPED")) {
            return "nether";
        }
        if (name.endsWith("_ORE") || name.startsWith("RAW_") || name.endsWith("_INGOT") || name.endsWith("_NUGGET")
                || name.equals("COAL") || name.equals("REDSTONE") || name.contains("STONE")
                || name.contains("DEEPSLATE") || name.contains("GRANITE") || name.contains("DIORITE")
                || name.contains("ANDESITE")) {
            return "mineral";
        }
        if (name.endsWith("_LOG") || name.endsWith("_WOOD") || name.endsWith("_PLANKS") || name.endsWith("_SAPLING")
                || name.endsWith("_LEAVES") || name.endsWith("_PROPAGULE") || name.contains("BAMBOO")
                || name.contains("KELP")) {
            return "forestry";
        }
        if (name.contains("WOOL") || name.contains("LEATHER") || name.equals("STRING") || name.contains("FEATHER")
                || name.contains("EGG") || material.isEdible()) {
            return "husbandry";
        }
        return null;
    }
}
