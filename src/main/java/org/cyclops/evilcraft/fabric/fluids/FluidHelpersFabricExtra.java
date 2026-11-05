package org.cyclops.evilcraft.fabric.fluids;

import net.minecraft.util.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/**
 * Small fluid helpers that NeoForge keeps on its fluid type.
 */
public final class FluidHelpersFabricExtra {

    private FluidHelpersFabricExtra() {
    }

    /**
     * @param fluid A fluid.
     * @return The translation key of a fluid, like NeoForge's fluid type ("fluid_type.<namespace>.<path>"); water and
     *         lava use the vanilla block keys.
     */
    public static String getDescriptionId(Fluid fluid) {
        if (fluid == Fluids.EMPTY) {
            return "block.minecraft.air";
        }
        if (fluid.isSame(Fluids.WATER)) {
            return "block.minecraft.water";
        }
        if (fluid.isSame(Fluids.LAVA)) {
            return "block.minecraft.lava";
        }
        return Util.makeDescriptionId("fluid_type", BuiltInRegistries.FLUID.getKey(fluid));
    }

    public static net.minecraft.world.item.Rarity getRarity(Fluid fluid) {
        return fluid instanceof BaseFlowingFluid baseFlowingFluid ? baseFlowingFluid.getFluidType().getRarity() : net.minecraft.world.item.Rarity.COMMON;
    }

    public static int getLightLevel(Fluid fluid) {
        return net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes.getLuminance(net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant.of(fluid));
    }
}
