package world.bentobox.acidisland.geysers;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

/**
 * One weighted entry in the geyser reward table. Either an item (material +
 * amount range) or a console command with {@code %player%} substitution.
 *
 * @param material the material, or null for command entries
 * @param command the console command, or null for item entries
 * @param weight the base weight
 * @param channel the offering channel this entry belongs to, or null
 * @param min minimum amount
 * @param max maximum amount
 * @param value what one of this reward costs the vent, or null to use the
 * material's worth from geyser-values.yml
 * @param from materials that transmute into this reward - offering any of them
 * makes this entry far more likely
 *
 * @author tastybento
 * @since 2.1.0
 */
public record GeyserLootEntry(@Nullable Material material, @Nullable String command, int weight,
        @Nullable String channel, int min, int max, @Nullable Integer value, Set<Material> from) {

    /**
     * An entry with no explicit value and no transmutation materials.
     *
     * @param material the material, or null for command entries
     * @param command the console command, or null for item entries
     * @param weight the base weight
     * @param channel the offering channel this entry belongs to, or null
     * @param min minimum amount
     * @param max maximum amount
     */
    public GeyserLootEntry(@Nullable Material material, @Nullable String command, int weight, @Nullable String channel,
            int min, int max) {
        this(material, command, weight, channel, min, max, null, Set.of());
    }

    /**
     * @return true if this is a command reward rather than an item
     */
    public boolean isCommand() {
        return command != null;
    }

    /**
     * @param random random source
     * @return a random amount in [min, max]
     */
    public int rollAmount(Random random) {
        int amount = min >= max ? min : min + random.nextInt(max - min + 1);
        return Math.max(1, amount);
    }

    /**
     * Roll an item stack from this entry.
     *
     * @param random random source
     * @return stack with a random amount in [min, max]
     */
    public ItemStack toItemStack(Random random) {
        return toItemStack(rollAmount(random));
    }

    /**
     * @param amount how many to give
     * @return a stack of this entry's material
     */
    public ItemStack toItemStack(int amount) {
        return new ItemStack(material, Math.max(1, amount));
    }

    /**
     * Parse an entry from a YAML map, e.g.
     * {@code {item: RAW_IRON, weight: 30, channel: mineral, amount: {min: 1, max: 3}}}
     * or {@code {command: "say hi %player%", weight: 1}}.
     *
     * @param map the raw map from the YAML list
     * @return the entry, or null if invalid
     */
    @Nullable
    public static GeyserLootEntry parse(Map<?, ?> map) {
        int weight = map.get("weight") instanceof Number n ? n.intValue() : 1;
        String channel = map.get("channel") instanceof String s ? s.toLowerCase(Locale.ROOT) : null;
        Integer value = map.get("value") instanceof Number n ? n.intValue() : null;
        Set<Material> from = parseFrom(map.get("from"));
        int min = 1;
        int max = 1;
        if (map.get("amount") instanceof Map<?, ?> amount) {
            min = amount.get("min") instanceof Number n ? n.intValue() : 1;
            max = amount.get("max") instanceof Number n ? n.intValue() : min;
        } else if (map.get("amount") instanceof Number n) {
            min = n.intValue();
            max = n.intValue();
        }
        if (map.get("command") instanceof String command && !command.isBlank()) {
            return new GeyserLootEntry(null, command, weight, channel, min, max, value, from);
        }
        if (map.get("item") instanceof String item) {
            Material material = Material.matchMaterial(item);
            if (material != null) {
                return new GeyserLootEntry(material, null, weight, channel, min, max, value, from);
            }
        }
        return null;
    }

    /**
     * Parse the optional {@code from:} list of materials that transmute into
     * this reward. Unknown materials are skipped, so a table may name materials
     * from a newer Minecraft version than the server runs.
     *
     * @param raw the raw value from the YAML map
     * @return the materials, empty if there are none
     */
    private static Set<Material> parseFrom(@Nullable Object raw) {
        if (!(raw instanceof List<?> list) || list.isEmpty()) {
            return Set.of();
        }
        Set<Material> from = EnumSet.noneOf(Material.class);
        for (Object o : list) {
            Material material = o instanceof String s ? Material.matchMaterial(s) : null;
            if (material != null) {
                from.add(material);
            }
        }
        return from;
    }
}
