package org.cyclops.evilcraft.scene;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.client.creativetab.v1.FabricCreativeModeInventoryScreen;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.RegistryEntries;
import org.cyclops.evilcraft.blockentity.BlockEntityBloodInfuser;
import org.cyclops.evilcraft.blockentity.BlockEntityDarkTank;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Development scene of the Fabric port, inert unless the evilcraft.scene system property is set
 * ({@code ./gradlew runScene}): creates the world ec_scene, shows the blocks, machines, fluids and mobs of the mod,
 * the creative tab, a machine screen and the info book, runs functional checks (fluid transfer, the blood infuser,
 * data of the port) and logs them as {@code [scene] CHECK}, saves run/screenshots/scene_*.png, then quits.
 */
public final class DevScene {

    private static void hideGui(Minecraft mc, boolean hidden) {
        if (mc.gui.hud.isHidden() != hidden) {
            mc.gui.hud.toggle();
        }
    }

    private static final Logger LOGGER = LogManager.getLogger("evilcraft-scene");
    private static final String LANG = System.getProperty("evilcraft.scene.lang", "en_us");
    private static final String WORLD = "ec_scene";
    private static final List<Pair<Integer, Consumer<Minecraft>>> STEPS = new ArrayList<>();
    private static boolean opened;
    private static int tick;
    private static int failures;
    private static BlockPos stage;

