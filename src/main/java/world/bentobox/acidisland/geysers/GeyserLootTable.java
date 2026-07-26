package world.bentobox.acidisland.geysers;

import java.util.ArrayList;
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
 * Entries with a channel are boosted by the offerings sacrificed to the vent:
 * <pre>
 * effective = weight × (1 + 0.5 × offeringPoints(entry.channel))
 * </pre>
 * so a vent fed diamonds leans towards gems, a vent fed logs towards forestry,
 * and so on.
 *
 * @author tastybento
 * @since 2.1.0
 */
public class GeyserLootTable {

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
     * Weighted roll honoring the vent's offering bias.
     *
     * @param random random source
     * @param bias channel name to offering bias points
     * @return the rolled entry, or null if the table is empty
     */
    @Nullable
    public GeyserLootEntry roll(Random random, @NonNull Map<String, Integer> bias) {
        double total = 0;
        double[] weights = new double[loot.size()];
        for (int i = 0; i < loot.size(); i++) {
            weights[i] = effectiveWeight(loot.get(i), bias);
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
        double weight = entry.weight();
        if (entry.channel() != null) {
            weight *= 1 + 0.5 * bias.getOrDefault(entry.channel(), 0);
        }
        return weight;
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
