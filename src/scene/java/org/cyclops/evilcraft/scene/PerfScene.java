package org.cyclops.evilcraft.scene;

import net.minecraft.world.entity.EntityTypes;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cyclops.evilcraft.RegistryEntries;
import org.cyclops.evilcraft.blockentity.BlockEntityBloodChest;
import org.cyclops.evilcraft.blockentity.BlockEntityBloodInfuser;
import org.cyclops.evilcraft.blockentity.BlockEntityDarkTank;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Stress test of the Fabric port ({@code ./gradlew runPerf}, tools/perf.sh), inert unless the evilcraft.perf system
 * property is set: in one survival world at day, a window of 600 ticks with a vanilla base (40 cows in a pen), then the
 * same base plus working EvilCraft content (blood infusers fed from dark tanks, blood chests repairing tools, dark
 * tanks, environmental accumulators, purifiers, 32 mobs of the mod). Logs {@code [perf]} lines: server MSPT (own
 * timing of each server tick), client FPS, entity and block entity counts per window.
 */
public final class PerfScene {

    private static void hideGui(Minecraft mc, boolean hidden) {
        if (mc.gui.hud.isHidden() != hidden) {
            mc.gui.hud.toggle();
        }
    }

    private static final Logger LOGGER = LogManager.getLogger("evilcraft-perf");
    private static final String WORLD = "ec_perf";
    private static final int WINDOW = 600;

    private static boolean opened;
    private static int tick;
    private static BlockPos stage;

    private static volatile String window;
    private static volatile long tickStart;
    private static final AtomicLong serverNanos = new AtomicLong();
    private static final AtomicLong serverTicks = new AtomicLong();
    private static final AtomicLong serverMax = new AtomicLong();
    private static long fpsSum;
    private static int fpsSamples;
    private static double vanillaMspt;
    private static double vanillaFps;

    private PerfScene() {
    }

