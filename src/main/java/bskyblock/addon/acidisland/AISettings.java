package bskyblock.addon.acidisland;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import us.tastybento.bskyblock.api.configuration.WorldSettings;

/**
 * A lot of placeholders right now in here...
 * @author tastybento
 *
 */
public class AISettings implements WorldSettings {

    public int getAcidDamage() {
        return 1;
    }

    public List<PotionEffectType> getAcidDamageType() {
        List<PotionEffectType> result = new ArrayList<>();
        result.add(PotionEffectType.CONFUSION);
        return result;
    }

    public List<ItemStack> getChestItems() {
        // TODO Auto-generated method stub
        List<ItemStack> result = new ArrayList<>();
        return result;
    }

    public boolean getDamageOps() {
        return true;
    }

    @Override
    public Map<EntityType, Integer> getEntityLimits() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean getFullArmorProtection() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean getHelmetProtection() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getIslandDistance() {
        // TODO Auto-generated method stub
        return 60;
    }
    
    @Override
    public int getIslandHeight() {
        // TODO Auto-generated method stub
        return 50;
    }
    
    @Override
    public int getIslandProtectionRange() {
        // TODO Auto-generated method stub
        return 50;
    }
    
    @Override
    public int getIslandStartX() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getIslandStartZ() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getIslandXOffset() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getIslandZOffset() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMaxIslands() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getNetherSpawnRadius() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getRainDamage() {
        return 1;
    }

    public int getSeaHeight() {
        return 55;
    }

    @Override
    public Map<String, Integer> getTileEntityLimits() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getWorldName() {
        return "AcidIsland-world";
    }

    @Override
    public boolean isEndGenerate() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEndIslands() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isNetherGenerate() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isNetherIslands() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isNetherRoof() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isNetherTrees() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getFriendlyName() {
        return "AcidIsland";
    }

    @Override
    public boolean isDragonSpawn() {
        // TODO Auto-generated method stub
        return false;
    }


}
