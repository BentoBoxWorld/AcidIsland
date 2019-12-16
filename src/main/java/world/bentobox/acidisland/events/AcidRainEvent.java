package world.bentobox.acidisland.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffectType;

/**
 * This event is fired when a player is going to be burned by acid rain
 *
 * @author tastybento
 *
 */
public class AcidRainEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private double rainDamage;
    private final double protection;
    /**
     * @since 1.9.1
     */
    private List<PotionEffectType> potionEffects;

    private boolean cancelled;


    /**
     * @param player
     * @param rainDamage
     * @param protection
     */
    public AcidRainEvent(Player player, double rainDamage, double protection, List<PotionEffectType> potionEffects) {
        this.player = player;
        this.rainDamage = rainDamage;
        this.protection = protection;
        this.potionEffects = potionEffects;
    }

    /**
     * @return the player being damaged by acid rain
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @param player the player to set
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Get the amount of damage caused
     * @return the rainDamage
     */
    public double getRainDamage() {
        return rainDamage;
    }

    /**
     * Get the amount the damage was reduced for this player due to armor, etc.
     * @return the protection
     */
    public double getProtection() {
        return protection;
    }

    /**
     * @param rainDamage the rainDamage to set
     */
    public void setRainDamage(double rainDamage) {
        this.rainDamage = rainDamage;
    }

    /**
     * Returns the potion effects that will be applied to the player.
     * @return list of potion effect types
     * @since 1.9.1
     */
    public List<PotionEffectType> getPotionEffects() {
        return potionEffects;
    }

    /**
     *
     * @param potionEffects the potionEffects to set
     * @since 1.9.1
     */
    public void setPotionEffects(List<PotionEffectType> potionEffects) {
        this.potionEffects = potionEffects;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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
