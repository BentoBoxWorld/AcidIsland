package world.bentobox.acidisland.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when an entity (items excluded) receives damage from acid
 * @author Poslovitch
 * @since 1.0
 */
public class EntityDamageByAcidEvent extends Event {

    private final Entity entity;
    private double damage;

    public enum Acid { RAIN, WATER }
    private final Acid cause;
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public EntityDamageByAcidEvent(Entity entity, double damage, Acid cause) {
        this.entity = entity;
        this.damage = damage;
        this.cause = cause;
    }

    /**
     * Gets the Entity who is receiving Acid
     * @return the damaged Entity
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Gets the amount of damage that is applied to the Entity
     * @return the amount of damage caused by the acid
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Sets the amount of damage that will be applied to the entity
     * @param damage - the amount of damage caused by the acid
     */
    public void setDamage(double damage) {
        this.damage = damage;
    }

    /**
     * Gets the cause of the acid damage
     * @return the cause of the acid damage
     */
    public Acid getCause() {
        return cause;
    }
}
