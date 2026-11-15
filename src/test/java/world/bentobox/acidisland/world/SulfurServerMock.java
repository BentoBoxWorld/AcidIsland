package world.bentobox.acidisland.world;

import static org.mockito.Mockito.mock;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.PotentSulfur;
import org.bukkit.block.data.type.Speleothem;
import org.mockbukkit.mockbukkit.ServerMock;

/**
 * A {@link ServerMock} that can create block data for the 26.2+ sulfur vent blocks.
 * <p>
 * MockBukkit 4.119.0 has these blocks in its registry but not their block data:
 * <ul>
 * <li>{@code potent_sulfur[potent_sulfur_state=dormant]} throws {@code Unknown block data key}</li>
 * <li>{@code sulfur_spike[waterlogged=true]} throws {@code Can not apply block data key}, although
 * Paper types it as {@link Speleothem} (which is {@code Waterlogged}, like pointed dripstone)</li>
 * </ul>
 * This returns mocks of the real Paper interfaces for those two materials and defers
 * everything else to MockBukkit. Remove it once MockBukkit supports them.
 */
public class SulfurServerMock extends ServerMock {

    private final PotentSulfur potentSulfur = mock(PotentSulfur.class);
    private final Speleothem sulfurSpike = mock(Speleothem.class);

    @Override
    public BlockData createBlockData(Material material, String data) {
        if (material == Material.POTENT_SULFUR) {
            return potentSulfur;
        }
        if (material == Material.SULFUR_SPIKE) {
            return sulfurSpike;
        }
        return super.createBlockData(material, data);
    }

    /**
     * @return the block data returned for every potent sulfur request
     */
    public PotentSulfur getPotentSulfur() {
        return potentSulfur;
    }
}
