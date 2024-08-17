package world.bentobox.acidisland;

import java.util.*;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import com.google.common.base.Enums;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.StoreAt;
import world.bentobox.bentobox.api.configuration.WorldSettings;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.bentobox.database.objects.adapters.*;


/**
 * Settings for AcidIsland
 * @author tastybento
 *
 */
@ConfigComment("AcidIsland Configuration [version]")
@StoreAt(filename="config.yml", path="addons/AcidIsland") // Explicitly call out what name this should have.
public class AISettings implements WorldSettings {

    // ---------------------------------------------

    // Command
    @ConfigComment("Island Command. What command users will run to access their island")
    @ConfigEntry(path = "acid.command.island")
    private String playerCommandAliases = "ai";

    @ConfigComment("The island admin command.")
    @ConfigEntry(path = "acid.command.admin")
    private String adminCommandAliases = "acid";

    @ConfigComment("The default action for new player command call.")
    @ConfigComment("Sub-command of main player command that will be run on first player command call.")
    @ConfigComment("By default it is sub-command 'create'.")
    @ConfigEntry(path = "acid.command.new-player-action", since = "1.13.1")
    private String defaultNewPlayerAction = "create";

    @ConfigComment("The default action for player command.")
    @ConfigComment("Sub-command of main player command that will be run on each player command call.")
    @ConfigComment("By default it is sub-command 'go'.")
    @ConfigEntry(path = "acid.command.default-action", since = "1.13.1")
    private String defaultPlayerAction = "go";

    /*      ACID        */
    @ConfigComment("Acid can damage ops or not")
    @ConfigEntry(path = "acid.damage-op")
    private boolean acidDamageOp = false;

    @ConfigComment("Acid can damage chickens - best to leave false because they like to swim")
    @ConfigEntry(path = "acid.damage-chickens")
    private boolean acidDamageChickens = false;

    // Damage
    @ConfigComment("Damage that a player will experience in acid. 10 is half their health typically. 5 would be easier.")
    @ConfigEntry(path = "acid.damage.acid.player")
    private int acidDamage = 10;

    @ConfigComment("Damage that monsters experience from acid. Some monsters have armor or natural armor so will take less damage.")
    @ConfigEntry(path = "acid.damage.acid.monster")
    private int acidDamageMonster = 5;

    @ConfigComment("Damage animals experience from acid")
    @ConfigEntry(path = "acid.damage.acid.animal")
    private int acidDamageAnimal = 1;

    @ConfigComment("Destroy items after this many seconds in acid. 0 = do not destroy items")
    @ConfigEntry(path = "acid.damage.acid.item")
    private long acidDestroyItemTime = 0;

    @ConfigComment("Damage from acid rain (and snow, if toggled on).")
    @ConfigEntry(path = "acid.damage.rain")
    private int acidRainDamage = 1;

    @ConfigComment("Damage from acid snow")
    @ConfigEntry(path = "acid.damage.snow")
    private boolean acidDamageSnow;

    @ConfigComment("Delay before acid or acid rain starts burning")
    @ConfigComment("This can give time for conduit power to kick in")
    @ConfigEntry(path = "acid.damage.delay")
    private long acidDamageDelay = 2;

    @ConfigComment("Potion effects from going into acid water.")
    @ConfigComment("You can list multiple effects.")
    @ConfigComment("Available effects are:")
    @ConfigComment("   BLINDNESS")
    @ConfigComment("   CONFUSION")
    @ConfigComment("   HUNGER")
    @ConfigComment("   POISON")
    @ConfigComment("   SLOW")
    @ConfigComment("   SLOW_DIGGING")
    @ConfigComment("   WEAKNESS")
    @ConfigEntry(path = "acid.damage.effects")
    @Adapter(PotionEffectListAdapter.class)
    private List<PotionEffectType> acidEffects = new ArrayList<>();
    @ConfigComment("Acid effect duration in seconds")
    @ConfigEntry(path = "acid.damage.acid-effect-duration", since = "1.11.2")
    private int acidEffectDuation = 30;

    @ConfigComment("Potion effects from going into acid rain and snow.")
    @ConfigComment("You can list multiple effects.")
    @ConfigComment("Available effects are:")
    @ConfigComment("   BLINDNESS")
    @ConfigComment("   CONFUSION")
    @ConfigComment("   HUNGER")
    @ConfigComment("   POISON")
    @ConfigComment("   SLOW")
    @ConfigComment("   SLOW_DIGGING")
    @ConfigComment("   WEAKNESS")
    @ConfigEntry(path = "acid.damage.rain-effects", since = "1.9.1")
    @Adapter(PotionEffectListAdapter.class)
    private List<PotionEffectType> acidRainEffects = new ArrayList<>();

    @ConfigComment("Rain effect duration in seconds")
    @ConfigEntry(path = "acid.damage.rain-effect-duration", since = "1.11.2")
    private int rainEffectDuation = 10;

    @ConfigComment("If player wears a helmet then they will not suffer from acid rain")
    @ConfigEntry(path = "acid.damage.protection.helmet")
    private boolean helmetProtection;

    @ConfigComment("If player wears any set of full armor, they will not suffer from acid damage")
    @ConfigEntry(path = "acid.damage.protection.full-armor")
    private boolean fullArmorProtection;


    /*      WORLD       */
    @ConfigComment("Friendly name for this world. Used in admin commands. Must be a single word")
    @ConfigEntry(path = "world.friendly-name", needsReset = true)
    private String friendlyName = "AcidIsland";

    @ConfigComment("Name of the world - if it does not exist then it will be generated.")
    @ConfigComment("It acts like a prefix for nether and end (e.g. acidisland_world, acidisland_world_nether, acidisland_world_end)")
    @ConfigEntry(path = "world.world-name", needsReset = true)
    private String worldName = "acidisland_world";

    @ConfigComment("World difficulty setting - PEACEFUL, EASY, NORMAL, HARD")
    @ConfigComment("Other plugins may override this setting")
    @ConfigEntry(path = "world.difficulty")
    private Difficulty difficulty = Difficulty.NORMAL;

    @ConfigComment("Spawn limits. These override the limits set in bukkit.yml")
    @ConfigComment("If set to a negative number, the server defaults will be used")
    @ConfigEntry(path = "world.spawn-limits.monsters", since = "1.11.2")
    private int spawnLimitMonsters = -1;
    @ConfigEntry(path = "world.spawn-limits.animals", since = "1.11.2")
    private int spawnLimitAnimals = -1;
    @ConfigEntry(path = "world.spawn-limits.water-animals", since = "1.11.2")
    private int spawnLimitWaterAnimals = -1;
    @ConfigEntry(path = "world.spawn-limits.ambient", since = "1.11.2")
    private int spawnLimitAmbient = -1;
    @ConfigComment("Setting to 0 will disable animal spawns, but this is not recommended. Minecraft default is 400.")
    @ConfigComment("A negative value uses the server default")
    @ConfigEntry(path = "world.spawn-limits.ticks-per-animal-spawns", since = "1.11.2")
    private int ticksPerAnimalSpawns = -1;
    @ConfigComment("Setting to 0 will disable monster spawns, but this is not recommended. Minecraft default is 400.")
    @ConfigComment("A negative value uses the server default")
    @ConfigEntry(path = "world.spawn-limits.ticks-per-monster-spawns", since = "1.11.2")
    private int ticksPerMonsterSpawns = -1;

    @ConfigComment("Radius of island in blocks. (So distance between islands is twice this)")
    @ConfigComment("It is the same for every dimension : Overworld, Nether and End.")
    @ConfigComment("This value cannot be changed mid-game and the plugin will not start if it is different.")
    @ConfigEntry(path = "world.distance-between-islands", needsReset = true)
    private int islandDistance = 64;

    @ConfigComment("Default protection range radius in blocks. Cannot be larger than distance.")
    @ConfigComment("Admins can change protection sizes for players individually using /acid range set <player> <new range>")
    @ConfigComment("or set this permission: acidisland.island.range.<number>")
    @ConfigEntry(path = "world.protection-range", overrideOnChange = true)
    private int islandProtectionRange = 50;

    @ConfigComment("Start islands at these coordinates. This is where new islands will start in the")
    @ConfigComment("world. These must be a factor of your island distance, but the plugin will auto")
    @ConfigComment("calculate the closest location on the grid. Islands develop around this location")
    @ConfigComment("both positively and negatively in a square grid.")
    @ConfigComment("If none of this makes sense, leave it at 0,0.")
    @ConfigEntry(path = "world.start-x", needsReset = true)
    private int islandStartX = 0;

    @ConfigEntry(path = "world.start-z", needsReset = true)
    private int islandStartZ = 0;

