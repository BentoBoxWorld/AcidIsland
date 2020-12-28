package world.bentobox.acidisland.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import world.bentobox.bentobox.api.events.IslandBaseEvent;
import world.bentobox.bentobox.database.objects.Island;

/**
 * Fired when a player drinks acid and... DIES
 * @author Poslovitch
 * @since 1.0
 * @deprecated - never fired
 */
@Deprecated
public class PlayerDrinkAcidEvent extends IslandBaseEvent {

    private final Player player;
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

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
