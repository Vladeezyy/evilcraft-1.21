package org.cyclops.evilcraft.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.cyclops.cyclopscore.config.extendedconfig.BlockClientConfig;
import org.cyclops.cyclopscore.config.extendedconfig.BlockConfigCommon;
import org.cyclops.evilcraft.RegistryEntries;
import org.cyclops.evilcraft.client.render.model.ModelDarkTankBaked;
import org.cyclops.evilcraft.fabric.cyclopscore.client.render.model.ItemDynamicItemAndBlockModel;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBaseNeoForge;
import org.jetbrains.annotations.Nullable;

/**
 * Cyclops Core's dynamic model elements are only applied on NeoForge. The item shows its fluid through a dynamic model;
 * the block keeps its plain baked model, as {@link org.cyclops.evilcraft.client.render.blockentity.RenderBlockEntityDarkTank}
 * draws the fluid in the world.
 * @author rubensworks
 */
public class BlockDarkTankConfigClient extends BlockClientConfig<ModBaseNeoForge<?>> {
    public BlockDarkTankConfigClient(BlockConfigCommon<ModBaseNeoForge<?>> blockConfig) {
        super(blockConfig);
        ModelLoadingPlugin.register(context -> context.modifyItemModelAfterBake().register((model, modelContext) -> {
            Identifier tank = BuiltInRegistries.BLOCK.getKey(RegistryEntries.BLOCK_DARK_TANK.get());
            return tank.equals(modelContext.itemId()) ? new ItemModelDarkTank() : model;
        }));
    }

    /**
     * Resolved at the first use: block models may not be baked yet when this item model is.
     */
    private static final class ItemModelDarkTank implements ItemModel {

        private ItemDynamicItemAndBlockModel model;

        @Override
        public void update(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext,
                           @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
            if (model == null) {
                ModelDarkTankBaked baked = new ModelDarkTankBaked(Minecraft.getInstance().getModelManager().getBlockModelShaper()
                        .getBlockModel(RegistryEntries.BLOCK_DARK_TANK.get().defaultBlockState()));
                model = new ItemDynamicItemAndBlockModel(baked, baked.getModelRenderProperties());
            }
            model.update(renderState, stack, itemModelResolver, displayContext, level, owner, seed);
        }
    }
}
