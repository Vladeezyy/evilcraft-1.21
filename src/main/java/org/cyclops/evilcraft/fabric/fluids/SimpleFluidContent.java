package org.cyclops.evilcraft.fabric.fluids;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Objects;

/**
 * An immutable fluid stack, for use as a data component.
 */
public final class SimpleFluidContent {
    public static final SimpleFluidContent EMPTY = new SimpleFluidContent(FluidStack.EMPTY);
    public static final Codec<SimpleFluidContent> CODEC = FluidStack.OPTIONAL_CODEC.xmap(SimpleFluidContent::copyOf, SimpleFluidContent::copy);
    public static final StreamCodec<RegistryFriendlyByteBuf, SimpleFluidContent> STREAM_CODEC = FluidStack.OPTIONAL_STREAM_CODEC
            .map(SimpleFluidContent::copyOf, SimpleFluidContent::copy);

    private final FluidStack fluidStack;

    private SimpleFluidContent(FluidStack fluidStack) {
        this.fluidStack = fluidStack;
    }

    public static SimpleFluidContent copyOf(FluidStack fluidStack) {
        return fluidStack.isEmpty() ? EMPTY : new SimpleFluidContent(fluidStack.copy());
    }

    public FluidStack copy() {
        return fluidStack.copy();
    }

    public boolean isEmpty() {
        return fluidStack.isEmpty();
    }

    public Fluid getFluid() {
        return fluidStack.getFluid();
    }

    public Holder<Fluid> getFluidHolder() {
        return fluidStack.getFluidHolder();
    }

    public boolean is(Fluid fluid) {
        return fluidStack.is(fluid);
    }

    public int getAmount() {
        return fluidStack.getAmount();
    }

    public DataComponentPatch getComponentsPatch() {
        return fluidStack.getComponentsPatch();
    }

    public boolean matches(FluidStack other) {
        return FluidStack.matches(fluidStack, other);
    }

    public boolean isSameFluidSameComponents(FluidStack other) {
        return FluidStack.isSameFluidSameComponents(fluidStack, other);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof SimpleFluidContent o && FluidStack.matches(fluidStack, o.fluidStack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fluidStack.getAmount(), FluidStack.hashFluidAndComponents(fluidStack));
    }
}
