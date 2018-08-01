package bentobox.addon.acidisland.events;

import org.bukkit.entity.Player;

import world.bentobox.bentobox.api.events.IslandBaseEvent;
import world.bentobox.bentobox.database.objects.Island;

/**
 * Fired when a player drinks acid and... DIES
 * @author Poslovitch
 * @since 1.0
 */
public class PlayerDrinkAcidEvent extends IslandBaseEvent {
    private final Player player;

    public PlayerDrinkAcidEvent(Island island, Player player) {
        super(island);
        this.player = player;
    }

    /**
     * Gets the player which is getting killed by its stupid thirsty
     * @return the killed player
     */
    public Player getPlayer() {
        return player;
    }
}
