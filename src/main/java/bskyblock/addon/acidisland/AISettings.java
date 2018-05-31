package bskyblock.addon.acidisland;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import us.tastybento.bskyblock.api.configuration.ConfigEntry;
import us.tastybento.bskyblock.api.configuration.ISettings;
import us.tastybento.bskyblock.api.configuration.StoreAt;
import us.tastybento.bskyblock.api.configuration.WorldSettings;
import us.tastybento.bskyblock.database.objects.adapters.Adapter;
import us.tastybento.bskyblock.database.objects.adapters.PotionEffectListAdapter;

/**
 * A lot of placeholders right now in here...
 * @author tastybento
 *
 */
@StoreAt(filename="config.yml", path="addons/BSkyBlock-AcidIsland") // Explicitly call out what name this should have.
public class AISettings implements ISettings<AISettings>, WorldSettings {
    
    private String uniqueId = "config";
    
    /*      WORLD       */
    @ConfigEntry(path = "world.friendly-name", needsReset = true)
    private String friendlyName = "AcidIsland";
    
    @ConfigEntry(path = "world.world-name", needsReset = true)
    private String worldName = "AcidIsland_world";

    @ConfigEntry(path = "world.distance-between-islands", needsReset = true)
    private int islandDistance = 200;

    @ConfigEntry(path = "world.protection-range", overrideOnChange = true)
    private int islandProtectionRange = 100;

    @ConfigEntry(path = "world.start-x", needsReset = true)
    private int islandStartX = 0;

    @ConfigEntry(path = "world.start-z", needsReset = true)
    private int islandStartZ = 0;

    @ConfigEntry(path = "world.sea-height")
    private int seaHeight = 55;

    @ConfigEntry(path = "world.island-height")
    private int islandHeight = 50;

    @ConfigEntry(path = "world.max-islands")
    private int maxIslands = -1;

    // Nether
    @ConfigEntry(path = "world.nether.generate")
    private boolean netherGenerate = true;

    @ConfigEntry(path = "world.nether.islands", needsReset = true)
    private boolean netherIslands = true;

    @ConfigEntry(path = "world.nether.trees")
    private boolean netherTrees = true;

    @ConfigEntry(path = "world.nether.roof")
    private boolean netherRoof = true;

    @ConfigEntry(path = "world.nether.spawn-radius")
    private int netherSpawnRadius = 32;

    // End
    @ConfigEntry(path = "world.end.generate")
    private boolean endGenerate = true;

    @ConfigEntry(path = "world.end.islands", needsReset = true)
    private boolean endIslands = true;
    
    @ConfigEntry(path = "world.end.dragon-spawn")
    private boolean dragonSpawn = false;
    
    // ---------------------------------------------

    /*      ACID        */

    @ConfigEntry(path = "acid.damage-op")
    private boolean acidDamageOp = false;

    @ConfigEntry(path = "acid.damage-chickens")
    private boolean acidDamageChickens = false;

    // Damage
    @ConfigEntry(path = "acid.damage.acid.player")
    private int acidDamage = 10;
    
    @ConfigEntry(path = "acid.damage.acid.monster")
    private int acidDamageMonster = 10;
    
    @ConfigEntry(path = "acid.damage.acid.animal")
    private int acidDamageAnimal = 10;
    
    @ConfigEntry(path = "acid.damage.acid.item")
    private int acidDestroyItemTime = 0;

    @ConfigEntry(path = "acid.damage.rain")
    private int acidRainDamage = 1;

    @ConfigEntry(path = "acid.damage.effects")
    @Adapter(PotionEffectListAdapter.class)
    private List<PotionEffectType> acidEffects = new ArrayList<>();

    @ConfigEntry(path = "acid.damage.protection.helmet")
    private boolean helmetProtection;
    
    @ConfigEntry(path = "acid.damage.protection.full-armor")
    private boolean fullArmorProtection;
    // ---------------------------------------------

