package org.cyclops.evilcraft.fabric.cyclopscore.client.model;

import org.cyclops.evilcraft.fabric.client.model.ModelData;



import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A dynamic model with a parent that can be used for items and blocks that delegates its blockstate.
 * @author rubensworks
 */
public abstract class DelegatingChildDynamicItemAndBlockModel extends DelegatingDynamicItemAndBlockModel {

    protected final BlockStateModel baseModel;

    public DelegatingChildDynamicItemAndBlockModel(BlockStateModel baseModel) {
        super();
        this.baseModel = baseModel;
    }

    public DelegatingChildDynamicItemAndBlockModel(BlockStateModel baseModel, BlockAndTintGetter level, BlockState blockState, Direction facing,
                                                   RandomSource rand, ModelData modelData, ChunkSectionLayer renderType) {
        super(level, blockState, facing, rand, modelData, renderType);
        this.baseModel = baseModel;
    }

    public DelegatingChildDynamicItemAndBlockModel(BlockStateModel baseModel, ItemStack itemStack, ClientLevel level, LivingEntity entity) {
        super(itemStack, level, entity);
        this.baseModel = baseModel;
    }

}
