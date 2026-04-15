package world.bentobox.acidisland.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import world.bentobox.bentobox.api.events.IslandBaseEvent;
import world.bentobox.bentobox.database.objects.Island;

/**
 * Fired when a player drinks acid water from a bottle.
 * Cancel this event to prevent the damage from being applied.
 * @author Poslovitch
 * @since 1.0
 */
public class PlayerDrinkAcidEvent extends IslandBaseEvent implements Cancellable {

    private final Player player;
    private double damage;
    private boolean cancelled;
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public PlayerDrinkAcidEvent(Island island, Player player, double damage) {
        super(island);
        this.player = player;
        this.damage = damage;
    }

    /**
     * Gets the player who drank acid water
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the damage that will be applied to the player
     * @return damage amount
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Sets the damage that will be applied to the player
     * @param damage new damage amount
     */
    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