    /*      ISLAND      */
    // Entities
    @ConfigEntry(path = "island.limits.entities")
    private Map<EntityType, Integer> entityLimits = new EnumMap<>(EntityType.class);
    @ConfigEntry(path = "island.limits.tile-entities")
    private Map<String, Integer> tileEntityLimits = new HashMap<>();

    
    @ConfigEntry(path = "island.max-team-size")
    private int maxTeamSize = 4;
    @ConfigEntry(path = "island.max-homes")
    private int maxHomes = 5;
    @ConfigEntry(path = "island.name.min-length")
    private int nameMinLength = 4;
    @ConfigEntry(path = "island.name.max-length")
    private int nameMaxLength = 20;
    @ConfigEntry(path = "island.invite-wait")
    private int inviteWait = 60;

    // Reset
    @ConfigEntry(path = "island.reset.reset-limit")
    private int resetLimit = -1;

    @ConfigEntry(path = "island.require-confirmation.reset")
    private boolean resetConfirmation = true;

    @ConfigEntry(path = "island.reset-wait")
    private long resetWait = 10L;

    @ConfigEntry(path = "island.reset.leavers-lose-reset")
    private boolean leaversLoseReset = false;

    @ConfigEntry(path = "island.reset.kicked-keep-inventory")
    private boolean kickedKeepInventory = false;

    // Remove mobs
    @ConfigEntry(path = "island.remove-mobs.on-login")
    private boolean removeMobsOnLogin = false;
    @ConfigEntry(path = "island.remove-mobs.on-island")
    private boolean removeMobsOnIsland = false;

    @ConfigEntry(path = "island.remove-mobs.whitelist")
    private List<String> removeMobsWhitelist = new ArrayList<>();

    @ConfigEntry(path = "island.make-island-if-none")
    private boolean makeIslandIfNone = false;

    @ConfigEntry(path = "island.immediate-teleport-on-island")
    private boolean immediateTeleportOnIsland = false;

    private boolean respawnOnIsland = true;

    private int islandXOffset;

    private int islandZOffset;
    
    /*      SCHEMATICS      */
    private List<String> companionNames = new ArrayList<>();

    @ConfigEntry(path = "island.chest-items")
    private List<ItemStack> chestItems = new ArrayList<>();

    private EntityType companionType = EntityType.COW;

    private boolean useOwnGenerator;

    public List<PotionEffectType> getAcidDamageType() {
        return acidEffects;
    }

    public List<ItemStack> getChestItems() {
        return chestItems;
    }

    @Override
    public Map<EntityType, Integer> getEntityLimits() {
        return entityLimits;
    }

    @Override
    public int getIslandDistance() {
        return islandDistance;
    }
    
    @Override
    public int getIslandHeight() {
        return islandHeight;
    }
    
    @Override
    public int getIslandProtectionRange() {
        return islandProtectionRange;
    }
    
    @Override
    public int getIslandStartX() {
        return islandStartX;
    }

    @Override
    public int getIslandStartZ() {
        return islandStartZ;
    }

    @Override
    public int getIslandXOffset() {
        return islandXOffset;
    }

    @Override
    public int getIslandZOffset() {
        return islandZOffset;
    }

    @Override
    public int getMaxIslands() {
        return maxIslands;
    }

    @Override
    public int getNetherSpawnRadius() {
        return netherSpawnRadius;
    }

    public int getAcidRainDamage() {
        return acidRainDamage;
    }

    public int getSeaHeight() {
        return seaHeight;
    }

    @Override
    public Map<String, Integer> getTileEntityLimits() {
        return tileEntityLimits;
    }

    @Override
    public String getWorldName() {
        return worldName;
    }

    @Override
    public boolean isEndGenerate() {
        return endGenerate;
    }

    @Override
    public boolean isEndIslands() {
        return endIslands;
    }

    @Override
    public boolean isNetherGenerate() {
        return netherGenerate;
    }

    @Override
    public boolean isNetherIslands() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isNetherRoof() {
        return netherRoof;
    }

    @Override
    public boolean isNetherTrees() {
        return netherTrees;
    }

    @Override
    public String getFriendlyName() {
        return friendlyName;
    }

    @Override
    public boolean isDragonSpawn() {
        return dragonSpawn;
    }

    @Override
    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    @Override
    public int getMaxHomes() {
        return maxHomes;
    }

    @Override
    public AISettings getInstance() {
        return this;
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
        
    }

    /**
     * @return the acidDamageOp
     */
    public boolean isAcidDamageOp() {
        return acidDamageOp;
    }

