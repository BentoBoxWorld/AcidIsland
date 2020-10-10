package world.bentobox.acidisland.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * This event is fired when a player is going to be burned by acid (not rain)
 *
 * @author tastybento
 *
 */
public class AcidEvent extends AbstractAcidEvent {

    private double totalDamage;

    /**
     * @param player - player
     * @param totalDamage - total damager
     * @param protection - protection given by armor
     * @param potionEffects - potion effects given to the player
     */
    public AcidEvent(Player player, double totalDamage, double protection, List<PotionEffectType> potionEffects) {
        super(player, protection, potionEffects);
        this.totalDamage = totalDamage;
    }

    /**
     * Get the amount of damage caused
     * @return the totalDamage
     */
    public double getTotalDamage() {
        return totalDamage;
    }

    /**
     * @param totalDamage to set
     */
    public void setTotalDamage(double totalDamage) {
        this.totalDamage = totalDamage;
    }

}
