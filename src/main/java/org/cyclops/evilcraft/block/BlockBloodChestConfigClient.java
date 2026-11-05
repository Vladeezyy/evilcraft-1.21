package org.cyclops.evilcraft.block;

import org.cyclops.evilcraft.fabric.event.RegisterSpecialModelRendererEvent;
import org.cyclops.cyclopscore.config.extendedconfig.BlockClientConfig;
import org.cyclops.cyclopscore.config.extendedconfig.BlockConfigCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBaseNeoForge;
import org.cyclops.evilcraft.client.render.blockentity.RenderItemStackBlockEntityBloodChest;

/**
 * @author rubensworks
 */
public class BlockBloodChestConfigClient extends BlockClientConfig<ModBaseNeoForge<?>>  {
    public BlockBloodChestConfigClient(BlockConfigCommon<ModBaseNeoForge<?>> blockConfig) {
        super(blockConfig);
        RegisterSpecialModelRendererEvent.addListener((RegisterSpecialModelRendererEvent event) -> event.register(blockConfig.getResourceKey().identifier(), RenderItemStackBlockEntityBloodChest.Unbaked.MAP_CODEC));
    }
}
