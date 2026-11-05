package org.cyclops.evilcraft.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.cyclops.evilcraft.fabric.event.BlockEvent;

import javax.annotation.Nullable;

/**
 * Vanilla equivalents of the NeoForge hooks EvilCraft uses.
 */
public final class CommonHooks {

    private static final ThreadLocal<Player> CRAFTING_PLAYER = new ThreadLocal<>();

    private CommonHooks() {
    }

    /**
     * @return The player that is taking a crafting result, or null (set by a mixin on the crafting result slot).
     */
    @Nullable
    public static Player getCraftingPlayer() {
        return CRAFTING_PLAYER.get();
    }

    public static void setCraftingPlayer(@Nullable Player player) {
        if (player == null) {
            CRAFTING_PLAYER.remove();
        } else {
            CRAFTING_PLAYER.set(player);
        }
    }

    public static ItemStack getCraftingRemainingItem(ItemStack stack) {
        net.minecraft.world.item.ItemStackTemplate remainder = stack.getItem().getCraftingRemainder();
        return remainder == null ? ItemStack.EMPTY : remainder.create();
    }

    public static ItemStack getProjectile(LivingEntity entity, ItemStack projectileWeaponItem, ItemStack projectile) {
        return projectile;
    }

    /**
     * @return The registry lookup of the running server, or of the client level.
     */
    @Nullable
    public static <T> HolderLookup.RegistryLookup<T> resolveLookup(ResourceKey<? extends Registry<T>> key) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return server.registryAccess().lookup(key).orElse(null);
        }
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return ClientHooks.resolveLookup(key);
        }
        return null;
    }

    /**
     * Ask other mods (protection) if a player may break a block.
     */
    public static BlockEvent.BreakEvent fireBlockBreak(Level level, GameType gameType, ServerPlayer player, BlockPos pos, BlockState state) {
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, pos, state, player);
        boolean allowed = !gameType.isBlockPlacingRestricted() || player.mayBuild();
        allowed = allowed && PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(level, player, pos, state, level.getBlockEntity(pos));
        event.setCanceled(!allowed);
        return event;
    }
}