    static {
        at(40, mc -> server(mc, DevScene::setup));
        at(45, mc -> hideGui(mc, true));
        at(60, mc -> server(mc, DevScene::buildStage));
        at(70, mc -> server(mc, DevScene::fillTanks));
        at(160, mc -> server(mc, p -> look(p, stage.offset(0, 1, 0), 15)));
        at(225, mc -> {
            BlockEntity tank = mc.level.getBlockEntity(stage.offset(-5, 1, 0));
            check("the client knows the fluid of the dark tank", tank instanceof BlockEntityDarkTank t && t.getTank().getFluidAmount() == 8000,
                    tank instanceof BlockEntityDarkTank t ? t.getTank().getFluid() : tank);
        });
        at(230, mc -> shot(mc, "blocks"));
        at(235, mc -> server(mc, p -> look(p, stage.offset(0, 1, 6), 15)));
        at(290, mc -> shot(mc, "nature"));
        // Vengeance spirits are only visible with spectral glasses
        at(295, mc -> server(mc, p -> {
            p.getInventory().setItem(8, new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("evilcraft", "spectral_glasses"))));
            mobs(p);
        }));
        at(300, mc -> server(mc, p -> look(p, stage.offset(0, 1, -6), 15, 4.5)));
        at(355, mc -> mc.level.entitiesForRendering().forEach(e -> {
            if (e.getType().getDescriptionId().startsWith("entity.evilcraft")) {
                LOGGER.info("[scene] mob on the client: " + e.getType().getDescriptionId() + " at " + e.position() + (e.isInvisible() ? " invisible" : ""));
            }
        }));
        at(360, mc -> shot(mc, "mobs"));
        // Creative tab
        at(365, mc -> {
            hideGui(mc, false);
            mc.gui.toastManager().clear();
            mc.gui.setScreen(new CreativeModeInventoryScreen(mc.player, mc.player.connection.enabledFeatures(), true));
        });
        at(375, mc -> selectTab(mc, EvilCraft._instance.getDefaultCreativeTab()));
        at(395, mc -> shot(mc, "creative_tab"));
        at(400, mc -> mc.gui.setScreen(null));
        // Blood infuser screen
        at(405, mc -> server(mc, DevScene::openInfuser));
        at(430, mc -> { check("blood infuser screen opens", mc.gui.screen() != null, mc.gui.screen()); shot(mc, "gui_blood_infuser"); });
        at(435, mc -> { if (mc.gui.screen() != null) mc.player.closeContainer(); });
        // Info book
        at(440, mc -> server(mc, p -> p.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(RegistryEntries.ITEM_ORIGINS_OF_DARKNESS.get()))));
        at(450, mc -> mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND));
        at(480, mc -> { check("info book opens", mc.gui.screen() != null, mc.gui.screen()); shot(mc, "book"); });
        at(485, mc -> { if (mc.gui.screen() != null) mc.player.closeContainer(); hideGui(mc, true); });
        // Functional checks
        at(490, mc -> server(mc, DevScene::checkTransfer));
        at(495, mc -> server(mc, DevScene::prepareInfusion));
        at(800, mc -> server(mc, DevScene::checkInfusion));
        at(805, mc -> server(mc, DevScene::checkData));
        at(810, mc -> server(mc, p -> look(p, stage.offset(0, 1, 0), 15)));
        at(840, mc -> shot(mc, "blocks_after"));
        // Dynamic item models: filled tank and chalice (fluid and gem colors), display stand with a type, filled box, broom
        at(842, mc -> { hideGui(mc, false); server(mc, DevScene::dynamicItems); });
        at(848, mc -> shot(mc, "items_dynamic"));
        // EvilCraft-Compat: JEI recipe categories and the Jade tooltip (only with JEI and Jade, as in runScene)
        if (FabricLoader.getInstance().isModLoaded("jei")) {
            at(850, mc -> SceneCompat.checkJei());
            at(855, mc -> { hideGui(mc, false); SceneCompat.showJei("blood_infuser"); });
            at(880, mc -> shot(mc, "jei_blood_infuser"));
            at(885, mc -> SceneCompat.showJei("environmental_accumulator"));
            at(910, mc -> shot(mc, "jei_environmental_accumulator"));
            at(915, mc -> SceneCompat.showJei("spirit_furnace"));
            at(940, mc -> shot(mc, "jei_spirit_furnace"));
            at(945, mc -> mc.gui.setScreen(null));
        }
        if (FabricLoader.getInstance().isModLoaded("jade")) {
            at(950, mc -> { hideGui(mc, false); server(mc, p -> look(p, stage.offset(2, 1, 0), 30, 2.5)); });
            at(990, mc -> shot(mc, "jade_box_of_eternal_closure"));
        }
        at(1000, mc -> {
            LOGGER.info("[scene] DONE, failed checks: " + failures);
            mc.stop();
        });
    }

    private DevScene() {
    }

    private static void at(int t, Consumer<Minecraft> action) {
        STEPS.add(Pair.of(t, action));
    }

    public static void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (!opened && mc.gui.screen() instanceof net.minecraft.client.gui.screens.AccessibilityOnboardingScreen) {
            mc.options.onboardAccessibility = false;
            mc.options.save();
            mc.gui.setScreen(new TitleScreen());
            return;
        }
        if (!opened && mc.gui.overlay() != null) {
            return;
        }
        if (!opened && mc.gui.screen() instanceof TitleScreen) {
            if (!mc.options.languageCode.equals(LANG)) {
                mc.options.languageCode = LANG;
                mc.getLanguageManager().setSelected(LANG);
                mc.options.save();
                mc.reloadResourcePacks();
                return;
            }
            opened = true;
            GLFW.glfwSetWindowSize(mc.getWindow().handle(), 1600, 900);
            if (mc.getLevelSource().levelExists(WORLD)) {
                mc.createWorldOpenFlows().openWorld(WORLD, () -> mc.gui.setScreen(new TitleScreen()));
            } else {
                LevelSettings settings = new LevelSettings(WORLD, GameType.CREATIVE, new LevelSettings.DifficultySettings(Difficulty.NORMAL, false, false), true, WorldDataConfiguration.DEFAULT);
                mc.createWorldOpenFlows().createFreshLevel(WORLD, settings, new WorldOptions(1234L, false, false), WorldPresets::createNormalWorldDimensions, new TitleScreen());
            }
            return;
        }
        if (mc.player == null || mc.getSingleplayerServer() == null) {
            return;
        }
        mc.options.pauseOnLostFocus = false;
        mc.getTutorial().setStep(net.minecraft.client.tutorial.TutorialSteps.NONE);
        mc.gui.toastManager().clear();
        if (mc.gui.screen() instanceof PauseScreen) {
            mc.gui.setScreen(null);
        }
        tick++;
        for (Pair<Integer, Consumer<Minecraft>> step : STEPS) {
            if (step.getFirst() == tick) {
                LOGGER.info("[scene] tick " + tick);
                try {
                    step.getSecond().accept(mc);
                } catch (RuntimeException e) {
                    check("step at tick " + tick + " ran", false, e);
                    LOGGER.error("[scene] step failed", e);
                }
            }
        }
    }

    static void check(String name, boolean ok, Object detail) {
        if (!ok) {
            failures++;
        }
        LOGGER.info("[scene] CHECK " + (ok ? "OK" : "FAIL") + " " + name + (ok ? "" : " (" + detail + ")"));
    }

    private static void server(Minecraft mc, Consumer<ServerPlayer> action) {
        MinecraftServer server = mc.getSingleplayerServer();
        server.execute(() -> {
            try {
                action.accept(server.getPlayerList().getPlayer(mc.player.getUUID()));
            } catch (RuntimeException e) {
                check("server step ran", false, e);
                LOGGER.error("[scene] server step failed", e);
            }
        });
    }

    private static void shot(Minecraft mc, String name) {
        Screenshot.grab(mc.gameDirectory, "scene_" + name + ".png", mc.gameRenderer.mainRenderTarget(), 1, c -> LOGGER.info("[scene] " + c.getString()));
    }

    private static void selectTab(Minecraft mc, CreativeModeTab tab) {
        if (mc.gui.screen() instanceof CreativeModeInventoryScreen screen) {
            ((FabricCreativeModeInventoryScreen) screen).setSelectedTab(tab);
        }
    }

    private static void setup(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        player.setGameMode(GameType.CREATIVE);
        server.getGameRules().set(GameRules.SPAWN_MOBS, false, server);
        server.getGameRules().set(GameRules.ADVANCE_TIME, false, server);
        server.getGameRules().set(GameRules.ADVANCE_WEATHER, false, server);
        // The day time and the weather are clocks since 26.1, the commands set them
        player.level().getServer().getCommands().performPrefixedCommand(player.level().getServer().createCommandSourceStack().withSuppressedOutput(), "time set 6000");
        player.level().getServer().getCommands().performPrefixedCommand(player.level().getServer().createCommandSourceStack().withSuppressedOutput(), "weather clear");
        stage = new BlockPos(player.getBlockX(), 120, player.getBlockZ() + 6);
    }

    private static void look(ServerPlayer player, BlockPos target, float pitch) {
        look(player, target, pitch, 5.5);
    }

    private static void look(ServerPlayer player, BlockPos target, float pitch, double distance) {
        Vec3 eye = Vec3.atCenterOf(target).add(0, 1.5, -distance);
        player.teleportTo(player.level(), eye.x, eye.y - player.getEyeHeight(), eye.z, java.util.Set.of(), 0, pitch, true);
    }

    private static void set(ServerLevel level, BlockPos pos, Block block) {
        level.setBlockAndUpdate(pos, block.defaultBlockState());
    }

    private static void buildStage(ServerPlayer player) {
        ServerLevel level = player.level();
        for (int x = -8; x <= 8; x++) {
            for (int z = -16; z <= 12; z++) {
                set(level, stage.offset(x, 0, z), Blocks.SMOOTH_STONE);
                for (int y = 1; y <= 6; y++) {
                    set(level, stage.offset(x, y, z), Blocks.AIR);
                }
            }
        }
        // Machines and storage
        Block[] machines = {
                RegistryEntries.BLOCK_DARK_TANK.get(), RegistryEntries.BLOCK_BLOOD_INFUSER.get(), RegistryEntries.BLOCK_BLOOD_CHEST.get(),
                RegistryEntries.BLOCK_DISPLAY_STAND.get(), RegistryEntries.BLOCK_ENTANGLED_CHALICE.get(), RegistryEntries.BLOCK_PURIFIER.get(),
                RegistryEntries.BLOCK_ENVIRONMENTAL_ACCUMULATOR.get(), RegistryEntries.BLOCK_BOX_OF_ETERNAL_CLOSURE.get(),
                RegistryEntries.BLOCK_SPIRIT_REANIMATOR.get(), RegistryEntries.BLOCK_SANGUINARY_ENVIRONMENTAL_ACCUMULATOR.get(),
                RegistryEntries.BLOCK_ETERNAL_WATER.get()
        };
        for (int i = 0; i < machines.length; i++) {
            set(level, stage.offset(-5 + i, 1, 0), machines[i]);
        }
        // A second dark tank, filled through the item handler of its own item below
        set(level, stage.offset(-5, 2, 0), RegistryEntries.BLOCK_DARK_TANK.get());
        // Building blocks
        Block[] building = {
                RegistryEntries.BLOCK_DARK_BRICK.get(), RegistryEntries.BLOCK_DARK_BLOOD_BRICK.get(), RegistryEntries.BLOCK_BLOODY_COBBLESTONE.get(),
                RegistryEntries.BLOCK_HARDENED_BLOOD.get(), RegistryEntries.BLOCK_REINFORCED_UNDEAD_PLANKS.get(), RegistryEntries.BLOCK_UNDEAD_PLANK.get(),
                RegistryEntries.BLOCK_GEM_STONE_TORCH.get()
        };
        for (int i = 0; i < building.length; i++) {
            set(level, stage.offset(-3 + i, 1, 2), building[i]);
        }
        // Undead tree, fluids in a basin
        BlockPos nature = stage.offset(0, 0, 6);
        set(level, nature.offset(-4, 1, 0), RegistryEntries.BLOCK_UNDEAD_LOG.get());
        set(level, nature.offset(-4, 2, 0), RegistryEntries.BLOCK_UNDEAD_LOG.get());
        set(level, nature.offset(-4, 3, 0), RegistryEntries.BLOCK_UNDEAD_LEAVES.get());
        set(level, nature.offset(-3, 1, 0), RegistryEntries.BLOCK_UNDEAD_SAPLING.get());
        set(level, nature.offset(-2, 1, 0), RegistryEntries.BLOCK_UNDEAD_WOOD.get());
        set(level, nature.offset(-1, 0, 0), Blocks.DIRT);
        // Blood and poison in a glass basin
        for (int x = -1; x <= 4; x++) {
            for (int z = -1; z <= 1; z++) {
                set(level, nature.offset(x, 1, z), Blocks.GLASS);
            }
        }
        set(level, nature.offset(0, 1, 0), RegistryEntries.BLOCK_BLOOD.get());
        set(level, nature.offset(1, 1, 0), RegistryEntries.BLOCK_BLOOD.get());
        set(level, nature.offset(2, 1, 0), RegistryEntries.BLOCK_POISON.get());
        set(level, nature.offset(3, 1, 0), RegistryEntries.BLOCK_POISON.get());
        set(level, nature.offset(6, 1, 0), BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("evilcraft", "dark_ore")));
    }

    private static long insert(Storage<FluidVariant> storage, FluidVariant fluid, long amount) {
        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = storage.insert(fluid, amount, transaction);
            transaction.commit();
            return inserted;
        }
    }

    private static long extract(Storage<FluidVariant> storage, FluidVariant fluid, long amount) {
        try (Transaction transaction = Transaction.openOuter()) {
            long extracted = storage.extract(fluid, amount, transaction);
            transaction.commit();
            return extracted;
        }
    }

    private static void fillTanks(ServerPlayer player) {
        ServerLevel level = player.level();
        BlockPos tank = stage.offset(-5, 1, 0);
        Storage<FluidVariant> storage = FluidStorage.SIDED.find(level, tank, Direction.UP);
        check("dark tank has a Fabric fluid storage", storage != null, null);
        if (storage != null) {
            long inserted = insert(storage, FluidVariant.of(RegistryEntries.FLUID_BLOOD.get()), 8 * FluidConstants.BUCKET);
            check("8 buckets of blood go into the dark tank", inserted == 8 * FluidConstants.BUCKET, inserted);
        }
        // The upper tank: water through the fabric storage of a water bucket stack
        Storage<FluidVariant> upper = FluidStorage.SIDED.find(level, tank.above(), Direction.UP);
        if (upper != null) {
            insert(upper, FluidVariant.of(net.minecraft.world.level.material.Fluids.WATER), 12 * FluidConstants.BUCKET);
        }
        // Fill the chalice and the purifier with blood too, for their renderers
        for (BlockPos pos : new BlockPos[]{stage.offset(-1, 1, 0), stage.offset(0, 1, 0)}) {
            Storage<FluidVariant> s = FluidStorage.SIDED.find(level, pos, Direction.UP);
            if (s != null) {
                insert(s, FluidVariant.of(RegistryEntries.FLUID_BLOOD.get()), 2 * FluidConstants.BUCKET);
            }
        }
    }

    private static void mobs(ServerPlayer player) {
        ServerLevel level = player.level();
        BlockPos pen = stage.offset(0, 1, -6);
        EntityType<?>[] types = {RegistryEntries.ENTITY_WEREWOLF.get(), RegistryEntries.ENTITY_POISONOUS_LIBELLE.get(),
                RegistryEntries.ENTITY_VENGEANCE_SPIRIT.get(), RegistryEntries.ENTITY_NETHERFISH.get(), RegistryEntries.ENTITY_CONTROLLED_ZOMBIE.get()};
        for (int i = 0; i < types.length; i++) {
            Entity entity = types[i].create(level, net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            if (entity != null) {
                entity.snapTo(pen.getX() + 0.5 - 3.2 + i * 1.6, pen.getY(), pen.getZ() + 0.5, 180, 0);
                level.addFreshEntity(entity);
            }
            check(types[i].getDescriptionId() + " spawns", entity != null, null);
            if (entity instanceof Mob mob) {
                mob.setNoAi(true);
                mob.setYRot(180);
                mob.setYHeadRot(180);
            }
        }
    }

    private static void dynamicItems(ServerPlayer player) {
        ItemStack tank = new ItemStack(RegistryEntries.ITEM_DARK_TANK.get());
        org.cyclops.evilcraft.fabric.transfer.ItemAccess access = org.cyclops.evilcraft.fabric.transfer.ItemAccess.forStack(tank);
        var handler = access.getCapability(org.cyclops.evilcraft.fabric.capabilities.Capabilities.Fluid.ITEM);
        check("the dark tank item has a fluid handler", handler != null, null);
        if (handler != null) {
            try (var transaction = org.cyclops.evilcraft.fabric.transfer.Transaction.openRoot()) {
                int inserted = handler.insert(org.cyclops.evilcraft.fabric.transfer.FluidResource.of(RegistryEntries.FLUID_BLOOD.get()), 8000, transaction);
                transaction.commit();
                check("blood goes into the dark tank item", inserted > 0, inserted);
            }
        }
        ItemStack chalice = new ItemStack(RegistryEntries.BLOCK_ENTANGLED_CHALICE.get().asItem());
        ItemStack stand = new ItemStack(RegistryEntries.BLOCK_DISPLAY_STAND.get().asItem());
        ItemStack[] items = {tank, new ItemStack(RegistryEntries.ITEM_DARK_TANK.get()), chalice, stand,
                org.cyclops.evilcraft.block.BlockBoxOfEternalClosure.boxOfEternalClosureFilled.create(),
                new ItemStack(RegistryEntries.BLOCK_BOX_OF_ETERNAL_CLOSURE.get().asItem()),
                new ItemStack(RegistryEntries.ITEM_BROOM.get()), new ItemStack(RegistryEntries.BLOCK_BLOOD_CHEST.get().asItem())};
        for (int i = 0; i < items.length; i++) {
            player.getInventory().setItem(i, items[i]);
        }
        player.getInventory().setSelectedSlot(8);
    }

    private static void openInfuser(ServerPlayer player) {
        BlockPos pos = stage.offset(-4, 1, 0);
        if (player.level().getBlockEntity(pos) instanceof BlockEntityBloodInfuser infuser) {
            infuser.getInventory().setItem(BlockEntityBloodInfuser.SLOT_INFUSE, new ItemStack(Items.MOSSY_COBBLESTONE, 8));
            infuser.getInventory().setItem(BlockEntityBloodInfuser.SLOT_CONTAINER, new ItemStack(RegistryEntries.ITEM_BUCKET_BLOOD.get()));
            player.openMenu(infuser);
        } else {
            check("blood infuser block entity exists", false, player.level().getBlockEntity(pos));
        }
    }

    private static void checkTransfer(ServerPlayer player) {
        ServerLevel level = player.level();
        BlockPos tank = stage.offset(-5, 1, 0);
        if (level.getBlockEntity(tank) instanceof BlockEntityDarkTank darkTank) {
            check("dark tank holds 8000 mB of blood", darkTank.getTank().getFluidAmount() == 8000
                    && darkTank.getTank().getFluid().getFluid() == RegistryEntries.FLUID_BLOOD.get(), darkTank.getTank().getFluid());
            Storage<FluidVariant> storage = FluidStorage.SIDED.find(level, tank, Direction.UP);
            long extracted = extract(storage, FluidVariant.of(RegistryEntries.FLUID_BLOOD.get()), FluidConstants.BUCKET);
            check("1 bucket of blood comes out of the dark tank", extracted == FluidConstants.BUCKET && darkTank.getTank().getFluidAmount() == 7000, extracted);
            // Saved and loaded again (chunk save, world reload)
            BlockEntity loaded = BlockEntity.loadStatic(tank, darkTank.getBlockState(), darkTank.saveWithFullMetadata(level.registryAccess()), level.registryAccess());
            check("the blood in the dark tank is saved", loaded instanceof BlockEntityDarkTank t && t.getTank().getFluidAmount() == 7000, loaded);
            // An aborted transaction changes nothing
            try (Transaction transaction = Transaction.openOuter()) {
                storage.insert(FluidVariant.of(RegistryEntries.FLUID_BLOOD.get()), FluidConstants.BUCKET, transaction);
            }
            check("an aborted insertion is undone", darkTank.getTank().getFluidAmount() == 7000, darkTank.getTank().getFluidAmount());
        } else {
            check("dark tank block entity exists", false, level.getBlockEntity(tank));
        }
        // The Fabric fluid storage of the blood bucket and of a filled dark tank item
        ItemStack bucket = new ItemStack(RegistryEntries.ITEM_BUCKET_BLOOD.get());
        Storage<FluidVariant> bucketStorage = FluidStorage.ITEM.find(bucket, ContainerItemContext.withConstant(bucket));
        long bucketAmount = 0;
        if (bucketStorage != null) {
            for (StorageView<FluidVariant> view : bucketStorage) {
                if (view.getResource().isOf(RegistryEntries.FLUID_BLOOD.get())) {
                    bucketAmount += view.getAmount();
                }
            }
        }
        check("the blood bucket holds a bucket of blood for Fabric", bucketAmount == FluidConstants.BUCKET, bucketAmount);
        ItemStack tankItem = new ItemStack(RegistryEntries.ITEM_DARK_TANK.get());
        Storage<FluidVariant> tankItemStorage = FluidStorage.ITEM.find(tankItem, ContainerItemContext.withConstant(tankItem));
        check("the dark tank item has a Fabric fluid storage", tankItemStorage != null, null);
    }

    private static void prepareInfusion(ServerPlayer player) {
        BlockPos pos = stage.offset(-4, 1, 0);
        if (player.level().getBlockEntity(pos) instanceof BlockEntityBloodInfuser infuser) {
            infuser.getInventory().setItem(BlockEntityBloodInfuser.SLOT_INFUSE, new ItemStack(Items.MOSSY_COBBLESTONE, 1));
            infuser.getInventory().setItem(BlockEntityBloodInfuser.SLOT_CONTAINER, ItemStack.EMPTY);
            Storage<FluidVariant> storage = FluidStorage.SIDED.find(player.level(), pos, Direction.UP);
            long inserted = storage == null ? 0 : insert(storage, FluidVariant.of(RegistryEntries.FLUID_BLOOD.get()), FluidConstants.BUCKET);
            check("blood goes into the blood infuser", inserted == FluidConstants.BUCKET || infuser.getTank().getFluidAmount() > 0, inserted);
        }
    }

    private static void checkInfusion(ServerPlayer player) {
        BlockPos pos = stage.offset(-4, 1, 0);
        if (player.level().getBlockEntity(pos) instanceof BlockEntityBloodInfuser infuser) {
            ItemStack result = infuser.getInventory().getItem(BlockEntityBloodInfuser.SLOT_INFUSE_RESULT);
            check("the blood infuser makes bloody cobblestone", result.is(RegistryEntries.BLOCK_BLOODY_COBBLESTONE.get().asItem()), result);
        }
    }

    private static void checkData(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        // Recipes with Fabric's components ingredient and load conditions
        check("recipe with a components ingredient is loaded",
                server.getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("evilcraft", "crafting/weather_container_rain"))).isPresent(), null);
        check("broom part recipe for a missing metal is not loaded",
                server.getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("evilcraft", "crafting/broom_part_cap_metal_cobalt"))).isEmpty(), null);
        // Biome modifiers: dark ore in the plains
        Biome plains = server.registryAccess().lookupOrThrow(Registries.BIOME).getValue(Biomes.PLAINS);
        ResourceKey<PlacedFeature> darkOre = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath("evilcraft", "ore_dark"));
        boolean hasOre = plains.getGenerationSettings().features().get(GenerationStep.Decoration.UNDERGROUND_ORES.ordinal())
                .stream().anyMatch(holder -> holder.is(darkOre));
        check("dark ore generates in the plains", hasOre, null);
        // Loot modifiers: condensed blood in dungeon chests
        int condensed = 0;
        for (int i = 0; i < 60; i++) {
            LootParams params = new LootParams.Builder(player.level())
                    .withParameter(LootContextParams.ORIGIN, player.position())
                    .create(LootContextParamSets.CHEST);
            for (ItemStack stack : server.reloadableRegistries().getLootTable(BuiltInLootTables.SIMPLE_DUNGEON).getRandomItems(params)) {
                if (stack.is(RegistryEntries.ITEM_CONDENSED_BLOOD.get())) {
                    condensed++;
                }
            }
        }
        check("loot modifiers put condensed blood in dungeon chests", condensed > 0, condensed);
    }
}