    @ConfigEntry(path = "world.offset-x")
    private int islandXOffset;
    @ConfigEntry(path = "world.offset-z")
    private int islandZOffset;

    @ConfigComment("Island height - Lowest is 5.")
    @ConfigComment("It is the y coordinate of the bedrock block in the schem.")
    @ConfigEntry(path = "world.island-height")
    private int islandHeight = 50;
    
    @ConfigComment("The number of concurrent islands a player can have in the world")
    @ConfigComment("A value of 0 will use the BentoBox config.yml default")
    @ConfigEntry(path = "world.concurrent-islands")
    private int concurrentIslands = 0;

    @ConfigComment("Disallow players to have other islands if they are in a team.")
    @ConfigEntry(path = "world.disallow-team-member-islands")
    boolean disallowTeamMemberIslands = true;

    @ConfigComment("Use your own world generator for this world.")
    @ConfigComment("In this case, the plugin will not generate anything.")
    @ConfigEntry(path = "world.use-own-generator", experimental = true)
    private boolean useOwnGenerator;

    @ConfigComment("Sea height (don't changes this mid-game unless you delete the world)")
    @ConfigComment("Minimum is 0, which means you are playing Skyblock!")
    @ConfigEntry(path = "world.sea-height")
    private int seaHeight = 54;
    
    @ConfigComment("Water block. This should usually stay as WATER, but may be LAVA for fun")
    @ConfigEntry(path = "world.water-block", needsReset = true)
    private Material waterBlock = Material.WATER;
    
    @ConfigComment("Ocean Floor")
    @ConfigComment("This creates an ocean floor environment, with vanilla elements.")
    @ConfigEntry(path = "world.ocean-floor", needsReset = true)
    private boolean oceanFloor = false;

    @ConfigComment("Structures")
    @ConfigComment("This creates an vanilla structures in the worlds.")
    @ConfigEntry(path = "world.make-structures", needsReset = true)
    private boolean makeStructures = false;

    @ConfigComment("Caves")
    @ConfigComment("This creates an vanilla caves in the worlds.")
    @ConfigEntry(path = "world.make-caves", needsReset = true)
    private boolean makeCaves = false;

    @ConfigComment("Decorations")
    @ConfigComment("This creates an vanilla decorations in the worlds.")
    @ConfigEntry(path = "world.make-decorations", needsReset = true)
    private boolean makeDecorations = true;

    @ConfigComment("Maximum number of islands in the world. Set to -1 or 0 for unlimited. ")
    @ConfigComment("If the number of islands is greater than this number, no new island will be created.")
    @ConfigEntry(path = "world.max-islands")
    private int maxIslands = -1;

    @ConfigComment("The default game mode for this world. Players will be set to this mode when they create")
    @ConfigComment("a new island for example. Options are SURVIVAL, CREATIVE, ADVENTURE, SPECTATOR")
    @ConfigEntry(path = "world.default-game-mode")
    private GameMode defaultGameMode = GameMode.SURVIVAL;

    @ConfigComment("The default biome for the overworld")
    @ConfigEntry(path = "world.default-biome")
    private Biome defaultBiome = Biome.WARM_OCEAN;
    @ConfigComment("The default biome for the nether world (this may affect what mobs can spawn)")
    @ConfigEntry(path = "world.default-nether-biome")
    private Biome defaultNetherBiome = Enums.getIfPresent(Biome.class, "NETHER").or(Enums.getIfPresent(Biome.class, "NETHER_WASTES").or(Biome.BADLANDS));
    @ConfigComment("The default biome for the end world (this may affect what mobs can spawn)")
    @ConfigEntry(path = "world.default-end-biome")
    private Biome defaultEndBiome = Biome.THE_END;

    @ConfigComment("The maximum number of players a player can ban at any one time in this game mode.")
    @ConfigComment("The permission acidisland.ban.maxlimit.X where X is a number can also be used per player")
    @ConfigComment("-1 = unlimited")
    @ConfigEntry(path = "world.ban-limit")
    private int banLimit = -1;

    // Nether
    @ConfigComment("Generate Nether - if this is false, the nether world will not be made and access to")
    @ConfigComment("the nether will not occur. Other plugins may still enable portal usage.")
    @ConfigComment("Note: Some default challenges will not be possible if there is no nether.")
    @ConfigComment("Note that with a standard nether all players arrive at the same portal and entering a")
    @ConfigComment("portal will return them back to their islands.")
    @ConfigEntry(path = "world.nether.generate")
    private boolean netherGenerate = true;

    @ConfigComment("Islands in Nether. Change to false for standard vanilla nether.")
    @ConfigEntry(path = "world.nether.islands", needsReset = true)
    private boolean netherIslands = true;

    @ConfigComment("Sea height in Nether. Only operates if nether islands is true.")
    @ConfigComment("Changing mid-game will cause problems!")
    @ConfigEntry(path = "world.nether.sea-height", needsReset = true)
    private int netherSeaHeight = 54;
    
    @ConfigComment("Water block. This should usually stay as WATER, but may be LAVA for fun")
    @ConfigEntry(path = "world.nether.water-block", needsReset = true)
    private Material netherWaterBlock = Material.WATER;

    @ConfigComment("Make the nether roof, if false, there is nothing up there")
    @ConfigComment("Change to false if lag is a problem from the generation")
    @ConfigComment("Only applies to islands Nether")
    @ConfigEntry(path = "world.nether.roof")
    private boolean netherRoof = true;

    @ConfigComment("Nether spawn protection radius - this is the distance around the nether spawn")
    @ConfigComment("that will be protected from player interaction (breaking blocks, pouring lava etc.)")
    @ConfigComment("Minimum is 0 (not recommended), maximum is 100. Default is 25.")
    @ConfigComment("Only applies to vanilla nether")
    @ConfigEntry(path = "world.nether.spawn-radius")
    private int netherSpawnRadius = 32;

    @ConfigComment("This option indicates if nether portals should be linked via dimensions.")
    @ConfigComment("Option will simulate vanilla portal mechanics that links portals together")
    @ConfigComment("or creates a new portal, if there is not a portal in that dimension.")
    @ConfigEntry(path = "world.nether.create-and-link-portals", since = "1.14.6")
    private boolean makeNetherPortals = false;

    // End
    @ConfigComment("End Nether - if this is false, the end world will not be made and access to")
    @ConfigComment("the end will not occur. Other plugins may still enable portal usage.")
    @ConfigEntry(path = "world.end.generate")
    private boolean endGenerate = true;

    @ConfigComment("Islands in The End. Change to false for standard vanilla end.")
    @ConfigEntry(path = "world.end.islands", needsReset = true)
    private boolean endIslands = true;

    @ConfigComment("Sea height in The End. Only operates if end islands is true.")
    @ConfigComment("Changing mid-game will cause problems!")
    @ConfigEntry(path = "world.end.sea-height", needsReset = true)
    private int endSeaHeight = 54;
    
    @ConfigComment("Water block. This should usually stay as WATER, but may be LAVA for fun")
    @ConfigEntry(path = "world.end.water-block", needsReset = true)
    private Material endWaterBlock = Material.WATER;

    @ConfigComment("This option indicates if obsidian platform in the end should be generated")
    @ConfigComment("when player enters the end world.")
    @ConfigEntry(path = "world.end.create-obsidian-platform", since = "1.14.6")
    private boolean makeEndPortals = false;

    @ConfigEntry(path = "world.end.dragon-spawn", experimental = true)
    private boolean dragonSpawn = false;

    @ConfigComment("Removing mobs - this kills all monsters in the vicinity. Benefit is that it helps")
    @ConfigComment("players return to their island if the island has been overrun by monsters.")
    @ConfigComment("This setting is toggled in world flags and set by the settings GUI.")
    @ConfigComment("Mob white list - these mobs will NOT be removed when logging in or doing /island")
    @ConfigEntry(path = "world.remove-mobs-whitelist")
    private Set<EntityType> removeMobsWhitelist = new HashSet<>();

    @ConfigComment("World flags. These are boolean settings for various flags for this world")
    @ConfigEntry(path = "world.flags")
    private Map<String, Boolean> worldFlags = new HashMap<>();

    @ConfigComment("These are the default protection settings for new islands.")
    @ConfigComment("The value is the minimum island rank required allowed to do the action")
    @ConfigComment("Ranks are: Visitor = 0, Member = 900, Owner = 1000")
    @ConfigEntry(path = "world.default-island-flags")
    private Map<String, Integer> defaultIslandFlagNames = new HashMap<>();

    @ConfigComment("These are the default settings for new islands")
    @ConfigEntry(path = "world.default-island-settings")
    @Adapter(FlagBooleanSerializer.class)
    private Map<String, Integer> defaultIslandSettingNames = new HashMap<>();