    /**
     * @return the acidDamage
     */
    public int getAcidDamage() {
        return acidDamage;
    }

    /**
     * @param acidDamageOp the acidDamageOp to set
     */
    public void setAcidDamageOp(boolean acidDamageOp) {
        this.acidDamageOp = acidDamageOp;
    }

    /**
     * @return the acidDamageChickens
     */
    public boolean isAcidDamageChickens() {
        return acidDamageChickens;
    }

    /**
     * @param acidDamageChickens the acidDamageChickens to set
     */
    public void setAcidDamageChickens(boolean acidDamageChickens) {
        this.acidDamageChickens = acidDamageChickens;
    }

    /**
     * @return the acidDestroyItemTime
     */
    public int getAcidDestroyItemTime() {
        return acidDestroyItemTime;
    }

    /**
     * @param acidDestroyItemTime the acidDestroyItemTime to set
     */
    public void setAcidDestroyItemTime(int acidDestroyItemTime) {
        this.acidDestroyItemTime = acidDestroyItemTime;
    }

    /**
     * @return the acidEffects
     */
    public List<PotionEffectType> getAcidEffects() {
        return acidEffects;
    }

    /**
     * @param acidEffects the acidEffects to set
     */
    public void setAcidEffects(List<PotionEffectType> acidEffects) {
        this.acidEffects = acidEffects;
    }

    /**
     * @return the nameMinLength
     */
    public int getNameMinLength() {
        return nameMinLength;
    }

    /**
     * @param nameMinLength the nameMinLength to set
     */
    public void setNameMinLength(int nameMinLength) {
        this.nameMinLength = nameMinLength;
    }

    /**
     * @return the nameMaxLength
     */
    public int getNameMaxLength() {
        return nameMaxLength;
    }

    /**
     * @param nameMaxLength the nameMaxLength to set
     */
    public void setNameMaxLength(int nameMaxLength) {
        this.nameMaxLength = nameMaxLength;
    }

    /**
     * @return the inviteWait
     */
    public int getInviteWait() {
        return inviteWait;
    }

    /**
     * @param inviteWait the inviteWait to set
     */
    public void setInviteWait(int inviteWait) {
        this.inviteWait = inviteWait;
    }

    /**
     * @return the resetLimit
     */
    public int getResetLimit() {
        return resetLimit;
    }

    /**
     * @param resetLimit the resetLimit to set
     */
    public void setResetLimit(int resetLimit) {
        this.resetLimit = resetLimit;
    }

    /**
     * @return the resetConfirmation
     */
    public boolean isResetConfirmation() {
        return resetConfirmation;
    }

    /**
     * @param resetConfirmation the resetConfirmation to set
     */
    public void setResetConfirmation(boolean resetConfirmation) {
        this.resetConfirmation = resetConfirmation;
    }

    /**
     * @return the resetWait
     */
    public long getResetWait() {
        return resetWait;
    }

    /**
     * @param resetWait the resetWait to set
     */
    public void setResetWait(long resetWait) {
        this.resetWait = resetWait;
    }

    /**
     * @return the leaversLoseReset
     */
    public boolean isLeaversLoseReset() {
        return leaversLoseReset;
    }

    /**
     * @param leaversLoseReset the leaversLoseReset to set
     */
    public void setLeaversLoseReset(boolean leaversLoseReset) {
        this.leaversLoseReset = leaversLoseReset;
    }

    /**
     * @return the kickedKeepInventory
     */
    public boolean isKickedKeepInventory() {
        return kickedKeepInventory;
    }

    /**
     * @param kickedKeepInventory the kickedKeepInventory to set
     */
    public void setKickedKeepInventory(boolean kickedKeepInventory) {
        this.kickedKeepInventory = kickedKeepInventory;
    }

    /**
     * @return the removeMobsOnLogin
     */
    public boolean isRemoveMobsOnLogin() {
        return removeMobsOnLogin;
    }

    /**
     * @param removeMobsOnLogin the removeMobsOnLogin to set
     */
    public void setRemoveMobsOnLogin(boolean removeMobsOnLogin) {
        this.removeMobsOnLogin = removeMobsOnLogin;
    }

    /**
     * @return the removeMobsOnIsland
     */
    public boolean isRemoveMobsOnIsland() {
        return removeMobsOnIsland;
    }

