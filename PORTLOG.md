# Port log: EvilCraft Fabric 1.21.1 -> 26.1.2 (WIP)

Status (2026-11-03): **does not build yet.** Upstream 26.1.2 is merged into the Fabric port, the build is not set up.

## How this branch was made
- `vendor/26.1.2` (local branch): upstream CyclopsMC/EvilCraft `master-26-lts` @ 94d5b596 (1.2.109) on top of the
  1.21.1 import da1ca17, same file set.
- `gen/121`, `gen/2612` (local): both upstream trees after the mechanical port scripts
  (`tools/port/ccshim.py` with CyclopsCore 5eb184ec12^ / master-26-lts, `normalize.py`, `data_fabric.py`).
  `ccshim.py` was fixed for Windows paths (os.sep).
- The diff gen/121 -> main (our manual Fabric edits) merged onto gen/2612. Delete conflicts: our deletions kept,
  upstream deletions taken. Content conflicts (145 files): **upstream hunks taken** (`git merge-file --theirs`);
  the files with conflict markers (our side) are saved in `D:/mods/_ref/ec-notes/conflicts/`, our edits on files that
  upstream deleted in `D:/mods/_ref/ec-notes/ours-edits-on-theirs-del.diff`.
- `build.gradle` is still the 1.21.1 one; `gradle.properties` and the wrapper (Gradle 9.8.1) are for 26.1.2.

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
1. build.gradle for 26.x: `net.fabricmc.fabric-loom` 1.18-SNAPSHOT, no mappings/parchment, `implementation`
   instead of `mod*`, no remapJar/remapSceneJar (jar task), JAVA_HOME = JDK 25
   (`~/.gradle/jdks/eclipse_adoptium-25-amd64-windows.2`). See iron-backpacks-26.1.2/fabric/build.gradle.
2. EvilCraft extends ModBaseFabric (instead of the ModBaseVersionable shim); re-check the 1.21.1 fixes
   (registration order, setup after registries, Cyclops Core entrypoint order, NBT types) against CC 26 Fabric.
3. Transfer API shim, then compile and fix errors (`tools/port/errs.sh`), with the saved conflict notes.
4. fabric.mod.json, access widener (`accessWidener v2 official`), mixins checked with javap.
5. Tests: GameTests, scene, production server, stress; then 26.2 (`master-26.2`) and 26.3 (`master-26`).
