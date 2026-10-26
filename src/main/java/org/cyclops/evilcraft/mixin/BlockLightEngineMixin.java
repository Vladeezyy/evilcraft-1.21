package org.cyclops.evilcraft.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.BlockLightEngine;
import org.cyclops.evilcraft.fabric.extensions.IDynamicLightBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Light levels of {@link IDynamicLightBlock}s depend on their position (their block entity).
 */
@Mixin(BlockLightEngine.class)
public abstract class BlockLightEngineMixin {
    @ModifyExpressionValue(method = "getEmission", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"))
    private int evilcraft$dynamicLight(int emission, long packedPos, BlockState state) {
        if (state.getBlock() instanceof IDynamicLightBlock block
                && ((LightEngineAccessor) this).evilcraft$getChunkSource().getLevel() instanceof BlockGetter level) {
            return block.getLightEmission(state, level, BlockPos.of(packedPos));
        }
        return emission;
    }
}
