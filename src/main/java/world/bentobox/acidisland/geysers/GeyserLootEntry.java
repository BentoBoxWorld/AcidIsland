package world.bentobox.acidisland.geysers;

import java.util.Locale;
import java.util.Map;
import java.util.Random;

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
 *
 * @author tastybento
 * @since 2.1.0
 */
public record GeyserLootEntry(@Nullable Material material, @Nullable String command, int weight,
        @Nullable String channel, int min, int max) {

    /**
     * @return true if this is a command reward rather than an item
     */
    public boolean isCommand() {
        return command != null;
    }

    /**
     * Roll an item stack from this entry.
     *
     * @param random random source
     * @return stack with a random amount in [min, max]
     */
    public ItemStack toItemStack(Random random) {
        int amount = min >= max ? min : min + random.nextInt(max - min + 1);
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
            return new GeyserLootEntry(null, command, weight, channel, min, max);
        }
        if (map.get("item") instanceof String item) {
            Material material = Material.matchMaterial(item);
            if (material != null) {
                return new GeyserLootEntry(material, null, weight, channel, min, max);
            }
        }
        return null;
    }
}
