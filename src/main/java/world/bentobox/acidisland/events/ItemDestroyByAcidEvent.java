package world.bentobox.acidisland.events;

import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when an item (on the ground) gets destroyed by acid
 * @author Poslovitch
 * @since 1.0
 */
public class ItemDestroyByAcidEvent extends Event {

    private final Item item;
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ItemDestroyByAcidEvent(Item item) {
        this.item = item;
    }

    /**
     * Gets the item which is getting destroyed by Acid
     * @return the destroyed item
     */
    public Item getItem() {
        return item;
    }
}