    @ConfigComment("These settings/flags are hidden from users")
    @ConfigComment("Ops can toggle hiding in-game using SHIFT-LEFT-CLICK on flags in settings")
    @ConfigEntry(path = "world.hidden-flags")
    private List<String> hiddenFlags = new ArrayList<>();

    @ConfigComment("Visitor banned commands - Visitors to islands cannot use these commands in this world")
    @ConfigEntry(path = "world.visitor-banned-commands")
    private List<String> visitorBannedCommands = new ArrayList<>();

    @ConfigComment("Falling banned commands - players cannot use these commands when falling")
    @ConfigComment("if the PREVENT_TELEPORT_WHEN_FALLING world setting flag is active")
    @ConfigEntry(path = "world.falling-banned-commands")
    private List<String> fallingBannedCommands = new ArrayList<>();

    // ---------------------------------------------

    /*      ISLAND      */
    @ConfigComment("Default max team size")
    @ConfigComment("Use this permission to set for specific user groups: acidisland.team.maxsize.<number>")
    @ConfigComment("Permission size cannot be less than the default below.")
    @ConfigEntry(path = "island.max-team-size")
    private int maxTeamSize = 4;

    @ConfigComment("Default maximum number of coop rank members per island")
    @ConfigComment("Players can have the acidisland.coop.maxsize.<number> permission to be bigger but")
    @ConfigComment("permission size cannot be less than the default below. ")
    @ConfigEntry(path = "island.max-coop-size", since = "1.13.0")
    private int maxCoopSize = 4;

    @ConfigComment("Default maximum number of trusted rank members per island")
    @ConfigComment("Players can have the acidisland.trust.maxsize.<number> permission to be bigger but")
    @ConfigComment("permission size cannot be less than the default below. ")
    @ConfigEntry(path = "island.max-trusted-size", since = "1.13.0")
    private int maxTrustSize = 4;

    @ConfigComment("Default maximum number of homes a player can have. Min = 1")
    @ConfigComment("Accessed via /ai sethome <number> or /ai go <number>")
    @ConfigComment("Use this permission to set for specific user groups: acidisland.island.maxhomes.<number>")
    @ConfigEntry(path = "island.max-homes")
    private int maxHomes = 5;

    // Reset
    @ConfigComment("How many resets a player is allowed (manage with /acid reset add/remove/reset/set command)")
    @ConfigComment("Value of -1 means unlimited, 0 means hardcore - no resets.")
    @ConfigComment("Example, 2 resets means they get 2 resets or 3 islands lifetime")
    @ConfigEntry(path = "island.reset.reset-limit")
    private int resetLimit = -1;

    @ConfigComment("Kicked or leaving players lose resets")
    @ConfigComment("Players who leave a team will lose an island reset chance")
    @ConfigComment("If a player has zero resets left and leaves a team, they cannot make a new")
    @ConfigComment("island by themselves and can only join a team.")
    @ConfigComment("Leave this true to avoid players exploiting free islands")
    @ConfigEntry(path = "island.reset.leavers-lose-reset")
    private boolean leaversLoseReset = false;

    @ConfigComment("Allow kicked players to keep their inventory.")
    @ConfigComment("Overrides the on-leave inventory reset for kicked players.")
    @ConfigEntry(path = "island.reset.kicked-keep-inventory")
    private boolean kickedKeepInventory = false;

    @ConfigComment("What the plugin should reset when the player joins or creates an island")
    @ConfigComment("Reset Money - if this is true, will reset the player's money to the starting money")
    @ConfigComment("Recommendation is that this is set to true, but if you run multi-worlds")
    @ConfigComment("make sure your economy handles multi-worlds too.")
    @ConfigEntry(path = "island.reset.on-join.money")
    private boolean onJoinResetMoney = false;

    @ConfigComment("Reset inventory - if true, the player's inventory will be cleared.")
    @ConfigComment("Note: if you have MultiInv running or a similar inventory control plugin, that")
    @ConfigComment("plugin may still reset the inventory when the world changes.")
    @ConfigEntry(path = "island.reset.on-join.inventory")
    private boolean onJoinResetInventory = false;

    @ConfigComment("Reset health - if true, the player's health will be reset.")
    @ConfigEntry(path = "island.reset.on-join.health")
    private boolean onJoinResetHealth = true;

    @ConfigComment("Reset hunger - if true, the player's hunger will be reset.")
    @ConfigEntry(path = "island.reset.on-join.hunger")
    private boolean onJoinResetHunger = true;

    @ConfigComment("Reset experience points - if true, the player's experience will be reset.")
    @ConfigEntry(path = "island.reset.on-join.exp")
    private boolean onJoinResetXP = false;

    @ConfigComment("Reset Ender Chest - if true, the player's Ender Chest will be cleared.")
    @ConfigEntry(path = "island.reset.on-join.ender-chest")
    private boolean onJoinResetEnderChest = false;

    @ConfigComment("What the plugin should reset when the player leaves or is kicked from an island")
    @ConfigComment("Reset Money - if this is true, will reset the player's money to the starting money")
    @ConfigComment("Recommendation is that this is set to true, but if you run multi-worlds")
    @ConfigComment("make sure your economy handles multi-worlds too.")
    @ConfigEntry(path = "island.reset.on-leave.money")
    private boolean onLeaveResetMoney = false;

    @ConfigComment("Reset inventory - if true, the player's inventory will be cleared.")
    @ConfigComment("Note: if you have MultiInv running or a similar inventory control plugin, that")
    @ConfigComment("plugin may still reset the inventory when the world changes.")
    @ConfigEntry(path = "island.reset.on-leave.inventory")
    private boolean onLeaveResetInventory = false;

    @ConfigComment("Reset health - if true, the player's health will be reset.")
    @ConfigEntry(path = "island.reset.on-leave.health")
    private boolean onLeaveResetHealth = false;

    @ConfigComment("Reset hunger - if true, the player's hunger will be reset.")
    @ConfigEntry(path = "island.reset.on-leave.hunger")
    private boolean onLeaveResetHunger = false;

    @ConfigComment("Reset experience - if true, the player's experience will be reset.")
    @ConfigEntry(path = "island.reset.on-leave.exp")
    private boolean onLeaveResetXP = false;

    @ConfigComment("Reset Ender Chest - if true, the player's Ender Chest will be cleared.")
    @ConfigEntry(path = "island.reset.on-leave.ender-chest")
    private boolean onLeaveResetEnderChest = false;

    @ConfigComment("Toggles the automatic island creation upon the player's first login on your server.")
    @ConfigComment("If set to true,")
    @ConfigComment("   * Upon connecting to your server for the first time, the player will be told that")
    @ConfigComment("    an island will be created for him.")
    @ConfigComment("  * Make sure you have a Blueprint Bundle called \"default\": this is the one that will")
    @ConfigComment("    be used to create the island.")
    @ConfigComment("  * An island will be created for the player without needing him to run the create command.")
    @ConfigComment("If set to false, this will disable this feature entirely.")
    @ConfigComment("Warning:")
    @ConfigComment("  * If you are running multiple gamemodes on your server, and all of them have")
    @ConfigComment("    this feature enabled, an island in all the gamemodes will be created simultaneously.")
    @ConfigComment("    However, it is impossible to know on which island the player will be teleported to afterwards.")
    @ConfigComment("  * Island creation can be resource-intensive, please consider the options below to help mitigate")
    @ConfigComment("    the potential issues, especially if you expect a lot of players to connect to your server")
    @ConfigComment("    in a limited period of time.")
    @ConfigEntry(path = "island.create-island-on-first-login.enable")
    private boolean createIslandOnFirstLoginEnabled;

    @ConfigComment("Time in seconds after the player logged in, before his island gets created.")
    @ConfigComment("If set to 0 or less, the island will be created directly upon the player's login.")
    @ConfigComment("It is recommended to keep this value under a minute's time.")
    @ConfigEntry(path = "island.create-island-on-first-login.delay")
    private int createIslandOnFirstLoginDelay = 5;

    @ConfigComment("Toggles whether the island creation should be aborted if the player logged off while the")
    @ConfigComment("delay (see the option above) has not worn off yet.")
    @ConfigComment("If set to true,")
    @ConfigComment("  * If the player has logged off the server while the delay (see the option above) has not")
    @ConfigComment("    worn off yet, this will cancel the island creation.")
    @ConfigComment("  * If the player relogs afterward, since he will not be recognized as a new player, no island")
    @ConfigComment("    would be created for him.")
    @ConfigComment("  * If the island creation started before the player logged off, it will continue.")
    @ConfigComment("If set to false, the player's island will be created even if he went offline in the meantime.")
    @ConfigComment("Note this option has no effect if the delay (see the option above) is set to 0 or less.")
    @ConfigEntry(path = "island.create-island-on-first-login.abort-on-logout")
    private boolean createIslandOnFirstLoginAbortOnLogout = true;

