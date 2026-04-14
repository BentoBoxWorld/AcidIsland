# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

AcidIsland is a BentoBox GameModeAddon for Minecraft (Paper). It implements a Skyblock-like game mode where the ocean is acid that damages players, mobs, and items. Requires BentoBox 3.12.0+ as the parent plugin framework.

## Build Commands

```bash
mvn clean package                          # Build (default goal)
mvn clean test                             # Run all tests
mvn test -Dtest=AcidEffectTest             # Run a single test class
mvn test -Dtest=AcidEffectTest#testMethod  # Run a single test method
mvn -DskipTests clean package              # Build without tests
```

Java 21 is required. The project uses Maven with the Shade plugin to produce the final JAR.

## Architecture

### Addon Lifecycle

This is a BentoBox addon, not a standalone plugin. The lifecycle flows through:
1. **`AcidIslandPladdon`** — Bukkit plugin entry point (Pladdon wrapper), instantiates the addon
2. **`AcidIsland`** (extends `GameModeAddon`) — Main addon class with `onLoad()` → `createWorlds()` → `onEnable()` → `allLoaded()` lifecycle
3. **`AISettings`** — Configuration POJO with BentoBox `@ConfigEntry` annotations, implements `WorldSettings`

### Key Packages

- **`listeners/`** — `AcidEffect` is the core gameplay listener handling all acid damage (players, mobs, items, armor protection, potion effects, Essentials integration). `LavaCheck` prevents normal stone generation from lava+acid water.
- **`world/`** — `ChunkGeneratorWorld` generates ocean floor terrain using Perlin noise. `AcidTask` is a repeating task applying acid damage to entities in water. `AcidBiomeProvider` provides biomes per environment type.
- **`events/`** — Custom Bukkit events (`AcidEvent`, `AcidRainEvent`, `EntityDamageByAcidEvent`, etc.) that are cancellable, allowing other plugins to modify acid behavior.

### Testing

Tests use **JUnit 5 + MockBukkit + Mockito 5**. The standard pattern for each test class:

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MyTest {
    private ServerMock server;
    private MockedStatic<Bukkit> mockedBukkit;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();  // always first
        mockedBukkit = Mockito.mockStatic(Bukkit.class, Mockito.RETURNS_DEEP_STUBS);
        mockedBukkit.when(Bukkit::getMinecraftVersion).thenReturn("1.21.11");
        mockedBukkit.when(Bukkit::getServer).thenReturn(server);
        // ...
    }

    @AfterEach
    void tearDown() {
        mockedBukkit.closeOnDemand();
        MockBukkit.unmock();
    }
}
```

Key rules:
- **Do NOT call `Mockito.framework().clearInlineMocks()` in `@AfterEach`** — it disables `@BeforeAll` mocks and interferes with MockitoExtension's mock lifecycle for subsequent tests.
- Always mock `Bukkit::getMinecraftVersion` — BentoBox 3.12.0 calls it in a static initializer and Paper API is required for it to exist on the classpath.
- `paper-api` is a `provided` dependency (not `spigot-api`) — this is what puts `getMinecraftVersion()` on the compile/test classpath.
- MockBukkit's JUnit transitive deps are excluded in `pom.xml` to avoid JUnit 6 version conflicts with surefire.
- Do not use `new ItemStack(Material.AIR)` in tests — use `null` for empty armor slots; Paper 1.21's ItemStack handles AIR differently.
- Do not reference `world.bentobox.bentobox.lists.Flags` static fields in tests — the class static initializer requires full BentoBox initialization. Use the string flag ID instead (e.g., `"ANIMAL_NATURAL_SPAWN"`).
- **No `public` modifier** on test methods (`@Test`, `@BeforeEach`, `@AfterEach`, `@BeforeAll`, `@AfterAll`) — JUnit 5 does not require it and SonarCloud flags it.
- **No `throws Exception`** on `@BeforeEach`/`@AfterEach` unless the method body actually declares a checked exception.
- **`assertDoesNotThrow(() -> ...)`** for tests that verify no exception is thrown — bare method calls with no assertions are flagged by SonarCloud.
- **`assertEquals(expected, actual)`** for numeric equality — `assertTrue(x == y)` is flagged (S5785).
- **`@Disabled` must include a reason string** — e.g. `@Disabled("PotionEffectType cannot be mocked without full server initialisation")`.
- **Do not use `eq()` in `verify()` or `when()` for concrete values** — pass the value directly; `eq()` wrappers on non-matcher arguments are redundant and flagged (S6068).

### Locales

All 24 locale files use **MiniMessage format** (e.g., `<red>`, `<dark_blue>`). Legacy `&` color codes must not be used. BentoBox 3.12.0+ is required for MiniMessage support.

### Configuration

- `src/main/resources/config.yml` — Default config with acid damage values, rain settings, potion effects, world generation params
- `src/main/resources/addon.yml` — BentoBox addon metadata and permissions (requires `api-version: 3.12.0`)
- `src/main/resources/locales/` — 24 language translation files (MiniMessage format)
- `src/main/resources/blueprints/` — Island templates (overworld, nether, end)

## Code Conventions

### Java 21 idioms (enforced by SonarCloud)

- **Pattern-matching instanceof** — use `instanceof Type varName` instead of `instanceof Type` + explicit cast (S6201).
- **`Math.clamp`** — use `Math.clamp(value, min, max)` instead of `Math.max(min, Math.min(max, value))` (S6885).
- **`Map.putIfAbsent`** — replace `if (!map.containsKey(k)) { map.put(k, v); ... }` with `if (map.putIfAbsent(k, v) == null) { ... }` (S3824).
- **`@Deprecated` annotation** — always include `since` and `forRemoval` arguments, e.g. `@Deprecated(since = "1.21", forRemoval = true)` (S6355). If `forRemoval` intent is unclear use `since` only (S1123).
- **Reduce cognitive complexity** by extracting private helper methods rather than nesting conditions.

### SonarCloud issue lookup

Query open issues via the public API (no auth needed for public projects):

```bash
curl -s "https://sonarcloud.io/api/issues/search?componentKeys=BentoBoxWorld_AcidIsland&statuses=OPEN&impactSeverities=MEDIUM&ps=100" \
  | python3 -c "import json,sys; [print(f'{i[\"component\"].split(\":\",1)[-1]}:{i.get(\"line\",\"?\")} [{i[\"rule\"]}] {i[\"message\"]}') for i in json.load(sys.stdin)[\"issues\"]]"
