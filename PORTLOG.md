# Port log: EvilCraft Fabric 26.1.2 -> 1.21.11 (backport, WIP)

Status (2026-11-05): **1.21.11 works on server and client in automatic tests; no human has played it, no release yet.**
- `compileJava`, `compileSceneJava` after `clean`: OK. GameTests of the original: 104/104 (`tools/gametest.sh`; the same 102 `@GameTest` of the mod as 26.1.2).
- Client scene (`tools/scenetest.sh`, with JEI and Jade): 30 checks, 0 failed; screenshots looked at, one compared with 26.1.2.
- Production server and client (`tools/prodtest.sh`): OK, 24 checks, no JEI there.
- Stress (`tools/perf.sh`, Mac M-series, one world, 600 ticks per window, base + 72 working blocks and 24 mobs), two runs:
  FPS 141 -> 109 and 116 -> 107 (-23%, -7%); MSPT 5.42 -> 5.48 and 9.00 -> 7.25. The base alone moves more than the difference: MSPT is noise,
  FPS has no stable number. Base entities differ per run (256, 163). No 60-70 FPS cap here, unlike the 26.2 and 26.3 runs. A JFR share of the server thread is still to do.

## How 1.21.11 was made (from mc/26.1.2 @4cd1bce)
- Reverse three-way merge with the port passes on both sides (`tools/port/merge_upstream.py`, `merge_cc.py`): upstream "Update to MC 26.1.1" 293bcb9edf
  and 80648d12b3, Cyclops Core 8fe42774a9, 2904b4a04e, 3daecc6cae. 199 + 26 files merged, 33 + 14 conflicts resolved by rule, then by hand.
- Build: Loom 1.17.21 with Mojang names and `remapJar`, Java 21, Cyclops Core 1.25.5 from Modrinth, JEI 27.54, Jade 21.1.6.
- Checked after the merge: methods that exist on 26.1.2 and not here. One real loss found by a GameTest: `getResource` of the creative blood drop.
- Open here: `SlotSingleIngredient` is a copy from Cyclops Core 26.x; `setVengeanceSwarmContent` in the box config comes from upstream's 1.21.11 state.

The rest of this file is the log of the 26.1.2 port, which this branch is based on.

## Port log: EvilCraft Fabric 1.21.1 -> 26.1.2

Status of 26.1.2 (2026-11-05): **26.1.2 works on server and client in automatic tests; no human has played it, no release yet.**
- `compileJava`, `jar`: OK. GameTests of the original: 106/106 (`tools/gametest.sh`).
- Client scene (`tools/scenetest.sh`, with JEI and Jade): all checks pass; screenshots looked at and compared with `docs/images`.
- Production server and production client (`tools/prodtest.sh`): OK, 24 checks, no JEI there.
- Stress (`tools/perf.sh`, Mac M-series, one world, 600 ticks per window, base 158-261 entities; base + 72 working blocks of the mod
  and 24 mobs), two runs: FPS 108 -> 91 and 108 -> 96 (-16%, -11%); MSPT 9.87 -> 11.55 and 14.65 -> 11.16, so the base alone moves by
  more than the difference: noise. 1.21.1 was -9% FPS, +0.29 MSPT on a quieter machine. A JFR share of the server thread is still to do.

## How this branch was made
- `vendor/26.1.2` (local branch): upstream CyclopsMC/EvilCraft `master-26-lts` @ 94d5b596 (1.2.109) on top of the
  1.21.1 import da1ca17, same file set. Upstream's `.gitignore` has a `config` rule, so the import lost
  `core/config/*` (5 sources); restored from upstream in step 2. Importing 26.2 and 26.3: compare with
  `git diff --name-status vendor/<version> upstream/<branch> -- src` (texture lines there are only file mode changes).
- `gen/121`, `gen/2612` (local): both upstream trees after the mechanical port scripts
  (`tools/port/ccshim.py` with CyclopsCore 5eb184ec12^ / master-26-lts, `normalize.py`, `data_fabric.py`).
  `ccshim.py` was fixed for Windows paths (os.sep).
- The diff gen/121 -> main (our manual Fabric edits) merged onto gen/2612. Delete conflicts: our deletions kept,
  upstream deletions taken. Content conflicts (145 files): **upstream hunks taken** (`git merge-file --theirs`);
  the files with conflict markers (our side) are saved in `D:/mods/_ref/ec-notes/conflicts/`, our edits on files that
  upstream deleted in `D:/mods/_ref/ec-notes/ours-edits-on-theirs-del.diff`.
- `build.gradle`, `gradle.properties`, the wrapper (Gradle 9.8.1) and CI (Java 25) are for 26.1.2.

## What changed upstream (26.x)
- Cyclops Core 26 moved almost everything to loader-common (`*ConfigCommon`, `IModHelpers.get().get*Helpers()`,
  ConfigHandlerCommon, ModBaseFabric with packets, commands and GameTests): most of `fabric/cyclopscore/*` is no
  longer needed. Upstream EvilCraft only uses ModBaseNeoForge, IModHelpersNeoForge, FluidConfigNeoForge,
  LootModifierConfigNeoForge, IFluidHelpersNeoForge, IStructureHelpersNeoForge, CyclopsCoreNeoForge.
