package world.bentobox.acidisland.world;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.eclipse.jdt.annotation.NonNull;

import world.bentobox.acidisland.AcidIsland;

/**
 * Randomly places a sulfur vent just below the sea surface in one of four natural
 * shapes. Every vent has at least one potent sulfur cap over a magma block, which
 * bubbles, gasses the surface with nausea, and periodically erupts as a geyser.
 * Minecraft 26.2+ only - does nothing on older servers.
 * <p>
 * Vents are placed by a populator rather than in
 * {@link ChunkGeneratorWorld#generateNoise} because populators run after vanilla
 * decoration. From Minecraft 26.3, biome features such as the sulfur caves
 * decorations overwrite blocks set during noise generation, which replaced the
 * potent sulfur cap and orphaned its block entity.
 */
public class SulfurVentPopulator extends BlockPopulator {

    // Only exist on Minecraft 26.2 and later; null on older servers, which disables sulfur vents
    private static final Material POTENT_SULFUR = Material.getMaterial("POTENT_SULFUR");
    private static final Material SULFUR = Material.getMaterial("SULFUR");
    private static final Material SULFUR_SPIKE = Material.getMaterial("SULFUR_SPIKE");
    // Depth of the vent cap below the sea surface. Must be 4 or less for the potent
    // sulfur to gas the surface, and sets the geyser height (5 x water depth)
    private static final int VENT_DEPTH = 3;
    // Offsets to the four horizontal neighbours
    private static final int[][] SIDES = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private final AcidIsland addon;
    // Generator-placed potent sulfur keeps its default 'dry' state and never activates
    // because worldgen blocks get no placement update, so the cap is pre-set to
    // 'dormant' (water above, magma below), which starts the eruption cycle
    private final BlockData ventCap;
    // Spikes are placed waterlogged so they do not leave air pockets in the sea
    private final BlockData ventSpike;

    /**
     * @param addon - addon
     */
    public SulfurVentPopulator(AcidIsland addon) {
        this.addon = addon;
        ventCap = POTENT_SULFUR == null ? null : POTENT_SULFUR.createBlockData("[potent_sulfur_state=dormant]");
        ventSpike = SULFUR_SPIKE == null ? null : SULFUR_SPIKE.createBlockData("[waterlogged=true]");
    }

    /**
     * @return true if this server has the blocks needed to make sulfur vents
     */
    public static boolean isSupported() {
        return POTENT_SULFUR != null && SULFUR != null;
    }

    @Override
    public void populate(@NonNull WorldInfo worldInfo, @NonNull Random random, int chunkX, int chunkZ,
            @NonNull LimitedRegion region) {
        int capY = addon.getSettings().getSeaHeight() - VENT_DEPTH;
        if (ventCap == null || SULFUR == null || !worldInfo.getEnvironment().equals(Environment.NORMAL)
                || !addon.getSettings().getWaterBlock().equals(Material.WATER)
                || capY - 4 <= worldInfo.getMinHeight()
                || random.nextInt(100) >= Math.clamp(addon.getSettings().getSulfurVentChance(), 0, 100)) {
            return;
        }
        // Keep one block away from the chunk edge so shoulder blocks stay in this chunk
        int x = (chunkX << 4) + 1 + random.nextInt(14);
        int z = (chunkZ << 4) + 1 + random.nextInt(14);
        switch (random.nextInt(4)) {
        case 0 -> ventChimney(region, random, x, capY, z);
        case 1 -> ventMound(region, random, x, capY, z);
        case 2 -> ventTwin(region, random, x, capY, z);
        default -> ventCrag(region, random, x, capY, z);
        }
    }

    /**
     * Places a potent sulfur cap over a magma block on a sulfur base - the working
     * heart of every vent
     */
    private void placeCap(LimitedRegion region, int x, int capY, int z) {
        region.setBlockData(x, capY, z, ventCap);
        region.setType(x, capY - 1, z, Material.MAGMA_BLOCK);
        region.setType(x, capY - 2, z, SULFUR);
    }

    /**
     * A slim chimney with a single lopsided sulfur shoulder
     */
    private void ventChimney(LimitedRegion region, Random random, int x, int capY, int z) {
        placeCap(region, x, capY, z);
        int[] side = SIDES[random.nextInt(SIDES.length)];
        region.setType(x + side[0], capY - 1, z + side[1], SULFUR);
    }

    /**
     * A rounded sulfur mound with the cap poking out of the top
     */
    private void ventMound(LimitedRegion region, Random random, int x, int capY, int z) {
        placeCap(region, x, capY, z);
        for (int[] side : SIDES) {
            if (random.nextInt(4) < 3) {
                region.setType(x + side[0], capY - 1, z + side[1], SULFUR);
            }
            region.setType(x + side[0], capY - 2, z + side[1], SULFUR);
        }
        // Ragged diagonal skirt
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dz = -1; dz <= 1; dz += 2) {
                if (random.nextBoolean()) {
                    region.setType(x + dx, capY - 2, z + dz, SULFUR);
                }
            }
        }
    }

    /**
     * Two caps at different heights on a shared sulfur outcrop; the deeper cap sits
     * under four water blocks so its geyser erupts higher
     */
    private void ventTwin(LimitedRegion region, Random random, int x, int capY, int z) {
        placeCap(region, x, capY, z);
        int dx = random.nextBoolean() ? 1 : -1;
        int dz = random.nextBoolean() ? 1 : -1;
        placeCap(region, x + dx, capY - 1, z + dz);
        // Join the two stacks at their corners
        region.setType(x + dx, capY - 2, z, SULFUR);
        region.setType(x, capY - 2, z + dz, SULFUR);
    }

    /**
     * A craggy vent with sulfur spikes growing from its shoulders
     */
    private void ventCrag(LimitedRegion region, Random random, int x, int capY, int z) {
        placeCap(region, x, capY, z);
        for (int[] side : SIDES) {
            if (random.nextBoolean()) {
                region.setType(x + side[0], capY - 1, z + side[1], SULFUR);
                if (ventSpike != null && random.nextBoolean()) {
                    region.setBlockData(x + side[0], capY, z + side[1], ventSpike);
                }
            }
        }
    }
}
