package org.cyclops.evilcraft.fabric.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A block whose light level depends on its block entity (read by the block light engine through a mixin).
 */
public interface IDynamicLightBlock {
    int getLightEmission(BlockState state, BlockGetter level, BlockPos pos);
}