```

Change `impactSeverities` to `HIGH`, `MEDIUM`, or `LOW` as needed.

## CI

GitHub Actions workflow on `develop` branch and PRs: builds with Java 21, runs JaCoCo coverage, reports to SonarCloud.

## Dependency Source Lookup

When you need to inspect source code for a dependency (e.g., BentoBox, addons):

1. **Check local Maven repo first**: `~/.m2/repository/` — sources jars are named `*-sources.jar`
2. **Check the workspace**: Look for sibling directories or Git submodules that may contain the dependency as a local project (e.g., `../bentoBox`, `../addon-*`)
3. **Check Maven local cache for already-extracted sources** before downloading anything
4. Only download a jar or fetch from the internet if the above steps yield nothing useful

Prefer reading `.java` source files directly from a local Git clone over decompiling or extracting a jar.

In general, the latest version of BentoBox should be targeted.

## Project Layout

Related projects are checked out as siblings under `~/git/`:

**Core:**
- `bentobox/` — core BentoBox framework

**Game modes:**
- `addon-acidisland/` — AcidIsland game mode
- `addon-bskyblock/` — BSkyBlock game mode
- `Boxed/` — Boxed game mode (expandable box area)
- `CaveBlock/` — CaveBlock game mode
- `OneBlock/` — AOneBlock game mode
- `SkyGrid/` — SkyGrid game mode
- `RaftMode/` — Raft survival game mode
- `StrangerRealms/` — StrangerRealms game mode
- `Brix/` — plot game mode
- `parkour/` — Parkour game mode
- `poseidon/` — Poseidon game mode
- `gg/` — gg game mode

**Addons:**
- `addon-level/` — island level calculation
- `addon-challenges/` — challenges system
- `addon-welcomewarpsigns/` — warp signs
- `addon-limits/` — block/entity limits
- `addon-invSwitcher/` / `invSwitcher/` — inventory switcher
- `addon-biomes/` / `Biomes/` — biomes management
- `Bank/` — island bank
- `Border/` — world border for islands
- `Chat/` — island chat
- `CheckMeOut/` — island submission/voting
- `ControlPanel/` — game mode control panel
- `Converter/` — ASkyBlock to BSkyBlock converter
- `DimensionalTrees/` — dimension-specific trees
- `discordwebhook/` — Discord integration
- `Downloads/` — BentoBox downloads site
- `DragonFights/` — per-island ender dragon fights
- `ExtraMobs/` — additional mob spawning rules
- `FarmersDance/` — twerking crop growth
- `GravityFlux/` — gravity addon
- `Greenhouses-addon/` — greenhouse biomes
- `IslandFly/` — island flight permission
- `IslandRankup/` — island rankup system
- `Likes/` — island likes/dislikes
- `Limits/` — block/entity limits
- `lost-sheep/` — lost sheep adventure
- `MagicCobblestoneGenerator/` — custom cobblestone generator
- `PortalStart/` — portal-based island start
- `pp/` — pp addon
- `Regionerator/` — region management
- `Residence/` — residence addon
- `TopBlock/` — top ten for OneBlock
- `TwerkingForTrees/` — twerking tree growth
- `Upgrades/` — island upgrades (Vault)
- `Visit/` — island visiting
- `weblink/` — web link addon
- `CrowdBound/` — CrowdBound addon

**Data packs:**
- `BoxedDataPack/` — advancement datapack for Boxed

**Documentation & tools:**
- `docs/` — main documentation site
- `docs-chinese/` — Chinese documentation
- `docs-french/` — French documentation
- `BentoBoxWorld.github.io/` — GitHub Pages site
- `website/` — website
- `translation-tool/` — translation tool

Check these for source before any network fetch.

## Key Dependencies (source locations)

- `world.bentobox:bentobox` → `~/git/bentobox/src/`
