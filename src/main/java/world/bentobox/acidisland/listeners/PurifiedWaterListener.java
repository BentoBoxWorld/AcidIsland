package world.bentobox.acidisland.listeners;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import net.kyori.adventure.text.Component;

import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.PointedDripstone;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent.ChangeReason;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;

import world.bentobox.acidisland.AISettings;
import world.bentobox.acidisland.AcidIsland;
import world.bentobox.acidisland.events.ItemFillWithAcidEvent;
import world.bentobox.acidisland.events.PlayerDrinkPurifiedWaterEvent;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.util.Util;

/**
 * Handles the purified water mechanic.
 * <p>
 * Water in AcidIsland is acid. Glass bottles filled from the ocean deal damage when drunk.
 * Purified water heals the player instead. Purified water can be obtained by:
 * <ul>
 *   <li>Collecting rain in a cauldron filled by dripstone (stalactite)</li>
 *   <li>Smelting a water bottle in a furnace</li>
 *   <li>Brewing water bottles with a Coal ingredient in a brewing stand</li>
 *   <li>Smelting a water bucket in a furnace (if enabled in config)</li>
 * </ul>
 * @author tastybento
 * @since 1.21
 */
public class PurifiedWaterListener implements Listener {

    /** PDC key that marks a water item as acid or purified */
    static final NamespacedKey WATER_TYPE_KEY = new NamespacedKey("acidisland", "water_type");
    static final String ACID = "acid";
    static final String PURIFIED = "purified";

    private final AcidIsland addon;
    /** Tracks whether each cauldron location holds purified (true) or acid (false) water */
    private final Map<Location, Boolean> cauldronPurity = new HashMap<>();
    /** Persists cauldron purity across server restarts */
    private final File cauldronDataFile;
    private final YamlConfiguration cauldronYaml;

    public PurifiedWaterListener(AcidIsland addon) {
        this.addon = addon;
        registerFurnaceRecipes();
        File dataFolder = addon.getDataFolder();
        if (dataFolder != null) {
            cauldronDataFile = new File(dataFolder, "cauldrons.yml");
            cauldronYaml = YamlConfiguration.loadConfiguration(cauldronDataFile);
            loadCauldronData();
        } else {
            cauldronDataFile = null;
            cauldronYaml = new YamlConfiguration();
        }
    }

    private void registerFurnaceRecipes() {
        // Plain vanilla water bottle — the only untagged input we accept in the furnace
        ItemStack plainWaterBottle = new ItemStack(Material.POTION);
        PotionMeta wMeta = (PotionMeta) plainWaterBottle.getItemMeta();
        wMeta.setBasePotionType(PotionType.WATER);
        plainWaterBottle.setItemMeta(wMeta);

        NamespacedKey bottleKey = NamespacedKey.fromString("acidisland:purified_water_bottle");
        FurnaceRecipe bottleRecipe = new FurnaceRecipe(
                bottleKey,
                makePurifiedBottle(),
                new RecipeChoice.ExactChoice(plainWaterBottle, makeAcidBottle()),
                0.1f, 200);
        Bukkit.addRecipe(bottleRecipe);

        if (addon.getSettings().isPurifiedBucketFurnaceEnabled()) {
            NamespacedKey bucketKey = NamespacedKey.fromString("acidisland:purified_water_bucket");
            FurnaceRecipe bucketRecipe = new FurnaceRecipe(
                    bucketKey,
                    makePurifiedBucket(),
                    Material.WATER_BUCKET,
                    0.1f, 2000);  // 100 seconds — simulates boiling
            Bukkit.addRecipe(bucketRecipe);
        }
    }

    // -----------------------------------------------------------------------
    // Item helpers
    // -----------------------------------------------------------------------

