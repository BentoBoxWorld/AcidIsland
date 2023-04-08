package world.bentobox.acidisland;


import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.Pladdon;

public class AcidIslandPladdon extends Pladdon {

    @Override
    public Addon getAddon() {
        return new AcidIsland();
    }
}