    /**
     * @param removeMobsOnIsland the removeMobsOnIsland to set
     */
    public void setRemoveMobsOnIsland(boolean removeMobsOnIsland) {
        this.removeMobsOnIsland = removeMobsOnIsland;
    }

    /**
     * @return the removeMobsWhitelist
     */
    public List<String> getRemoveMobsWhitelist() {
        return removeMobsWhitelist;
    }

    /**
     * @param removeMobsWhitelist the removeMobsWhitelist to set
     */
    public void setRemoveMobsWhitelist(List<String> removeMobsWhitelist) {
        this.removeMobsWhitelist = removeMobsWhitelist;
    }

    /**
     * @return the makeIslandIfNone
     */
    public boolean isMakeIslandIfNone() {
        return makeIslandIfNone;
    }

    /**
     * @param makeIslandIfNone the makeIslandIfNone to set
     */
    public void setMakeIslandIfNone(boolean makeIslandIfNone) {
        this.makeIslandIfNone = makeIslandIfNone;
    }

    /**
     * @return the immediateTeleportOnIsland
     */
    public boolean isImmediateTeleportOnIsland() {
        return immediateTeleportOnIsland;
    }

    /**
     * @param immediateTeleportOnIsland the immediateTeleportOnIsland to set
     */
    public void setImmediateTeleportOnIsland(boolean immediateTeleportOnIsland) {
        this.immediateTeleportOnIsland = immediateTeleportOnIsland;
    }

    /**
     * @return the respawnOnIsland
     */
    public boolean isRespawnOnIsland() {
        return respawnOnIsland;
    }

    /**
     * @param respawnOnIsland the respawnOnIsland to set
     */
    public void setRespawnOnIsland(boolean respawnOnIsland) {
        this.respawnOnIsland = respawnOnIsland;
    }

    /**
     * @return the companionNames
     */
    public List<String> getCompanionNames() {
        return companionNames;
    }

    /**
     * @param companionNames the companionNames to set
     */
    public void setCompanionNames(List<String> companionNames) {
        this.companionNames = companionNames;
    }

    /**
     * @return the companionType
     */
    public EntityType getCompanionType() {
        return companionType;
    }

    /**
     * @param companionType the companionType to set
     */
    public void setCompanionType(EntityType companionType) {
        this.companionType = companionType;
    }

    /**
     * @return the useOwnGenerator
     */
    public boolean isUseOwnGenerator() {
        return useOwnGenerator;
    }

    /**
     * @param useOwnGenerator the useOwnGenerator to set
     */
    public void setUseOwnGenerator(boolean useOwnGenerator) {
        this.useOwnGenerator = useOwnGenerator;
    }

    /**
     * @param friendlyName the friendlyName to set
     */
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    /**
     * @param worldName the worldName to set
     */
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    /**
     * @param islandDistance the islandDistance to set
     */
    public void setIslandDistance(int islandDistance) {
        this.islandDistance = islandDistance;
    }

    /**
     * @param islandProtectionRange the islandProtectionRange to set
     */
    public void setIslandProtectionRange(int islandProtectionRange) {
        this.islandProtectionRange = islandProtectionRange;
    }

    /**
     * @param islandStartX the islandStartX to set
     */
    public void setIslandStartX(int islandStartX) {
        this.islandStartX = islandStartX;
    }

    /**
     * @param islandStartZ the islandStartZ to set
     */
    public void setIslandStartZ(int islandStartZ) {
        this.islandStartZ = islandStartZ;
    }

    /**
     * @param seaHeight the seaHeight to set
     */
    public void setSeaHeight(int seaHeight) {
        this.seaHeight = seaHeight;
    }

    /**
     * @param islandHeight the islandHeight to set
     */
    public void setIslandHeight(int islandHeight) {
        this.islandHeight = islandHeight;
    }

    /**
     * @param maxIslands the maxIslands to set
     */
    public void setMaxIslands(int maxIslands) {
        this.maxIslands = maxIslands;
    }

    /**
     * @param netherGenerate the netherGenerate to set
     */
    public void setNetherGenerate(boolean netherGenerate) {
        this.netherGenerate = netherGenerate;
    }

