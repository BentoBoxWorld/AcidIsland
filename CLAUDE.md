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
    public void setUp() {
        server = MockBukkit.mock();  // always first
        mockedBukkit = Mockito.mockStatic(Bukkit.class, Mockito.RETURNS_DEEP_STUBS);
        mockedBukkit.when(Bukkit::getMinecraftVersion).thenReturn("1.21.11");
        mockedBukkit.when(Bukkit::getServer).thenReturn(server);
        // ...
    }

    @AfterEach
    public void tearDown() {
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

### Locales

All 24 locale files use **MiniMessage format** (e.g., `<red>`, `<dark_blue>`). Legacy `&` color codes must not be used. BentoBox 3.12.0+ is required for MiniMessage support.

### Configuration

- `src/main/resources/config.yml` — Default config with acid damage values, rain settings, potion effects, world generation params
- `src/main/resources/addon.yml` — BentoBox addon metadata and permissions (requires `api-version: 3.12.0`)
- `src/main/resources/locales/` — 24 language translation files (MiniMessage format)
- `src/main/resources/blueprints/` — Island templates (overworld, nether, end)

## CI

GitHub Actions workflow on `develop` branch and PRs: builds with Java 21, runs JaCoCo coverage, reports to SonarCloud.
