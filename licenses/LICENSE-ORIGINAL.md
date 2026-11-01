# License of the original EvilCraft

EvilCraft is made by **rubensworks** (kroeser) and the CyclopsMC contributors:
https://github.com/CyclopsMC/EvilCraft (ported from branch `master-1.21-lts`, commit `2b67228`).

- The mod metadata of the original declares the **MIT** license: [LICENSE-MIT](LICENSE-MIT).
- The README of the original states: "All code and images are licenced under Creative Commons 4"
  — [Creative Commons Attribution 4.0](https://creativecommons.org/licenses/by/4.0/).

The textures, models, sounds and texts of the original (`src/main/resources/assets`) stay under CC BY 4.0, credited to
their authors above. Changes made in this port: the mod was ported from NeoForge to Fabric (platform layer, fluid and
capability bridges, event hooks, data format conversions); the assets are unchanged.

## EvilCraft-Compat

The JEI and Jade integrations come from **EvilCraft-Compat** by the same authors,
https://github.com/CyclopsMC/EvilCraft-Compat (branch `master-1.21-lts`, commit `8193ed2`), which the original bundles into
the EvilCraft jar. License: MIT, [LICENSE-MIT-EvilCraft-Compat](LICENSE-MIT-EvilCraft-Compat) (Copyright (c) 2015
Cyclops). Its textures and translations (`assets/evilcraftcompat`) are unchanged. Changes made in this port: the JEI
plugin on JEI's Fabric API (fluid amounts in Fabric units, the Spirit Furnace recipes sent on a channel of their own),
the Jade plugin as a Fabric entrypoint; the integrations of mods that do not exist on Fabric 1.21.1 are left out.
