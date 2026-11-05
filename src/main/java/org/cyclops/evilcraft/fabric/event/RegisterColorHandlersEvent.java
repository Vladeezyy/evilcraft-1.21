package org.cyclops.evilcraft.fabric.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.block.BlockColor;

import java.util.function.Consumer;

/**
 * The part of NeoForge's event of this name that the mod uses. Item colors are in the item model files since 1.21.4.
 */
@Environment(EnvType.CLIENT)
public final class RegisterColorHandlersEvent {

    private RegisterColorHandlersEvent() {
    }

    public static final class Block {
        public static void addListener(Consumer<Block> listener) {
            ModLifecycle.afterRegistries(() -> listener.accept(new Block()));
        }

        public void register(BlockColor color, net.minecraft.world.level.block.Block... blocks) {
            ColorProviderRegistry.BLOCK.register(color, blocks);
        }
    }
}