    /**
     * @param netherIslands the netherIslands to set
     */
    public void setNetherIslands(boolean netherIslands) {
        this.netherIslands = netherIslands;
    }

    /**
     * @param netherTrees the netherTrees to set
     */
    public void setNetherTrees(boolean netherTrees) {
        this.netherTrees = netherTrees;
    }

    /**
     * @param netherRoof the netherRoof to set
     */
    public void setNetherRoof(boolean netherRoof) {
        this.netherRoof = netherRoof;
    }

    /**
     * @param netherSpawnRadius the netherSpawnRadius to set
     */
    public void setNetherSpawnRadius(int netherSpawnRadius) {
        this.netherSpawnRadius = netherSpawnRadius;
    }

    /**
     * @param endGenerate the endGenerate to set
     */
    public void setEndGenerate(boolean endGenerate) {
        this.endGenerate = endGenerate;
    }

    /**
     * @param endIslands the endIslands to set
     */
    public void setEndIslands(boolean endIslands) {
        this.endIslands = endIslands;
    }

    /**
     * @param dragonSpawn the dragonSpawn to set
     */
    public void setDragonSpawn(boolean dragonSpawn) {
        this.dragonSpawn = dragonSpawn;
    }

    /**
     * @param acidDamage the acidDamage to set
     */
    public void setAcidDamage(int acidDamage) {
        this.acidDamage = acidDamage;
    }

    /**
     * @param acidRainDamage the acidRainDamage to set
     */
    public void setAcidRainDamage(int acidRainDamage) {
        this.acidRainDamage = acidRainDamage;
    }

    /**
     * @param entityLimits the entityLimits to set
     */
    public void setEntityLimits(Map<EntityType, Integer> entityLimits) {
        this.entityLimits = entityLimits;
    }

    /**
     * @param tileEntityLimits the tileEntityLimits to set
     */
    public void setTileEntityLimits(Map<String, Integer> tileEntityLimits) {
        this.tileEntityLimits = tileEntityLimits;
    }

    /**
     * @param maxTeamSize the maxTeamSize to set
     */
    public void setMaxTeamSize(int maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
    }

    /**
     * @param maxHomes the maxHomes to set
     */
    public void setMaxHomes(int maxHomes) {
        this.maxHomes = maxHomes;
    }

    /**
     * @param islandXOffset the islandXOffset to set
     */
    public void setIslandXOffset(int islandXOffset) {
        this.islandXOffset = islandXOffset;
    }

    /**
     * @param islandZOffset the islandZOffset to set
     */
    public void setIslandZOffset(int islandZOffset) {
        this.islandZOffset = islandZOffset;
    }

    /**
     * @param chestItems the chestItems to set
     */
    public void setChestItems(List<ItemStack> chestItems) {
        this.chestItems = chestItems;
    }

    @Override
    public String getPermissionPrefix() {
        return "acidisland";
    }

    /**
     * @return the acidDamageMonster
     */
    public int getAcidDamageMonster() {
        return acidDamageMonster;
    }

    /**
     * @param acidDamageMonster the acidDamageMonster to set
     */
    public void setAcidDamageMonster(int acidDamageMonster) {
        this.acidDamageMonster = acidDamageMonster;
    }

    /**
     * @return the acidDamageAnimal
     */
    public int getAcidDamageAnimal() {
        return acidDamageAnimal;
    }

    /**
     * @param acidDamageAnimal the acidDamageAnimal to set
     */
    public void setAcidDamageAnimal(int acidDamageAnimal) {
        this.acidDamageAnimal = acidDamageAnimal;
    }

    /**
     * @return the helmetProtection
     */
    public boolean isHelmetProtection() {
        return helmetProtection;
    }

    /**
     * @param helmetProtection the helmetProtection to set
     */
    public void setHelmetProtection(boolean helmetProtection) {
        this.helmetProtection = helmetProtection;
    }

    /**
     * @return the fullArmorProtection
     */
    public boolean isFullArmorProtection() {
        return fullArmorProtection;
    }

    /**
     * @param fullArmorProtection the fullArmorProtection to set
     */
    public void setFullArmorProtection(boolean fullArmorProtection) {
        this.fullArmorProtection = fullArmorProtection;
    }


}
