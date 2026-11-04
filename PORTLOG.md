# Port log: EvilCraft Fabric 1.21.1 -> 26.1.2 (WIP)

Status (2026-11-04): **does not build yet.** Upstream 26.1.2 is merged into the Fabric port, the Gradle build is set
up for 26.x and stops at `compileJava`.

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
3. Transfer API shim, then compile and fix errors (`tools/port/errs.sh`), with the saved conflict notes.
4. fabric.mod.json, the access widener entries (the header is done; entries are still from 1.21.1), mixins checked with
   javap.
5. Tests: GameTests, scene, production server, stress; then 26.2 (`master-26.2`) and 26.3 (`master-26`).
