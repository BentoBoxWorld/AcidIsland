package world.bentobox.acidisland.geysers;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

import world.bentobox.acidisland.AcidIsland;
import world.bentobox.bentobox.api.addons.Addon;

/**
 * What a material is worth to a sulfur vent, so an offering can be answered
 * with rewards of roughly equal worth rather than a random handful. A vent
 * transmutes rather than destroys: feed it a diamond and it owes you a
 * diamond's worth back.
 * <p>
 * Values are resolved in this order, first hit wins:
 * <ol>
 * <li>{@code values:} in {@code geyser-values.yml} - the admin's word is final</li>
 * <li>the Level addon's block values for this world, if it is installed and
 * {@code use-level-addon} is true, so a server that has already tuned what
 * blocks are worth does not have to tune it twice</li>
 * <li>{@code default:} in {@code geyser-values.yml}</li>
 * </ol>
 * The Level addon is optional and not a compile dependency, so it is reached
 * reflectively and simply ignored if it is absent or its API has moved on.
 *
 * @author tastybento
 * @since 2.1.1
 */
public class GeyserValues {

    private final AcidIsland addon;
    /** Values from geyser-values.yml, and the resolved value of every material asked for so far. */
    private final Map<Material, Integer> values = new EnumMap<>(Material.class);
    private int defaultValue = 1;
    private boolean useLevelAddon = true;
    /** Level's BlockConfig#getValue(World, Object), or null if Level is not usable. */
    private @Nullable MethodHandle levelValue;
    private @Nullable Object levelConfig;
    private boolean levelChecked;

    public GeyserValues(AcidIsland addon) {
        this.addon = addon;
        load();
    }

    private void load() {
        File file = new File(addon.getDataFolder(), "geyser-values.yml");
        if (!file.exists()) {
            addon.saveResource("geyser-values.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        defaultValue = Math.max(0, config.getInt("default", 1));
        useLevelAddon = config.getBoolean("use-level-addon", true);
        ConfigurationSection section = config.getConfigurationSection("values");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            Material material = Material.matchMaterial(key);
            if (material == null) {
                // Materials from newer Minecraft versions are listed on purpose - skip quietly
                continue;
            }
            values.put(material, Math.max(0, section.getInt(key)));
        }
    }

    /**
     * @param material the material offered or rewarded
     * @return what one of it is worth to a vent
     */
    public int getValue(Material material) {
        Integer value = values.get(material);
        if (value != null) {
            return value;
        }
        int resolved = fromLevelAddon(material).orElse(defaultValue);
        values.put(material, resolved);
        return resolved;
    }

    /**
     * @param stack the stack offered
     * @return what the whole stack is worth to a vent
     */
    public int getValue(ItemStack stack) {
        return getValue(stack.getType()) * stack.getAmount();
    }

    /**
     * What one payout of a loot entry costs the vent. Item entries are worth
     * their material unless the entry sets its own {@code value}; command
     * entries are free unless they set one.
     *
     * @param entry the loot entry
     * @return the cost of one of it
     */
    public int unitValue(GeyserLootEntry entry) {
        if (entry.value() != null) {
            return entry.value();
        }
        return entry.material() == null ? 0 : getValue(entry.material());
    }

    /**
     * @return the fallback value for materials nothing else knows about
     */
    public int getDefaultValue() {
        return defaultValue;
    }

    /**
     * Ask the Level addon what a block is worth in this world. Looked up
     * lazily and cached, so load order does not matter.
     *
     * @param material the material
     * @return the Level addon's value, or empty if it has none or is not installed
     */
    // MethodHandle#invoke is declared to throw Throwable, and a broken hook
    // into another addon must never take the vent down with it
    @SuppressWarnings("java:S1181")
    private Optional<Integer> fromLevelAddon(Material material) {
        if (!useLevelAddon) {
            return Optional.empty();
        }
        if (!levelChecked) {
            hookLevelAddon();
        }
        if (levelValue == null) {
            return Optional.empty();
        }
        World world = addon.getOverWorld();
        try {
            Object value = levelValue.invoke(levelConfig, world, material);
            return value instanceof Integer i && i > 0 ? Optional.of(i) : Optional.empty();
        } catch (Throwable e) {
            // Level has changed under us - stop asking
            levelValue = null;
            addon.logWarning("Could not read block values from the Level addon: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Look up {@code Level#getBlockConfig()} and {@code BlockConfig#getValue()}
     * once, by method handle.
     * <p>
     * Deliberately not {@link Class#getMethod(String, Class...)}: that builds
     * the whole declared-method list of Level's class first, which makes the
     * JVM load every class named in every one of its signatures. Level has
     * methods that mention its own soft dependencies, so on a server without
     * Visit installed that throws {@code NoClassDefFoundError} before it ever
     * reaches the method we want. A method handle resolves only the member
     * asked for, so the missing optional classes are never touched.
     */
    // Throwable on purpose: the failure this guards against is a LinkageError,
    // not an exception, and MethodHandle#invoke is declared to throw Throwable
    @SuppressWarnings("java:S1181")
    private void hookLevelAddon() {
        levelChecked = true;
        Optional<Addon> level = addon.getPlugin().getAddonsManager().getAddonByName("Level");
        if (level.isEmpty()) {
            return;
        }
        Addon levelAddon = level.get();
        try {
            Class<?> blockConfigClass = Class.forName("world.bentobox.level.config.BlockConfig", false,
                    levelAddon.getClass().getClassLoader());
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            Object config = lookup
                    .findVirtual(levelAddon.getClass(), "getBlockConfig", MethodType.methodType(blockConfigClass))
                    .invoke(levelAddon);
            if (config == null) {
                return;
            }
            levelValue = lookup.findVirtual(blockConfigClass, "getValue",
                    MethodType.methodType(Integer.class, World.class, Object.class));
            levelConfig = config;
            addon.log("Geyser offerings will use the Level addon's block values where geyser-values.yml is silent.");
        } catch (Throwable e) {
            // Level absent, shaded differently, or its API has moved on - the
            // values file alone is a perfectly good source of worth
            addon.logWarning("Could not hook into the Level addon for block values, using geyser-values.yml only: "
                    + e.getMessage());
        }
    }
}
