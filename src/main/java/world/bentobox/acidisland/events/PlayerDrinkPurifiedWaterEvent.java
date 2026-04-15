package world.bentobox.acidisland.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import world.bentobox.bentobox.api.events.IslandBaseEvent;
import world.bentobox.bentobox.database.objects.Island;

/**
 * Fired when a player drinks purified water from a bottle.
 * Cancel this event to prevent the healing from being applied.
 * @author tastybento
 * @since 1.21
 */
public class PlayerDrinkPurifiedWaterEvent extends IslandBaseEvent implements Cancellable {

    private final Player player;
    private double healAmount;
    private boolean cancelled;
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public PlayerDrinkPurifiedWaterEvent(Island island, Player player, double healAmount) {
        super(island);
        this.player = player;
        this.healAmount = healAmount;
    }

    /**
     * Gets the player who drank purified water
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the amount of health that will be restored
     * @return heal amount in half-hearts
     */
    public double getHealAmount() {
        return healAmount;
    }

    /**
     * Sets the amount of health that will be restored
     * @param healAmount new heal amount in half-hearts
     */
    public void setHealAmount(double healAmount) {
        this.healAmount = healAmount;
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
