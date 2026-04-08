package world.bentobox.acidisland;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author tastybento
 *
 */
@ExtendWith(MockitoExtension.class)
public class AISettingsTest {

    /**
     * Class under test
     */
    private AISettings s;

    @BeforeEach
    public void setUp() throws Exception {
        MockBukkit.mock();
        s = new AISettings();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testGetAcidDamage() {
        assertEquals(10, s.getAcidDamage());
    }

    @Test
    public void testGetAcidDamageAnimal() {
        assertEquals(1, s.getAcidDamageAnimal());
    }

    @Test
    public void testGetAcidDamageDelay() {
        assertEquals(2, s.getAcidDamageDelay());
    }

    @Test
    public void testGetAcidDamageMonster() {
        assertEquals(5, s.getAcidDamageMonster());
    }

    @Test
    public void testGetAcidDestroyItemTime() {
        assertEquals(0, s.getAcidDestroyItemTime());
    }

    @Test
    public void testGetAcidEffects() {
        assertTrue(s.getAcidEffects().isEmpty());
    }

    @Test
    public void testGetAcidRainDamage() {
        assertEquals(1, s.getAcidRainDamage());
    }

    @Test
    public void testGetBanLimit() {
        assertEquals(-1, s.getBanLimit());
    }

    @Test
    public void testGetCustomRanks() {
        assertTrue(s.getCustomRanks().isEmpty());
    }

    @Test
    public void testGetDeathsMax() {
        assertEquals(10, s.getDeathsMax());
    }

    @Test
    public void testGetDefaultBiome() {
        assertEquals(Biome.WARM_OCEAN, s.getDefaultBiome());
    }

    @Test
    public void testGetDefaultGameMode() {
        assertEquals(GameMode.SURVIVAL, s.getDefaultGameMode());
    }

    @Test
    public void testGetDifficulty() {
        assertEquals(Difficulty.NORMAL, s.getDifficulty());
    }

    @Test
    public void testGetEndSeaHeight() {
        assertEquals(54, s.getEndSeaHeight());
    }

    @Test
    public void testGetFriendlyName() {
        assertEquals("AcidIsland", s.getFriendlyName());
    }

    @Test
    public void testGetGeoLimitSettings() {
        assertTrue(s.getGeoLimitSettings().isEmpty());
    }

    @Test
    public void testGetIslandDistance() {
        assertEquals(64, s.getIslandDistance());
    }

    @Test
    public void testGetIslandHeight() {
        assertEquals(50, s.getIslandHeight());
    }

    @Test
    public void testGetIslandProtectionRange() {
        assertEquals(50, s.getIslandProtectionRange());
    }

    @Test
    public void testGetIslandStartX() {
        assertEquals(0, s.getIslandStartX());
    }

    @Test
    public void testGetIslandStartZ() {
        assertEquals(0, s.getIslandStartZ());
    }

    @Test
    public void testGetIslandXOffset() {
        assertEquals(0, s.getIslandXOffset());
    }

    @Test
    public void testGetIslandZOffset() {
        assertEquals(0, s.getIslandZOffset());
    }

    @Test
    public void testGetIvSettings() {
        assertTrue(s.getIvSettings().isEmpty());
    }

    @Test
    public void testGetMaxHomes() {
        assertEquals(5, s.getMaxHomes());
    }

    @Test
    public void testGetMaxIslands() {
        assertEquals(-1, s.getMaxIslands());
    }

    @Test
    public void testGetMaxTeamSize() {
        assertEquals(4, s.getMaxTeamSize());
    }

    @Test
    public void testGetNetherSeaHeight() {
        assertEquals(54, s.getNetherSeaHeight());
    }

    @Test
    public void testGetNetherSpawnRadius() {
        assertEquals(32, s.getNetherSpawnRadius());
    }

    @Test
    public void testGetPermissionPrefix() {
        assertEquals("acidisland", s.getPermissionPrefix());
    }

    @Test
    public void testGetRemoveMobsWhitelist() {
        assertTrue(s.getRemoveMobsWhitelist().isEmpty());
    }

    @Test
    public void testGetResetEpoch() {
        assertEquals(0, s.getResetEpoch());
    }

    @Test
    public void testGetResetLimit() {
        assertEquals(-1, s.getResetLimit());
    }

    @Test
    public void testGetSeaHeight() {
        assertEquals(54, s.getSeaHeight());
    }

    @Test
    public void testGetHiddenFlags() {
        assertTrue(s.getHiddenFlags().isEmpty());
    }

    @Test
    public void testGetVisitorBannedCommands() {
        assertTrue(s.getVisitorBannedCommands().isEmpty());
    }

    @Test
    public void testGetFallingBannedCommands() {
        assertTrue(s.getFallingBannedCommands().isEmpty());
    }

    @Test
    public void testGetWorldFlags() {
        assertTrue(s.getWorldFlags().isEmpty());
    }

    @Test
    public void testGetWorldName() {
        assertEquals("acidisland_world", s.getWorldName());
    }

    @Test
    public void testIsAcidDamageChickens() {
        assertFalse(s.isAcidDamageChickens());
    }

    @Test
    public void testIsAcidDamageOp() {
        assertFalse(s.isAcidDamageOp());
    }

    @Test
    public void testIsAllowSetHomeInNether() {
        assertTrue(s.isAllowSetHomeInNether());
    }

    @Test
    public void testIsAllowSetHomeInTheEnd() {
        assertTrue(s.isAllowSetHomeInTheEnd());
    }

    @Test
    public void testIsDeathsCounted() {
        assertTrue(s.isDeathsCounted());
    }

    @Test
    public void testIsDragonSpawn() {
        assertFalse(s.isDragonSpawn());
    }

    @Test
    public void testIsEndGenerate() {
        assertTrue(s.isEndGenerate());
    }

    @Test
    public void testIsEndIslands() {
        assertTrue(s.isEndIslands());
    }

    @Test
    public void testIsFullArmorProtection() {
        assertFalse(s.isFullArmorProtection());
    }

    @Test
    public void testIsHelmetProtection() {
        assertFalse(s.isHelmetProtection());
    }

    @Test
    public void testIsKickedKeepInventory() {
        assertFalse(s.isKickedKeepInventory());
    }

    @Test
    public void testIsCreateIslandOnFirstLoginEnabled() {
        assertFalse(s.isCreateIslandOnFirstLoginEnabled());
    }

    @Test
    public void testGetCreateIslandOnFirstLoginDelay() {
        assertEquals(5, s.getCreateIslandOnFirstLoginDelay());
    }

    @Test
    public void testIsCreateIslandOnFirstLoginAbortOnLogout() {
        assertTrue(s.isCreateIslandOnFirstLoginAbortOnLogout());
    }

    @Test
    public void testIsLeaversLoseReset() {
        assertFalse(s.isLeaversLoseReset());
    }

    @Test
    public void testIsNetherGenerate() {
        assertTrue(s.isNetherGenerate());
    }

    @Test
    public void testIsNetherIslands() {
        assertTrue(s.isNetherIslands());
    }

    @Test
    public void testIsNetherRoof() {
        assertTrue(s.isNetherRoof());
    }

    @Test
    public void testIsOnJoinResetEnderChest() {
        assertFalse(s.isOnJoinResetEnderChest());
    }

    @Test
    public void testIsOnJoinResetInventory() {
        assertFalse(s.isOnJoinResetInventory());
    }

    @Test
    public void testIsOnJoinResetMoney() {
        assertFalse(s.isOnJoinResetMoney());
    }

    @Test
    public void testIsOnLeaveResetEnderChest() {
        assertFalse(s.isOnLeaveResetEnderChest());
    }

    @Test
    public void testIsOnLeaveResetInventory() {
        assertFalse(s.isOnLeaveResetInventory());
    }

    @Test
    public void testIsOnLeaveResetMoney() {
        assertFalse(s.isOnLeaveResetMoney());
    }

    @Test
    public void testIsRequireConfirmationToSetHomeInNether() {
        assertTrue(s.isRequireConfirmationToSetHomeInNether());
    }

    @Test
    public void testIsRequireConfirmationToSetHomeInTheEnd() {
        assertTrue(s.isRequireConfirmationToSetHomeInTheEnd());
    }

    @Test
    public void testIsTeamJoinDeathReset() {
        assertTrue(s.isTeamJoinDeathReset());
    }

    @Test
    public void testIsUseOwnGenerator() {
        assertFalse(s.isUseOwnGenerator());
    }

    @Test
    public void testIsWaterUnsafe() {
        assertTrue(s.isWaterUnsafe());
    }

    @Test
    public void testSetAcidDamage() {
        s.setAcidDamage(99);
        assertEquals(99, s.getAcidDamage());
    }

    @Test
    public void testSetAcidDamageAnimal() {
        s.setAcidDamageAnimal(99);
        assertEquals(99, s.getAcidDamageAnimal());
    }

    @Test
    public void testSetAcidDamageChickens() {
        s.setAcidDamageChickens(true);
        assertTrue(s.isAcidDamageChickens());
    }

    @Test
    public void testSetAcidDamageDelay() {
        s.setAcidDamageDelay(99);
        assertEquals(99, s.getAcidDamageDelay());
    }

    @Test
    public void testSetAcidDamageMonster() {
        s.setAcidDamageMonster(99);
        assertEquals(99, s.getAcidDamageMonster());
    }

    @Test
    public void testSetAcidDamageOp() {
        s.setAcidDamageOp(true);
        assertTrue(s.isAcidDamageOp());
    }

    @Test
    public void testSetAcidDestroyItemTime() {
        s.setAcidDestroyItemTime(99);
        assertEquals(99, s.getAcidDestroyItemTime());
    }

    @Test
    @Disabled
    public void testSetAcidEffects() {
        List<PotionEffectType> list = Collections.singletonList(PotionEffectType.ABSORPTION);
        s.setAcidEffects(list);
        assertEquals(list, s.getAcidEffects());
    }

    @Test
    public void testSetAcidRainDamage() {
        s.setAcidRainDamage(99);
        assertEquals(99, s.getAcidRainDamage());
    }

    @Test
    public void testSetAdminCommand() {
        s.setAdminCommand("admin");
        assertEquals("admin", s.getAdminCommandAliases());
    }

    @Test
    public void testSetAllowSetHomeInNether() {
        s.setAllowSetHomeInNether(false);
        assertFalse(s.isAllowSetHomeInNether());
        s.setAllowSetHomeInNether(true);
        assertTrue(s.isAllowSetHomeInNether());
    }

    @Test
    public void testSetAllowSetHomeInTheEnd() {
        s.setAllowSetHomeInTheEnd(false);
        assertFalse(s.isAllowSetHomeInTheEnd());
        s.setAllowSetHomeInTheEnd(true);
        assertTrue(s.isAllowSetHomeInTheEnd());
    }

    @Test
    public void testSetBanLimit() {
        s.setBanLimit(99);
        assertEquals(99, s.getBanLimit());
    }

    @Test
    public void testSetCustomRanks() {
        s.setCustomRanks(Collections.singletonMap("string", 10));
        assertEquals(10, (int) s.getCustomRanks().get("string"));
    }

    @Test
    public void testSetDeathsCounted() {
        s.setDeathsCounted(false);
        assertFalse(s.isDeathsCounted());
        s.setDeathsCounted(true);
        assertTrue(s.isDeathsCounted());
    }

    @Test
    public void testSetDeathsMax() {
        s.setDeathsMax(99);
        assertEquals(99, s.getDeathsMax());
    }

    @Test
    public void testSetDefaultBiome() {
        s.setDefaultBiome(Biome.BADLANDS);
        assertEquals(Biome.BADLANDS, s.getDefaultBiome());
    }

    @Test
    public void testSetDefaultGameMode() {
        s.setDefaultGameMode(GameMode.SPECTATOR);
        assertEquals(GameMode.SPECTATOR, s.getDefaultGameMode());
    }

    @Test
    public void testSetDefaultIslandFlags() {
        s.setDefaultIslandFlagNames(Collections.singletonMap("ANIMAL_NATURAL_SPAWN", 10));
        assertEquals(10, (int) s.getDefaultIslandFlagNames().get("ANIMAL_NATURAL_SPAWN"));
    }

    @Test
    public void testSetDefaultIslandSettings() {
        s.setDefaultIslandSettingNames(Collections.singletonMap("ANIMAL_NATURAL_SPAWN", 10));
        assertEquals(10, (int) s.getDefaultIslandSettingNames().get("ANIMAL_NATURAL_SPAWN"));
    }

    @Test
    public void testSetDifficulty() {
        s.setDifficulty(Difficulty.PEACEFUL);
        assertEquals(Difficulty.PEACEFUL, s.getDifficulty());
    }

    @Test
    public void testSetDragonSpawn() {
        s.setDragonSpawn(false);
        assertFalse(s.isDragonSpawn());
        s.setDragonSpawn(true);
        assertTrue(s.isDragonSpawn());
    }

    @Test
    public void testSetEndGenerate() {
        s.setEndGenerate(false);
        assertFalse(s.isEndGenerate());
        s.setEndGenerate(true);
        assertTrue(s.isEndGenerate());
    }

    @Test
    public void testSetEndIslands() {
        s.setEndIslands(false);
        assertFalse(s.isEndIslands());
        s.setEndIslands(true);
        assertTrue(s.isEndIslands());
    }

    @Test
    public void testSetFriendlyName() {
        s.setFriendlyName("hshshs");
        assertEquals("hshshs", s.getFriendlyName());
    }

    @Test
    public void testSetFullArmorProtection() {
        s.setFullArmorProtection(false);
        assertFalse(s.isFullArmorProtection());
        s.setFullArmorProtection(true);
        assertTrue(s.isFullArmorProtection());
    }

    @Test
    public void testSetGeoLimitSettings() {
        s.setGeoLimitSettings(Collections.singletonList("ghghhg"));
        assertEquals("ghghhg", s.getGeoLimitSettings().get(0));
    }

    @Test
    public void testSetHelmetProtection() {
        s.setHelmetProtection(false);
        assertFalse(s.isHelmetProtection());
        s.setHelmetProtection(true);
        assertTrue(s.isHelmetProtection());
    }

    @Test
    public void testSetIslandCommand() {
        s.setIslandCommand("command");
        assertEquals("command", s.getPlayerCommandAliases());
    }

    @Test
    public void testSetIslandDistance() {
        s.setIslandDistance(99);
        assertEquals(99, s.getIslandDistance());
    }

    @Test
    public void testSetIslandHeight() {
        s.setIslandHeight(99);
        assertEquals(99, s.getIslandHeight());
    }

    @Test
    public void testSetIslandProtectionRange() {
        s.setIslandProtectionRange(99);
        assertEquals(99, s.getIslandProtectionRange());
    }

    @Test
    public void testSetIslandStartX() {
        s.setIslandStartX(99);
        assertEquals(99, s.getIslandStartX());
    }

    @Test
    public void testSetIslandStartZ() {
        s.setIslandStartZ(99);
        assertEquals(99, s.getIslandStartZ());
    }

    @Test
    public void testSetIslandXOffset() {
        s.setIslandXOffset(99);
        assertEquals(99, s.getIslandXOffset());
    }

    @Test
    public void testSetIslandZOffset() {
        s.setIslandZOffset(99);
        assertEquals(99, s.getIslandZOffset());
    }

    @Test
    public void testSetIvSettings() {
        s.setIvSettings(Collections.singletonList("ffff"));
        assertEquals("ffff", s.getIvSettings().get(0));
    }

    @Test
    public void testSetKickedKeepInventory() {
        s.setKickedKeepInventory(false);
        assertFalse(s.isKickedKeepInventory());
        s.setKickedKeepInventory(true);
        assertTrue(s.isKickedKeepInventory());
    }

    @Test
    public void testSetLeaversLoseReset() {
        s.setLeaversLoseReset(false);
        assertFalse(s.isLeaversLoseReset());
        s.setLeaversLoseReset(true);
        assertTrue(s.isLeaversLoseReset());
    }

    @Test
    public void testSetMaxHomes() {
        s.setMaxHomes(99);
        assertEquals(99, s.getMaxHomes());
    }

    @Test
    public void testSetMaxIslands() {
        s.setMaxIslands(99);
        assertEquals(99, s.getMaxIslands());
    }

    @Test
    public void testSetMaxTeamSize() {
        s.setMaxTeamSize(99);
        assertEquals(99, s.getMaxTeamSize());
    }

    @Test
    public void testSetNetherGenerate() {
        s.setNetherGenerate(false);
        assertFalse(s.isNetherGenerate());
        s.setNetherGenerate(true);
        assertTrue(s.isNetherGenerate());
    }

    @Test
    public void testSetNetherIslands() {
        s.setNetherIslands(false);
        assertFalse(s.isNetherIslands());
        s.setNetherIslands(true);
        assertTrue(s.isNetherIslands());
    }

    @Test
    public void testSetNetherRoof() {
        s.setNetherRoof(false);
        assertFalse(s.isNetherRoof());
        s.setNetherRoof(true);
        assertTrue(s.isNetherRoof());
    }

    @Test
    public void testSetNetherSpawnRadius() {
        s.setNetherSpawnRadius(99);
        assertEquals(99, s.getNetherSpawnRadius());
    }

    @Test
    public void testSetOnJoinResetEnderChest() {
        s.setOnJoinResetEnderChest(false);
        assertFalse(s.isOnJoinResetEnderChest());
        s.setOnJoinResetEnderChest(true);
        assertTrue(s.isOnJoinResetEnderChest());
    }

    @Test
    public void testSetOnJoinResetInventory() {
        s.setOnJoinResetInventory(false);
        assertFalse(s.isOnJoinResetInventory());
        s.setOnJoinResetInventory(true);
        assertTrue(s.isOnJoinResetInventory());
    }

    @Test
    public void testSetOnJoinResetMoney() {
        s.setOnJoinResetMoney(false);
        assertFalse(s.isOnJoinResetMoney());
        s.setOnJoinResetMoney(true);
        assertTrue(s.isOnJoinResetMoney());
    }

    @Test
    public void testSetOnLeaveResetEnderChest() {
        s.setOnLeaveResetEnderChest(false);
        assertFalse(s.isOnLeaveResetEnderChest());
        s.setOnLeaveResetEnderChest(true);
        assertTrue(s.isOnLeaveResetEnderChest());
    }

    @Test
    public void testSetOnLeaveResetInventory() {
        s.setOnLeaveResetInventory(false);
        assertFalse(s.isOnLeaveResetInventory());
        s.setOnLeaveResetInventory(true);
        assertTrue(s.isOnLeaveResetInventory());
    }

    @Test
    public void testSetOnLeaveResetMoney() {
        s.setOnLeaveResetMoney(false);
        assertFalse(s.isOnLeaveResetMoney());
        s.setOnLeaveResetMoney(true);
        assertTrue(s.isOnLeaveResetMoney());
    }

    @Test
    public void testSetRemoveMobsWhitelist() {
        s.setRemoveMobsWhitelist(Collections.singleton(EntityType.GHAST));
        assertTrue(s.getRemoveMobsWhitelist().contains(EntityType.GHAST));
    }

    @Test
    public void testSetRequireConfirmationToSetHomeInNether() {
        s.setRequireConfirmationToSetHomeInNether(false);
        assertFalse(s.isRequireConfirmationToSetHomeInNether());
        s.setRequireConfirmationToSetHomeInNether(true);
        assertTrue(s.isRequireConfirmationToSetHomeInNether());
    }

    @Test
    public void testSetRequireConfirmationToSetHomeInTheEnd() {
        s.setRequireConfirmationToSetHomeInTheEnd(false);
        assertFalse(s.isRequireConfirmationToSetHomeInTheEnd());
        s.setRequireConfirmationToSetHomeInTheEnd(true);
        assertTrue(s.isRequireConfirmationToSetHomeInTheEnd());
    }

    @Test
    public void testSetResetEpoch() {
        s.setResetEpoch(3456L);
        assertEquals(3456L, s.getResetEpoch());
    }

    @Test
    public void testSetResetLimit() {
        s.setResetLimit(99);
        assertEquals(99, s.getResetLimit());
    }

    @Test
    public void testSetSeaHeight() {
        s.setSeaHeight(99);
        assertEquals(99, s.getSeaHeight());
    }

    @Test
    public void testSetNetherSeaHeight() {
        s.setNetherSeaHeight(99);
        assertEquals(99, s.getNetherSeaHeight());
    }

    @Test
    public void testSetEndSeaHeight() {
        s.setEndSeaHeight(99);
        assertEquals(99, s.getEndSeaHeight());
    }

    @Test
    public void testSetTeamJoinDeathReset() {
        s.setTeamJoinDeathReset(false);
        assertFalse(s.isTeamJoinDeathReset());
        s.setTeamJoinDeathReset(true);
        assertTrue(s.isTeamJoinDeathReset());
    }

    @Test
    public void testSetUseOwnGenerator() {
        s.setUseOwnGenerator(false);
        assertFalse(s.isUseOwnGenerator());
        s.setUseOwnGenerator(true);
        assertTrue(s.isUseOwnGenerator());
    }

    @Test
    public void testSetHiddenFlags() {
        s.setHiddenFlags(Collections.singletonList("flag"));
        assertEquals("flag", s.getHiddenFlags().get(0));
    }

    @Test
    public void testSetVisitorBannedCommands() {
        s.setVisitorBannedCommands(Collections.singletonList("flag"));
        assertEquals("flag", s.getVisitorBannedCommands().get(0));
    }

    @Test
    public void testSetFallingBannedCommands() {
        s.setFallingBannedCommands(Collections.singletonList("flag"));
        assertEquals("flag", s.getFallingBannedCommands().get(0));
    }

    @Test
    public void testSetWorldFlags() {
        s.setWorldFlags(Collections.singletonMap("flag", true));
        assertTrue(s.getWorldFlags().get("flag"));
    }

    @Test
    public void testSetWorldName() {
        s.setWorldName("ugga");
        assertEquals("ugga", s.getWorldName());
    }

    @Test
    public void testIsAcidDamageSnow() {
        assertFalse(s.isAcidDamageSnow());
    }

    @Test
    public void testSetAcidDamageSnow() {
        s.setAcidDamageSnow(false);
        assertFalse(s.isAcidDamageSnow());
        s.setAcidDamageSnow(true);
        assertTrue(s.isAcidDamageSnow());
    }

    @Test
    public void testIsDeathsResetOnNewIsland() {
        assertTrue(s.isDeathsResetOnNewIsland());
    }

    @Test
    public void testSetDeathsResetOnNewIsland() {
        s.setDeathsResetOnNewIsland(false);
        assertFalse(s.isDeathsResetOnNewIsland());
        s.setDeathsResetOnNewIsland(true);
        assertTrue(s.isDeathsResetOnNewIsland());
    }

    @Test
    public void testGetOnJoinCommands() {
        assertTrue(s.getOnJoinCommands().isEmpty());
    }

    @Test
    public void testSetOnJoinCommands() {
        s.setOnJoinCommands(Collections.singletonList("command"));
        assertEquals("command", s.getOnJoinCommands().get(0));
    }

    @Test
    public void testGetOnLeaveCommands() {
        assertTrue(s.getOnLeaveCommands().isEmpty());
    }

    @Test
    public void testSetOnLeaveCommands() {
        s.setOnLeaveCommands(Collections.singletonList("command"));
        assertEquals("command", s.getOnLeaveCommands().get(0));
    }

    @Test
    public void testIsOnJoinResetHealth() {
        assertTrue(s.isOnJoinResetHealth());
    }

    @Test
    public void testSetOnJoinResetHealth() {
        s.setOnJoinResetHealth(false);
        assertFalse(s.isOnJoinResetHealth());
        s.setOnJoinResetHealth(true);
        assertTrue(s.isOnJoinResetHealth());
    }

    @Test
    public void testIsOnJoinResetHunger() {
        assertTrue(s.isOnJoinResetHunger());
    }

    @Test
    public void testSetOnJoinResetHunger() {
        s.setOnJoinResetHunger(false);
        assertFalse(s.isOnJoinResetHunger());
        s.setOnJoinResetHunger(true);
        assertTrue(s.isOnJoinResetHunger());
    }

    @Test
    public void testIsOnJoinResetXP() {
        assertFalse(s.isOnJoinResetXP());
    }

    @Test
    public void testSetOnJoinResetXP() {
        s.setOnJoinResetXP(false);
        assertFalse(s.isOnJoinResetXP());
        s.setOnJoinResetXP(true);
        assertTrue(s.isOnJoinResetXP());
    }

    @Test
    public void testIsOnLeaveResetHealth() {
        assertFalse(s.isOnLeaveResetHealth());
    }

    @Test
    public void testSetOnLeaveResetHealth() {
        s.setOnLeaveResetHealth(false);
        assertFalse(s.isOnLeaveResetHealth());
        s.setOnLeaveResetHealth(true);
        assertTrue(s.isOnLeaveResetHealth());
    }

    @Test
    public void testIsOnLeaveResetHunger() {
        assertFalse(s.isOnLeaveResetHunger());
    }

    @Test
    public void testSetOnLeaveResetHunger() {
        s.setOnLeaveResetHunger(false);
        assertFalse(s.isOnLeaveResetHunger());
        s.setOnLeaveResetHunger(true);
        assertTrue(s.isOnLeaveResetHunger());
    }

    @Test
    public void testIsOnLeaveResetXP() {
        assertFalse(s.isOnLeaveResetXP());
    }

    @Test
    public void testSetOnLeaveResetXP() {
        s.setOnLeaveResetXP(false);
        assertFalse(s.isOnLeaveResetXP());
        s.setOnLeaveResetXP(true);
        assertTrue(s.isOnLeaveResetXP());
    }

    @Test
    public void testSetCreateIslandOnFirstLoginEnabled() {
        s.setCreateIslandOnFirstLoginEnabled(false);
        assertFalse(s.isCreateIslandOnFirstLoginEnabled());
        s.setCreateIslandOnFirstLoginEnabled(true);
        assertTrue(s.isCreateIslandOnFirstLoginEnabled());
    }

    @Test
    public void testSetCreateIslandOnFirstLoginDelay() {
        s.setCreateIslandOnFirstLoginDelay(40);
        assertEquals(40, s.getCreateIslandOnFirstLoginDelay());
    }

    @Test
    public void testSetCreateIslandOnFirstLoginAbortOnLogout() {
        s.setCreateIslandOnFirstLoginAbortOnLogout(false);
        assertFalse(s.isCreateIslandOnFirstLoginAbortOnLogout());
        s.setCreateIslandOnFirstLoginAbortOnLogout(true);
        assertTrue(s.isCreateIslandOnFirstLoginAbortOnLogout());
    }

    @Test
    public void testIsPasteMissingIslands() {
        assertFalse(s.isPasteMissingIslands());
    }

    @Test
    public void testSetPasteMissingIslands() {
        s.setPasteMissingIslands(false);
        assertFalse(s.isPasteMissingIslands());
        s.setPasteMissingIslands(true);
        assertTrue(s.isPasteMissingIslands());
    }

    @Test
    public void testGetAcidRainEffects() {
        assertTrue(s.getAcidRainEffects().isEmpty());
    }

    @Test
    @Disabled("Bukkit made this so we can't test")
    public void testSetAcidRainEffects() {
        s.setAcidRainEffects(Collections.singletonList(PotionEffectType.BAD_OMEN));
        assertEquals(PotionEffectType.BAD_OMEN, s.getAcidRainEffects().get(0));
    }

    @Test
    public void testGetRainEffectDuation() {
        assertEquals(10, s.getRainEffectDuation());
    }

    @Test
    public void testSetRainEffectDuation() {
        s.setRainEffectDuation(99);
        assertEquals(99, s.getRainEffectDuation());
    }

    @Test
    public void testGetAcidEffectDuation() {
        assertEquals(30, s.getAcidEffectDuation());
    }

    @Test
    public void testSetAcidEffectDuation() {
        s.setAcidEffectDuation(99);
        assertEquals(99, s.getAcidEffectDuation());
    }

    @Test
    public void testGetSpawnLimitMonsters() {
        assertEquals(-1, s.getSpawnLimitMonsters());
    }

    @Test
    public void testSetSpawnLimitMonsters() {
        s.setSpawnLimitMonsters(99);
        assertEquals(99, s.getSpawnLimitMonsters());
    }

    @Test
    public void testGetSpawnLimitAnimals() {
        assertEquals(-1, s.getSpawnLimitAnimals());
    }

    @Test
    public void testSetSpawnLimitAnimals() {
        s.setSpawnLimitAnimals(99);
        assertEquals(99, s.getSpawnLimitAnimals());
    }

    @Test
    public void testGetSpawnLimitWaterAnimals() {
        assertEquals(-1, s.getSpawnLimitWaterAnimals());
    }

    @Test
    public void testSetSpawnLimitWaterAnimals() {
        s.setSpawnLimitWaterAnimals(99);
        assertEquals(99, s.getSpawnLimitWaterAnimals());
    }

    @Test
    public void testGetSpawnLimitAmbient() {
        assertEquals(-1, s.getSpawnLimitAmbient());
    }

    @Test
    public void testSetSpawnLimitAmbient() {
        s.setSpawnLimitAmbient(99);
        assertEquals(99, s.getSpawnLimitAmbient());
    }

    @Test
    public void testGetTicksPerAnimalSpawns() {
        assertEquals(-1, s.getTicksPerAnimalSpawns());
    }

    @Test
    public void testSetTicksPerAnimalSpawns() {
        s.setTicksPerAnimalSpawns(99);
        assertEquals(99, s.getTicksPerAnimalSpawns());
    }

    @Test
    public void testGetTicksPerMonsterSpawns() {
        assertEquals(-1, s.getTicksPerMonsterSpawns());
    }

    @Test
    public void testSetTicksPerMonsterSpawns() {
        s.setTicksPerMonsterSpawns(99);
        assertEquals(99, s.getTicksPerMonsterSpawns());
    }

    @Test
    public void testGetMaxCoopSize() {
        assertEquals(4, s.getMaxCoopSize());
    }

    @Test
    public void testSetMaxCoopSize() {
        s.setMaxCoopSize(99);
        assertEquals(99, s.getMaxCoopSize());
    }

    @Test
    public void testGetMaxTrustSize() {
        assertEquals(4, s.getMaxTrustSize());
    }

    @Test
    public void testSetMaxTrustSize() {
        s.setMaxTrustSize(99);
        assertEquals(99, s.getMaxTrustSize());
    }

    @Test
    public void testGetPlayerCommandAliases() {
        assertEquals("ai", s.getPlayerCommandAliases());
    }

    @Test
    public void testSetPlayerCommandAliases() {
        s.setPlayerCommandAliases("adm");
        assertEquals("adm", s.getPlayerCommandAliases());
    }

    @Test
    public void testGetAdminCommandAliases() {
        assertEquals("acid", s.getAdminCommandAliases());
    }

    @Test
    public void testSetAdminCommandAliases() {
        s.setAdminCommandAliases("adm");
        assertEquals("adm", s.getAdminCommandAliases());
    }

    @Test
    public void testGetDefaultNewPlayerAction() {
        assertEquals("create", s.getDefaultNewPlayerAction());
    }

    @Test
    public void testSetDefaultNewPlayerAction() {
        s.setDefaultNewPlayerAction("cr");
        assertEquals("cr", s.getDefaultNewPlayerAction());
    }

    @Test
    public void testGetDefaultPlayerAction() {
        assertEquals("go", s.getDefaultPlayerAction());
    }

    @Test
    public void testSetDefaultPlayerAction() {
        s.setDefaultPlayerAction("go2");
        assertEquals("go2", s.getDefaultPlayerAction());
    }

    @Test
    public void testGetDefaultNetherBiome() {
        assertEquals(Biome.NETHER_WASTES, s.getDefaultNetherBiome());
    }

    @Test
    public void testSetDefaultNetherBiome() {
        s.setDefaultNetherBiome(Biome.END_BARRENS);
        assertEquals(Biome.END_BARRENS, s.getDefaultNetherBiome());
    }

    @Test
    public void testGetDefaultEndBiome() {
        assertEquals(Biome.THE_END, s.getDefaultEndBiome());
    }

    @Test
    public void testSetDefaultEndBiome() {
        s.setDefaultEndBiome(Biome.END_BARRENS);
        assertEquals(Biome.END_BARRENS, s.getDefaultEndBiome());
    }

}