    @ConfigComment("Toggles whether the player should be teleported automatically to his island when it is created.")
    @ConfigComment("If set to false, the player will be told his island is ready but will have to teleport to his island using the command.")
    @ConfigEntry(path = "island.teleport-player-to-island-when-created", since = "1.10.0")
    private boolean teleportPlayerToIslandUponIslandCreation = true;

    @ConfigComment("Create Nether or End islands if they are missing when a player goes through a portal.")
    @ConfigComment("Nether and End islands are usually pasted when a player makes their island, but if they are")
    @ConfigComment("missing for some reason, you can switch this on.")
    @ConfigComment("Note that bedrock removal glitches can exploit this option.")
    @ConfigEntry(path = "island.create-missing-nether-end-islands")
    private boolean pasteMissingIslands = false;

    // Commands
    @ConfigComment("List of commands to run when a player joins an island or creates one.")
    @ConfigComment("These commands are run by the console, unless otherwise stated using the [SUDO] prefix,")
    @ConfigComment("in which case they are executed by the player.")
    @ConfigComment("")
    @ConfigComment("Available placeholders for the commands are the following:")
    @ConfigComment("   * [name]: name of the player")
    @ConfigComment("")
    @ConfigComment("Here are some examples of valid commands to execute:")
    @ConfigComment("   * \"[SUDO] bbox version\"")
    @ConfigComment("   * \"acid deaths set [player] 0\"")
    @ConfigEntry(path = "island.commands.on-join")
    private List<String> onJoinCommands = new ArrayList<>();

    @ConfigComment("List of commands to run when a player leaves an island, resets his island or gets kicked from it.")
    @ConfigComment("These commands are run by the console, unless otherwise stated using the [SUDO] prefix,")
    @ConfigComment("in which case they are executed by the player.")
    @ConfigComment("")
    @ConfigComment("Available placeholders for the commands are the following:")
    @ConfigComment("   * [name]: name of the player")
    @ConfigComment("")
    @ConfigComment("Here are some examples of valid commands to execute:")
    @ConfigComment("   * '[SUDO] bbox version'")
    @ConfigComment("   * 'acid deaths set [player] 0'")
    @ConfigComment("")
    @ConfigComment("Note that player-executed commands might not work, as these commands can be run with said player being offline.")
    @ConfigEntry(path = "island.commands.on-leave")
    private List<String> onLeaveCommands = new ArrayList<>();

    @ConfigComment("List of commands that should be executed when the player respawns after death if Flags.ISLAND_RESPAWN is true.")
    @ConfigComment("These commands are run by the console, unless otherwise stated using the [SUDO] prefix,")
    @ConfigComment("in which case they are executed by the player.")
    @ConfigComment("")
    @ConfigComment("Available placeholders for the commands are the following:")
    @ConfigComment("   * [name]: name of the player")
    @ConfigComment("")
    @ConfigComment("Here are some examples of valid commands to execute:")
    @ConfigComment("   * '[SUDO] bbox version'")
    @ConfigComment("   * 'bsbadmin deaths set [player] 0'")
    @ConfigComment("")
    @ConfigComment("Note that player-executed commands might not work, as these commands can be run with said player being offline.")
    @ConfigEntry(path = "island.commands.on-respawn", since = "1.14.0")
    private List<String> onRespawnCommands = new ArrayList<>();

    // Sethome
    @ConfigComment("Allow setting home in the nether. Only available on nether islands, not vanilla nether.")
    @ConfigEntry(path = "island.sethome.nether.allow")
    private boolean allowSetHomeInNether = true;

    @ConfigEntry(path = "island.sethome.nether.require-confirmation")
    private boolean requireConfirmationToSetHomeInNether = true;

    @ConfigComment("Allow setting home in the end. Only available on end islands, not vanilla end.")
    @ConfigEntry(path = "island.sethome.the-end.allow")
    private boolean allowSetHomeInTheEnd = true;

    @ConfigEntry(path = "island.sethome.the-end.require-confirmation")
    private boolean requireConfirmationToSetHomeInTheEnd = true;

    // Deaths
    @ConfigComment("Whether deaths are counted or not.")
    @ConfigEntry(path = "island.deaths.counted")
    private boolean deathsCounted = true;

    @ConfigComment("Maximum number of deaths to count. The death count can be used by add-ons.")
    @ConfigEntry(path = "island.deaths.max")
    private int deathsMax = 10;

    @ConfigComment("When a player joins a team, reset their death count")
    @ConfigEntry(path = "island.deaths.team-join-reset")
    private boolean teamJoinDeathReset = true;

    @ConfigComment("Reset player death count when they start a new island or reset and island")
    @ConfigEntry(path = "island.deaths.reset-on-new-island")
    private boolean deathsResetOnNewIsland = true;

    // Ranks
    @ConfigEntry(path = "island.customranks")
    private Map<String, Integer> customRanks = new HashMap<>();

    // ---------------------------------------------

    /*      PROTECTION      */
    @ConfigComment("Geo restrict mobs.")
    @ConfigComment("Mobs that exit the island space where they were spawned will be removed.")
    @ConfigEntry(path = "protection.geo-limit-settings")
    private List<String> geoLimitSettings = new ArrayList<>();

    @ConfigComment("AcidIsland blocked mobs.")
    @ConfigComment("List of mobs that should not spawn in AcidIsland.")
    @ConfigEntry(path = "protection.block-mobs")
    private List<String> mobLimitSettings = new ArrayList<>();

    // Invincible visitor settings
    @ConfigComment("Invincible visitors. List of damages that will not affect visitors.")
    @ConfigComment("Make list blank if visitors should receive all damages")
    @ConfigEntry(path = "protection.invincible-visitors")
    private List<String> ivSettings = new ArrayList<>();

    //---------------------------------------------------------------------------------------/
    @ConfigComment("These settings should not be edited")
    @ConfigEntry(path = "do-not-edit-these-settings.reset-epoch")
    private long resetEpoch = 0;


    /**
     * @return the acidDamage
     */
    public int getAcidDamage() {
        return acidDamage;
    }
    /**
     * @return the acidDamageAnimal
     */
    public int getAcidDamageAnimal() {
        return acidDamageAnimal;
    }
    /**
     * @return the acidDamageDelay
     */
    public long getAcidDamageDelay() {
        return acidDamageDelay;
    }
    /**
     * @return the acidDamageMonster
     */
    public int getAcidDamageMonster() {
        return acidDamageMonster;
    }
    /**
     * @return the acidDestroyItemTime
     */
    public long getAcidDestroyItemTime() {
        return acidDestroyItemTime;
    }
    /**
     * @return the acidEffects
     */
    public List<PotionEffectType> getAcidEffects() {
        return acidEffects;
    }
    /**
     * @return the acidRainDamage
     */
    public int getAcidRainDamage() {
        return acidRainDamage;
    }

    @Override
    public int getBanLimit() {
        return banLimit;
    }
    //---------------------------------------------------------------------------------------/
    /**
     * @return the customRanks
     */
    public Map<String, Integer> getCustomRanks() {
        return customRanks;
    }
    /**
     * @return the deathsMax
     */
    @Override
    public int getDeathsMax() {
        return deathsMax;
    }
    /**
     * @return the defaultBiome
     */
    public Biome getDefaultBiome() {
        return defaultBiome;
    }
    /**
     * @return the defaultGameMode
     */
    @Override
    public GameMode getDefaultGameMode() {
        return defaultGameMode;
    }


    /**
     * @return the defaultIslandFlags
     * @since 1.21.0
     */
    @Override
    public Map<String, Integer> getDefaultIslandFlagNames()
    {
        return defaultIslandFlagNames;
    }


    /**
     * @return the defaultIslandSettings
     * @since 1.21.0
     */
    @Override
    public Map<String, Integer> getDefaultIslandSettingNames()
    {
        return defaultIslandSettingNames;
    }


    /**
     * @return the defaultIslandProtection
     * @deprecated since 1.21
     */
    @Override
    public Map<Flag, Integer> getDefaultIslandFlags() {
        return Collections.emptyMap();
    }


    /**
     * @return the defaultIslandSettings
     * @deprecated since 1.21
     */
    @Override
    public Map<Flag, Integer> getDefaultIslandSettings() {
        return Collections.emptyMap();
    }