    /**
     * @return true if the item is tagged as purified water
     */
    static boolean isPurified(ItemStack item) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return PURIFIED.equals(pdc.get(WATER_TYPE_KEY, PersistentDataType.STRING));
    }

    /**
     * @return true if the item is a water bottle (plain or acid-tagged) that should cause acid damage
     */
    static boolean isWaterBottle(ItemStack item) {
        if (item == null || item.getType() != Material.POTION) return false;
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof PotionMeta pm)) return false;
        // Check for our acid tag
        String tag = pm.getPersistentDataContainer().get(WATER_TYPE_KEY, PersistentDataType.STRING);
        if (ACID.equals(tag)) return true;
        if (PURIFIED.equals(tag)) return false;
        // Plain water bottle (no custom tag): PotionType.WATER or no type with no effects
        PotionType base = pm.getBasePotionType();
        return (base == null || base == PotionType.WATER) && pm.getCustomEffects().isEmpty();
    }

    /**
     * @return a Potion of Poison with the acid PDC tag and red lore
     */
    ItemStack makeAcidBottle() {
        ItemStack bottle = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) bottle.getItemMeta();
        meta.setBasePotionType(PotionType.POISON);
        meta.lore(List.of(lore("lore-acid")));
        meta.getPersistentDataContainer().set(WATER_TYPE_KEY, PersistentDataType.STRING, ACID);
        bottle.setItemMeta(meta);
        return bottle;
    }

    /**
     * @return a Potion of Healing with the purified PDC tag and green lore
     */
    ItemStack makePurifiedBottle() {
        ItemStack bottle = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) bottle.getItemMeta();
        meta.setBasePotionType(PotionType.WATER);
        meta.lore(List.of(lore("lore-purified")));
        meta.getPersistentDataContainer().set(WATER_TYPE_KEY, PersistentDataType.STRING, PURIFIED);
        bottle.setItemMeta(meta);
        return bottle;
    }

    /**
     * @return a water bucket marked as acid (red lore, PDC tag)
     */
    ItemStack makeAcidBucket() {
        ItemStack bucket = new ItemStack(Material.WATER_BUCKET);
        ItemMeta meta = bucket.getItemMeta();
        meta.lore(List.of(lore("lore-acid")));
        meta.getPersistentDataContainer().set(WATER_TYPE_KEY, PersistentDataType.STRING, ACID);
        bucket.setItemMeta(meta);
        return bucket;
    }

    /**
     * @return a water bucket marked as purified (green lore, PDC tag)
     */
    ItemStack makePurifiedBucket() {
        ItemStack bucket = new ItemStack(Material.WATER_BUCKET);
        ItemMeta meta = bucket.getItemMeta();
        meta.lore(List.of(lore("lore-purified")));
        meta.getPersistentDataContainer().set(WATER_TYPE_KEY, PersistentDataType.STRING, PURIFIED);
        bucket.setItemMeta(meta);
        return bucket;
    }

    /**
     * Returns the translated lore Component for the given purified-water locale sub-key.
     * Falls back to parsing the key as a MiniMessage string if the locale entry is missing.
     */
    private Component lore(String key) {
        String raw = addon.getPlugin().getLocalesManager()
                .getOrDefault("acidisland.purified-water." + key, "<gray>" + key);
        return Util.parseMiniMessageOrLegacy(raw);
    }

    // -----------------------------------------------------------------------
    // Event handlers
    // -----------------------------------------------------------------------

    /**
     * Track whether a cauldron holds acid or purified water based on how it was filled.
     * <ul>
     *   <li>Rain / water bucket → acid</li>
     *   <li>Dripstone stalactite → purified</li>
     *   <li>Cauldron emptied → remove from map</li>
     * </ul>
     */
    @EventHandler(ignoreCancelled = true)
    public void onCauldronChange(CauldronLevelChangeEvent e) {
        if (!addon.getSettings().isPurifiedWaterEnabled()) return;
        if (!isAcidIslandWorld(e.getBlock().getWorld())) return;

        Location loc = e.getBlock().getLocation();
        ChangeReason reason = e.getReason();

        if (reason == ChangeReason.NATURAL_FILL) {
            // NATURAL_FILL covers both rain and dripstone stalactite drip.
            // Distinguish by checking for a downward-pointing dripstone tip above the cauldron.
            boolean pure = hasDripstoneStalactiteAbove(e.getBlock());
            cauldronPurity.put(loc, pure);
            persistCauldronPurity(loc, pure);
        } else if (reason == ChangeReason.BUCKET_EMPTY) {
            // Player poured a bucket into the cauldron — check if it was a purified water bucket
            boolean pure;
            if (e.getEntity() instanceof Player p) {
                ItemStack held = p.getInventory().getItemInMainHand();
                pure = isPurified(held);
            } else {
                pure = false; // acid if no player (dispenser, etc.)
            }
            cauldronPurity.put(loc, pure);
            persistCauldronPurity(loc, pure);
        } else if (reason == ChangeReason.BOTTLE_EMPTY) {
            // Player emptied a bottle into the cauldron — check if it was purified
            if (e.getEntity() instanceof Player p) {
                ItemStack held = p.getInventory().getItemInMainHand();
                boolean pure = isPurified(held);
                cauldronPurity.put(loc, pure);
                persistCauldronPurity(loc, pure);
            }
        } else if (reason == ChangeReason.UNKNOWN) {
            // Unrecognised reason — treat as acid
            cauldronPurity.put(loc, false);
            persistCauldronPurity(loc, false);
        } else if (e.getNewLevel() == 0) {
            // Any other reason that empties the cauldron (including BUCKET_FILL taking the last water)
            cauldronPurity.remove(loc);
            evictCauldronPurity(loc);
        }
    }

    /**
     * Intercept bottle-filling from water blocks and water cauldrons to give
     * acid or purified water bottles instead of the vanilla water bottle.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onBottleFill(PlayerInteractEvent e) {
        if (!addon.getSettings().isPurifiedWaterEnabled()) return;

        Player player = e.getPlayer();
        if (!isAcidIslandWorld(player.getWorld())) return;

        ItemStack item = e.getItem();
        if (item == null || item.getType() != Material.GLASS_BOTTLE) return;

        // Paper fires bottle-fill-from-water as RIGHT_CLICK_AIR with getClickedBlock() == null
        // because water is a fluid, not a solid block. Fall back to a ray-cast that includes fluids.
        Block block = e.getClickedBlock();
        if (block == null) {
            block = player.getTargetBlockExact(5, FluidCollisionMode.ALWAYS);
        }
        if (block == null) return;

        if (block.getType() == Material.WATER) {
            // Let vanilla fill the bottle — client prediction stays in sync.
            // Snapshot which slots already hold water bottles; on the next tick we find the
            // newly-filled vanilla water bottle and swap it for an acid bottle.
            PlayerInventory inv = player.getInventory();
            Set<Integer> preExisting = new HashSet<>();
            for (int i = 0; i < inv.getSize(); i++) {
                if (isWaterBottle(inv.getItem(i))) preExisting.add(i);
            }
            Bukkit.getScheduler().runTask(addon.getPlugin(), () -> {
                for (int i = 0; i < inv.getSize(); i++) {
                    if (preExisting.contains(i) || !isWaterBottle(inv.getItem(i))) continue;

                    ItemFillWithAcidEvent fillEvent = new ItemFillWithAcidEvent(
                            getIsland(player).orElse(null), player, makeAcidBottle());
                    Bukkit.getPluginManager().callEvent(fillEvent);
                    if (!fillEvent.isCancelled()) {
                        inv.setItem(i, makeAcidBottle());
                    }
                    break;
                }
            });

        } else if (block.getType() == Material.WATER_CAULDRON) {
            if (e.isCancelled()) return; // respect another plugin's cancellation
            // Filling from cauldron
            boolean pure = cauldronPurity.getOrDefault(block.getLocation(), false);
            ItemStack result = pure ? makePurifiedBottle() : makeAcidBottle();

            if (!pure) {
                ItemFillWithAcidEvent fillEvent = new ItemFillWithAcidEvent(
                        getIsland(player).orElse(null), player, result);
                Bukkit.getPluginManager().callEvent(fillEvent);
                if (fillEvent.isCancelled()) return;
            }

            e.setCancelled(true);
            consumeBottleAndGive(player, result);
            decrementCauldron(block);
        }
    }

    /**
     * When a player fills a water bucket in the acid world, tag it as acid water.
     */
    @EventHandler(ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent e) {
        if (!addon.getSettings().isPurifiedWaterEnabled()) return;
        if (!isAcidIslandWorld(e.getPlayer().getWorld())) return;

        ItemStack result = e.getItemStack();
        if (result == null || result.getType() != Material.WATER_BUCKET) return;

        Player player = e.getPlayer();

        // A purified cauldron yields a purified bucket; everything else is acid.
        boolean purified = e.getBlock().getType() == Material.WATER_CAULDRON
                && cauldronPurity.getOrDefault(e.getBlock().getLocation(), false);

        if (purified) {
            e.setItemStack(makePurifiedBucket());
        } else {
            ItemFillWithAcidEvent fillEvent = new ItemFillWithAcidEvent(
                    getIsland(player).orElse(null), player, makeAcidBucket());
            Bukkit.getPluginManager().callEvent(fillEvent);
            if (fillEvent.isCancelled()) return;
            e.setItemStack(makeAcidBucket());
        }
    }

    /**
     * When a player drinks a purified water bottle, apply a configurable health boost.
     * Vanilla handles returning the glass bottle; we only need to add the heal.
     */
    @EventHandler(ignoreCancelled = true)
    public void onDrinkPurified(PlayerItemConsumeEvent e) {
        if (!addon.getSettings().isPurifiedWaterEnabled()) return;
        ItemStack item = e.getItem();
        if (item.getType() != Material.POTION || !isPurified(item)) return;

        Player player = e.getPlayer();
        double healAmount = addon.getSettings().getPurifiedWaterHeal();
        PlayerDrinkPurifiedWaterEvent drinkEvent = new PlayerDrinkPurifiedWaterEvent(
                getIsland(player).orElse(null), player, healAmount);
        Bukkit.getPluginManager().callEvent(drinkEvent);
        if (drinkEvent.isCancelled()) return;

        double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        player.setHealth(Math.min(maxHealth, player.getHealth() + drinkEvent.getHealAmount()));
    }

    /**
     * When a water bottle or water bucket finishes smelting, mark the output as purified.
     * Non-water potions cancel the smelt so other potions are not converted.
     */
    @EventHandler(ignoreCancelled = true)
    public void onFurnaceSmelt(FurnaceSmeltEvent e) {
        if (!addon.getSettings().isPurifiedWaterEnabled()) return;
        ItemStack source = e.getSource();

        if (source.getType() == Material.POTION) {
            if (!isWaterBottle(source)) {
                // Not a water bottle — cancel so other potions are not consumed
                e.setCancelled(true);
                return;
            }
            e.setResult(makePurifiedBottle());
        } else if (source.getType() == Material.WATER_BUCKET
                && addon.getSettings().isPurifiedBucketFurnaceEnabled()) {
            e.setResult(makePurifiedBucket());
        }
    }

    /**
     * When coal is used as a brewing ingredient, purify any water bottles in the stand.
     */
    @EventHandler(ignoreCancelled = true)
    public void onBrew(BrewEvent e) {
        if (!addon.getSettings().isPurifiedWaterEnabled()) return;
        BrewerInventory inv = e.getContents();
        ItemStack ingredient = inv.getIngredient();
        if (ingredient == null || ingredient.getType() != Material.COAL) return;

        for (int i = 0; i < 3; i++) {
            ItemStack slot = inv.getItem(i);
            if (isWaterBottle(slot)) {
                inv.setItem(i, makePurifiedBottle());
            }
        }
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private void consumeBottleAndGive(Player player, ItemStack result) {
        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getAmount() == 1) {
            player.getInventory().setItemInMainHand(result);
        } else {
            held.setAmount(held.getAmount() - 1);
            player.getInventory().addItem(result);
        }
    }

    private void decrementCauldron(Block block) {
        if (block.getBlockData() instanceof Levelled levelled) {
            int newLevel = levelled.getLevel() - 1;
            if (newLevel <= 0) {
                block.setType(Material.CAULDRON);
                cauldronPurity.remove(block.getLocation());
            } else {
                levelled.setLevel(newLevel);
                block.setBlockData(levelled);
            }
        }
    }

    private Optional<Island> getIsland(Player player) {
        return addon.getIslands().getIslandAt(player.getLocation());
    }

    /**
     * @return {@code true} if the purified water mechanic should operate in the given world.
     * The mechanic always runs in the AcidIsland overworld. It also runs in the addon's Nether
     * and End worlds (whether they are island or vanilla) when the per-dimension config toggle
     * is enabled.
     */
    private boolean isAcidIslandWorld(World world) {
        if (world == null) return false;
        if (world.equals(addon.getOverWorld())) return true;
        AISettings s = addon.getSettings();
        if (world.equals(addon.getNetherWorld()) && s.isPurifiedWaterNetherEnabled()) {
            return true;
        }
        if (world.equals(addon.getEndWorld()) && s.isPurifiedWaterEndEnabled()) {
            return true;
        }
        return false;
    }

    /**
     * Returns {@code true} if there is a downward-pointing dripstone stalactite tip
     * within 12 blocks directly above the given block.
     * Used to distinguish dripstone fills (purified) from rain fills (acid).
     */
    private boolean hasDripstoneStalactiteAbove(Block block) {
        for (int i = 1; i <= 12; i++) {
            Block above = block.getRelative(BlockFace.UP, i);
            if (above.getType() == Material.POINTED_DRIPSTONE
                    && above.getBlockData() instanceof PointedDripstone ds
                    && ds.getVerticalDirection() == BlockFace.DOWN) {
                return true;
            }
        }
        return false;
    }

    private void loadCauldronData() {
        for (String key : cauldronYaml.getKeys(false)) {
            String[] parts = key.split(",");
            if (parts.length != 4) continue;
            try {
                World world = Bukkit.getWorld(parts[0]);
                if (world == null) continue;
                Location loc = new Location(world,
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3]));
                cauldronPurity.put(loc, cauldronYaml.getBoolean(key));
            } catch (NumberFormatException ignored) {
                // Skip malformed entries
            }
        }
    }

    private void persistCauldronPurity(Location loc, boolean purified) {
        if (cauldronDataFile == null || loc.getWorld() == null) return;
        String key = loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
        cauldronYaml.set(key, purified);
        saveCauldronYaml();
    }

    private void evictCauldronPurity(Location loc) {
        if (cauldronDataFile == null || loc.getWorld() == null) return;
        String key = loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
        cauldronYaml.set(key, null);
        saveCauldronYaml();
    }

    private void saveCauldronYaml() {
        try {
            cauldronDataFile.getParentFile().mkdirs();
            cauldronYaml.save(cauldronDataFile);
        } catch (IOException ex) {
            addon.logError("Could not save cauldron purity data: " + ex.getMessage());
        }
    }

    /**
     * @return the cauldron purity map (package-private for testing)
     */
    Map<Location, Boolean> getCauldronPurity() {
        return cauldronPurity;
    }
}
