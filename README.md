<div align="center">

<img src="src/main/resources/logo.png" width="160" alt="EvilCraft logo">

# EvilCraft — Fabric 26.2

**Dark magic, blood and vengeful spirits.**

[![Minecraft 26.2](https://img.shields.io/badge/Minecraft-26.2-3c8527)](https://www.minecraft.net)
[![Fabric Loader 0.19.5](https://img.shields.io/badge/Fabric%20Loader-0.19.5%2B-dbd0b4)](https://fabricmc.net)
[![Java 25](https://img.shields.io/badge/Java-25-b07219)](https://adoptium.net)
[![License LGPL-3.0](https://img.shields.io/badge/license-LGPL--3.0-blue)](LICENSE)

An unofficial port of **[EvilCraft](https://www.curseforge.com/minecraft/mc-mods/evilcraft)** by **rubensworks**
and the CyclopsMC contributors from NeoForge to **Fabric 26.2**, with all of its content.

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
- **Languages**: English, Russian, Ukrainian, German, Spanish and Brazilian Portuguese are complete;
  the other translations of the original are in various stages

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

The base is upstream EvilCraft **1.2.109**. Upstream's `master-26.2` is older than its LTS branch, so only its 26.2 migration is merged on top of 1.2.109 (`master-26-lts`); the newer features stay. The mod version is 1.2.109.


## 📦 Requirements

- Fabric Loader 0.19.5 or newer, Java 25.
- [Fabric API](https://modrinth.com/mod/fabric-api) 0.161.0+26.2.
- [Cyclops Core](https://modrinth.com/mod/cyclops-core) for Fabric 1.30.0-1143.
- [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port) 26.2.1.
- Optional, on the client: [JEI](https://modrinth.com/mod/jei) 30.48.0.244 (with [MezzConfig](https://modrinth.com/mod/mezzconfig) 0.6.10, which JEI needs on Fabric) and [Jade](https://modrinth.com/mod/jade) 26.2.11.

Releases are separate for each Minecraft version (26.1.2, 26.2, 26.3): a file for one version does not load on another.

Needed on both the client and the server.

## 🔧 Notes on the port

- Content, recipes, loot and numbers are as in the original.
- The fixes of 1.2.99.1 are included.
- Fluids work through the Fabric transfer API, so the machines connect to pipes and tanks of other Fabric mods.
- Languages: English, Russian, Ukrainian, German, Spanish and Brazilian Portuguese are complete. The other translations of the original are in various stages.
- Not ported: integrations with mods that do not exist on Fabric 26.2.

## ✅ Tested

- GameTests of the original: 104/104 (Cyclops Core for 26.2 lacks 2 tests of its own that the 26.1.2 build has).
- Client test scene (with JEI and Jade): 30 checks.
- Production server and production client: OK.
- Load test: 72 working blocks of the mod and 24 mobs ran without errors.

## ⚠️ Known issues

- Nobody has played this version yet. It was checked only by automatic tests.
- FPS under load on 26.2 was not measured reliably (the numbers were near the refresh rate and the machine was busy).

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
и участников CyclopsMC с NeoForge на **Fabric 26.2** — со всем содержимым.

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
- **Языки**: английский, русский, украинский, немецкий, испанский и бразильский португальский переведены полностью;
  остальные переводы оригинала — в разной степени готовности

</details>

<details>
<summary><b>📦 Требования</b></summary>

- **Fabric Loader 0.19.5** или новее для Minecraft **26.2**, Java **25**.
- **[Fabric API](https://modrinth.com/mod/fabric-api)** 0.161.0+26.2;
- **[Cyclops Core](https://modrinth.com/mod/cyclops-core)** для **Fabric** 1.30.0-1143;
- **[Forge Config API Port](https://modrinth.com/mod/forge-config-api-port)** 26.2.1 (настройки).

Релизы выпускаются отдельно для каждой версии Minecraft (26.1.2, 26.2, 26.3): файл для одной версии не запустится на другой.

Нужен и на клиенте, и на сервере. Настройки: `config/evilcraft-common.toml`, `config/evilcraft-client.toml`.

Необязательно, на клиенте: **[JEI](https://modrinth.com/mod/jei)** 30.48.0.244 (с
**[MezzConfig](https://modrinth.com/mod/mezzconfig)** 0.6.10, он нужен JEI на Fabric) и **[Jade](https://modrinth.com/mod/jade)** 26.2.11.

</details>

<details>
<summary><b>🔧 О порте</b></summary>

- За основу взят EvilCraft **1.2.109** оригинала. Ветка `master-26.2` оригинала старше LTS-ветки, поэтому поверх 1.2.109 (`master-26-lts`) влита только её миграция на 26.2; новые возможности остаются. Версия мода 1.2.109.
- Содержимое, рецепты, добыча и числа — как в оригинале.
- Жидкости работают через transfer API Fabric: Тёмные резервуары, Кровавый инфузор и другие машины соединяются с
  трубами и баками других Fabric-модов.
- **JEI и Jade** (необязательно, из EvilCraft-Compat оригинала): JEI показывает рецепты Кровавого инфузора,
  Накопителя окружения, Кровавого накопителя окружения, Реаниматора духов и Духовной печи (добычу с мобов присылает
  сервер, JEI на сервере не нужен), области клика в окнах машин и перенос рецептов; Jade показывает содержимое Ящика
  вечного заточения и детали метлы. JEI на Fabric нужен ещё [MezzConfig](https://modrinth.com/mod/mezzconfig).
- Исправления версии 1.2.99.1 включены.
- Проверено: GameTest'ы оригинала — 104 из 104 (в сборке Cyclops Core для 26.2 нет 2 собственных тестов, которые есть в сборке для 26.1.2); тестовая сцена на клиенте (с JEI и Jade) — 30 проверок; production-сервер и
  клиент — без ошибок; нагрузочный тест: 72 работающих блока мода и 24 его моба работали без ошибок.
- Не перенесено: интеграции модов, которых нет на Fabric 26.2.

**Известные проблемы**

- В эту версию ещё никто не играл: её проверяли только автоматические тесты.
- FPS под нагрузкой на 26.2 не удалось измерить надёжно (значения были у частоты обновления, машина была занята).

</details>

<details>
<summary><b>❤️ Авторы и лицензия</b></summary>

- **EvilCraft** — автор **rubensworks** (kroeser) и
  [участники CyclopsMC](https://github.com/CyclopsMC/EvilCraft/graphs/contributors) ·
  [CurseForge](https://www.curseforge.com/minecraft/mc-mods/evilcraft) ·
  [исходники](https://github.com/CyclopsMC/EvilCraft). Всё содержимое, графика, тексты и идеи — их; переводы
  оригинала — его переводчиков на Crowdin.
- **Порт** на Fabric 26.2 — [Vladeezyy](https://github.com/Vladeezyy).
- Ошибки **порта**: [issues](../../issues). Пожалуйста, не сообщайте об ошибках порта авторам оригинала.

Порт распространяется под **GNU LGPL 3.0 only** ([LICENSE](LICENSE), GPL 3.0 — в [licenses/COPYING](licenses/COPYING)).
Оригинал объявляет лицензию **MIT** ([licenses/LICENSE-MIT](licenses/LICENSE-MIT)), а в его README сказано, что код и
изображения — под **Creative Commons Attribution 4.0**. Текстуры, модели, звуки и тексты оригинала остаются под CC BY
4.0 с указанием авторов ([licenses/LICENSE-ORIGINAL.md](licenses/LICENSE-ORIGINAL.md)).

</details>

</details>