- Fluids and capabilities use the **NeoForge transfer API** (`ResourceHandler`, `FluidResource`, `Transaction`,
  `ItemAccess`, `Capabilities.Fluid.*`) instead of IFluidHandler: our `fabric/fluids` layer needs a shim of these
  names over Fabric's Transfer API (Storage<FluidVariant>, Transaction, ContainerItemContext; mB <-> droplets).
- Models rewritten for 26.1 (ModelDarkTank, BroomModel, ... deleted upstream): our Fabric model code must be redone.
- GameTests are in `src/main/java/.../gametest` with Cyclops Core's `@GameTest` (registered by ModBaseFabric):
  plan is to drop our `src/gametest` source set and use upstream's.

## Next steps
1. Done: build.gradle for 26.x (`net.fabricmc.fabric-loom` 1.18-SNAPSHOT, no mappings/parchment, plain configurations,
   jar task instead of remapJar) and the access widener header. Local runs need JAVA_HOME = JDK 25
   (`~/.gradle/jdks/eclipse_adoptium-25-amd64-windows.2`). ASM in `buildSrc` is 9.10.1: with 9.7.1 Loom silently merged
   a Minecraft jar without classes. Syntax errors are gone; `compileJava` reports **2,319 errors in 338 files**
   (2026-11-04), about 30% of them in `fabric/cyclopscore` (685), which step 2 mostly removes. Count them again after
   each step, the number can grow as classes start to resolve.
2. Done (compiles at this level, **nothing checked at runtime**): `ModBaseNeoForge` keeps upstream's name (45 files use
   it, smaller merges of 26.2 and 26.3) and extends ModBaseFabric. `compileJava` reports **1,853 errors in 274 files**
   (2026-11-04), 365 of them in `fabric/cyclopscore`. The base class, proxies, config and action classes have none left.
   The 1.21.1 fixes against CC 26 Fabric (from its source):
   - Setup after registries: still needed, `onInitialize` calls `loadSetup` first. Now in `config/ConfigHandler`.
   - Registration order: still needed, configs register in add order. Now in `config/ConfigHandler`, with block items
     queued until the item rank instead of the old `BlockActionFabric`. `ARMOR_MATERIAL` is no longer a registry.
   - Cyclops Core entrypoint order: still needed. `constructCyclopsCore`, the `onInitialize` guard and
     `CyclopsCoreFabricMixin` stay.
   - NBT types: still needed, `NBTClassTypesFabric` now uses CC's common `NBTClassType`.
   - `keyShift` on `CLIENT_STARTED`: still needed.
   - Data components `fluid_content` and `inventory` stay `evilcraft:*`, the same ids as the 1.21.1 port. CC Fabric only
     registers capacity and energy storage.
   Moved to step 3:
   - 23 `getModEventBus()` calls (`*Config`, `*ConfigClient`, `BroomPartRegistry`, `DamageIndicatedItemFluidContainer`):
     the method is gone, each needs a Fabric hook. `ClientProxy` already dropped `onModelBakingCompleted`, its field was
     unused.
   - `SavedDataType` in `GlobalCounters` and `WorldStorageSharedTank`: upstream's 3-argument constructor is NeoForge's,
     vanilla needs a `DataFixTypes` too and passes it to `SavedDataStorage.readTagFromDisk`. Check that path before
     choosing a value, data must survive a reload.
   - `getGameTestClasses` in `EvilCraft` points to classes in `src/gametest`, which `main` cannot see: do the GameTest
     move described above.
   - Model loaders and the dark tank render layer (removed from `ClientProxy`) go with the model rewrite.
   - IMC is gone: other mods call `EntityVengeanceSpirit.addToBlacklistIMC` directly.
   Not checked: key category registration (`getMainKeyCategory`), block items order, setup order, packets in game.
