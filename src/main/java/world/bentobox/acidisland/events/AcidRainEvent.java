package world.bentobox.acidisland.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffectType;

/**
 * This event is fired when a player is going to be burned by acid rain
 *
 * @author tastybento
 *
 */
public class AcidRainEvent extends AbstractAcidEvent {

    private double rainDamage;
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @param player - player
     * @param rainDamage - rain damage caused
     * @param protection - protection reducer to damage
     * @param potionEffects - potion effects to apply if acid rain affects player
     */
    public AcidRainEvent(Player player, double rainDamage, double protection, List<PotionEffectType> potionEffects) {
        super(player, protection, potionEffects);
        this.rainDamage = rainDamage;
    }

    /**
     * Get the amount of damage caused
     * @return the rainDamage
     */
    public double getRainDamage() {
        return rainDamage;
    }

    /**
     * @param rainDamage the rainDamage to set
     */
    public void setRainDamage(double rainDamage) {
        this.rainDamage = rainDamage;
    }

}
