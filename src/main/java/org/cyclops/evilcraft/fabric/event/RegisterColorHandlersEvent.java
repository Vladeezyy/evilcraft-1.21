package org.cyclops.evilcraft.fabric.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

/**
 * Registers item and block color handlers once all registries are filled.
 */
@Environment(EnvType.CLIENT)
public class RegisterColorHandlersEvent {

    public static class Item {
        public static void addListener(Consumer<Item> listener) {
            ModLifecycle.afterRegistries(() -> listener.accept(new Item()));
        }

        public void register(ItemColor color, ItemLike... items) {
            ColorProviderRegistry.ITEM.register(color, items);
        }
    }

    public static class Block {
        public static void addListener(Consumer<Block> listener) {
            ModLifecycle.afterRegistries(() -> listener.accept(new Block()));
        }

        public void register(BlockColor color, net.minecraft.world.level.block.Block... blocks) {
            ColorProviderRegistry.BLOCK.register(color, blocks);
        }
    }
}
