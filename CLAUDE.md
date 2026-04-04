# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

AcidIsland is a BentoBox GameModeAddon for Minecraft (Spigot/Paper). It implements a Skyblock-like game mode where the ocean is acid that damages players, mobs, and items. Requires BentoBox as the parent plugin framework.

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
- **`world/`** — `ChunkGeneratorWorld` generates ocean floor terrain using Perlin noise. `AcidTask` is a repeating BukkitRunnable applying acid damage to entities in water. `AcidBiomeProvider` provides biomes per environment type.
- **`events/`** — Custom Bukkit events (`AcidEvent`, `AcidRainEvent`, `EntityDamageByAcidEvent`, etc.) that are cancellable, allowing other plugins to modify acid behavior.

### Testing

Tests use **JUnit 4 + PowerMock + Mockito**. PowerMock is needed to mock static Bukkit methods. The Surefire plugin is configured with extensive `--add-opens` JVM args for Java 21 compatibility. `ServerMocks` utility class provides reusable mock server setup.

### Configuration

- `src/main/resources/config.yml` — Default config with acid damage values, rain settings, potion effects, world generation params
- `src/main/resources/addon.yml` — BentoBox addon metadata and permissions
- `src/main/resources/locales/` — 24 language translation files
- `src/main/resources/blueprints/` — Island templates (overworld, nether, end)

## CI

GitHub Actions workflow on `develop` branch and PRs: builds with Java 21, runs JaCoCo coverage, reports to SonarCloud.
