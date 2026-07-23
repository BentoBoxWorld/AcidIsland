package world.bentobox.acidisland.events;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Fired when an item in the water around a sulfur vent is about to be consumed
 * as an offering - either by the offerings task or by acid item destruction
 * dissolving it within the vent's pool. Cancelling leaves the item floating in
 * the pool (acid destruction then proceeds as normal, if it applies).
 *
 * @author tastybento
 * @since 2.1.0
 */
public class GeyserSacrificeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Item item;
    private final Location vent;
    private final @Nullable String channel;
    private boolean cancelled;

    public GeyserSacrificeEvent(Item item, Location vent, @Nullable String channel) {
        this.item = item;
        this.vent = vent;
        this.channel = channel;
    }

    /**
     * @return the item being sacrificed
     */
    public Item getItem() {
        return item;
    }

    /**
     * @return the location of the vent's potent sulfur cap
     */
    public Location getVent() {
        return vent;
    }

    /**
     * @return the reward channel the offering biases, or null if it burns with no bias
     */
    @Nullable
    public String getChannel() {
        return channel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
