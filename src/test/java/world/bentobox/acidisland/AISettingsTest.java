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
    void setUp() throws Exception {
        MockBukkit.mock();
        s = new AISettings();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void testGetAcidDamage() {
        assertEquals(10, s.getAcidDamage());
    }

    @Test
    void testGetAcidDamageAnimal() {
        assertEquals(1, s.getAcidDamageAnimal());
    }

    @Test
    void testGetAcidDamageDelay() {
        assertEquals(2, s.getAcidDamageDelay());
    }

    @Test
    void testGetAcidDamageMonster() {
        assertEquals(5, s.getAcidDamageMonster());
    }

    @Test
    void testGetAcidDestroyItemTime() {
        assertEquals(0, s.getAcidDestroyItemTime());
    }

    @Test
    void testGetAcidEffects() {
        assertTrue(s.getAcidEffects().isEmpty());
    }

    @Test
    void testGetAcidRainDamage() {
        assertEquals(1, s.getAcidRainDamage());
    }

    @Test
    void testGetBanLimit() {
        assertEquals(-1, s.getBanLimit());
    }

    @Test
    void testGetCustomRanks() {
        assertTrue(s.getCustomRanks().isEmpty());
    }

    @Test
    void testGetDeathsMax() {
        assertEquals(10, s.getDeathsMax());
    }

    @Test
    void testGetDefaultBiome() {
        assertEquals(Biome.WARM_OCEAN, s.getDefaultBiome());
    }

    @Test
    void testGetDefaultGameMode() {
        assertEquals(GameMode.SURVIVAL, s.getDefaultGameMode());
    }

    @Test
    void testGetDifficulty() {
        assertEquals(Difficulty.NORMAL, s.getDifficulty());
    }

    @Test
    void testGetEndSeaHeight() {
        assertEquals(54, s.getEndSeaHeight());
    }

    @Test
    void testGetFriendlyName() {
        assertEquals("AcidIsland", s.getFriendlyName());
    }

    @Test
    void testGetGeoLimitSettings() {
        assertTrue(s.getGeoLimitSettings().isEmpty());
    }

    @Test
    void testGetIslandDistance() {
        assertEquals(64, s.getIslandDistance());
    }

    @Test
    void testGetIslandHeight() {
        assertEquals(50, s.getIslandHeight());
    }

    @Test
    void testGetIslandProtectionRange() {
        assertEquals(50, s.getIslandProtectionRange());
    }

    @Test
    void testGetIslandStartX() {
        assertEquals(0, s.getIslandStartX());
    }

    @Test
    void testGetIslandStartZ() {
        assertEquals(0, s.getIslandStartZ());
    }

    @Test
    void testGetIslandXOffset() {
        assertEquals(0, s.getIslandXOffset());
    }

    @Test
    void testGetIslandZOffset() {
        assertEquals(0, s.getIslandZOffset());
    }

    @Test
    void testGetIvSettings() {
        assertTrue(s.getIvSettings().isEmpty());
    }

    @Test
    void testGetMaxHomes() {
        assertEquals(5, s.getMaxHomes());
    }

    @Test
    void testGetMaxIslands() {
        assertEquals(-1, s.getMaxIslands());
    }

    @Test
    void testGetMaxTeamSize() {
        assertEquals(4, s.getMaxTeamSize());
    }

    @Test
    void testGetNetherSeaHeight() {
        assertEquals(54, s.getNetherSeaHeight());
    }

    @Test
    void testGetNetherSpawnRadius() {
        assertEquals(32, s.getNetherSpawnRadius());
    }

    @Test
    void testGetPermissionPrefix() {
        assertEquals("acidisland", s.getPermissionPrefix());
    }

    @Test
    void testGetRemoveMobsWhitelist() {
        assertTrue(s.getRemoveMobsWhitelist().isEmpty());
    }

    @Test
    void testGetResetEpoch() {
        assertEquals(0, s.getResetEpoch());
    }

    @Test
    void testGetResetLimit() {
        assertEquals(-1, s.getResetLimit());
    }

    @Test
    void testGetSeaHeight() {
        assertEquals(54, s.getSeaHeight());
    }

    @Test
    void testGetHiddenFlags() {
        assertTrue(s.getHiddenFlags().isEmpty());
    }

    @Test
    void testGetVisitorBannedCommands() {
        assertTrue(s.getVisitorBannedCommands().isEmpty());
    }

    @Test
    void testGetFallingBannedCommands() {
        assertTrue(s.getFallingBannedCommands().isEmpty());
    }

    @Test
    void testGetWorldFlags() {
        assertTrue(s.getWorldFlags().isEmpty());
    }

    @Test
    void testGetWorldName() {
        assertEquals("acidisland_world", s.getWorldName());
    }

    @Test
    void testIsAcidDamageChickens() {
        assertFalse(s.isAcidDamageChickens());
    }

    @Test
    void testIsAcidDamageOp() {
        assertFalse(s.isAcidDamageOp());
    }

    @Test
    void testIsAllowSetHomeInNether() {
        assertTrue(s.isAllowSetHomeInNether());
    }

    @Test
    void testIsAllowSetHomeInTheEnd() {
        assertTrue(s.isAllowSetHomeInTheEnd());
    }

    @Test
    void testIsDeathsCounted() {
        assertTrue(s.isDeathsCounted());
    }

    @Test
    void testIsDragonSpawn() {
        assertFalse(s.isDragonSpawn());
    }

    @Test
    void testIsEndGenerate() {
        assertTrue(s.isEndGenerate());
    }

    @Test
    void testIsEndIslands() {
        assertTrue(s.isEndIslands());
    }

    @Test
    void testIsFullArmorProtection() {
        assertFalse(s.isFullArmorProtection());
    }

    @Test
    void testIsHelmetProtection() {
        assertFalse(s.isHelmetProtection());
    }

    @Test
    void testIsKickedKeepInventory() {
        assertFalse(s.isKickedKeepInventory());
    }

    @Test
    void testIsCreateIslandOnFirstLoginEnabled() {
        assertFalse(s.isCreateIslandOnFirstLoginEnabled());
    }

    @Test
    void testGetCreateIslandOnFirstLoginDelay() {
        assertEquals(5, s.getCreateIslandOnFirstLoginDelay());
    }

    @Test
    void testIsCreateIslandOnFirstLoginAbortOnLogout() {
        assertTrue(s.isCreateIslandOnFirstLoginAbortOnLogout());
    }

    @Test
    void testIsLeaversLoseReset() {
        assertFalse(s.isLeaversLoseReset());
    }

    @Test
    void testIsNetherGenerate() {
        assertTrue(s.isNetherGenerate());
    }

    @Test
    void testIsNetherIslands() {
        assertTrue(s.isNetherIslands());
    }

    @Test
    void testIsNetherRoof() {
        assertTrue(s.isNetherRoof());
    }

    @Test
    void testIsOnJoinResetEnderChest() {
        assertFalse(s.isOnJoinResetEnderChest());
    }

    @Test
    void testIsOnJoinResetInventory() {
        assertFalse(s.isOnJoinResetInventory());
    }

    @Test
    void testIsOnJoinResetMoney() {
        assertFalse(s.isOnJoinResetMoney());
    }

    @Test
    void testIsOnLeaveResetEnderChest() {
        assertFalse(s.isOnLeaveResetEnderChest());
    }

    @Test
    void testIsOnLeaveResetInventory() {
        assertFalse(s.isOnLeaveResetInventory());
    }

    @Test
    void testIsOnLeaveResetMoney() {
        assertFalse(s.isOnLeaveResetMoney());
    }

    @Test
    void testIsRequireConfirmationToSetHomeInNether() {
        assertTrue(s.isRequireConfirmationToSetHomeInNether());
    }

    @Test
    void testIsRequireConfirmationToSetHomeInTheEnd() {
        assertTrue(s.isRequireConfirmationToSetHomeInTheEnd());
    }

    @Test
    void testIsTeamJoinDeathReset() {
        assertTrue(s.isTeamJoinDeathReset());
    }

    @Test
    void testIsUseOwnGenerator() {
        assertFalse(s.isUseOwnGenerator());
    }

    @Test
    void testIsWaterUnsafe() {
        assertTrue(s.isWaterUnsafe());
    }

    @Test
    void testSetAcidDamage() {
        s.setAcidDamage(99);
        assertEquals(99, s.getAcidDamage());
    }

    @Test
    void testSetAcidDamageAnimal() {
        s.setAcidDamageAnimal(99);
        assertEquals(99, s.getAcidDamageAnimal());
    }

    @Test
    void testSetAcidDamageChickens() {
        s.setAcidDamageChickens(true);
        assertTrue(s.isAcidDamageChickens());
    }

    @Test
    void testSetAcidDamageDelay() {
        s.setAcidDamageDelay(99);
        assertEquals(99, s.getAcidDamageDelay());
    }

    @Test
    void testSetAcidDamageMonster() {
        s.setAcidDamageMonster(99);
        assertEquals(99, s.getAcidDamageMonster());
    }

    @Test
    void testSetAcidDamageOp() {
        s.setAcidDamageOp(true);
        assertTrue(s.isAcidDamageOp());
    }

    @Test
    void testSetAcidDestroyItemTime() {
        s.setAcidDestroyItemTime(99);
        assertEquals(99, s.getAcidDestroyItemTime());
    }

    @Test
    @Disabled
    void testSetAcidEffects() {
        List<PotionEffectType> list = Collections.singletonList(PotionEffectType.ABSORPTION);
        s.setAcidEffects(list);
        assertEquals(list, s.getAcidEffects());
    }

    @Test
    void testSetAcidRainDamage() {
        s.setAcidRainDamage(99);
        assertEquals(99, s.getAcidRainDamage());
    }

    @Test
    void testSetAdminCommand() {
        s.setAdminCommand("admin");
        assertEquals("admin", s.getAdminCommandAliases());
    }

    @Test
    void testSetAllowSetHomeInNether() {
        s.setAllowSetHomeInNether(false);
        assertFalse(s.isAllowSetHomeInNether());
        s.setAllowSetHomeInNether(true);
        assertTrue(s.isAllowSetHomeInNether());
    }

    @Test
    void testSetAllowSetHomeInTheEnd() {
        s.setAllowSetHomeInTheEnd(false);
        assertFalse(s.isAllowSetHomeInTheEnd());
        s.setAllowSetHomeInTheEnd(true);
        assertTrue(s.isAllowSetHomeInTheEnd());
    }

    @Test
    void testSetBanLimit() {
        s.setBanLimit(99);
        assertEquals(99, s.getBanLimit());
    }

    @Test
    void testSetCustomRanks() {
        s.setCustomRanks(Collections.singletonMap("string", 10));
        assertEquals(10, (int) s.getCustomRanks().get("string"));
    }

    @Test
    void testSetDeathsCounted() {
        s.setDeathsCounted(false);
        assertFalse(s.isDeathsCounted());
        s.setDeathsCounted(true);
        assertTrue(s.isDeathsCounted());
    }

    @Test
    void testSetDeathsMax() {
        s.setDeathsMax(99);
        assertEquals(99, s.getDeathsMax());
    }

    @Test
    void testSetDefaultBiome() {
        s.setDefaultBiome(Biome.BADLANDS);
        assertEquals(Biome.BADLANDS, s.getDefaultBiome());
    }

    @Test
    void testSetDefaultGameMode() {
        s.setDefaultGameMode(GameMode.SPECTATOR);
        assertEquals(GameMode.SPECTATOR, s.getDefaultGameMode());
    }

    @Test
    void testSetDefaultIslandFlags() {
        s.setDefaultIslandFlagNames(Collections.singletonMap("ANIMAL_NATURAL_SPAWN", 10));
        assertEquals(10, (int) s.getDefaultIslandFlagNames().get("ANIMAL_NATURAL_SPAWN"));
    }

    @Test
    void testSetDefaultIslandSettings() {
        s.setDefaultIslandSettingNames(Collections.singletonMap("ANIMAL_NATURAL_SPAWN", 10));
        assertEquals(10, (int) s.getDefaultIslandSettingNames().get("ANIMAL_NATURAL_SPAWN"));
    }

    @Test
    void testSetDifficulty() {
        s.setDifficulty(Difficulty.PEACEFUL);
        assertEquals(Difficulty.PEACEFUL, s.getDifficulty());
    }

    @Test
    void testSetDragonSpawn() {
        s.setDragonSpawn(false);
        assertFalse(s.isDragonSpawn());
        s.setDragonSpawn(true);
        assertTrue(s.isDragonSpawn());
    }

    @Test
    void testSetEndGenerate() {
        s.setEndGenerate(false);
        assertFalse(s.isEndGenerate());
        s.setEndGenerate(true);
        assertTrue(s.isEndGenerate());
    }

    @Test
    void testSetEndIslands() {
        s.setEndIslands(false);
        assertFalse(s.isEndIslands());
        s.setEndIslands(true);
        assertTrue(s.isEndIslands());
    }

    @Test
    void testSetFriendlyName() {
        s.setFriendlyName("hshshs");
        assertEquals("hshshs", s.getFriendlyName());
    }

    @Test
    void testSetFullArmorProtection() {
        s.setFullArmorProtection(false);
        assertFalse(s.isFullArmorProtection());
        s.setFullArmorProtection(true);
        assertTrue(s.isFullArmorProtection());
    }

    @Test
    void testSetGeoLimitSettings() {
        s.setGeoLimitSettings(Collections.singletonList("ghghhg"));
        assertEquals("ghghhg", s.getGeoLimitSettings().get(0));
    }

    @Test
    void testSetHelmetProtection() {
        s.setHelmetProtection(false);
        assertFalse(s.isHelmetProtection());
        s.setHelmetProtection(true);
        assertTrue(s.isHelmetProtection());
    }

    @Test
    void testSetIslandCommand() {
        s.setIslandCommand("command");
        assertEquals("command", s.getPlayerCommandAliases());
    }

    @Test
    void testSetIslandDistance() {
        s.setIslandDistance(99);
        assertEquals(99, s.getIslandDistance());
    }

    @Test
    void testSetIslandHeight() {
        s.setIslandHeight(99);
        assertEquals(99, s.getIslandHeight());
    }

    @Test
    void testSetIslandProtectionRange() {
        s.setIslandProtectionRange(99);
        assertEquals(99, s.getIslandProtectionRange());
    }

    @Test
    void testSetIslandStartX() {
        s.setIslandStartX(99);
        assertEquals(99, s.getIslandStartX());
    }

    @Test
    void testSetIslandStartZ() {
        s.setIslandStartZ(99);
        assertEquals(99, s.getIslandStartZ());
    }

    @Test
    void testSetIslandXOffset() {
        s.setIslandXOffset(99);
        assertEquals(99, s.getIslandXOffset());
    }

    @Test
    void testSetIslandZOffset() {
        s.setIslandZOffset(99);
        assertEquals(99, s.getIslandZOffset());
    }

    @Test
    void testSetIvSettings() {
        s.setIvSettings(Collections.singletonList("ffff"));
        assertEquals("ffff", s.getIvSettings().get(0));
    }

    @Test
    void testSetKickedKeepInventory() {
        s.setKickedKeepInventory(false);
        assertFalse(s.isKickedKeepInventory());
        s.setKickedKeepInventory(true);
        assertTrue(s.isKickedKeepInventory());
    }

    @Test
    void testSetLeaversLoseReset() {
        s.setLeaversLoseReset(false);
        assertFalse(s.isLeaversLoseReset());
        s.setLeaversLoseReset(true);
        assertTrue(s.isLeaversLoseReset());
    }

    @Test
    void testSetMaxHomes() {
        s.setMaxHomes(99);
        assertEquals(99, s.getMaxHomes());
    }

    @Test
    void testSetMaxIslands() {
        s.setMaxIslands(99);
        assertEquals(99, s.getMaxIslands());
    }

    @Test
    void testSetMaxTeamSize() {
        s.setMaxTeamSize(99);
        assertEquals(99, s.getMaxTeamSize());
    }

    @Test
    void testSetNetherGenerate() {
        s.setNetherGenerate(false);
        assertFalse(s.isNetherGenerate());
        s.setNetherGenerate(true);
        assertTrue(s.isNetherGenerate());
    }

    @Test
    void testSetNetherIslands() {
        s.setNetherIslands(false);
        assertFalse(s.isNetherIslands());
        s.setNetherIslands(true);
        assertTrue(s.isNetherIslands());
    }

    @Test
    void testSetNetherRoof() {
        s.setNetherRoof(false);
        assertFalse(s.isNetherRoof());
        s.setNetherRoof(true);
        assertTrue(s.isNetherRoof());
    }

    @Test
    void testSetNetherSpawnRadius() {
        s.setNetherSpawnRadius(99);
        assertEquals(99, s.getNetherSpawnRadius());
    }

    @Test
    void testSetOnJoinResetEnderChest() {
        s.setOnJoinResetEnderChest(false);
        assertFalse(s.isOnJoinResetEnderChest());
        s.setOnJoinResetEnderChest(true);
        assertTrue(s.isOnJoinResetEnderChest());
    }

    @Test
    void testSetOnJoinResetInventory() {
        s.setOnJoinResetInventory(false);
        assertFalse(s.isOnJoinResetInventory());
        s.setOnJoinResetInventory(true);
        assertTrue(s.isOnJoinResetInventory());
    }

    @Test
    void testSetOnJoinResetMoney() {
        s.setOnJoinResetMoney(false);
        assertFalse(s.isOnJoinResetMoney());
        s.setOnJoinResetMoney(true);
        assertTrue(s.isOnJoinResetMoney());
    }

    @Test
    void testSetOnLeaveResetEnderChest() {
        s.setOnLeaveResetEnderChest(false);
        assertFalse(s.isOnLeaveResetEnderChest());
        s.setOnLeaveResetEnderChest(true);
        assertTrue(s.isOnLeaveResetEnderChest());
    }

    @Test
    void testSetOnLeaveResetInventory() {
        s.setOnLeaveResetInventory(false);
        assertFalse(s.isOnLeaveResetInventory());
        s.setOnLeaveResetInventory(true);
        assertTrue(s.isOnLeaveResetInventory());
    }

    @Test
    void testSetOnLeaveResetMoney() {
        s.setOnLeaveResetMoney(false);
        assertFalse(s.isOnLeaveResetMoney());
        s.setOnLeaveResetMoney(true);
        assertTrue(s.isOnLeaveResetMoney());
    }

    @Test
    void testSetRemoveMobsWhitelist() {
        s.setRemoveMobsWhitelist(Collections.singleton(EntityType.GHAST));
        assertTrue(s.getRemoveMobsWhitelist().contains(EntityType.GHAST));
    }

    @Test
    void testSetRequireConfirmationToSetHomeInNether() {
        s.setRequireConfirmationToSetHomeInNether(false);
        assertFalse(s.isRequireConfirmationToSetHomeInNether());
        s.setRequireConfirmationToSetHomeInNether(true);
        assertTrue(s.isRequireConfirmationToSetHomeInNether());
    }

    @Test
    void testSetRequireConfirmationToSetHomeInTheEnd() {
        s.setRequireConfirmationToSetHomeInTheEnd(false);
        assertFalse(s.isRequireConfirmationToSetHomeInTheEnd());
        s.setRequireConfirmationToSetHomeInTheEnd(true);
        assertTrue(s.isRequireConfirmationToSetHomeInTheEnd());
    }

    @Test
    void testSetResetEpoch() {
        s.setResetEpoch(3456L);
        assertEquals(3456L, s.getResetEpoch());
    }

    @Test
    void testSetResetLimit() {
        s.setResetLimit(99);
        assertEquals(99, s.getResetLimit());
    }

    @Test
    void testSetSeaHeight() {
        s.setSeaHeight(99);
        assertEquals(99, s.getSeaHeight());
    }

    @Test
    void testSetNetherSeaHeight() {
        s.setNetherSeaHeight(99);
        assertEquals(99, s.getNetherSeaHeight());
    }

    @Test
    void testSetEndSeaHeight() {
        s.setEndSeaHeight(99);
        assertEquals(99, s.getEndSeaHeight());
    }

    @Test
    void testSetTeamJoinDeathReset() {
        s.setTeamJoinDeathReset(false);
        assertFalse(s.isTeamJoinDeathReset());
        s.setTeamJoinDeathReset(true);
        assertTrue(s.isTeamJoinDeathReset());
    }

    @Test
    void testSetUseOwnGenerator() {
        s.setUseOwnGenerator(false);
        assertFalse(s.isUseOwnGenerator());
        s.setUseOwnGenerator(true);
        assertTrue(s.isUseOwnGenerator());
    }

    @Test
    void testSetHiddenFlags() {
        s.setHiddenFlags(Collections.singletonList("flag"));
        assertEquals("flag", s.getHiddenFlags().get(0));
    }

    @Test
    void testSetVisitorBannedCommands() {
        s.setVisitorBannedCommands(Collections.singletonList("flag"));
        assertEquals("flag", s.getVisitorBannedCommands().get(0));
    }

    @Test
    void testSetFallingBannedCommands() {
        s.setFallingBannedCommands(Collections.singletonList("flag"));
        assertEquals("flag", s.getFallingBannedCommands().get(0));
    }

    @Test
    void testSetWorldFlags() {
        s.setWorldFlags(Collections.singletonMap("flag", true));
        assertTrue(s.getWorldFlags().get("flag"));
    }

    @Test
    void testSetWorldName() {
        s.setWorldName("ugga");
        assertEquals("ugga", s.getWorldName());
    }

    @Test
    void testIsAcidDamageSnow() {
        assertFalse(s.isAcidDamageSnow());
    }

    @Test
    void testSetAcidDamageSnow() {
        s.setAcidDamageSnow(false);
        assertFalse(s.isAcidDamageSnow());
        s.setAcidDamageSnow(true);
        assertTrue(s.isAcidDamageSnow());
    }

    @Test
    void testIsDeathsResetOnNewIsland() {
        assertTrue(s.isDeathsResetOnNewIsland());
    }

    @Test
    void testSetDeathsResetOnNewIsland() {
        s.setDeathsResetOnNewIsland(false);
        assertFalse(s.isDeathsResetOnNewIsland());
        s.setDeathsResetOnNewIsland(true);
        assertTrue(s.isDeathsResetOnNewIsland());
    }

    @Test
    void testGetOnJoinCommands() {
        assertTrue(s.getOnJoinCommands().isEmpty());
    }

    @Test
    void testSetOnJoinCommands() {
        s.setOnJoinCommands(Collections.singletonList("command"));
        assertEquals("command", s.getOnJoinCommands().get(0));
    }

    @Test
    void testGetOnLeaveCommands() {
        assertTrue(s.getOnLeaveCommands().isEmpty());
    }

    @Test
    void testSetOnLeaveCommands() {
        s.setOnLeaveCommands(Collections.singletonList("command"));
        assertEquals("command", s.getOnLeaveCommands().get(0));
    }

    @Test
    void testIsOnJoinResetHealth() {
        assertTrue(s.isOnJoinResetHealth());
    }

    @Test
    void testSetOnJoinResetHealth() {
        s.setOnJoinResetHealth(false);
        assertFalse(s.isOnJoinResetHealth());
        s.setOnJoinResetHealth(true);
        assertTrue(s.isOnJoinResetHealth());
    }

    @Test
    void testIsOnJoinResetHunger() {
        assertTrue(s.isOnJoinResetHunger());
    }

    @Test
    void testSetOnJoinResetHunger() {
        s.setOnJoinResetHunger(false);
        assertFalse(s.isOnJoinResetHunger());
        s.setOnJoinResetHunger(true);
        assertTrue(s.isOnJoinResetHunger());
    }

    @Test
    void testIsOnJoinResetXP() {
        assertFalse(s.isOnJoinResetXP());
    }

    @Test
    void testSetOnJoinResetXP() {
        s.setOnJoinResetXP(false);
        assertFalse(s.isOnJoinResetXP());
        s.setOnJoinResetXP(true);
        assertTrue(s.isOnJoinResetXP());
    }

    @Test
    void testIsOnLeaveResetHealth() {
        assertFalse(s.isOnLeaveResetHealth());
    }

    @Test
    void testSetOnLeaveResetHealth() {
        s.setOnLeaveResetHealth(false);
        assertFalse(s.isOnLeaveResetHealth());
        s.setOnLeaveResetHealth(true);
        assertTrue(s.isOnLeaveResetHealth());
    }

    @Test
    void testIsOnLeaveResetHunger() {
        assertFalse(s.isOnLeaveResetHunger());
    }

    @Test
    void testSetOnLeaveResetHunger() {
        s.setOnLeaveResetHunger(false);
        assertFalse(s.isOnLeaveResetHunger());
        s.setOnLeaveResetHunger(true);
        assertTrue(s.isOnLeaveResetHunger());
    }

    @Test
    void testIsOnLeaveResetXP() {
        assertFalse(s.isOnLeaveResetXP());
    }

    @Test
    void testSetOnLeaveResetXP() {
        s.setOnLeaveResetXP(false);
        assertFalse(s.isOnLeaveResetXP());
        s.setOnLeaveResetXP(true);
        assertTrue(s.isOnLeaveResetXP());
    }

    @Test
    void testSetCreateIslandOnFirstLoginEnabled() {
        s.setCreateIslandOnFirstLoginEnabled(false);
        assertFalse(s.isCreateIslandOnFirstLoginEnabled());
        s.setCreateIslandOnFirstLoginEnabled(true);
        assertTrue(s.isCreateIslandOnFirstLoginEnabled());
    }

    @Test
    void testSetCreateIslandOnFirstLoginDelay() {
        s.setCreateIslandOnFirstLoginDelay(40);
        assertEquals(40, s.getCreateIslandOnFirstLoginDelay());
    }

    @Test
    void testSetCreateIslandOnFirstLoginAbortOnLogout() {
        s.setCreateIslandOnFirstLoginAbortOnLogout(false);
        assertFalse(s.isCreateIslandOnFirstLoginAbortOnLogout());
        s.setCreateIslandOnFirstLoginAbortOnLogout(true);
        assertTrue(s.isCreateIslandOnFirstLoginAbortOnLogout());
    }

    @Test
    void testIsPasteMissingIslands() {
        assertFalse(s.isPasteMissingIslands());
    }

    @Test
    void testSetPasteMissingIslands() {
        s.setPasteMissingIslands(false);
        assertFalse(s.isPasteMissingIslands());
        s.setPasteMissingIslands(true);
        assertTrue(s.isPasteMissingIslands());
    }

    @Test
    void testGetAcidRainEffects() {
        assertTrue(s.getAcidRainEffects().isEmpty());
    }

    @Test
    @Disabled("Bukkit made this so we can't test")
    void testSetAcidRainEffects() {
        s.setAcidRainEffects(Collections.singletonList(PotionEffectType.BAD_OMEN));
        assertEquals(PotionEffectType.BAD_OMEN, s.getAcidRainEffects().get(0));
    }

    @Test
    void testGetRainEffectDuation() {
        assertEquals(10, s.getRainEffectDuation());
    }

    @Test
    void testSetRainEffectDuation() {
        s.setRainEffectDuation(99);
        assertEquals(99, s.getRainEffectDuation());
    }

    @Test
    void testGetAcidEffectDuation() {
        assertEquals(30, s.getAcidEffectDuation());
    }

    @Test
    void testSetAcidEffectDuation() {
        s.setAcidEffectDuation(99);
        assertEquals(99, s.getAcidEffectDuation());
    }

    @Test
    void testGetSpawnLimitMonsters() {
        assertEquals(-1, s.getSpawnLimitMonsters());
    }

    @Test
    void testSetSpawnLimitMonsters() {
        s.setSpawnLimitMonsters(99);
        assertEquals(99, s.getSpawnLimitMonsters());
    }

    @Test
    void testGetSpawnLimitAnimals() {
        assertEquals(-1, s.getSpawnLimitAnimals());
    }

    @Test
    void testSetSpawnLimitAnimals() {
        s.setSpawnLimitAnimals(99);
        assertEquals(99, s.getSpawnLimitAnimals());
    }

    @Test
    void testGetSpawnLimitWaterAnimals() {
        assertEquals(-1, s.getSpawnLimitWaterAnimals());
    }

    @Test
    void testSetSpawnLimitWaterAnimals() {
        s.setSpawnLimitWaterAnimals(99);
        assertEquals(99, s.getSpawnLimitWaterAnimals());
    }

    @Test
    void testGetSpawnLimitAmbient() {
        assertEquals(-1, s.getSpawnLimitAmbient());
    }

    @Test
    void testSetSpawnLimitAmbient() {
        s.setSpawnLimitAmbient(99);
        assertEquals(99, s.getSpawnLimitAmbient());
    }

    @Test
    void testGetTicksPerAnimalSpawns() {
        assertEquals(-1, s.getTicksPerAnimalSpawns());
    }

    @Test
    void testSetTicksPerAnimalSpawns() {
        s.setTicksPerAnimalSpawns(99);
        assertEquals(99, s.getTicksPerAnimalSpawns());
    }

    @Test
    void testGetTicksPerMonsterSpawns() {
        assertEquals(-1, s.getTicksPerMonsterSpawns());
    }

    @Test
    void testSetTicksPerMonsterSpawns() {
        s.setTicksPerMonsterSpawns(99);
        assertEquals(99, s.getTicksPerMonsterSpawns());
    }

    @Test
    void testGetMaxCoopSize() {
        assertEquals(4, s.getMaxCoopSize());
    }

    @Test
    void testSetMaxCoopSize() {
        s.setMaxCoopSize(99);
        assertEquals(99, s.getMaxCoopSize());
    }

    @Test
    void testGetMaxTrustSize() {
        assertEquals(4, s.getMaxTrustSize());
    }

    @Test
    void testSetMaxTrustSize() {
        s.setMaxTrustSize(99);
        assertEquals(99, s.getMaxTrustSize());
    }

    @Test
    void testGetPlayerCommandAliases() {
        assertEquals("ai", s.getPlayerCommandAliases());
    }

    @Test
    void testSetPlayerCommandAliases() {
        s.setPlayerCommandAliases("adm");
        assertEquals("adm", s.getPlayerCommandAliases());
    }

    @Test
    void testGetAdminCommandAliases() {
        assertEquals("acid", s.getAdminCommandAliases());
    }

    @Test
    void testSetAdminCommandAliases() {
        s.setAdminCommandAliases("adm");
        assertEquals("adm", s.getAdminCommandAliases());
    }

    @Test
    void testGetDefaultNewPlayerAction() {
        assertEquals("create", s.getDefaultNewPlayerAction());
    }

    @Test
    void testSetDefaultNewPlayerAction() {
        s.setDefaultNewPlayerAction("cr");
        assertEquals("cr", s.getDefaultNewPlayerAction());
    }

    @Test
    void testGetDefaultPlayerAction() {
        assertEquals("go", s.getDefaultPlayerAction());
    }

    @Test
    void testSetDefaultPlayerAction() {
        s.setDefaultPlayerAction("go2");
        assertEquals("go2", s.getDefaultPlayerAction());
    }

    @Test
    void testGetDefaultNetherBiome() {
        assertEquals(Biome.NETHER_WASTES, s.getDefaultNetherBiome());
    }

    @Test
    void testSetDefaultNetherBiome() {
        s.setDefaultNetherBiome(Biome.END_BARRENS);
        assertEquals(Biome.END_BARRENS, s.getDefaultNetherBiome());
    }

    @Test
    void testGetDefaultEndBiome() {
        assertEquals(Biome.THE_END, s.getDefaultEndBiome());
    }

    @Test
    void testSetDefaultEndBiome() {
        s.setDefaultEndBiome(Biome.END_BARRENS);
        assertEquals(Biome.END_BARRENS, s.getDefaultEndBiome());
    }

}
