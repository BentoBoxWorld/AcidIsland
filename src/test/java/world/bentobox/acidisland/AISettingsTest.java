package world.bentobox.acidisland;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import world.bentobox.bentobox.lists.Flags;

/**
 * @author tastybento
 *
 */
public class AISettingsTest {

    /**
     * Class under test
     */
    private AISettings s;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        s = new AISettings();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getAcidDamage()}.
     */
    @Test
    public void testGetAcidDamage() {
        assertEquals(10, s.getAcidDamage());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getAcidDamageAnimal()}.
     */
    @Test
    public void testGetAcidDamageAnimal() {
        assertEquals(1, s.getAcidDamageAnimal());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getAcidDamageDelay()}.
     */
    @Test
    public void testGetAcidDamageDelay() {
        assertEquals(2, s.getAcidDamageDelay());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getAcidDamageMonster()}.
     */
    @Test
    public void testGetAcidDamageMonster() {
        assertEquals(5, s.getAcidDamageMonster());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getAcidDestroyItemTime()}.
     */
    @Test
    public void testGetAcidDestroyItemTime() {
        assertEquals(0, s.getAcidDestroyItemTime());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getAcidEffects()}.
     */
    @Test
    public void testGetAcidEffects() {
        assertTrue(s.getAcidEffects().isEmpty());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getAcidRainDamage()}.
     */
    @Test
    public void testGetAcidRainDamage() {
        assertEquals(1, s.getAcidRainDamage());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getBanLimit()}.
     */
    @Test
    public void testGetBanLimit() {
        assertEquals(-1, s.getBanLimit());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getCustomRanks()}.
     */
    @Test
    public void testGetCustomRanks() {
        assertTrue(s.getCustomRanks().isEmpty());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getDeathsMax()}.
     */
    @Test
    public void testGetDeathsMax() {
        assertEquals(10, s.getDeathsMax());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getDefaultBiome()}.
     */
    @Test
    public void testGetDefaultBiome() {
        assertEquals(Biome.WARM_OCEAN, s.getDefaultBiome());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getDefaultGameMode()}.
     */
    @Test
    public void testGetDefaultGameMode() {
        assertEquals(GameMode.SURVIVAL, s.getDefaultGameMode());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getDifficulty()}.
     */
    @Test
    public void testGetDifficulty() {
        assertEquals(Difficulty.NORMAL, s.getDifficulty());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getEndSeaHeight()}.
     */
    @Test
    public void testGetEndSeaHeight() {
        assertEquals(54, s.getEndSeaHeight());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getFriendlyName()}.
     */
    @Test
    public void testGetFriendlyName() {
        assertEquals("AcidIsland", s.getFriendlyName());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getGeoLimitSettings()}.
     */
    @Test
    public void testGetGeoLimitSettings() {
        assertTrue(s.getGeoLimitSettings().isEmpty());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getIslandDistance()}.
     */
    @Test
    public void testGetIslandDistance() {
        assertEquals(64, s.getIslandDistance());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getIslandHeight()}.
     */
    @Test
    public void testGetIslandHeight() {
        assertEquals(50, s.getIslandHeight());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getIslandProtectionRange()}.
     */
    @Test
    public void testGetIslandProtectionRange() {
        assertEquals(50, s.getIslandProtectionRange());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getIslandStartX()}.
     */
    @Test
    public void testGetIslandStartX() {
        assertEquals(0, s.getIslandStartX());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getIslandStartZ()}.
     */
    @Test
    public void testGetIslandStartZ() {
        assertEquals(0, s.getIslandStartZ());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getIslandXOffset()}.
     */
    @Test
    public void testGetIslandXOffset() {
        assertEquals(0, s.getIslandXOffset());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getIslandZOffset()}.
     */
    @Test
    public void testGetIslandZOffset() {
        assertEquals(0, s.getIslandZOffset());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getIvSettings()}.
     */
    @Test
    public void testGetIvSettings() {
        assertTrue(s.getIvSettings().isEmpty());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getMaxHomes()}.
     */
    @Test
    public void testGetMaxHomes() {
        assertEquals(5, s.getMaxHomes());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getMaxIslands()}.
     */
    @Test
    public void testGetMaxIslands() {
        assertEquals(-1, s.getMaxIslands());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getMaxTeamSize()}.
     */
    @Test
    public void testGetMaxTeamSize() {
        assertEquals(4, s.getMaxTeamSize());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getNetherSeaHeight()}.
     */
    @Test
    public void testGetNetherSeaHeight() {
        assertEquals(54, s.getNetherSeaHeight());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getNetherSpawnRadius()}.
     */
    @Test
    public void testGetNetherSpawnRadius() {
        assertEquals(32, s.getNetherSpawnRadius());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getPermissionPrefix()}.
     */
    @Test
    public void testGetPermissionPrefix() {
        assertEquals("acidisland", s.getPermissionPrefix());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getRemoveMobsWhitelist()}.
     */
    @Test
    public void testGetRemoveMobsWhitelist() {
        assertTrue(s.getRemoveMobsWhitelist().isEmpty());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getResetEpoch()}.
     */
    @Test
    public void testGetResetEpoch() {
        assertEquals(0, s.getResetEpoch());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getResetLimit()}.
     */
    @Test
    public void testGetResetLimit() {
        assertEquals(-1, s.getResetLimit());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getSeaHeight()}.
     */
    @Test
    public void testGetSeaHeight() {
        assertEquals(54, s.getSeaHeight());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getHiddenFlags()}.
     */
    @Test
    public void testGetHiddenFlags() {
        assertTrue(s.getHiddenFlags().isEmpty());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getVisitorBannedCommands()}.
     */
    @Test
    public void testGetVisitorBannedCommands() {
        assertTrue(s.getVisitorBannedCommands().isEmpty());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getFallingBannedCommands()}.
     */
    @Test
    public void testGetFallingBannedCommands() {
        assertTrue(s.getFallingBannedCommands().isEmpty());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getWorldFlags()}.
     */
    @Test
    public void testGetWorldFlags() {
        assertTrue(s.getWorldFlags().isEmpty());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getWorldName()}.
     */
    @Test
    public void testGetWorldName() {
        assertEquals("acidisland_world", s.getWorldName());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isAcidDamageChickens()}.
     */
    @Test
    public void testIsAcidDamageChickens() {
        assertFalse(s.isAcidDamageChickens());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isAcidDamageOp()}.
     */
    @Test
    public void testIsAcidDamageOp() {
        assertFalse(s.isAcidDamageOp());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isAllowSetHomeInNether()}.
     */
    @Test
    public void testIsAllowSetHomeInNether() {
        assertTrue(s.isAllowSetHomeInNether());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isAllowSetHomeInTheEnd()}.
     */
    @Test
    public void testIsAllowSetHomeInTheEnd() {
        assertTrue(s.isAllowSetHomeInTheEnd());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isDeathsCounted()}.
     */
    @Test
    public void testIsDeathsCounted() {
        assertTrue(s.isDeathsCounted());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isDragonSpawn()}.
     */
    @Test
    public void testIsDragonSpawn() {
        assertFalse(s.isDragonSpawn());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isEndGenerate()}.
     */
    @Test
    public void testIsEndGenerate() {
        assertTrue(s.isEndGenerate());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isEndIslands()}.
     */
    @Test
    public void testIsEndIslands() {
        assertTrue(s.isEndIslands());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isFullArmorProtection()}.
     */
    @Test
    public void testIsFullArmorProtection() {
        assertFalse(s.isFullArmorProtection());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isHelmetProtection()}.
     */
    @Test
    public void testIsHelmetProtection() {
        assertFalse(s.isHelmetProtection());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isKickedKeepInventory()}.
     */
    @Test
    public void testIsKickedKeepInventory() {
        assertFalse(s.isKickedKeepInventory());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isCreateIslandOnFirstLoginEnabled()}.
     */
    @Test
    public void testIsCreateIslandOnFirstLoginEnabled() {
        assertFalse(s.isCreateIslandOnFirstLoginEnabled());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getCreateIslandOnFirstLoginDelay()}.
     */
    @Test
    public void testGetCreateIslandOnFirstLoginDelay() {
        assertEquals(5, s.getCreateIslandOnFirstLoginDelay());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isCreateIslandOnFirstLoginAbortOnLogout()}.
     */
    @Test
    public void testIsCreateIslandOnFirstLoginAbortOnLogout() {
        assertTrue(s.isCreateIslandOnFirstLoginAbortOnLogout());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isLeaversLoseReset()}.
     */
    @Test
    public void testIsLeaversLoseReset() {
        assertFalse(s.isLeaversLoseReset());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isNetherGenerate()}.
     */
    @Test
    public void testIsNetherGenerate() {
        assertTrue(s.isNetherGenerate());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isNetherIslands()}.
     */
    @Test
    public void testIsNetherIslands() {
        assertTrue(s.isNetherIslands());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isNetherRoof()}.
     */
    @Test
    public void testIsNetherRoof() {
        assertTrue(s.isNetherRoof());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isOnJoinResetEnderChest()}.
     */
    @Test
    public void testIsOnJoinResetEnderChest() {
        assertFalse(s.isOnJoinResetEnderChest());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isOnJoinResetInventory()}.
     */
    @Test
    public void testIsOnJoinResetInventory() {
        assertFalse(s.isOnJoinResetInventory());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isOnJoinResetMoney()}.
     */
    @Test
    public void testIsOnJoinResetMoney() {
        assertFalse(s.isOnJoinResetMoney());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isOnLeaveResetEnderChest()}.
     */
    @Test
    public void testIsOnLeaveResetEnderChest() {
        assertFalse(s.isOnLeaveResetEnderChest());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isOnLeaveResetInventory()}.
     */
    @Test
    public void testIsOnLeaveResetInventory() {
        assertFalse(s.isOnLeaveResetInventory());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isOnLeaveResetMoney()}.
     */
    @Test
    public void testIsOnLeaveResetMoney() {
        assertFalse(s.isOnLeaveResetMoney());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isRequireConfirmationToSetHomeInNether()}.
     */
    @Test
    public void testIsRequireConfirmationToSetHomeInNether() {
        assertTrue(s.isRequireConfirmationToSetHomeInNether());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isRequireConfirmationToSetHomeInTheEnd()}.
     */
    @Test
    public void testIsRequireConfirmationToSetHomeInTheEnd() {
        assertTrue(s.isRequireConfirmationToSetHomeInTheEnd());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isTeamJoinDeathReset()}.
     */
    @Test
    public void testIsTeamJoinDeathReset() {
        assertTrue(s.isTeamJoinDeathReset());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isUseOwnGenerator()}.
     */
    @Test
    public void testIsUseOwnGenerator() {
        assertFalse(s.isUseOwnGenerator());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isWaterUnsafe()}.
     */
    @Test
    public void testIsWaterUnsafe() {
        assertTrue(s.isWaterUnsafe());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setAcidDamage(int)}.
     */
    @Test
    public void testSetAcidDamage() {
        s.setAcidDamage(99);
        assertEquals(99, s.getAcidDamage());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setAcidDamageAnimal(int)}.
     */
    @Test
    public void testSetAcidDamageAnimal() {
        s.setAcidDamageAnimal(99);
        assertEquals(99, s.getAcidDamageAnimal());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setAcidDamageChickens(boolean)}.
     */
    @Test
    public void testSetAcidDamageChickens() {
        s.setAcidDamageChickens(true);
        assertTrue(s.isAcidDamageChickens());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setAcidDamageDelay(long)}.
     */
    @Test
    public void testSetAcidDamageDelay() {
        s.setAcidDamageDelay(99);
        assertEquals(99, s.getAcidDamageDelay());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setAcidDamageMonster(int)}.
     */
    @Test
    public void testSetAcidDamageMonster() {
        s.setAcidDamageMonster(99);
        assertEquals(99, s.getAcidDamageMonster());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setAcidDamageOp(boolean)}.
     */
    @Test
    public void testSetAcidDamageOp() {
        s.setAcidDamageOp(true);
        assertTrue(s.isAcidDamageOp());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setAcidDestroyItemTime(long)}.
     */
    @Test
    public void testSetAcidDestroyItemTime() {
        s.setAcidDestroyItemTime(99);
        assertEquals(99, s.getAcidDestroyItemTime());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setAcidEffects(java.util.List)}.
     */
    @Test
    public void testSetAcidEffects() {
        List<PotionEffectType> list = Collections.singletonList(PotionEffectType.ABSORPTION);
        s.setAcidEffects(list);
        assertEquals(list, s.getAcidEffects());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setAcidRainDamage(int)}.
     */
    @Test
    public void testSetAcidRainDamage() {
        s.setAcidRainDamage(99);
        assertEquals(99, s.getAcidRainDamage());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setAdminCommand(java.lang.String)}.
     */
    @Test
    public void testSetAdminCommand() {
        s.setAdminCommand("admin");
        assertEquals("admin", s.getAdminCommandAliases());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setAllowSetHomeInNether(boolean)}.
     */
    @Test
    public void testSetAllowSetHomeInNether() {
        s.setAllowSetHomeInNether(false);
        assertFalse(s.isAllowSetHomeInNether());
        s.setAllowSetHomeInNether(true);
        assertTrue(s.isAllowSetHomeInNether());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setAllowSetHomeInTheEnd(boolean)}.
     */
    @Test
    public void testSetAllowSetHomeInTheEnd() {
        s.setAllowSetHomeInTheEnd(false);
        assertFalse(s.isAllowSetHomeInTheEnd());
        s.setAllowSetHomeInTheEnd(true);
        assertTrue(s.isAllowSetHomeInTheEnd());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setBanLimit(int)}.
     */
    @Test
    public void testSetBanLimit() {
        s.setBanLimit(99);
        assertEquals(99, s.getBanLimit());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setCustomRanks(java.util.Map)}.
     */
    @Test
    public void testSetCustomRanks() {
        s.setCustomRanks(Collections.singletonMap("string", 10));
        assertEquals(10, (int)s.getCustomRanks().get("string"));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setDeathsCounted(boolean)}.
     */
    @Test
    public void testSetDeathsCounted() {
        s.setDeathsCounted(false);
        assertFalse(s.isDeathsCounted());
        s.setDeathsCounted(true);
        assertTrue(s.isDeathsCounted());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setDeathsMax(int)}.
     */
    @Test
    public void testSetDeathsMax() {
        s.setDeathsMax(99);
        assertEquals(99, s.getDeathsMax());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setDefaultBiome(org.bukkit.block.Biome)}.
     */
    @Test
    public void testSetDefaultBiome() {
        s.setDefaultBiome(Biome.BADLANDS);
        assertEquals(Biome.BADLANDS, s.getDefaultBiome());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setDefaultGameMode(org.bukkit.GameMode)}.
     */
    @Test
    public void testSetDefaultGameMode() {
        s.setDefaultGameMode(GameMode.SPECTATOR);
        assertEquals(GameMode.SPECTATOR, s.getDefaultGameMode());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setDefaultIslandFlagNames(java.util.Map)}.
     */
    @Test
    public void testSetDefaultIslandFlags() {
        s.setDefaultIslandFlagNames(Collections.singletonMap(Flags.ANIMAL_NATURAL_SPAWN.getID(), 10));
        assertEquals(10, (int)s.getDefaultIslandFlagNames().get(Flags.ANIMAL_NATURAL_SPAWN.getID()));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setDefaultIslandSettingNames(java.util.Map)}.
     */
    @Test
    public void testSetDefaultIslandSettings() {
        s.setDefaultIslandSettingNames(Collections.singletonMap(Flags.ANIMAL_NATURAL_SPAWN.getID(), 10));
        assertEquals(10, (int)s.getDefaultIslandSettingNames().get(Flags.ANIMAL_NATURAL_SPAWN.getID()));

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setDifficulty(org.bukkit.Difficulty)}.
     */
    @Test
    public void testSetDifficulty() {
        s.setDifficulty(Difficulty.PEACEFUL);
        assertEquals(Difficulty.PEACEFUL, s.getDifficulty());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setDragonSpawn(boolean)}.
     */
    @Test
    public void testSetDragonSpawn() {
        s.setDragonSpawn(false);
        assertFalse(s.isDragonSpawn());
        s.setDragonSpawn(true);
        assertTrue(s.isDragonSpawn());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setEndGenerate(boolean)}.
     */
    @Test
    public void testSetEndGenerate() {
        s.setEndGenerate(false);
        assertFalse(s.isEndGenerate());
        s.setEndGenerate(true);
        assertTrue(s.isEndGenerate());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setEndIslands(boolean)}.
     */
    @Test
    public void testSetEndIslands() {
        s.setEndIslands(false);
        assertFalse(s.isEndIslands());
        s.setEndIslands(true);
        assertTrue(s.isEndIslands());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setFriendlyName(java.lang.String)}.
     */
    @Test
    public void testSetFriendlyName() {
        s.setFriendlyName("hshshs");
        assertEquals("hshshs", s.getFriendlyName());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setFullArmorProtection(boolean)}.
     */
    @Test
    public void testSetFullArmorProtection() {
        s.setFullArmorProtection(false);
        assertFalse(s.isFullArmorProtection());
        s.setFullArmorProtection(true);
        assertTrue(s.isFullArmorProtection());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setGeoLimitSettings(java.util.List)}.
     */
    @Test
    public void testSetGeoLimitSettings() {
        s.setGeoLimitSettings(Collections.singletonList("ghghhg"));
        assertEquals("ghghhg", s.getGeoLimitSettings().get(0));

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setHelmetProtection(boolean)}.
     */
    @Test
    public void testSetHelmetProtection() {
        s.setHelmetProtection(false);
        assertFalse(s.isHelmetProtection());
        s.setHelmetProtection(true);
        assertTrue(s.isHelmetProtection());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setIslandCommand(java.lang.String)}.
     */
    @Test
    public void testSetIslandCommand() {
        s.setIslandCommand("command");
        assertEquals("command", s.getPlayerCommandAliases());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setIslandDistance(int)}.
     */
    @Test
    public void testSetIslandDistance() {
        s.setIslandDistance(99);
        assertEquals(99, s.getIslandDistance());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setIslandHeight(int)}.
     */
    @Test
    public void testSetIslandHeight() {
        s.setIslandHeight(99);
        assertEquals(99, s.getIslandHeight());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setIslandProtectionRange(int)}.
     */
    @Test
    public void testSetIslandProtectionRange() {
        s.setIslandProtectionRange(99);
        assertEquals(99, s.getIslandProtectionRange());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setIslandStartX(int)}.
     */
    @Test
    public void testSetIslandStartX() {
        s.setIslandStartX(99);
        assertEquals(99, s.getIslandStartX());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setIslandStartZ(int)}.
     */
    @Test
    public void testSetIslandStartZ() {
        s.setIslandStartZ(99);
        assertEquals(99, s.getIslandStartZ());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setIslandXOffset(int)}.
     */
    @Test
    public void testSetIslandXOffset() {
        s.setIslandXOffset(99);
        assertEquals(99, s.getIslandXOffset());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setIslandZOffset(int)}.
     */
    @Test
    public void testSetIslandZOffset() {
        s.setIslandZOffset(99);
        assertEquals(99, s.getIslandZOffset());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setIvSettings(java.util.List)}.
     */
    @Test
    public void testSetIvSettings() {
        s.setIvSettings(Collections.singletonList("ffff"));
        assertEquals("ffff", s.getIvSettings().get(0));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setKickedKeepInventory(boolean)}.
     */
    @Test
    public void testSetKickedKeepInventory() {
        s.setKickedKeepInventory(false);
        assertFalse(s.isKickedKeepInventory());
        s.setKickedKeepInventory(true);
        assertTrue(s.isKickedKeepInventory());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setLeaversLoseReset(boolean)}.
     */
    @Test
    public void testSetLeaversLoseReset() {
        s.setLeaversLoseReset(false);
        assertFalse(s.isLeaversLoseReset());
        s.setLeaversLoseReset(true);
        assertTrue(s.isLeaversLoseReset());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setMaxHomes(int)}.
     */
    @Test
    public void testSetMaxHomes() {
        s.setMaxHomes(99);
        assertEquals(99, s.getMaxHomes());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setMaxIslands(int)}.
     */
    @Test
    public void testSetMaxIslands() {
        s.setMaxIslands(99);
        assertEquals(99, s.getMaxIslands());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setMaxTeamSize(int)}.
     */
    @Test
    public void testSetMaxTeamSize() {
        s.setMaxTeamSize(99);
        assertEquals(99, s.getMaxTeamSize());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setNetherGenerate(boolean)}.
     */
    @Test
    public void testSetNetherGenerate() {
        s.setNetherGenerate(false);
        assertFalse(s.isNetherGenerate());
        s.setNetherGenerate(true);
        assertTrue(s.isNetherGenerate());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setNetherIslands(boolean)}.
     */
    @Test
    public void testSetNetherIslands() {
        s.setNetherIslands(false);
        assertFalse(s.isNetherIslands());
        s.setNetherIslands(true);
        assertTrue(s.isNetherIslands());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setNetherRoof(boolean)}.
     */
    @Test
    public void testSetNetherRoof() {
        s.setNetherRoof(false);
        assertFalse(s.isNetherRoof());
        s.setNetherRoof(true);
        assertTrue(s.isNetherRoof());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setNetherSpawnRadius(int)}.
     */
    @Test
    public void testSetNetherSpawnRadius() {
        s.setNetherSpawnRadius(99);
        assertEquals(99,s.getNetherSpawnRadius());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setOnJoinResetEnderChest(boolean)}.
     */
    @Test
    public void testSetOnJoinResetEnderChest() {
        s.setOnJoinResetEnderChest(false);
        assertFalse(s.isOnJoinResetEnderChest());
        s.setOnJoinResetEnderChest(true);
        assertTrue(s.isOnJoinResetEnderChest());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setOnJoinResetInventory(boolean)}.
     */
    @Test
    public void testSetOnJoinResetInventory() {
        s.setOnJoinResetInventory(false);
        assertFalse(s.isOnJoinResetInventory());
        s.setOnJoinResetInventory(true);
        assertTrue(s.isOnJoinResetInventory());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setOnJoinResetMoney(boolean)}.
     */
    @Test
    public void testSetOnJoinResetMoney() {
        s.setOnJoinResetMoney(false);
        assertFalse(s.isOnJoinResetMoney());
        s.setOnJoinResetMoney(true);
        assertTrue(s.isOnJoinResetMoney());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setOnLeaveResetEnderChest(boolean)}.
     */
    @Test
    public void testSetOnLeaveResetEnderChest() {
        s.setOnLeaveResetEnderChest(false);
        assertFalse(s.isOnLeaveResetEnderChest());
        s.setOnLeaveResetEnderChest(true);
        assertTrue(s.isOnLeaveResetEnderChest());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setOnLeaveResetInventory(boolean)}.
     */
    @Test
    public void testSetOnLeaveResetInventory() {
        s.setOnLeaveResetInventory(false);
        assertFalse(s.isOnLeaveResetInventory());
        s.setOnLeaveResetInventory(true);
        assertTrue(s.isOnLeaveResetInventory());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setOnLeaveResetMoney(boolean)}.
     */
    @Test
    public void testSetOnLeaveResetMoney() {
        s.setOnLeaveResetMoney(false);
        assertFalse(s.isOnLeaveResetMoney());
        s.setOnLeaveResetMoney(true);
        assertTrue(s.isOnLeaveResetMoney());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setRemoveMobsWhitelist(java.util.Set)}.
     */
    @Test
    public void testSetRemoveMobsWhitelist() {
        s.setRemoveMobsWhitelist(Collections.singleton(EntityType.GHAST));
        assertTrue(s.getRemoveMobsWhitelist().contains(EntityType.GHAST));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setRequireConfirmationToSetHomeInNether(boolean)}.
     */
    @Test
    public void testSetRequireConfirmationToSetHomeInNether() {
        s.setRequireConfirmationToSetHomeInNether(false);
        assertFalse(s.isRequireConfirmationToSetHomeInNether());
        s.setRequireConfirmationToSetHomeInNether(true);
        assertTrue(s.isRequireConfirmationToSetHomeInNether());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setRequireConfirmationToSetHomeInTheEnd(boolean)}.
     */
    @Test
    public void testSetRequireConfirmationToSetHomeInTheEnd() {
        s.setRequireConfirmationToSetHomeInTheEnd(false);
        assertFalse(s.isRequireConfirmationToSetHomeInTheEnd());
        s.setRequireConfirmationToSetHomeInTheEnd(true);
        assertTrue(s.isRequireConfirmationToSetHomeInTheEnd());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setResetEpoch(long)}.
     */
    @Test
    public void testSetResetEpoch() {
        s.setResetEpoch(3456L);
        assertEquals(3456L, s.getResetEpoch());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setResetLimit(int)}.
     */
    @Test
    public void testSetResetLimit() {
        s.setResetLimit(99);
        assertEquals(99, s.getResetLimit());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setSeaHeight(int)}.
     */
    @Test
    public void testSetSeaHeight() {
        s.setSeaHeight(99);
        assertEquals(99, s.getSeaHeight());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setNetherSeaHeight(int)}.
     */
    @Test
    public void testSetNetherSeaHeight() {
        s.setNetherSeaHeight(99);
        assertEquals(99, s.getNetherSeaHeight());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setEndSeaHeight(int)}.
     */
    @Test
    public void testSetEndSeaHeight() {
        s.setEndSeaHeight(99);
        assertEquals(99, s.getEndSeaHeight());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setTeamJoinDeathReset(boolean)}.
     */
    @Test
    public void testSetTeamJoinDeathReset() {
        s.setTeamJoinDeathReset(false);
        assertFalse(s.isTeamJoinDeathReset());
        s.setTeamJoinDeathReset(true);
        assertTrue(s.isTeamJoinDeathReset());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setUseOwnGenerator(boolean)}.
     */
    @Test
    public void testSetUseOwnGenerator() {
        s.setUseOwnGenerator(false);
        assertFalse(s.isUseOwnGenerator());
        s.setUseOwnGenerator(true);
        assertTrue(s.isUseOwnGenerator());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setHiddenFlags(java.util.List)}.
     */
    @Test
    public void testSetHiddenFlags() {
        s.setHiddenFlags(Collections.singletonList("flag"));
        assertEquals("flag", s.getHiddenFlags().get(0));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setVisitorBannedCommands(java.util.List)}.
     */
    @Test
    public void testSetVisitorBannedCommands() {
        s.setVisitorBannedCommands(Collections.singletonList("flag"));
        assertEquals("flag", s.getVisitorBannedCommands().get(0));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setFallingBannedCommands(java.util.List)}.
     */
    @Test
    public void testSetFallingBannedCommands() {
        s.setFallingBannedCommands(Collections.singletonList("flag"));
        assertEquals("flag", s.getFallingBannedCommands().get(0));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setWorldFlags(java.util.Map)}.
     */
    @Test
    public void testSetWorldFlags() {
        s.setWorldFlags(Collections.singletonMap("flag", true));
        assertTrue(s.getWorldFlags().get("flag"));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setWorldName(java.lang.String)}.
     */
    @Test
    public void testSetWorldName() {
        s.setWorldName("ugga");
        assertEquals("ugga", s.getWorldName());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isAcidDamageSnow()}.
     */
    @Test
    public void testIsAcidDamageSnow() {
        assertFalse(s.isAcidDamageSnow());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setAcidDamageSnow(boolean)}.
     */
    @Test
    public void testSetAcidDamageSnow() {
        s.setAcidDamageSnow(false);
        assertFalse(s.isAcidDamageSnow());
        s.setAcidDamageSnow(true);
        assertTrue(s.isAcidDamageSnow());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isDeathsResetOnNewIsland()}.
     */
    @Test
    public void testIsDeathsResetOnNewIsland() {
        assertTrue(s.isDeathsResetOnNewIsland());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setDeathsResetOnNewIsland(boolean)}.
     */
    @Test
    public void testSetDeathsResetOnNewIsland() {
        s.setDeathsResetOnNewIsland(false);
        assertFalse(s.isDeathsResetOnNewIsland());
        s.setDeathsResetOnNewIsland(true);
        assertTrue(s.isDeathsResetOnNewIsland());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getOnJoinCommands()}.
     */
    @Test
    public void testGetOnJoinCommands() {
        assertTrue(s.getOnJoinCommands().isEmpty());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setOnJoinCommands(java.util.List)}.
     */
    @Test
    public void testSetOnJoinCommands() {
        s.setOnJoinCommands(Collections.singletonList("command"));
        assertEquals("command", s.getOnJoinCommands().get(0));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getOnLeaveCommands()}.
     */
    @Test
    public void testGetOnLeaveCommands() {
        assertTrue(s.getOnLeaveCommands().isEmpty());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setOnLeaveCommands(java.util.List)}.
     */
    @Test
    public void testSetOnLeaveCommands() {
        s.setOnLeaveCommands(Collections.singletonList("command"));
        assertEquals("command", s.getOnLeaveCommands().get(0));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isOnJoinResetHealth()}.
     */
    @Test
    public void testIsOnJoinResetHealth() {
        assertTrue(s.isOnJoinResetHealth());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setOnJoinResetHealth(boolean)}.
     */
    @Test
    public void testSetOnJoinResetHealth() {
        s.setOnJoinResetHealth(false);
        assertFalse(s.isOnJoinResetHealth());
        s.setOnJoinResetHealth(true);
        assertTrue(s.isOnJoinResetHealth());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isOnJoinResetHunger()}.
     */
    @Test
    public void testIsOnJoinResetHunger() {
        assertTrue(s.isOnJoinResetHunger());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setOnJoinResetHunger(boolean)}.
     */
    @Test
    public void testSetOnJoinResetHunger() {
        s.setOnJoinResetHunger(false);
        assertFalse(s.isOnJoinResetHunger());
        s.setOnJoinResetHunger(true);
        assertTrue(s.isOnJoinResetHunger());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isOnJoinResetXP()}.
     */
    @Test
    public void testIsOnJoinResetXP() {
        assertFalse(s.isOnJoinResetXP());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setOnJoinResetXP(boolean)}.
     */
    @Test
    public void testSetOnJoinResetXP() {
        s.setOnJoinResetXP(false);
        assertFalse(s.isOnJoinResetXP());
        s.setOnJoinResetXP(true);
        assertTrue(s.isOnJoinResetXP());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isOnLeaveResetHealth()}.
     */
    @Test
    public void testIsOnLeaveResetHealth() {
        assertFalse(s.isOnLeaveResetHealth());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setOnLeaveResetHealth(boolean)}.
     */
    @Test
    public void testSetOnLeaveResetHealth() {
        s.setOnLeaveResetHealth(false);
        assertFalse(s.isOnLeaveResetHealth());
        s.setOnLeaveResetHealth(true);
        assertTrue(s.isOnLeaveResetHealth());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isOnLeaveResetHunger()}.
     */
    @Test
    public void testIsOnLeaveResetHunger() {
        assertFalse(s.isOnLeaveResetHunger());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setOnLeaveResetHunger(boolean)}.
     */
    @Test
    public void testSetOnLeaveResetHunger() {
        s.setOnLeaveResetHunger(false);
        assertFalse(s.isOnLeaveResetHunger());
        s.setOnLeaveResetHunger(true);
        assertTrue(s.isOnLeaveResetHunger());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isOnLeaveResetXP()}.
     */
    @Test
    public void testIsOnLeaveResetXP() {
        assertFalse(s.isOnLeaveResetXP());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setOnLeaveResetXP(boolean)}.
     */
    @Test
    public void testSetOnLeaveResetXP() {

        s.setOnLeaveResetXP(false);
        assertFalse(s.isOnLeaveResetXP());
        s.setOnLeaveResetXP(true);
        assertTrue(s.isOnLeaveResetXP());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setCreateIslandOnFirstLoginEnabled(boolean)}.
     */
    @Test
    public void testSetCreateIslandOnFirstLoginEnabled() {
        s.setCreateIslandOnFirstLoginEnabled(false);
        assertFalse(s.isCreateIslandOnFirstLoginEnabled());
        s.setCreateIslandOnFirstLoginEnabled(true);
        assertTrue(s.isCreateIslandOnFirstLoginEnabled());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setCreateIslandOnFirstLoginDelay(int)}.
     */
    @Test
    public void testSetCreateIslandOnFirstLoginDelay() {
        s.setCreateIslandOnFirstLoginDelay(40);
        assertEquals(40, s.getCreateIslandOnFirstLoginDelay());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setCreateIslandOnFirstLoginAbortOnLogout(boolean)}.
     */
    @Test
    public void testSetCreateIslandOnFirstLoginAbortOnLogout() {
        s.setCreateIslandOnFirstLoginAbortOnLogout(false);
        assertFalse(s.isCreateIslandOnFirstLoginAbortOnLogout());
        s.setCreateIslandOnFirstLoginAbortOnLogout(true);
        assertTrue(s.isCreateIslandOnFirstLoginAbortOnLogout());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#isPasteMissingIslands()}.
     */
    @Test
    public void testIsPasteMissingIslands() {
        assertFalse(s.isPasteMissingIslands());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setPasteMissingIslands(boolean)}.
     */
    @Test
    public void testSetPasteMissingIslands() {
        s.setPasteMissingIslands(false);
        assertFalse(s.isPasteMissingIslands());
        s.setPasteMissingIslands(true);
        assertTrue(s.isPasteMissingIslands());

    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getAcidRainEffects()}.
     */
    @Test
    public void testGetAcidRainEffects() {
        assertTrue(s.getAcidRainEffects().isEmpty());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setAcidRainEffects(java.util.List)}.
     */
    @Test
    public void testSetAcidRainEffects() {
        s.setAcidRainEffects(Collections.singletonList(PotionEffectType.BAD_OMEN));
        assertEquals(PotionEffectType.BAD_OMEN, s.getAcidRainEffects().get(0));
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getRainEffectDuation()}.
     */
    @Test
    public void testGetRainEffectDuation() {
        assertEquals(10, s.getRainEffectDuation());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setRainEffectDuation(int)}.
     */
    @Test
    public void testSetRainEffectDuation() {
        s.setRainEffectDuation(99);
        assertEquals(99, s.getRainEffectDuation());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getAcidEffectDuation()}.
     */
    @Test
    public void testGetAcidEffectDuation() {
        assertEquals(30, s.getAcidEffectDuation());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setAcidEffectDuation(int)}.
     */
    @Test
    public void testSetAcidEffectDuation() {
        s.setAcidEffectDuation(99);
        assertEquals(99, s.getAcidEffectDuation());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getSpawnLimitMonsters()}.
     */
    @Test
    public void testGetSpawnLimitMonsters() {
        assertEquals(-1, s.getSpawnLimitMonsters());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setSpawnLimitMonsters(int)}.
     */
    @Test
    public void testSetSpawnLimitMonsters() {
        s.setSpawnLimitMonsters(99);
        assertEquals(99, s.getSpawnLimitMonsters());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getSpawnLimitAnimals()}.
     */
    @Test
    public void testGetSpawnLimitAnimals() {
        assertEquals(-1, s.getSpawnLimitAnimals());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setSpawnLimitAnimals(int)}.
     */
    @Test
    public void testSetSpawnLimitAnimals() {
        s.setSpawnLimitAnimals(99);
        assertEquals(99, s.getSpawnLimitAnimals());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getSpawnLimitWaterAnimals()}.
     */
    @Test
    public void testGetSpawnLimitWaterAnimals() {
        assertEquals(-1, s.getSpawnLimitWaterAnimals());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setSpawnLimitWaterAnimals(int)}.
     */
    @Test
    public void testSetSpawnLimitWaterAnimals() {
        s.setSpawnLimitWaterAnimals(99);
        assertEquals(99, s.getSpawnLimitWaterAnimals());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getSpawnLimitAmbient()}.
     */
    @Test
    public void testGetSpawnLimitAmbient() {
        assertEquals(-1, s.getSpawnLimitAmbient());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setSpawnLimitAmbient(int)}.
     */
    @Test
    public void testSetSpawnLimitAmbient() {
        s.setSpawnLimitAmbient(99);
        assertEquals(99, s.getSpawnLimitAmbient());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getTicksPerAnimalSpawns()}.
     */
    @Test
    public void testGetTicksPerAnimalSpawns() {
        assertEquals(-1, s.getTicksPerAnimalSpawns());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setTicksPerAnimalSpawns(int)}.
     */
    @Test
    public void testSetTicksPerAnimalSpawns() {
        s.setTicksPerAnimalSpawns(99);
        assertEquals(99, s.getTicksPerAnimalSpawns());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getTicksPerMonsterSpawns()}.
     */
    @Test
    public void testGetTicksPerMonsterSpawns() {
        assertEquals(-1, s.getTicksPerMonsterSpawns());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setTicksPerMonsterSpawns(int)}.
     */
    @Test
    public void testSetTicksPerMonsterSpawns() {
        s.setTicksPerMonsterSpawns(99);
        assertEquals(99, s.getTicksPerMonsterSpawns());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getMaxCoopSize()}.
     */
    @Test
    public void testGetMaxCoopSize() {
        assertEquals(4, s.getMaxCoopSize());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setMaxCoopSize(int)}.
     */
    @Test
    public void testSetMaxCoopSize() {
        s.setMaxCoopSize(99);
        assertEquals(99, s.getMaxCoopSize());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getMaxTrustSize()}.
     */
    @Test
    public void testGetMaxTrustSize() {
        assertEquals(4, s.getMaxTrustSize());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setMaxTrustSize(int)}.
     */
    @Test
    public void testSetMaxTrustSize() {
        s.setMaxTrustSize(99);
        assertEquals(99, s.getMaxTrustSize());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getPlayerCommandAliases()}.
     */
    @Test
    public void testGetPlayerCommandAliases() {
        assertEquals("ai", s.getPlayerCommandAliases());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setPlayerCommandAliases(java.lang.String)}.
     */
    @Test
    public void testSetPlayerCommandAliases() {
        s.setPlayerCommandAliases("adm");
        assertEquals("adm", s.getPlayerCommandAliases());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getAdminCommandAliases()}.
     */
    @Test
    public void testGetAdminCommandAliases() {
        assertEquals("acid", s.getAdminCommandAliases());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setAdminCommandAliases(java.lang.String)}.
     */
    @Test
    public void testSetAdminCommandAliases() {
        s.setAdminCommandAliases("adm");
        assertEquals("adm", s.getAdminCommandAliases());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getDefaultNewPlayerAction()}.
     */
    @Test
    public void testGetDefaultNewPlayerAction() {
        assertEquals("create", s.getDefaultNewPlayerAction());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setDefaultNewPlayerAction(java.lang.String)}.
     */
    @Test
    public void testSetDefaultNewPlayerAction() {
        s.setDefaultNewPlayerAction("cr");
        assertEquals("cr", s.getDefaultNewPlayerAction());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getDefaultPlayerAction()}.
     */
    @Test
    public void testGetDefaultPlayerAction() {
        assertEquals("go", s.getDefaultPlayerAction());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setDefaultPlayerAction(java.lang.String)}.
     */
    @Test
    public void testSetDefaultPlayerAction() {
        s.setDefaultPlayerAction("go2");
        assertEquals("go2", s.getDefaultPlayerAction());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getDefaultNetherBiome()}.
     */
    @Test
    public void testGetDefaultNetherBiome() {
        assertEquals(Biome.NETHER_WASTES, s.getDefaultNetherBiome());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setDefaultNetherBiome(org.bukkit.block.Biome)}.
     */
    @Test
    public void testSetDefaultNetherBiome() {
        s.setDefaultNetherBiome(Biome.END_BARRENS);
        assertEquals(Biome.END_BARRENS, s.getDefaultNetherBiome());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#getDefaultEndBiome()}.
     */
    @Test
    public void testGetDefaultEndBiome() {
        assertEquals(Biome.THE_END, s.getDefaultEndBiome());
    }

    /**
     * Test method for {@link world.bentobox.acidisland.AISettings#setDefaultEndBiome(org.bukkit.block.Biome)}.
     */
    @Test
    public void testSetDefaultEndBiome() {
        s.setDefaultEndBiome(Biome.END_BARRENS);
        assertEquals(Biome.END_BARRENS, s.getDefaultEndBiome());
    }

}
