AcidIsland
==========
[![Build Status](https://ci.codemc.org/buildStatus/icon?job=BentoBoxWorld/AcidIsland)](https://ci.codemc.org/job/BentoBoxWorld/job/AcidIsland/)
[![Lines Of Code](https://sonarcloud.io/api/project_badges/measure?project=BentoBoxWorld_AcidIsland&metric=ncloc)](https://sonarcloud.io/component_measures?id=BentoBoxWorld_AcidIsland&metric=ncloc)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=BentoBoxWorld_AcidIsland&metric=sqale_rating)](https://sonarcloud.io/component_measures?id=BentoBoxWorld_AcidIslandd&metric=Maintainability)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=BentoBoxWorld_AcidIsland&metric=reliability_rating)](https://sonarcloud.io/component_measures?id=BentoBoxWorld_AcidIsland&metric=Reliability)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=BentoBoxWorld_AcidIsland&metric=security_rating)](https://sonarcloud.io/component_measures?id=BentoBoxWorld_AcidIsland&metric=Security)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=BentoBoxWorld_AcidIsland&metric=bugs)](https://sonarcloud.io/project/issues?id=BentoBoxWorld_AcidIsland&resolved=false&types=BUG)

# Introduction
AcidIsland add-on for BentoBox, so to run an AcidIsland game, you must have BentoBox installed. Docs can be found at [https://docs.bentobox.world](https://docs.bentobox.world).

<img width="512" alt="AcidIsland" src="https://github.com/user-attachments/assets/a4fe5284-e20f-457b-8337-3bfe60ddf21d" />

## The Story
You're on an island, in a sea of acid! If you like Skyblock, try the AcidIsland™ game mode for a new challenge!

Instead of falling you must contend with acid water when expanding your island, and players can boat to each other's islands.

## Features

### The acid sea
* The ocean damages players, mobs and items. Damage amounts, potion effects and the delay before burning are all configurable.
* Acid rain (and optionally acid snow) burns players caught in the open.
* Armour can protect: a helmet against rain, a full set against the acid itself — both off by default.
* Boats are the way between islands, so the sea is a road as well as a hazard.

### Purified water
Water drawn from the acid sea hurts to drink. Clean water has to be made:
* Collect rain in a cauldron fed by a dripstone stalactite.
* Smelt a water bottle — or a water bucket — in a furnace.
* Brew water bottles with coal in a brewing stand.

Drinking purified water heals instead of harming. The whole mechanic, the heal amount, and whether it runs in the Nether and the End can be configured.

### Sulfur seas (Minecraft 26.2 and later)
On servers new enough to have them, AcidIsland uses the game's own toxic water:
* The ocean is sulfur pool water in the `SULFUR_CAVES` biome — acid-green water under green fog.
* Sulfur vents generate below the surface, bubbling and gassing before they erupt as geysers.
* The ocean floor is varied terrain rather than a flat plain, and vanilla structures such as trial chambers generate buried beneath it, so there is a reason to dig down.
* A "Sulfur Spring Refuge" starter island is included alongside the classic ones.

Older servers fall back to normal water and a warm ocean biome — nothing breaks.

### Geyser offerings (Minecraft 26.2 and later)
Throw items into the water around a sulfur vent and it swallows them with a hiss. Items floating nearby drift in on their own, so a throw does not have to be accurate. A few seconds later the vent erupts and pays you back:

* **It transmutes rather than destroys.** A vent works out what your offering was worth and hands back rewards worth about the same, so a diamond comes back in gems and a stack of cobble comes back in cobble-grade tat.
* **What you feed it steers what it gives.** Offerings pull the reward table towards their own channel — gems, nether, mineral, forestry or husbandry — in proportion to the worth that went in.
* **Named transmutations are worth learning.** Magma blocks make obsidian, iron makes gold, and bones with gunpowder make music discs — a way to get records without a mob farm.
* Items the acid destroys inside a vent's pool count as offerings instead of being lost.

Rewards live in `geyser-loot.yml` and item worth in `geyser-values.yml`, both created in the addon's data folder. Worth can defer to the Level addon's block values where the file is silent. Payout size, the exchange rate the vent trades at, the ceiling on any single reward, and whether feeding a vent provokes an early eruption are all configurable. `GeyserSacrificeEvent` and `GeyserTransmuteEvent` let other plugins watch or change what happens.

### The usual BentoBox toolkit
Teams with ranks and permissions, a large set of island protection flags, custom island blueprints, and compatibility with the whole family of BentoBox addons.

## Download

You can download from GitHub, or ready made plugin packs from [https://download.bentobox.world](https://download.bentobox.world).

## Installation

0. Install BentoBox and run it on the server at least once to create its data folders.
1. Place this jar in the addons folder of the BentoBox plugin.
2. Restart the server.
3. The addon will create worlds and a data folder and inside the folder will be a config.yml.
4. Stop the server.
5. Edit the config.yml how you want.
6. Delete any worlds that were created by default if you made changes that would affect them.
7. Restart the server.

### Other Add-ons

AcidIsland™ add-on uses the BentoBox API. Here are some other ones that you may be interested in:

* Level - provides island level calculation and a top ten
* Welcome Warps - provides the warp sign feature
* Challenges - challenges
* Biomes - enables biomes

You can find the projects on GitHub or download load from [https://download.bentobox.world](https://download.bentobox.world).

Bugs and Feature requests
=========================
File bug and feature requests here: [https://github.com/BentoBoxWorld/AcidIsland/issues](https://github.com/BentoBoxWorld/AcidIsland/issues)
