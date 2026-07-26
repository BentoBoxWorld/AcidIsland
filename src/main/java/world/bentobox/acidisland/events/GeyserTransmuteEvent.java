package world.bentobox.acidisland.events;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired after a sulfur vent's eruption has spewed out the rewards transmuted
 * from the offerings sacrificed to it.
 *
 * @author tastybento
 * @since 2.1.0
 */
public class GeyserTransmuteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Location vent;
    private final List<Item> rewards;
    private final int offerings;

    public GeyserTransmuteEvent(Location vent, List<Item> rewards, int offerings) {
        this.vent = vent;
        this.rewards = rewards;
        this.offerings = offerings;
    }

    /**
     * @return the location of the vent's potent sulfur cap
     */
    public Location getVent() {
        return vent;
    }

    /**
     * @return the spawned reward item entities
     */
    public List<Item> getRewards() {
        return rewards;
    }

    /**
     * @return the number of items that were sacrificed to this vent
     */
    public int getOfferings() {
        return offerings;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
