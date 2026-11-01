<div align="center">

<img src="src/main/resources/logo.png" width="160" alt="EvilCraft logo">

# EvilCraft — Fabric 1.21.1

**Dark magic, blood and vengeful spirits.**

[![Minecraft 1.21.1](https://img.shields.io/badge/Minecraft-1.21.1-3c8527)](https://www.minecraft.net)
[![Fabric 0.16](https://img.shields.io/badge/Fabric-0.16.14%2B-dbd0b4)](https://fabricmc.net)
[![License LGPL-3.0](https://img.shields.io/badge/license-LGPL--3.0-blue)](LICENSE)

An unofficial port of **[EvilCraft](https://www.curseforge.com/minecraft/mc-mods/evilcraft)** by **rubensworks**
and the CyclopsMC contributors from NeoForge 1.21.1 to **Fabric 1.21.1**, with all of its content.

</div>

---

## 🩸 About the mod

EvilCraft brings dark magic to Minecraft: collect blood, harness the power of spirits and the weather, and fight the
creatures of the night.

- **Blood**: a fluid that drops from the mobs you kill, stored in Dark Tanks, Blood Chests and Entangled Chalices
  (linked over any distance), and used by most machines
- **Machines**: the Blood Infuser, the Spirit Furnace, the Spirit Reanimator, the Environmental Accumulator (weather in
  a bottle), the Sanguinary Environmental Accumulator, the Purifier, the Colossal Blood Chest and more
- **Creatures**: Werewolves, Vengeance Spirits (catch them in a Box of Eternal Closure), Poisonous Libelles,
  Netherfish, Controlled Zombies
- **Tools and items**: brooms you build from parts, the Vengeance Ring, the Mace of Distortion, the Necromancer's Staff,
  the Kineticator, Weather Containers, Blood Extractors, Lightning Grenades, the Broom of Flight, ...
- **The world**: Dark Ore, undead trees, blood and poison fluids, Gem Stone Torches, the Spirit Portal and the
  Gemstone of Doom
- **Origins of Darkness**: the in-game book that explains it all, with its own rewards
- **Languages**: English and the translations of the original (Russian, Brazilian Portuguese, Spanish, Chinese and
  more, in various stages)

## 📸 Screenshots

<p align="center">
  <img src="docs/images/blocks.jpg" width="49%" alt="Machines, tanks and building blocks">
  <img src="docs/images/mobs.jpg" width="49%" alt="Werewolf, Poisonous Libelle, Vengeance Spirit, Netherfish and Controlled Zombie">
  <img src="docs/images/book.jpg" width="49%" alt="The Origins of Darkness book">
  <img src="docs/images/creative_tab.jpg" width="49%" alt="The creative tab">
  <img src="docs/images/nature.jpg" width="49%" alt="Blood and poison, undead tree, Dark Ore">
  <img src="docs/images/gui_blood_infuser.jpg" width="49%" alt="The Blood Infuser screen">
  <img src="docs/images/jei_blood_infuser.jpg" width="49%" alt="Blood Infuser recipes in JEI">
  <img src="docs/images/jade_box_of_eternal_closure.jpg" width="49%" alt="Jade tooltip of the Box of Eternal Closure">
</p>

## 📦 Requirements

- **Fabric Loader 0.16.14** or newer for Minecraft **1.21.1**.
- **[Fabric API](https://modrinth.com/mod/fabric-api)** 0.116.17 or newer;
- **[Cyclops Core](https://modrinth.com/mod/cyclops-core)** for **Fabric** 1.30.0 or newer;
- **[Forge Config API Port](https://modrinth.com/mod/forge-config-api-port)** 21.1.6 or newer (the settings).

Required on the client and the server. Settings: `config/evilcraft-common.toml`, `config/evilcraft-client.toml`.

## 🔧 Notes on the port

- Content, recipes, loot and numbers are those of the original (NeoForge 1.21.1, branch `master-1.21-lts`).
- Fluids work with Fabric's transfer API: Dark Tanks, the Blood Infuser and the other blood machines connect to the
  pipes and tanks of other Fabric mods.
- **JEI and Jade** (optional, from the original's EvilCraft-Compat): JEI shows the recipes of the Blood Infuser, the
  Environmental Accumulator, the Sanguinary Environmental Accumulator, the Spirit Reanimator and the Spirit Furnace (its
  mob drops come from the server, which does not need JEI), click areas in the machine screens and recipe transfer;
  Jade shows the contents of the Box of Eternal Closure and the parts of a broom. JEI on Fabric also needs
  [MezzConfig](https://modrinth.com/mod/mezzconfig).
- **Fixed in 1.2.99.1**: blood put into tanks and machines by the mod itself (not through pipes) was not saved with the
  chunk; villagers never took the Werewolf profession; a hopper under a halted Spirit Furnace did not restart it.
- Not ported: the integrations of mods that do not exist on Fabric 1.21.1 (Blood Magic, Common Capabilities, ...).

## ✅ Tested

On every push (GitHub Actions): the build, a dev server on a fresh world, **the 87 GameTests of the original** (blood
machines, the Spirit Furnace and Reanimator, the Purifier, eternal water, vengeance spirits, werewolf villagers,
recipes, advancements, ...) on the Fabric GameTest API, and the release jar on a production Fabric server.

- A client test scene (Fabric Loader 0.16.14, Fabric API 0.116.17, 28 checks): world creation, the blocks, fluids,
  machines and mobs of the mod on screen, the creative tab, the Blood Infuser screen, the Origins of Darkness book;
  fluid transfer through Fabric's API (insert, extract, aborted transactions, buckets and tank items), tanks synced to
  the client and saved, Blood Infuser processing, recipes with Fabric's ingredients and load conditions, world
  generation of Dark Ore, the dungeon loot of the mod; with JEI and Jade: the recipes of all five JEI categories (the
  Spirit Furnace ones from the server) and the Jade tooltip.
- The release jar on a production (intermediary-mapped) Fabric server, and the scene on a production client without
  JEI and Jade.
- Performance (`tools/perf.sh`, one world, 600 ticks per window): a vanilla base (40 cows) against the same base plus 72
  working blocks of the mod (16 Blood Infusers fed from Dark Tanks, Blood Chests repairing tools, tanks, accumulators,
  purifiers) and 24 of its mobs: server tick +0.3 to +1.0 ms over two runs (3.81 → 4.10 ms in the uncapped run), FPS
  213 → 195. EvilCraft's own code is 13.5% of the server thread's samples (JFR), the largest part is the flight of the
  Poisonous Libelles.

## ❤️ Credits

- **EvilCraft** — made by **rubensworks** (kroeser) and the
  [CyclopsMC contributors](https://github.com/CyclopsMC/EvilCraft/graphs/contributors) ·
  [CurseForge](https://www.curseforge.com/minecraft/mc-mods/evilcraft) ·
  [source](https://github.com/CyclopsMC/EvilCraft). All the content, art, texts and ideas are theirs; translations of
  the original by its Crowdin translators.
- **Port** to Fabric 1.21.1 — [Vladeezyy](https://github.com/Vladeezyy).
- Bugs of **this port**: [issues](../../issues). Please don't report port bugs to the original authors.

### License

The port is licensed under the **GNU LGPL 3.0 only** ([LICENSE](LICENSE), with the GPL 3.0 it refers to in
[licenses/COPYING](licenses/COPYING)). Copyright (c) 2026 Vladeezyy for the port.
The original EvilCraft declares the **MIT** license ([licenses/LICENSE-MIT](licenses/LICENSE-MIT)); its README states
that its code and images are under **Creative Commons Attribution 4.0**. The textures, models, sounds and texts of the
original stay under CC BY 4.0, credited to their authors ([licenses/LICENSE-ORIGINAL.md](licenses/LICENSE-ORIGINAL.md)).
Please download the mod only from the official pages.

---

<details>
<summary><b>🇷🇺 Описание на русском</b></summary>

<br>

**Тёмная магия, кровь и мстительные духи.**

Неофициальный порт мода **[EvilCraft](https://www.curseforge.com/minecraft/mc-mods/evilcraft)** от **rubensworks**
и участников CyclopsMC с NeoForge 1.21.1 на **Fabric 1.21.1** — со всем содержимым.

<details>
<summary><b>🩸 О моде</b></summary>

EvilCraft приносит в Minecraft тёмную магию: собирайте кровь, подчиняйте силу духов и погоды и сражайтесь с
созданиями ночи.

- **Кровь** — жидкость, которая выпадает из убитых мобов; хранится в Тёмных резервуарах, Кровавых сундуках и
  Запутанных чашах (связанных на любом расстоянии), нужна почти всем машинам
- **Машины**: Кровавый инфузор, Духовная печь, Реаниматор духов, Накопитель окружения (погода в бутылке),
  Кровавый накопитель окружения, Очиститель, Колоссальный кровавый сундук и другие
- **Существа**: оборотни, Духи мщения (ловятся Ящиком вечного заточения), Ядовитые стрекозы, Незерская чешуйница,
  Подчинённые зомби
- **Инструменты и предметы**: мётлы из деталей, Кольцо мщения, Булава искажения, Посох некроманта, Кинетикатор,
  Контейнеры погоды, Экстракторы крови, Молниевые гранаты, ...
- **Мир**: тёмная руда, мёртвые деревья, жидкости крови и яда, Факелы из самоцветов, Портал духов и Самоцвет рока
- **Origins of Darkness** — книга в игре, которая всё объясняет и даёт награды
- **Языки**: английский и переводы оригинала (русский, бразильский португальский, испанский, китайский и другие, в
  разной степени готовности)

</details>

<details>
<summary><b>📦 Требования</b></summary>

- **Fabric Loader 0.16.14** или новее для Minecraft **1.21.1**.
- **[Fabric API](https://modrinth.com/mod/fabric-api)** 0.116.17 или новее;
- **[Cyclops Core](https://modrinth.com/mod/cyclops-core)** для **Fabric** 1.30.0 или новее;
- **[Forge Config API Port](https://modrinth.com/mod/forge-config-api-port)** 21.1.6 или новее (настройки).

Нужен и на клиенте, и на сервере. Настройки: `config/evilcraft-common.toml`, `config/evilcraft-client.toml`.

</details>

<details>
<summary><b>🔧 О порте</b></summary>

- Содержимое, рецепты, добыча и числа — как в оригинале (NeoForge 1.21.1, ветка `master-1.21-lts`).
- Жидкости работают через transfer API Fabric: Тёмные резервуары, Кровавый инфузор и другие машины соединяются с
  трубами и баками других Fabric-модов.
- **JEI и Jade** (необязательно, из EvilCraft-Compat оригинала): JEI показывает рецепты Кровавого инфузора,
  Накопителя окружения, Кровавого накопителя окружения, Реаниматора духов и Духовной печи (добычу с мобов присылает
  сервер, JEI на сервере не нужен), области клика в окнах машин и перенос рецептов; Jade показывает содержимое Ящика
  вечного заточения и детали метлы. JEI на Fabric нужен ещё [MezzConfig](https://modrinth.com/mod/mezzconfig).
- **Исправлено в 1.2.99.1**: кровь, которую мод сам заливал в баки и машины (не через трубы), не сохранялась с
  чанком; жители никогда не становились оборотнями; воронка под остановленной Духовной печью не запускала её снова.
- Проверено: 87 GameTest'ов оригинала, тестовая сцена (28 проверок), production-сервер и клиент; нагрузка: 72 работающих блока мода и 24 его моба добавляют к тику
  сервера 0,3–1,0 мс (3,81 → 4,10 мс), FPS 213 → 195.
- Не перенесено: интеграции модов, которых нет на Fabric 1.21.1 (Blood Magic, Common Capabilities, ...).

</details>

<details>
<summary><b>❤️ Авторы и лицензия</b></summary>

- **EvilCraft** — автор **rubensworks** (kroeser) и
  [участники CyclopsMC](https://github.com/CyclopsMC/EvilCraft/graphs/contributors) ·
  [CurseForge](https://www.curseforge.com/minecraft/mc-mods/evilcraft) ·
  [исходники](https://github.com/CyclopsMC/EvilCraft). Всё содержимое, графика, тексты и идеи — их; переводы
  оригинала — его переводчиков на Crowdin.
- **Порт** на Fabric 1.21.1 — [Vladeezyy](https://github.com/Vladeezyy).
- Ошибки **порта**: [issues](../../issues). Пожалуйста, не сообщайте об ошибках порта авторам оригинала.

Порт распространяется под **GNU LGPL 3.0 only** ([LICENSE](LICENSE), GPL 3.0 — в [licenses/COPYING](licenses/COPYING)).
Оригинал объявляет лицензию **MIT** ([licenses/LICENSE-MIT](licenses/LICENSE-MIT)), а в его README сказано, что код и
изображения — под **Creative Commons Attribution 4.0**. Текстуры, модели, звуки и тексты оригинала остаются под CC BY
4.0 с указанием авторов ([licenses/LICENSE-ORIGINAL.md](licenses/LICENSE-ORIGINAL.md)).

</details>

</details>
