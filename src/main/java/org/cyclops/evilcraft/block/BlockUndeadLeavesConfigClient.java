package org.cyclops.evilcraft.block;

import org.cyclops.evilcraft.fabric.event.RegisterColorHandlersEvent;
import org.cyclops.cyclopscore.config.extendedconfig.BlockClientConfig;
import org.cyclops.cyclopscore.config.extendedconfig.BlockConfigCommon;
import org.cyclops.evilcraft.EvilCraft;

/**
 * @author rubensworks
 */
public class BlockUndeadLeavesConfigClient extends BlockClientConfig<EvilCraft> {

    // Dominant Undead Leaves texture color (#2E0F0F = R46 G15 B15), encoded as ARGB 0xFF2E0F0F.
    private static final int UNDEAD_LEAVES_COLOR = -13758705;

    public BlockUndeadLeavesConfigClient(BlockConfigCommon<EvilCraft> blockConfig) {
        super(blockConfig);
        RegisterColorHandlersEvent.Block.addListener(this::onRegisterColors);
    }

    public void onRegisterColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> UNDEAD_LEAVES_COLOR, getBlockConfig().getInstance());
    }
}
