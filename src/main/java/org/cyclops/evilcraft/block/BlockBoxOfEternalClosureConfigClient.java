package org.cyclops.evilcraft.block;

import org.cyclops.evilcraft.fabric.event.RegisterBlockStateModels;
import org.cyclops.evilcraft.fabric.event.RegisterItemModelsEvent;
import org.cyclops.evilcraft.fabric.event.RegisterSpecialModelRendererEvent;
import org.cyclops.cyclopscore.config.extendedconfig.BlockClientConfig;
import org.cyclops.cyclopscore.config.extendedconfig.BlockConfigCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBaseNeoForge;
import org.cyclops.evilcraft.client.render.blockentity.RenderItemStackBlockEntityBoxOfEternalClosure;
import org.cyclops.evilcraft.client.render.model.ItemModelBoxOfEternalClosure;
import org.cyclops.evilcraft.client.render.model.ModelBoxOfEternalClosureBaked;

/**
 * @author rubensworks
 */
public class BlockBoxOfEternalClosureConfigClient extends BlockClientConfig<ModBaseNeoForge<?>> {
    public BlockBoxOfEternalClosureConfigClient(BlockConfigCommon<ModBaseNeoForge<?>> blockConfig) {
        super(blockConfig);
        RegisterSpecialModelRendererEvent.addListener((RegisterSpecialModelRendererEvent event) -> event.register(blockConfig.getResourceKey().identifier(), RenderItemStackBlockEntityBoxOfEternalClosure.Unbaked.MAP_CODEC));
        RegisterItemModelsEvent.addListener((RegisterItemModelsEvent event) -> event.register(ItemModelBoxOfEternalClosure.Unbaked.ID, ItemModelBoxOfEternalClosure.Unbaked.MAP_CODEC));
        RegisterBlockStateModels.addListener((RegisterBlockStateModels event) -> event.registerModel(ModelBoxOfEternalClosureBaked.Unbaked.ID, ModelBoxOfEternalClosureBaked.Unbaked.CODEC));
    }
}
