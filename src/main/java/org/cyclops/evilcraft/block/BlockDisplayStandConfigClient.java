package org.cyclops.evilcraft.block;

import org.cyclops.evilcraft.fabric.event.RegisterBlockStateModels;
import org.cyclops.evilcraft.fabric.event.RegisterItemModelsEvent;
import org.cyclops.cyclopscore.config.extendedconfig.BlockClientConfig;
import org.cyclops.cyclopscore.config.extendedconfig.BlockConfigCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBaseNeoForge;
import org.cyclops.evilcraft.client.render.model.ItemModelDisplayStand;
import org.cyclops.evilcraft.client.render.model.ModelDisplayStandBaked;

/**
 * Config for the {@link BlockDisplayStand}.
 * @author rubensworks
 *
 */
public class BlockDisplayStandConfigClient extends BlockClientConfig<ModBaseNeoForge<?>> {
    public BlockDisplayStandConfigClient(BlockConfigCommon<ModBaseNeoForge<?>> blockConfig) {
        super(blockConfig);
        RegisterItemModelsEvent.addListener((RegisterItemModelsEvent event) -> event.register(ItemModelDisplayStand.Unbaked.ID, ItemModelDisplayStand.Unbaked.MAP_CODEC));
        RegisterBlockStateModels.addListener((RegisterBlockStateModels event) -> event.registerModel(ModelDisplayStandBaked.Unbaked.ID, ModelDisplayStandBaked.Unbaked.CODEC));
    }
}
