package org.cyclops.evilcraft.fabric.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

/**
 * Block events.
 */
public class BlockEvent {

    /**
     * Before a player breaks a block (server side); canceling it keeps the block.
     */
    public static class BreakEvent extends CancellableEvent {
        private final Level level;
        private final BlockPos pos;
        private final BlockState state;
        private final Player player;

        public BreakEvent(Level level, BlockPos pos, BlockState state, Player player) {
            this.level = level;
            this.pos = pos;
            this.state = state;
            this.player = player;
        }

        public static void addListener(Consumer<BreakEvent> listener) {
            PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
                BreakEvent event = new BreakEvent(level, pos, state, player);
                listener.accept(event);
                return !event.isCanceled();
            });
        }

        public LevelAccessor getLevel() {
            return level;
        }

        public BlockPos getPos() {
            return pos;
        }

        public BlockState getState() {
            return state;
        }

        public Player getPlayer() {
            return player;
        }
    }
}