    /**
     * @return the difficulty
     */
    @Override
    public Difficulty getDifficulty() {
        return difficulty;
    }
    /**
     * @return the endSeaHeight
     */
    public int getEndSeaHeight() {
        return endSeaHeight;
    }
    /* (non-Javadoc)
     * @see world.bentobox.bbox.api.configuration.WorldSettings#getFriendlyName()
     */
    @Override
    public String getFriendlyName() {
        return friendlyName;
    }
    /**
     * @return the geoLimitSettings
     */
    @Override
    public List<String> getGeoLimitSettings() {
        return geoLimitSettings;
    }
    /**
     * @return the islandDistance
     */
    @Override
    public int getIslandDistance() {
        return islandDistance;
    }
    /**
     * @return the islandHeight
     */
    @Override
    public int getIslandHeight() {
        return islandHeight;
    }
    /**
     * @return the islandProtectionRange
     */
    @Override
    public int getIslandProtectionRange() {
        return islandProtectionRange;
    }
    /**
     * @return the islandStartX
     */
    @Override
    public int getIslandStartX() {
        return islandStartX;
    }
    /**
     * @return the islandStartZ
     */
    @Override
    public int getIslandStartZ() {
        return islandStartZ;
    }
    /**
     * @return the islandXOffset
     */
    @Override
    public int getIslandXOffset() {
        return islandXOffset;
    }
    /**
     * @return the islandZOffset
     */
    @Override
    public int getIslandZOffset() {
        return islandZOffset;
    }
    /**
     * Invincible visitor settings
     * @return the ivSettings
     */
    @Override
    public List<String> getIvSettings() {
        return ivSettings;
    }
    /**
     * @return the maxHomes
     */
    @Override
    public int getMaxHomes() {
        return maxHomes;
    }
    /**
     * @return the maxIslands
     */
    @Override
    public int getMaxIslands() {
        return maxIslands;
    }
    /**
     * @return the maxTeamSize
     */
    @Override
    public int getMaxTeamSize() {
        return maxTeamSize;
    }
    /**
     * @return the netherSeaHeight
     */
    public int getNetherSeaHeight() {
        return netherSeaHeight;
    }
    /**
     * @return the netherSpawnRadius
     */
    @Override
    public int getNetherSpawnRadius() {
        return netherSpawnRadius;
    }
    @Override
    public String getPermissionPrefix() {
        return "acidisland";
    }
    /**
     * @return the removeMobsWhitelist
     */
    @Override
    public Set<EntityType> getRemoveMobsWhitelist() {
        return removeMobsWhitelist;
    }
    @Override
    public long getResetEpoch() {
        return resetEpoch;
    }
    /**
     * @return the resetLimit
     */
    @Override
    public int getResetLimit() {
        return resetLimit;
    }
    /**
     * @return the seaHeight
     */
    @Override
    public int getSeaHeight() {
        return seaHeight;
    }
    /**
     * @return the hidden flags
     */
    @Override
    public List<String> getHiddenFlags() {
        return hiddenFlags;
    }
    /**
     * @return the visitorbannedcommands
     */
    @Override
    public List<String> getVisitorBannedCommands() {
        return visitorBannedCommands;
    }

    /**
     * @return the fallingBannedCommands
     */
    @Override
    public List<String> getFallingBannedCommands() {
        return fallingBannedCommands;
    }

    /**
     * @return the worldFlags
     */
    @Override
    public Map<String, Boolean> getWorldFlags() {
        return worldFlags;
    }
    /**
     * @return the worldName
     */
    @Override
    public String getWorldName() {
        return worldName;
    }
    /**
     * @return the acidDamageChickens
     */
    public boolean isAcidDamageChickens() {
        return acidDamageChickens;
    }
    /**
     * @return the acidDamageOp
     */
    public boolean isAcidDamageOp() {
        return acidDamageOp;
    }
    /**
     * @return the allowSetHomeInNether
     */
    @Override
    public boolean isAllowSetHomeInNether() {
        return allowSetHomeInNether;
    }
    /**
     * @return the allowSetHomeInTheEnd
     */
    @Override
    public boolean isAllowSetHomeInTheEnd() {
        return allowSetHomeInTheEnd;
    }
    /**
     * @return the isDeathsCounted
     */
    @Override
    public boolean isDeathsCounted() {
        return deathsCounted;
    }
    /**
     * @return the dragonSpawn
     */
    @Override
    public boolean isDragonSpawn() {
        return dragonSpawn;
    }
    /**
     * @return the endGenerate
     */
    @Override
    public boolean isEndGenerate() {
        return endGenerate;
    }
    /**
     */
    @Override
    public boolean isEndIslands() {
        return endIslands;
    }
    /**
     * @return the fullArmorProtection
     */
    public boolean isFullArmorProtection() {
        return fullArmorProtection;
    }
    /**
     * @return the helmetProtection
     */
    public boolean isHelmetProtection() {
        return helmetProtection;
    }
    /**
     * @return the kickedKeepInventory
     */
    @Override
    public boolean isKickedKeepInventory() {
        return kickedKeepInventory;
    }


    /**
     * This method returns the createIslandOnFirstLoginEnabled boolean value.
     * @return the createIslandOnFirstLoginEnabled value
     * @since 1.9.0
     */
    @Override
    public boolean isCreateIslandOnFirstLoginEnabled()
    {
        return createIslandOnFirstLoginEnabled;
    }


    /**
     * This method returns the createIslandOnFirstLoginDelay int value.
     * @return the createIslandOnFirstLoginDelay value
     * @since 1.9.0
     */
    @Override
    public int getCreateIslandOnFirstLoginDelay()
    {
        return createIslandOnFirstLoginDelay;
    }


    /**
     * This method returns the createIslandOnFirstLoginAbortOnLogout boolean value.
     * @return the createIslandOnFirstLoginAbortOnLogout value
     * @since 1.9.0
     */
    @Override
    public boolean isCreateIslandOnFirstLoginAbortOnLogout()
    {
        return createIslandOnFirstLoginAbortOnLogout;
    }


