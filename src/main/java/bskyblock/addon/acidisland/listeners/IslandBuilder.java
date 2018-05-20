package bskyblock.addon.acidisland.listeners;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Chest;

import bskyblock.addon.acidisland.AcidIsland;
import us.tastybento.bskyblock.api.events.island.IslandEvent.IslandCreateEvent;
import us.tastybento.bskyblock.api.events.island.IslandEvent.IslandResetEvent;
import us.tastybento.bskyblock.api.user.User;
import us.tastybento.bskyblock.database.objects.Island;

public class IslandBuilder implements Listener {
    private static final String PLAYER_PLACEHOLDER = "[player]";
    private AcidIsland addon;

    /**
     * @param addon
     */
    public IslandBuilder(AcidIsland addon) {
        this.addon = addon;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onIslandCreate(final IslandCreateEvent event) {
        if (addon.isEnabled()) {
            event.setCancelled(true);
            generateAcidIslandBlocks(event.getIsland(), addon.getAiw().getOverWorld());
            if (addon.getSettings().isNetherGenerate() && addon.getSettings().isNetherIslands()) {
                generateAcidIslandBlocks(event.getIsland(), addon.getAiw().getNetherWorld());
            }
            if (addon.getSettings().isEndGenerate() && addon.getSettings().isEndIslands()) {
                generateAcidIslandBlocks(event.getIsland(), addon.getAiw().getEndWorld());
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onIslandReset(final IslandResetEvent event) {
        if (addon.isEnabled()) {
            event.setCancelled(true);
            generateAcidIslandBlocks(event.getIsland(), addon.getAiw().getOverWorld());
            if (addon.getSettings().isNetherGenerate() && addon.getSettings().isNetherIslands()) {
                generateAcidIslandBlocks(event.getIsland(), addon.getAiw().getNetherWorld());
            }
            if (addon.getSettings().isEndGenerate() && addon.getSettings().isEndIslands()) {
                generateAcidIslandBlocks(event.getIsland(), addon.getAiw().getEndWorld());
            }
        }
    }

    /**
     * Creates the AcidIsland default island block by block
     */
    private void generateAcidIslandBlocks(Island island, World world) {
        // AcidIsland
        // Build island layer by layer
        // Start from the base
        // half sandstone; half sand
        int x = island.getCenter().getBlockX();
        int z = island.getCenter().getBlockZ();
        int islandHeight = island.getCenter().getBlockY();

        int y = 0;
        for (int x_space = x - 4; x_space <= x + 4; x_space++) {
            for (int z_space = z - 4; z_space <= z + 4; z_space++) {
                Block b = world.getBlockAt(x_space, y, z_space);
                b.setType(Material.BEDROCK);
            }
        }
        for (y = 1; y < islandHeight + 5; y++) {
            for (int x_space = x - 4; x_space <= x + 4; x_space++) {
                for (int z_space = z - 4; z_space <= z + 4; z_space++) {
                    Block b = world.getBlockAt(x_space, y, z_space);
                    if (y < (islandHeight / 2)) {
                        b.setType(Material.SANDSTONE);
                    } else {
                        b.setType(Material.SAND);
                    }
                }
            }
        }
        // Then cut off the corners to make it round-ish
        for (y = 0; y < islandHeight + 5; y++) {
            for (int x_space = x - 4; x_space <= x + 4; x_space += 8) {
                for (int z_space = z - 4; z_space <= z + 4; z_space += 8) {
                    Block b = world.getBlockAt(x_space, y, z_space);
                    b.setType(Material.STATIONARY_WATER);
                }
            }
        }
        // Add some grass
        for (y = islandHeight + 4; y < islandHeight + 5; y++) {
            for (int x_space = x - 2; x_space <= x + 2; x_space++) {
                for (int z_space = z - 2; z_space <= z + 2; z_space++) {
                    Block blockToChange = world.getBlockAt(x_space, y, z_space);
                    blockToChange.setType(Material.GRASS);
                }
            }
        }
        // Place bedrock - MUST be there (ensures island are not
        // overwritten
        Block b = world.getBlockAt(x, islandHeight, z);
        b.setType(Material.BEDROCK);
        // Then add some more dirt in the classic shape
        y = islandHeight + 3;
        for (int x_space = x - 2; x_space <= x + 2; x_space++) {
            for (int z_space = z - 2; z_space <= z + 2; z_space++) {
                b = world.getBlockAt(x_space, y, z_space);
                b.setType(Material.DIRT);
            }
        }
        b = world.getBlockAt(x - 3, y, z);
        b.setType(Material.DIRT);
        b = world.getBlockAt(x + 3, y, z);
        b.setType(Material.DIRT);
        b = world.getBlockAt(x, y, z - 3);
        b.setType(Material.DIRT);
        b = world.getBlockAt(x, y, z + 3);
        b.setType(Material.DIRT);
        y = islandHeight + 2;
        for (int x_space = x - 1; x_space <= x + 1; x_space++) {
            for (int z_space = z - 1; z_space <= z + 1; z_space++) {
                b = world.getBlockAt(x_space, y, z_space);
                b.setType(Material.DIRT);
            }
        }
        b = world.getBlockAt(x - 2, y, z);
        b.setType(Material.DIRT);
        b = world.getBlockAt(x + 2, y, z);
        b.setType(Material.DIRT);
        b = world.getBlockAt(x, y, z - 2);
        b.setType(Material.DIRT);
        b = world.getBlockAt(x, y, z + 2);
        b.setType(Material.DIRT);
        y = islandHeight + 1;
        b = world.getBlockAt(x - 1, y, z);
        b.setType(Material.DIRT);
        b = world.getBlockAt(x + 1, y, z);
        b.setType(Material.DIRT);
        b = world.getBlockAt(x, y, z - 1);
        b.setType(Material.DIRT);
        b = world.getBlockAt(x, y, z + 1);
        b.setType(Material.DIRT);

        // Add island items
        y = islandHeight;
        // Add tree (natural)
        Location treeLoc = new Location(world, x, y + 5D, z);
        world.generateTree(treeLoc, TreeType.ACACIA);

        // Place a helpful sign in front of player
        placeSign(island.getOwner(), world, x, islandHeight + 5, z + 3);
        // Place the chest - no need to use the safe spawn function
        // because we
        // know what this island looks like
        placeChest(world, x, islandHeight + 5, z + 1);
    }

    private void placeSign(UUID playerUUID, World world, int x, int y, int z) {
        Block blockToChange = world.getBlockAt(x, y, z);
        blockToChange.setType(Material.SIGN_POST);
        if (playerUUID != null) {
            Sign sign = (Sign) blockToChange.getState();
            User user = User.getInstance(playerUUID);

            // Sets the lines of the sign
            sign.setLine(0, user.getTranslation("new-island.sign.line0", PLAYER_PLACEHOLDER, user.getName()));
            sign.setLine(1, user.getTranslation("new-island.sign.line1", PLAYER_PLACEHOLDER, user.getName()));
            sign.setLine(2, user.getTranslation("new-island.sign.line2", PLAYER_PLACEHOLDER, user.getName()));
            sign.setLine(3, user.getTranslation("new-island.sign.line3", PLAYER_PLACEHOLDER, user.getName()));

            ((org.bukkit.material.Sign) sign.getData()).setFacingDirection(BlockFace.NORTH);
            sign.update();
        }
    }

    private void placeChest(World world, int x, int y, int z) {
        // Fill the chest and orient it correctly
        Block blockToChange = world.getBlockAt(x, y, z);
        blockToChange.setType(Material.CHEST);
        BlockState state = blockToChange.getState();
        Chest chest = new Chest(BlockFace.SOUTH);
        state.setData(chest);
        state.update();
        InventoryHolder chestBlock = (InventoryHolder) state;
        for (ItemStack item: addon.getSettings().getChestItems()) {
            chestBlock.getInventory().addItem(item);
        }
    }
}
