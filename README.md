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

For every major Minecraft version, a `master-{mc_version}` branch exists.

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

Необязательно, на клиенте: **[JEI](https://modrinth.com/mod/jei)** 19.x для Fabric 1.21.1 (с
**[MezzConfig](https://modrinth.com/mod/mezzconfig)**, он нужен JEI на Fabric) и **[Jade](https://modrinth.com/mod/jade)** 15.x.

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