    static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> onTick());
        ServerTickEvents.START_SERVER_TICK.register(server -> tickStart = System.nanoTime());
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (window != null) {
                long nanos = System.nanoTime() - tickStart;
                serverNanos.addAndGet(nanos);
                serverTicks.incrementAndGet();
                serverMax.accumulateAndGet(nanos, Math::max);
            }
        });
    }

    private static void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (!opened && mc.gui.screen() instanceof net.minecraft.client.gui.screens.AccessibilityOnboardingScreen) {
            mc.options.onboardAccessibility = false;
            mc.options.save();
            mc.gui.setScreen(new TitleScreen());
            return;
        }
        if (!opened && (mc.gui.overlay() != null || !(mc.gui.screen() instanceof TitleScreen))) {
            return;
        }
        if (!opened) {
            opened = true;
            GLFW.glfwSetWindowSize(mc.getWindow().handle(), 1600, 900);
            // Uncapped frames, or the FPS of both windows is the display's refresh rate
            mc.options.enableVsync().set(false);
            mc.options.framerateLimit().set(260);
            LevelSettings settings = new LevelSettings(WORLD, GameType.SURVIVAL, new LevelSettings.DifficultySettings(Difficulty.NORMAL, false, false), true, WorldDataConfiguration.DEFAULT);
            mc.createWorldOpenFlows().createFreshLevel(WORLD, settings, new WorldOptions(1234L, false, false), WorldPresets::createNormalWorldDimensions, new TitleScreen());
            return;
        }
        if (mc.player == null || mc.getSingleplayerServer() == null) {
            return;
        }
        mc.options.pauseOnLostFocus = false;
        // Vanilla drops to 30 FPS after a minute without input, which would fall right in the second window
        mc.options.inactivityFpsLimit().set(net.minecraft.client.InactivityFpsLimit.MINIMIZED);
        hideGui(mc, true);
        mc.gui.toastManager().clear();
        if (mc.gui.screen() instanceof PauseScreen) {
            mc.gui.setScreen(null);
        }
        tick++;
        if (window != null && tick % 20 == 0) {
            fpsSum += mc.getFps();
            fpsSamples++;
        }
        switch (tick) {
            case 40 -> server(mc, PerfScene::setup);
            case 60 -> server(mc, PerfScene::buildVanilla);
            // Chunks and the cows settle first
            case 400 -> start("vanilla");
            case 400 + WINDOW -> { stop(mc, "vanilla"); server(mc, PerfScene::buildEvilCraft); }
            case 1300 -> start("evilcraft");
            case 1300 + WINDOW -> stop(mc, "evilcraft");
            case 1300 + WINDOW + 20 -> {
                LOGGER.info("[perf] DONE");
                mc.stop();
            }
            default -> {
            }
        }
    }

    private static void start(String name) {
        serverNanos.set(0);
        serverTicks.set(0);
        serverMax.set(0);
        fpsSum = 0;
        fpsSamples = 0;
        window = name;
    }

    private static void stop(Minecraft mc, String name) {
        window = null;
        double mspt = serverNanos.get() / 1e6 / Math.max(1, serverTicks.get());
        double fps = (double) fpsSum / Math.max(1, fpsSamples);
        LOGGER.info(String.format(Locale.ROOT, "[perf] %s: MSPT %.2f (max %.1f, %d ticks), FPS %.0f",
                name, mspt, serverMax.get() / 1e6, serverTicks.get(), fps));
        // On the server thread: ServerLevel.getBlockEntity is null on other threads
        server(mc, player -> {
            int entities = 0;
            for (Entity ignored : player.level().getAllEntities()) {
                entities++;
            }
            LOGGER.info(String.format(Locale.ROOT, "[perf] %s: %d entities, %d block entities of the mod", name, entities, modBlockEntities(player.level())));
        });
        if (name.equals("vanilla")) {
            vanillaMspt = mspt;
            vanillaFps = fps;
        } else {
            LOGGER.info(String.format(Locale.ROOT, "[perf] EvilCraft content: MSPT %+.2f (%+.0f%%), FPS %+.0f (%+.0f%%)",
                    mspt - vanillaMspt, 100 * (mspt - vanillaMspt) / vanillaMspt, fps - vanillaFps, 100 * (fps - vanillaFps) / vanillaFps));
        }
    }

    private static int modBlockEntities(ServerLevel level) {
        int count = 0;
        for (BlockPos pos : BlockPos.betweenClosed(stage.offset(-16, 1, -16), stage.offset(16, 4, 16))) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null && BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()).getNamespace().equals("evilcraft")) {
                count++;
            }
        }
        return count;
    }

    private static void server(Minecraft mc, java.util.function.Consumer<ServerPlayer> action) {
        MinecraftServer server = mc.getSingleplayerServer();
        server.execute(() -> {
            try {
                action.accept(server.getPlayerList().getPlayer(mc.player.getUUID()));
            } catch (RuntimeException e) {
                LOGGER.error("[perf] step failed", e);
            }
        });
    }

    private static void setup(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        // Survival rules, but a fixed day and no natural spawns: the same load in both windows
        player.setGameMode(GameType.SPECTATOR);
        server.getGameRules().set(GameRules.SPAWN_MOBS, false, server);
        server.getGameRules().set(GameRules.ADVANCE_TIME, false, server);
        server.getGameRules().set(GameRules.ADVANCE_WEATHER, false, server);
        // The day time and the weather are clocks since 26.1, the commands set them
        player.level().getServer().getCommands().performPrefixedCommand(player.level().getServer().createCommandSourceStack().withSuppressedOutput(), "time set 6000");
        player.level().getServer().getCommands().performPrefixedCommand(player.level().getServer().createCommandSourceStack().withSuppressedOutput(), "weather clear");
        stage = new BlockPos(player.getBlockX(), 120, player.getBlockZ());
        // Above the middle of the stage, looking down at it
        player.teleportTo(player.level(), stage.getX() + 0.5, stage.getY() + 14, stage.getZ() - 14, java.util.Set.of(), 0, 40, true);
    }

    private static void set(ServerLevel level, BlockPos pos, Block block) {
        level.setBlock(pos, block.defaultBlockState(), Block.UPDATE_CLIENTS);
    }

    private static void buildVanilla(ServerPlayer player) {
        ServerLevel level = player.level();
        for (int x = -16; x <= 16; x++) {
            for (int z = -16; z <= 16; z++) {
                set(level, stage.offset(x, 0, z), Blocks.SMOOTH_STONE);
                for (int y = 1; y <= 4; y++) {
                    boolean wall = Math.abs(x) == 16 || Math.abs(z) == 16;
                    set(level, stage.offset(x, y, z), wall && y <= 2 ? Blocks.GLASS : Blocks.AIR);
                }
            }
        }
        // 40 cows in the south half
        for (int i = 0; i < 40; i++) {
            Entity cow = EntityTypes.COW.create(level, net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            cow.snapTo(stage.getX() - 12 + (i % 10) * 2.5, stage.getY() + 1, stage.getZ() + 4 + (i / 10) * 2.5, 0, 0);
            level.addFreshEntity(cow);
        }
    }

    private static void buildEvilCraft(ServerPlayer player) {
        ServerLevel level = player.level();
        // North half: 16 blood infusers, each with a dark tank of blood above, infusing dark gems
        for (int i = 0; i < 16; i++) {
            BlockPos pos = stage.offset(-12 + (i % 8) * 3, 1, -12 + (i / 8) * 3);
            set(level, pos, RegistryEntries.BLOCK_BLOOD_INFUSER.get());
            set(level, pos.above(), RegistryEntries.BLOCK_DARK_TANK.get());
            if (level.getBlockEntity(pos.above()) instanceof BlockEntityDarkTank tank) {
                tank.getTank().setFluid(new FluidStack(RegistryEntries.FLUID_BLOOD, 16000));
                tank.setEnabled(true);
            }
            if (level.getBlockEntity(pos) instanceof BlockEntityBloodInfuser infuser) {
                infuser.getInventory().setItem(BlockEntityBloodInfuser.SLOT_INFUSE, new ItemStack(RegistryEntries.ITEM_DARK_GEM, 64));
            }
        }
        // 8 blood chests repairing damaged iron swords, with blood
        for (int i = 0; i < 8; i++) {
            BlockPos pos = stage.offset(-12 + i * 3, 1, -5);
            set(level, pos, RegistryEntries.BLOCK_BLOOD_CHEST.get());
            if (level.getBlockEntity(pos) instanceof BlockEntityBloodChest chest) {
                chest.getTank().setFluid(new FluidStack(RegistryEntries.FLUID_BLOOD, 10000));
                for (int slot = 0; slot < 4; slot++) {
                    ItemStack sword = new ItemStack(Items.IRON_SWORD);
                    sword.setDamageValue(200);
                    chest.getInventory().setItem(slot, sword);
                }
            }
        }
        // 8 environmental accumulators, 8 purifiers, 16 more dark tanks
        for (int i = 0; i < 8; i++) {
            set(level, stage.offset(-12 + i * 3, 1, -2), RegistryEntries.BLOCK_ENVIRONMENTAL_ACCUMULATOR.get());
            set(level, stage.offset(-11 + i * 3, 1, -2), RegistryEntries.BLOCK_PURIFIER.get());
            set(level, stage.offset(-12 + i * 3, 1, 1), RegistryEntries.BLOCK_DARK_TANK.get());
            set(level, stage.offset(-11 + i * 3, 1, 1), RegistryEntries.BLOCK_DARK_TANK.get());
        }
        // 32 mobs of the mod in the south half, with the cows
        EntityType<?>[] types = {RegistryEntries.ENTITY_WEREWOLF.get(), RegistryEntries.ENTITY_POISONOUS_LIBELLE.get(),
                RegistryEntries.ENTITY_NETHERFISH.get(), RegistryEntries.ENTITY_VENGEANCE_SPIRIT.get()};
        for (int i = 0; i < 32; i++) {
            BlockPos pos = stage.offset(-12 + (i % 8) * 3, 1, 8 + (i / 8) * 2);
            types[i % types.length].spawn(level, pos, EntitySpawnReason.COMMAND);
        }
    }
}
