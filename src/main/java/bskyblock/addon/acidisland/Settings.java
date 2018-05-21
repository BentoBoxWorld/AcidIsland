package bskyblock.addon.acidisland;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 * A lot of placeholders right now in here...
 * @author tastybento
 *
 */
public class Settings {

    public boolean isNetherGenerate() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isNetherIslands() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isEndGenerate() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isEndIslands() {
        // TODO Auto-generated method stub
        return false;
    }

    public int getSeaHeight() {
        return 125;
    }

    public boolean isNetherRoof() {
        // TODO Auto-generated method stub
        return false;
    }

    public String getWorldName() {
        return "AcidIsland";
    }

    public int getRainDamage() {
        return 1;
    }
    
    public int getAcidDamage() {
        return 1;
    }
    
    public boolean getDamageOps() {
        return true;
    }
    
    public List<PotionEffectType> getAcidDamageType() {
        List<PotionEffectType> result = new ArrayList<>();
        result.add(PotionEffectType.CONFUSION);
        return result;
    }

    public boolean getHelmetProtection() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean getFullArmorProtection() {
        // TODO Auto-generated method stub
        return false;
    }

    public List<ItemStack> getChestItems() {
        // TODO Auto-generated method stub
        List<ItemStack> result = new ArrayList<>();
        return result;
    }
}
