package org.cyclops.evilcraft.client.render.blockentity;

import net.minecraft.world.level.ItemLike;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.cyclopscore.client.render.blockentity.ItemStackBlockEntityRendererBase;
import org.cyclops.evilcraft.RegistryEntries;
import org.cyclops.evilcraft.blockentity.BlockEntityBloodChest;

/**
 * @author rubensworks
 */
@Environment(EnvType.CLIENT)
public class RenderItemStackBlockEntityBloodChest extends ItemStackBlockEntityRendererBase {

    public RenderItemStackBlockEntityBloodChest() {
        super(() -> new BlockEntityBloodChest(BlockPos.ZERO, RegistryEntries.BLOCK_BLOOD_CHEST.get().defaultBlockState()));
    }

    public static class ItemRenderProperties {
        private static RenderItemStackBlockEntityBloodChest renderer;

        public static void register(ItemLike item) {
            BuiltinItemRendererRegistry.INSTANCE.register(item, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
                if (renderer == null) {
                    renderer = new RenderItemStackBlockEntityBloodChest();
                }
                renderer.renderByItem(stack, mode, matrices, vertexConsumers, light, overlay);
            });
        }
    }
}