3. In progress (2026-11-05, on the Mac: `JAVA_HOME=~/.gradle/jdks/eclipse_adoptium-25-aarch64-os_x.2/jdk-25.0.4.1+1/Contents/Home`).
   **1,853 -> 349 errors.** Everything below compiles only; **none of it has run yet**.
   - Done: `fabric/transfer`, the names of NeoForge's transfer API written anew over Fabric's Transfer API (not copied:
     NeoForge is LGPL-2.1-only). Transactions wrap Fabric's, so they nest with other mods' transactions; `SnapshotJournal`
     is its own (Fabric's SnapshotParticipant does not pass the original state to the final commit, `onRootCommit` needs
     it); `ResourceHandlerStorage` and `StorageResourceHandler` adapt both ways, mB <-> droplets, only whole mB move.
     Replaces `fabric/fluids/capability`, `fabric/fluids/transfer` and the old `FluidUtil`.
   - Done: `fabric/capabilities` with the names `Capabilities.Fluid/Item.BLOCK/ITEM` (fallbacks to `FluidStorage`/`ItemStorage`;
     the mod's handlers are exposed to them), `IModHelpersNeoForge` (fluid helpers in mB, capability helpers),
     `FluidStackTemplate`, bus registrations -> static `addListener` of `fabric/event`.
   - Done: models and renderers, no errors left in `client/render`. `DynamicBaseModel` is `emitQuads` (each quad once; upstream
     repeats all quads for each side and layer). `fabric/client/model/ColoredQuads`: vanilla's BakedQuad has no color, so an opaque
     ARGB color goes in `MaterialInfo.tintIndex` (values below -1); vertex colors for blocks, `Constant` tints for items. Upstream's
     colors are ABGR, `withColor` converts. The `Register*` model events fill vanilla's id mappers (AW). Blockstates use
     `fabric:type`. Dark tank: Cyclops Core only calls `getDynamicModelElement` on NeoForge, so `BlockDarkTankConfigClient`
     replaces the item model with a plugin and leaves the block's baked model alone. Deleted, unused since 26.1: `IGeometry*`,
     `ModelLoaders`, `UnbakedGeometryHelper`, `ModelRenderTypes`, `IClientFluidTypeExtensions`.
   - Scripts for the 26.2 and 26.3 merges, run in this order: `tools/port/rewrite_transfer.py`, `rewrite_cc_shim.py`,
     `rewrite_events.py`, `autoimport.py`, `rewrite_models.py`, `rewrite_misc.py`, then `data_fabric.py` on the resources; `errsummary.sh` compiles and summarizes (edit its paths for another machine).
   - The saved conflict notes are on the Windows machine (`D:/mods/_ref/ec-notes`), and so are `vendor/*`, `gen/*`. Not needed
     to continue: our side of any conflict is `git show main:<path>`.
   - Done at the end of step 3: compile errors 349 -> 0. JEI is upstream's 26.x (`EvilCraft-Compat@master-26-lts`) with the Fabric
     parts of 1.21.1 back (`JeiFluids`, `JeiCompatLoader*`); upstream's GameTests in `src/main` replace `src/gametest`.
   - To check in game first, models: display stand, entangled chalice and box of eternal closure as blocks and items (a model
     that fails to load shows nothing in the compiler); colors of fluids and gems (ABGR conversion); the dark tank item; cutout
     and translucent blocks (17 model files have NeoForge's `render_type`, which vanilla ignores: the layer now comes from the
     sprite). Quads that vanilla keeps per cull face: upstream's `getQuads(null)` skips them, a JSON face with `cullface` would be
     missing. Performance: item models rebuild their quads each frame as upstream does; measure before caching.
   - To check in game first, transfer: `ItemAccess.forStack` (mutates the stack directly and restores it on abort, also components
     through `PatchedDataComponentMap.restorePatch`), hoppers on `ResourceHandlerStorage`, buckets through the
     `FluidStorage.ITEM` fallback, `FluidStacksResourceHandler` saving (`stacks` key; 1.21.1 port worlds wrote `Fluid`).
4. Done: fabric.mod.json and mixins on Java 25, access widener entries checked against the jar, mixins fixed by running.
5. Tests. Done: GameTests, scene, production server and client, stress.
   - Still by hand or to add to the scene: broom flight and overlay, entangled chalice links between players, display stand with a type,
     a world from the 1.21.1 port (tank contents: new key `stacks`, old `Fluid`).
   - JEI's item list covers the info book: the book is an AbstractContainerScreen, upstream too; hide it with a gui handler if it bothers.
   - `130 duplicate items were found in 'EvilCraft' creative tab`: logged by JEI's list factory, the tab itself shows each item once.
   - Warnings to look at: `The broom modifier evilcraft:witherer (hungerer, kamikaze, withershield) has no valid items` in the
     info book; `duplicate API provider` for the chalice and creative blood drop (`DamageIndicatedItemFluidContainer` and their
     configs both register; Fabric keeps the first, the config's, which is the right one).
   - Dropped from NeoForge, to check: `setNoCombineRepair` of the burning gem stone (anvil), `shouldDisplayFluidOverlay` (water next
     to chest walls), teleport event of the blood pearl.
   - Then 26.2 (`master-26.2`) and 26.3 (`master-26`).
6. 1.21.11: upstream has no such branch. The closest is `master-1.21.10` (1.2.62), older than our 1.2.99 and 1.2.109. Plan:
   backport from the finished 26.1.2 (the API is close; `fabric/transfer` carries over) with obfuscated Loom + Parchment,
   Java 21, against Cyclops Core `1.21.11-1.25.5-964` (Modrinth `g86bbBfU`, Fabric, 2026-03-28), which is older than
   CC 26: check what it lacks before starting. Dependencies on Modrinth (Fabric): Forge Config API Port 21.11.1, JEI
   27.54.0.116, MezzConfig 0.6.10, Jade 21.1.6.
