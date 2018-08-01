package bentobox.addon.acidisland.events;

import org.bukkit.entity.Item;

import world.bentobox.bentobox.api.events.IslandBaseEvent;
import world.bentobox.bentobox.database.objects.Island;

/**
 * Fired when an item (on the ground) gets destroyed by acid
 * @author Poslovitch
 * @since 1.0
 */
public class ItemDestroyByAcidEvent extends IslandBaseEvent {
    private final Item item;

    public ItemDestroyByAcidEvent(Island island, Item item) {
        super(island);
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