    /**
     * @return the leaversLoseReset
     */
    @Override
    public boolean isLeaversLoseReset() {
        return leaversLoseReset;
    }
    /**
     * @return the netherGenerate
     */
    @Override
    public boolean isNetherGenerate() {
        return netherGenerate;
    }
    /**
     * @return the netherIslands
     */
    @Override
    public boolean isNetherIslands() {
        return netherIslands;
    }
    /**
     * @return the netherRoof
     */
    public boolean isNetherRoof() {
        return netherRoof;
    }
    /**
     * @return the onJoinResetEnderChest
     */
    @Override
    public boolean isOnJoinResetEnderChest() {
        return onJoinResetEnderChest;
    }
    /**
     * @return the onJoinResetInventory
     */
    @Override
    public boolean isOnJoinResetInventory() {
        return onJoinResetInventory;
    }
    /**
     * @return the onJoinResetMoney
     */
    @Override
    public boolean isOnJoinResetMoney() {
        return onJoinResetMoney;
    }
    /**
     * @return the onLeaveResetEnderChest
     */
    @Override
    public boolean isOnLeaveResetEnderChest() {
        return onLeaveResetEnderChest;
    }
    /**
     * @return the onLeaveResetInventory
     */
    @Override
    public boolean isOnLeaveResetInventory() {
        return onLeaveResetInventory;
    }
    /**
     * @return the onLeaveResetMoney
     */
    @Override
    public boolean isOnLeaveResetMoney() {
        return onLeaveResetMoney;
    }
    /**
     * @return the requireConfirmationToSetHomeInNether
     */
    @Override
    public boolean isRequireConfirmationToSetHomeInNether() {
        return requireConfirmationToSetHomeInNether;
    }
    /**
     * @return the requireConfirmationToSetHomeInTheEnd
     */
    @Override
    public boolean isRequireConfirmationToSetHomeInTheEnd() {
        return requireConfirmationToSetHomeInTheEnd;
    }
    @Override
    public boolean isTeamJoinDeathReset() {
        return teamJoinDeathReset;
    }
    /**
     * @return the useOwnGenerator
     */
    @Override
    public boolean isUseOwnGenerator() {
        return useOwnGenerator;
    }
    @Override
    public boolean isWaterUnsafe() {
        // Water is unsafe in acid island
        return true;
    }
    /**
     * @param acidDamage the acidDamage to set
     */
    public void setAcidDamage(int acidDamage) {
        this.acidDamage = acidDamage;
    }
    /**
     * @param acidDamageAnimal the acidDamageAnimal to set
     */
    public void setAcidDamageAnimal(int acidDamageAnimal) {
        this.acidDamageAnimal = acidDamageAnimal;
    }
    /**
     * @param acidDamageChickens the acidDamageChickens to set
     */
    public void setAcidDamageChickens(boolean acidDamageChickens) {
        this.acidDamageChickens = acidDamageChickens;
    }
    /**
     * @param acidDamageDelay the acidDamageDelay to set
     */
    public void setAcidDamageDelay(long acidDamageDelay) {
        this.acidDamageDelay = acidDamageDelay;
    }
    /**
     * @param acidDamageMonster the acidDamageMonster to set
     */
    public void setAcidDamageMonster(int acidDamageMonster) {
        this.acidDamageMonster = acidDamageMonster;
    }
    /**
     * @param acidDamageOp the acidDamageOp to set
     */
    public void setAcidDamageOp(boolean acidDamageOp) {
        this.acidDamageOp = acidDamageOp;
    }
    /**
     * @param acidDestroyItemTime the acidDestroyItemTime to set
     */
    public void setAcidDestroyItemTime(long acidDestroyItemTime) {
        this.acidDestroyItemTime = acidDestroyItemTime;
    }
    /**
     * @param acidEffects the acidEffects to set
     */
    public void setAcidEffects(List<PotionEffectType> acidEffects) {
        this.acidEffects = acidEffects;
    }
    /**
     * @param acidRainDamage the acidRainDamage to set
     */
    public void setAcidRainDamage(int acidRainDamage) {
        this.acidRainDamage = acidRainDamage;
    }
    /**
     * @param adminCommand what you want your admin command to be
     */
    public void setAdminCommand(String adminCommand) {
        this.adminCommandAliases = adminCommand;
    }
    /**
     * @param allowSetHomeInNether the allowSetHomeInNether to set
     */
    public void setAllowSetHomeInNether(boolean allowSetHomeInNether) {
        this.allowSetHomeInNether = allowSetHomeInNether;
    }
    /**
     * @param allowSetHomeInTheEnd the allowSetHomeInTheEnd to set
     */
    public void setAllowSetHomeInTheEnd(boolean allowSetHomeInTheEnd) {
        this.allowSetHomeInTheEnd = allowSetHomeInTheEnd;
    }
    /**
     * @param banLimit the banLimit to set
     */
    public void setBanLimit(int banLimit) {
        this.banLimit = banLimit;
    }
    /**
     * @param customRanks the customRanks to set
     */
    public void setCustomRanks(Map<String, Integer> customRanks) {
        this.customRanks = customRanks;
    }
    /**
     * @param deathsCounted the deathsCounted to set
     */
    public void setDeathsCounted(boolean deathsCounted) {
        this.deathsCounted = deathsCounted;
    }
    /**
     * @param deathsMax the deathsMax to set
     */
    public void setDeathsMax(int deathsMax) {
        this.deathsMax = deathsMax;
    }
    /**
     * @param defaultBiome the defaultBiome to set
     */
    public void setDefaultBiome(Biome defaultBiome) {
        this.defaultBiome = defaultBiome;
    }
    /**
     * @param defaultGameMode the defaultGameMode to set
     */
    public void setDefaultGameMode(GameMode defaultGameMode) {
        this.defaultGameMode = defaultGameMode;
    }
    /**
     */
    public void setDefaultIslandFlagNames(Map<String, Integer> defaultIslandFlags) {
        this.defaultIslandFlagNames = defaultIslandFlags;
    }
    /**
     * @param defaultIslandSettings the defaultIslandSettings to set
     */
    public void setDefaultIslandSettingNames(Map<String, Integer> defaultIslandSettings) {
        this.defaultIslandSettingNames = defaultIslandSettings;
    }
    /**
     * @param difficulty the difficulty to set
     */
    @Override
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
    /**
     * @param dragonSpawn the dragonSpawn to set
     */
    public void setDragonSpawn(boolean dragonSpawn) {
        this.dragonSpawn = dragonSpawn;
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
     * @param friendlyName the friendlyName to set
     */
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }
    /**
     * @param fullArmorProtection the fullArmorProtection to set
     */
    public void setFullArmorProtection(boolean fullArmorProtection) {
        this.fullArmorProtection = fullArmorProtection;
    }
    /**
     * @param geoLimitSettings the geoLimitSettings to set
     */
    public void setGeoLimitSettings(List<String> geoLimitSettings) {
        this.geoLimitSettings = geoLimitSettings;
    }

    /**
     * @param helmetProtection the helmetProtection to set
     */
    public void setHelmetProtection(boolean helmetProtection) {
        this.helmetProtection = helmetProtection;
    }
    /**
     * @param islandCommand what you want your island command to be
     */
    public void setIslandCommand(String islandCommand) {
        this.playerCommandAliases = islandCommand;
    }

