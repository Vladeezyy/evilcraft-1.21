package org.cyclops.evilcraft.fabric.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Consumer;

/**
 * The part of NeoForge's event of this name that the mod uses. Item colors are in the item model files since 1.21.4.
 */
@Environment(EnvType.CLIENT)
public final class RegisterColorHandlersEvent {

    private RegisterColorHandlersEvent() {
    }

    public static final class BlockTintSources {
        public static void addListener(Consumer<BlockTintSources> listener) {
            ModLifecycle.afterRegistries(() -> listener.accept(new BlockTintSources()));
        }

        public void register(List<BlockTintSource> tints, Block... blocks) {
            BlockColorRegistry.register(tints, blocks);
        }
    }
}
