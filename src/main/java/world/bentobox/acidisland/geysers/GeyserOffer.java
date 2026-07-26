package world.bentobox.acidisland.geysers;

import java.util.Map;
import java.util.Set;

import org.bukkit.Material;

/**
 * What a vent has been fed, as the reward table sees it: which channels the
 * offerings lean towards, the actual materials that went in, and how much worth
 * the vent still owes.
 *
 * @param bias channel name to offering points
 * @param materials the materials offered, for {@code from:} transmutations
 * @param budget the worth left to pay out, or {@link #UNLIMITED} to ignore worth
 * @param ceiling the most a single reward may be worth, or {@link #NO_CEILING}.
 * Total worth alone is not enough: a stack of cobble is worth an emerald, and a
 * cobble generator is infinite, so a vent will not hand back anything far richer
 * than the richest thing it was fed. Entries with a matching {@code from:} list
 * ignore the ceiling - a named transmutation is the admin saying it is allowed.
 *
 * @author tastybento
 * @since 2.1.1
 */
public record GeyserOffer(Map<String, Integer> bias, Set<Material> materials, int budget, int ceiling) {

    /** Budget value meaning "pay out whatever, worth is not being matched". */
    public static final int UNLIMITED = -1;
    /** Ceiling value meaning "any single reward is allowed, however rich". */
    public static final int NO_CEILING = -1;

    /**
     * An offer with no limit on what a single reward may be worth.
     *
     * @param bias channel name to offering points
     * @param materials the materials offered
     * @param budget the worth left to pay out
     */
    public GeyserOffer(Map<String, Integer> bias, Set<Material> materials, int budget) {
        this(bias, materials, budget, NO_CEILING);
    }

    /**
     * @param bias channel name to offering points
     * @return an offer with no material transmutations, budget or ceiling
     */
    public static GeyserOffer of(Map<String, Integer> bias) {
        return new GeyserOffer(bias, Set.of(), UNLIMITED, NO_CEILING);
    }

    /**
     * @return true if rewards must be paid within a worth budget
     */
    public boolean isBudgeted() {
        return budget != UNLIMITED;
    }

    /**
     * @return true if there is a limit on what a single reward may be worth
     */
    public boolean hasCeiling() {
        return ceiling != NO_CEILING;
    }
}