    /**
     * @param islandDistance the islandDistance to set
     */
    public void setIslandDistance(int islandDistance) {
        this.islandDistance = islandDistance;
    }
    /**
     * @param islandHeight the islandHeight to set
     */
    public void setIslandHeight(int islandHeight) {
        this.islandHeight = islandHeight;
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
     * @param ivSettings the ivSettings to set
     */
    public void setIvSettings(List<String> ivSettings) {
        this.ivSettings = ivSettings;
    }
    /**
     * @param kickedKeepInventory the kickedKeepInventory to set
     */
    public void setKickedKeepInventory(boolean kickedKeepInventory) {
        this.kickedKeepInventory = kickedKeepInventory;
    }
    /**
     * @param leaversLoseReset the leaversLoseReset to set
     */
    public void setLeaversLoseReset(boolean leaversLoseReset) {
        this.leaversLoseReset = leaversLoseReset;
    }
    /**
     * @param maxHomes the maxHomes to set
     */
    public void setMaxHomes(int maxHomes) {
        this.maxHomes = maxHomes;
    }
    /**
     * @param maxIslands the maxIslands to set
     */
    public void setMaxIslands(int maxIslands) {
        this.maxIslands = maxIslands;
    }
    /**
     * @param maxTeamSize the maxTeamSize to set
     */
    public void setMaxTeamSize(int maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
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
     * @param onJoinResetEnderChest the onJoinResetEnderChest to set
     */
    public void setOnJoinResetEnderChest(boolean onJoinResetEnderChest) {
        this.onJoinResetEnderChest = onJoinResetEnderChest;
    }

    /**
     * @param onJoinResetInventory the onJoinResetInventory to set
     */
    public void setOnJoinResetInventory(boolean onJoinResetInventory) {
        this.onJoinResetInventory = onJoinResetInventory;
    }

    /**
     * @param onJoinResetMoney the onJoinResetMoney to set
     */
    public void setOnJoinResetMoney(boolean onJoinResetMoney) {
        this.onJoinResetMoney = onJoinResetMoney;
    }
    /**
     * @param onLeaveResetEnderChest the onLeaveResetEnderChest to set
     */
    public void setOnLeaveResetEnderChest(boolean onLeaveResetEnderChest) {
        this.onLeaveResetEnderChest = onLeaveResetEnderChest;
    }

    /**
     * @param onLeaveResetInventory the onLeaveResetInventory to set
     */
    public void setOnLeaveResetInventory(boolean onLeaveResetInventory) {
        this.onLeaveResetInventory = onLeaveResetInventory;
    }
    /**
     * @param onLeaveResetMoney the onLeaveResetMoney to set
     */
    public void setOnLeaveResetMoney(boolean onLeaveResetMoney) {
        this.onLeaveResetMoney = onLeaveResetMoney;
    }

    /**
     * @param removeMobsWhitelist the removeMobsWhitelist to set
     */
    public void setRemoveMobsWhitelist(Set<EntityType> removeMobsWhitelist) {
        this.removeMobsWhitelist = removeMobsWhitelist;
    }
    /**
     * @param requireConfirmationToSetHomeInNether the requireConfirmationToSetHomeInNether to set
     */
    public void setRequireConfirmationToSetHomeInNether(boolean requireConfirmationToSetHomeInNether) {
        this.requireConfirmationToSetHomeInNether = requireConfirmationToSetHomeInNether;
    }
    /**
     * @param requireConfirmationToSetHomeInTheEnd the requireConfirmationToSetHomeInTheEnd to set
     */
    public void setRequireConfirmationToSetHomeInTheEnd(boolean requireConfirmationToSetHomeInTheEnd) {
        this.requireConfirmationToSetHomeInTheEnd = requireConfirmationToSetHomeInTheEnd;
    }
    @Override
    public void setResetEpoch(long timestamp) {
        this.resetEpoch = timestamp;
    }
    /**
     * @param resetLimit the resetLimit to set
     */
    public void setResetLimit(int resetLimit) {
        this.resetLimit = resetLimit;
    }
    /**
     * @param seaHeight the seaHeight to set
     */
    public void setSeaHeight(int seaHeight) {
        this.seaHeight = seaHeight;
    }
    /**
     * @param netherSeaHeight the netherSeaHeight to set
     */
    public void setNetherSeaHeight(int netherSeaHeight) {
        this.netherSeaHeight = netherSeaHeight;
    }
    /**
     * @param endSeaHeight the endSeaHeight to set
     */
    public void setEndSeaHeight(int endSeaHeight) {
        this.endSeaHeight = endSeaHeight;
    }
    /**
     * @param teamJoinDeathReset the teamJoinDeathReset to set
     */
    public void setTeamJoinDeathReset(boolean teamJoinDeathReset) {
        this.teamJoinDeathReset = teamJoinDeathReset;
    }

    /**
     * @param useOwnGenerator the useOwnGenerator to set
     */
    public void setUseOwnGenerator(boolean useOwnGenerator) {
        this.useOwnGenerator = useOwnGenerator;
    }

    /**
     * @param hiddenFlags the hidden flags to set
     */
    public void setHiddenFlags(List<String> hiddenFlags) {
        this.hiddenFlags = hiddenFlags;
    }

    /**
     * @param visitorBannedCommands the visitorbannedcommands to set
     */
    public void setVisitorBannedCommands(List<String> visitorBannedCommands) {
        this.visitorBannedCommands = visitorBannedCommands;
    }

    /**
     * @param fallingBannedCommands the fallingBannedCommands to set
     */
    public void setFallingBannedCommands(List<String> fallingBannedCommands) {
        this.fallingBannedCommands = fallingBannedCommands;
    }

    /**
     * @param worldFlags the worldFlags to set
     */
    public void setWorldFlags(Map<String, Boolean> worldFlags) {
        this.worldFlags = worldFlags;
    }

    /**
     * @param worldName the worldName to set
     */
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }
    /**
     * @return the acidDamageSnow
     */
    public boolean isAcidDamageSnow() {
        return acidDamageSnow;
    }
    /**
     * @param acidDamageSnow the acidDamageSnow to set
     */
    public void setAcidDamageSnow(boolean acidDamageSnow) {
        this.acidDamageSnow = acidDamageSnow;
    }
    /**
     * @return the deathsResetOnNewIsland
     */
    @Override
    public boolean isDeathsResetOnNewIsland() {
        return deathsResetOnNewIsland;
    }
    /**
     * @param deathsResetOnNewIsland the deathsResetOnNewIsland to set
     */
    public void setDeathsResetOnNewIsland(boolean deathsResetOnNewIsland) {
        this.deathsResetOnNewIsland = deathsResetOnNewIsland;
    }
    /**
     * @return the onJoinCommands
     */
    @Override
    public List<String> getOnJoinCommands() {
        return onJoinCommands;
    }
    /**
     * @param onJoinCommands the onJoinCommands to set
     */
    public void setOnJoinCommands(List<String> onJoinCommands) {
        this.onJoinCommands = onJoinCommands;
    }
    /**
     * @return the onLeaveCommands
     */
    @Override
    public List<String> getOnLeaveCommands() {
        return onLeaveCommands;
    }
    /**
     * @param onLeaveCommands the onLeaveCommands to set
     */
    public void setOnLeaveCommands(List<String> onLeaveCommands) {
        this.onLeaveCommands = onLeaveCommands;
    }

    /**
     * @return the onRespawnCommands
     */
    @Override
    public List<String> getOnRespawnCommands() {
        return onRespawnCommands;
    }

    /**
     * Sets on respawn commands.
     *
     * @param onRespawnCommands the on respawn commands
     */
    public void setOnRespawnCommands(List<String> onRespawnCommands) {
        this.onRespawnCommands = onRespawnCommands;
    }

    /**
     * @return the onJoinResetHealth
     */
    @Override
    public boolean isOnJoinResetHealth() {
        return onJoinResetHealth;
    }
    /**
     * @param onJoinResetHealth the onJoinResetHealth to set
     */
    public void setOnJoinResetHealth(boolean onJoinResetHealth) {
        this.onJoinResetHealth = onJoinResetHealth;
    }
    /**
     * @return the onJoinResetHunger
     */
    @Override
    public boolean isOnJoinResetHunger() {
        return onJoinResetHunger;
    }
    /**
     * @param onJoinResetHunger the onJoinResetHunger to set
     */
    public void setOnJoinResetHunger(boolean onJoinResetHunger) {
        this.onJoinResetHunger = onJoinResetHunger;
    }
    /**
     * @return the onJoinResetXP
     */
    @Override
    public boolean isOnJoinResetXP() {
        return onJoinResetXP;
    }
    /**
     * @param onJoinResetXP the onJoinResetXP to set
     */
    public void setOnJoinResetXP(boolean onJoinResetXP) {
        this.onJoinResetXP = onJoinResetXP;
    }
    /**
     * @return the onLeaveResetHealth
     */
    @Override
    public boolean isOnLeaveResetHealth() {
        return onLeaveResetHealth;
    }
    /**
     * @param onLeaveResetHealth the onLeaveResetHealth to set
     */
    public void setOnLeaveResetHealth(boolean onLeaveResetHealth) {
        this.onLeaveResetHealth = onLeaveResetHealth;
    }
    /**
     * @return the onLeaveResetHunger
     */
    @Override
    public boolean isOnLeaveResetHunger() {
        return onLeaveResetHunger;
    }
    /**
     * @param onLeaveResetHunger the onLeaveResetHunger to set
     */
    public void setOnLeaveResetHunger(boolean onLeaveResetHunger) {
        this.onLeaveResetHunger = onLeaveResetHunger;
    }
    /**
     * @return the onLeaveResetXP
     */
    @Override
    public boolean isOnLeaveResetXP() {
        return onLeaveResetXP;
    }
    /**
     * @param onLeaveResetXP the onLeaveResetXP to set
     */
    public void setOnLeaveResetXP(boolean onLeaveResetXP) {
        this.onLeaveResetXP = onLeaveResetXP;
    }

    /**
     * @param createIslandOnFirstLoginEnabled the createIslandOnFirstLoginEnabled to set
     */
    public void setCreateIslandOnFirstLoginEnabled(boolean createIslandOnFirstLoginEnabled)
    {
        this.createIslandOnFirstLoginEnabled = createIslandOnFirstLoginEnabled;
    }

    /**
     * @param createIslandOnFirstLoginDelay the createIslandOnFirstLoginDelay to set
     */
    public void setCreateIslandOnFirstLoginDelay(int createIslandOnFirstLoginDelay)
    {
        this.createIslandOnFirstLoginDelay = createIslandOnFirstLoginDelay;
    }

    /**
     * @param createIslandOnFirstLoginAbortOnLogout the createIslandOnFirstLoginAbortOnLogout to set
     */
    public void setCreateIslandOnFirstLoginAbortOnLogout(boolean createIslandOnFirstLoginAbortOnLogout)
    {
        this.createIslandOnFirstLoginAbortOnLogout = createIslandOnFirstLoginAbortOnLogout;
    }

    /**
     * @return the pasteMissingIslands
     * @since 1.10.0
     */
    @Override
    public boolean isPasteMissingIslands() {
        return pasteMissingIslands;
    }

    /**
     * @param pasteMissingIslands the pasteMissingIslands to set
     * @since 1.10.0
     */
    public void setPasteMissingIslands(boolean pasteMissingIslands) {
        this.pasteMissingIslands = pasteMissingIslands;
    }

    /**
     * Get acid rain potion effects
     * @return liust of potion effects
     * @since 1.9.1
     */
    public List<PotionEffectType> getAcidRainEffects() {
        return acidRainEffects;
    }

    /**
     *
     * @param acidRainEffects potion effects from rain
     * @since 1.9.1
     */
    public void setAcidRainEffects(List<PotionEffectType> acidRainEffects) {
        this.acidRainEffects = acidRainEffects;
    }
    /**
     * @return the rainEffectDuation
     */
    public int getRainEffectDuation() {
        return rainEffectDuation;
    }
    /**
     * @param rainEffectDuation the rainEffectDuation to set
     */
    public void setRainEffectDuation(int rainEffectDuation) {
        this.rainEffectDuation = rainEffectDuation;
    }
    /**
     * @return the acidEffectDuation
     */
    public int getAcidEffectDuation() {
        return acidEffectDuation;
    }
    /**
     * @param acidEffectDuation the acidEffectDuation to set
     */
    public void setAcidEffectDuation(int acidEffectDuation) {
        this.acidEffectDuation = acidEffectDuation;
    }
    /**
     * @return the spawnLimitMonsters
     */
    public int getSpawnLimitMonsters() {
        return spawnLimitMonsters;
    }
    /**
     * @param spawnLimitMonsters the spawnLimitMonsters to set
     */
    public void setSpawnLimitMonsters(int spawnLimitMonsters) {
        this.spawnLimitMonsters = spawnLimitMonsters;
    }
    /**
     * @return the spawnLimitAnimals
     */
    public int getSpawnLimitAnimals() {
        return spawnLimitAnimals;
    }
    /**
     * @param spawnLimitAnimals the spawnLimitAnimals to set
     */
    public void setSpawnLimitAnimals(int spawnLimitAnimals) {
        this.spawnLimitAnimals = spawnLimitAnimals;
    }
    /**
     * @return the spawnLimitWaterAnimals
     */
    public int getSpawnLimitWaterAnimals() {
        return spawnLimitWaterAnimals;
    }
    /**
     * @param spawnLimitWaterAnimals the spawnLimitWaterAnimals to set
     */
    public void setSpawnLimitWaterAnimals(int spawnLimitWaterAnimals) {
        this.spawnLimitWaterAnimals = spawnLimitWaterAnimals;
    }
    /**
     * @return the spawnLimitAmbient
     */
    public int getSpawnLimitAmbient() {
        return spawnLimitAmbient;
    }
    /**
     * @param spawnLimitAmbient the spawnLimitAmbient to set
     */
    public void setSpawnLimitAmbient(int spawnLimitAmbient) {
        this.spawnLimitAmbient = spawnLimitAmbient;
    }
    /**
     * @return the ticksPerAnimalSpawns
     */
    public int getTicksPerAnimalSpawns() {
        return ticksPerAnimalSpawns;
    }
    /**
     * @param ticksPerAnimalSpawns the ticksPerAnimalSpawns to set
     */
    public void setTicksPerAnimalSpawns(int ticksPerAnimalSpawns) {
        this.ticksPerAnimalSpawns = ticksPerAnimalSpawns;
    }
    /**
     * @return the ticksPerMonsterSpawns
     */
    public int getTicksPerMonsterSpawns() {
        return ticksPerMonsterSpawns;
    }
    /**
     * @param ticksPerMonsterSpawns the ticksPerMonsterSpawns to set
     */
    public void setTicksPerMonsterSpawns(int ticksPerMonsterSpawns) {
        this.ticksPerMonsterSpawns = ticksPerMonsterSpawns;
    }
    /**
     * @return the maxCoopSize
     */
    @Override
    public int getMaxCoopSize() {
        return maxCoopSize;
    }

    /**
     * @param maxCoopSize the maxCoopSize to set
     */
    public void setMaxCoopSize(int maxCoopSize) {
        this.maxCoopSize = maxCoopSize;
    }

    /**
     * @return the maxTrustSize
     */
    @Override
    public int getMaxTrustSize() {
        return maxTrustSize;
    }

    /**
     * @param maxTrustSize the maxTrustSize to set
     */
    public void setMaxTrustSize(int maxTrustSize) {
        this.maxTrustSize = maxTrustSize;
    }
    /**
     * @return the playerCommandAliases
     */
    @Override
    public String getPlayerCommandAliases() {
        return playerCommandAliases;
    }
    /**
     * @param playerCommandAliases the playerCommandAliases to set
     */
    public void setPlayerCommandAliases(String playerCommandAliases) {
        this.playerCommandAliases = playerCommandAliases;
    }
    /**
     * @return the adminCommandAliases
     */
    @Override
    public String getAdminCommandAliases() {
        return adminCommandAliases;
    }
    /**
     * @param adminCommandAliases the adminCommandAliases to set
     */
    public void setAdminCommandAliases(String adminCommandAliases) {
        this.adminCommandAliases = adminCommandAliases;
    }
    /**
     * @return the defaultNewPlayerAction
     */
    @Override
    public String getDefaultNewPlayerAction() {
        return defaultNewPlayerAction;
    }
    /**
     * @param defaultNewPlayerAction the defaultNewPlayerAction to set
     */
    public void setDefaultNewPlayerAction(String defaultNewPlayerAction) {
        this.defaultNewPlayerAction = defaultNewPlayerAction;
    }
    /**
     * @return the defaultPlayerAction
     */
    @Override
    public String getDefaultPlayerAction() {
        return defaultPlayerAction;
    }
    /**
     * @param defaultPlayerAction the defaultPlayerAction to set
     */
    public void setDefaultPlayerAction(String defaultPlayerAction) {
        this.defaultPlayerAction = defaultPlayerAction;
    }
    /**
     * @return the defaultNetherBiome
     */
    public Biome getDefaultNetherBiome() {
        return defaultNetherBiome;
    }
    /**
     * @param defaultNetherBiome the defaultNetherBiome to set
     */
    public void setDefaultNetherBiome(Biome defaultNetherBiome) {
        this.defaultNetherBiome = defaultNetherBiome;
    }
    /**
     * @return the defaultEndBiome
     */
    public Biome getDefaultEndBiome() {
        return defaultEndBiome;
    }
    /**
     * @param defaultEndBiome the defaultEndBiome to set
     */
    public void setDefaultEndBiome(Biome defaultEndBiome) {
        this.defaultEndBiome = defaultEndBiome;
    }
    /**
     * @return the teleportPlayerToIslandUponIslandCreation
     */
    @Override
    public boolean isTeleportPlayerToIslandUponIslandCreation() {
        return teleportPlayerToIslandUponIslandCreation;
    }
    /**
     * @param teleportPlayerToIslandUponIslandCreation the teleportPlayerToIslandUponIslandCreation to set
     */
    public void setTeleportPlayerToIslandUponIslandCreation(boolean teleportPlayerToIslandUponIslandCreation) {
        this.teleportPlayerToIslandUponIslandCreation = teleportPlayerToIslandUponIslandCreation;
    }
    /**
     * @return the mobLimitSettings
     */
    @Override
    public List<String> getMobLimitSettings() {
        return mobLimitSettings;
    }
    /**
     * @param mobLimitSettings the mobLimitSettings to set
     */
    public void setMobLimitSettings(List<String> mobLimitSettings) {
        this.mobLimitSettings = mobLimitSettings;
    }

    /**
     * @return the makeNetherPortals
     */
    @Override
    public boolean isMakeNetherPortals() {
        return makeNetherPortals;
    }

    /**
     * @return the makeEndPortals
     */
    @Override
    public boolean isMakeEndPortals() {
        return makeEndPortals;
    }

    /**
     * Sets make nether portals.
     * @param makeNetherPortals the make nether portals
     */
    public void setMakeNetherPortals(boolean makeNetherPortals) {
        this.makeNetherPortals = makeNetherPortals;
    }

    /**
     * Sets make end portals.
     * @param makeEndPortals the make end portals
     */
    public void setMakeEndPortals(boolean makeEndPortals) {
        this.makeEndPortals = makeEndPortals;
    }
    public Material getWaterBlock() {
        return waterBlock;
    }
    public void setWaterBlock(Material waterBlock) {
        this.waterBlock = waterBlock;
    }
    public Material getNetherWaterBlock() {
        return netherWaterBlock;
    }
    public void setNetherWaterBlock(Material netherWaterBlock) {
        this.netherWaterBlock = netherWaterBlock;
    }
    public Material getEndWaterBlock() {
        return endWaterBlock;
    }
    public void setEndWaterBlock(Material endWaterBlock) {
        this.endWaterBlock = endWaterBlock;
    }
    public boolean isOceanFloor() {
        return oceanFloor;
    }
    public void setOceanFloor(boolean oceanFloor) {
        this.oceanFloor = oceanFloor;
    }

    /**
     * @return the makeStructures
     */
    public boolean isMakeStructures() {
        return makeStructures;
    }

    /**
     * @param makeStructures the makeStructures to set
     */
    public void setMakeStructures(boolean makeStructures) {
        this.makeStructures = makeStructures;
    }

    /**
     * @return the makeCaves
     */
    public boolean isMakeCaves() {
        return makeCaves;
    }

    /**
     * @param makeCaves the makeCaves to set
     */
    public void setMakeCaves(boolean makeCaves) {
        this.makeCaves = makeCaves;
    }

    /**
     * @return the makeDecorations
     */
    public boolean isMakeDecorations() {
        return makeDecorations;
    }

    /**
     * @param makeDecorations the makeDecorations to set
     */
    public void setMakeDecorations(boolean makeDecorations) {
        this.makeDecorations = makeDecorations;
    }

    /**
     * @return the disallowTeamMemberIslands
     */
    @Override
    public boolean isDisallowTeamMemberIslands() {
        return disallowTeamMemberIslands;
    }

    /**
     * @param disallowTeamMemberIslands the disallowTeamMemberIslands to set
     */
    public void setDisallowTeamMemberIslands(boolean disallowTeamMemberIslands) {
        this.disallowTeamMemberIslands = disallowTeamMemberIslands;
    }

    /**
     * @return the concurrentIslands
     */
    @Override
    public int getConcurrentIslands() {
        if (concurrentIslands <= 0) {
            return BentoBox.getInstance().getSettings().getIslandNumber();
        }
        return concurrentIslands;
    }

    /**
     * @param concurrentIslands the concurrentIslands to set
     */
    public void setConcurrentIslands(int concurrentIslands) {
        this.concurrentIslands = concurrentIslands;
    }
}
