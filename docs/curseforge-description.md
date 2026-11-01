# EvilCraft (Unofficial Fabric Port)

**Dark magic, blood and vengeful spirits. EvilCraft on Fabric 1.21.1, with all of its content.**

## 📜 Original mod

This is an unofficial port of **[EvilCraft](https://www.curseforge.com/minecraft/mc-mods/evilcraft)** by
**rubensworks** (kroeser) and the CyclopsMC contributors from NeoForge 1.21.1 (version 1.2.99, branch `master-1.21-lts`)
to **Fabric 1.21.1**. The original is open source under **MIT** (its README: code and images under Creative Commons
Attribution 4.0); the JEI and Jade integrations come from its **EvilCraft-Compat** (MIT), which the original bundles into
its jar. Source of this port: [github.com/Vladeezyy/evilcraft-1.21](https://github.com/Vladeezyy/evilcraft-1.21),
LGPL-3.0, with the licenses and credits of the original kept. All the content, art, texts and ideas are theirs; please
report bugs of this port to [this port's issues](https://github.com/Vladeezyy/evilcraft-1.21/issues), not to the
original authors.

### How this port differs from the original

- **Fabric** instead of NeoForge, for Minecraft **1.21.1**. Same content, recipes, loot and numbers.
- **Fluids use Fabric's transfer API:** Dark Tanks, the Blood Infuser and the other blood machines connect to the
  pipes and tanks of other Fabric mods.
- **JEI and Jade** (optional): the recipes of the Blood Infuser, the Environmental Accumulators, the Spirit Reanimator
  and the Spirit Furnace (its mob drops come from the server, which does not need JEI); Jade shows the contents of the
  Box of Eternal Closure and the parts of a broom.
- **Tested:** the 87 GameTests of the original on every build, a client test scene (blocks, fluids, machines, mobs,
  the book, fluid transfer, JEI and Jade), the release jar on a dedicated server and a production client.
- **Performance:** 72 working blocks of the mod and 24 of its mobs add 0.3–1.0 ms to the server tick in the stress
  test (against the same world with vanilla mobs only).

## 🩸 About the mod

EvilCraft brings dark magic to Minecraft: collect blood, harness the power of spirits and the weather, and fight the
creatures of the night.

- **Blood**: a fluid that drops from the mobs you kill, stored in Dark Tanks, Blood Chests and Entangled Chalices
  (linked over any distance), and used by most machines
- **Machines**: the Blood Infuser, the Spirit Furnace, the Spirit Reanimator, the Environmental Accumulator (weather in
  a bottle), the Sanguinary Environmental Accumulator, the Purifier, the Colossal Blood Chest and more
- **Creatures**: Werewolves, Vengeance Spirits (catch them in a Box of Eternal Closure), Poisonous Libelles,
  Netherfish, Controlled Zombies
- **Tools and items**: brooms you build from parts, the Vengeance Ring, the Mace of Distortion, the Necromancer's
  Staff, the Kineticator, Weather Containers, Blood Extractors, Lightning Grenades, ...
- **The world**: Dark Ore, undead trees, blood and poison, the Spirit Portal and the Gemstone of Doom
- **Origins of Darkness**: the in-game book that explains it all

## 📦 Installation

Needed on both the client and the server.

- **Fabric Loader** 0.16.14+ for Minecraft **1.21.1**
- **[Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)** 0.116.17+
- **[Cyclops Core](https://www.curseforge.com/minecraft/mc-mods/cyclops-core)** for **Fabric** 1.30.0+
- **[Forge Config API Port](https://www.curseforge.com/minecraft/mc-mods/forge-config-api-port-fabric)** 21.1.6+
- Optional: **[JEI](https://www.curseforge.com/minecraft/mc-mods/jei)** (with MezzConfig) and
  **[Jade](https://www.curseforge.com/minecraft/mc-mods/jade)**

Settings: `config/evilcraft-common.toml`, `config/evilcraft-client.toml`.

Source, changelog and older files: [github.com/Vladeezyy/evilcraft-1.21](https://github.com/Vladeezyy/evilcraft-1.21).
