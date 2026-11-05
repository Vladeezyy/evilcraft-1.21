package org.cyclops.evilcraft.block;

import org.cyclops.evilcraft.fabric.event.RegisterBlockStateModels;
import org.cyclops.evilcraft.fabric.event.RegisterItemModelsEvent;
import org.cyclops.cyclopscore.config.extendedconfig.BlockClientConfig;
import org.cyclops.cyclopscore.config.extendedconfig.BlockConfigCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBaseNeoForge;
import org.cyclops.evilcraft.client.render.model.ItemModelEntangledChalice;
import org.cyclops.evilcraft.client.render.model.ModelEntangledChaliceBaked;

/**
 * @author rubensworks
 */
public class BlockEntangledChaliceConfigClient extends BlockClientConfig<ModBaseNeoForge<?>> {
    public BlockEntangledChaliceConfigClient(BlockConfigCommon<ModBaseNeoForge<?>> blockConfig) {
        super(blockConfig);
        RegisterItemModelsEvent.addListener((RegisterItemModelsEvent event) -> event.register(ItemModelEntangledChalice.Unbaked.ID, ItemModelEntangledChalice.Unbaked.MAP_CODEC));
        RegisterBlockStateModels.addListener((RegisterBlockStateModels event) -> event.registerModel(ModelEntangledChaliceBaked.Unbaked.ID, ModelEntangledChaliceBaked.Unbaked.CODEC));
    }
}
