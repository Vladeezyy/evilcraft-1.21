package org.cyclops.evilcraft.fabric.transfer;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;

import java.util.function.Supplier;

/**
 * A fluid with components and without an amount; a view on a Fabric {@link FluidVariant}, which are cached.
 */
public final class FluidResource implements Resource {

    public static final FluidResource EMPTY = new FluidResource(FluidVariant.blank());
    public static final Codec<FluidResource> CODEC = FluidVariant.CODEC.xmap(FluidResource::of, FluidResource::variant);
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidResource> STREAM_CODEC =
            FluidVariant.PACKET_CODEC.map(FluidResource::of, FluidResource::variant);

    private final FluidVariant variant;

    private FluidResource(FluidVariant variant) {
        this.variant = variant;
    }

    public static FluidResource of(FluidVariant variant) {
        return variant.isBlank() ? EMPTY : new FluidResource(variant);
    }

    public static FluidResource of(FluidStack stack) {
        return stack.isEmpty() ? EMPTY : of(stack.toVariant());
    }

    public static FluidResource of(Fluid fluid) {
        return of(FluidVariant.of(fluid));
    }

    public static FluidResource of(Fluid fluid, DataComponentPatch patch) {
        return of(FluidVariant.of(fluid, patch));
    }

    public static FluidResource of(org.cyclops.evilcraft.fabric.registries.DeferredHolder<Fluid, ? extends Fluid> fluid) {
        return of(fluid.get());
    }

    public static FluidResource of(Holder<Fluid> fluid) {
        return of(fluid.value());
    }

    public static FluidResource of(Holder<Fluid> fluid, DataComponentPatch patch) {
        return of(fluid.value(), patch);
    }

    public FluidVariant variant() {
        return variant;
    }

    public Fluid value() {
        return variant.getFluid();
    }

    public Fluid getFluid() {
        return variant.getFluid();
    }

    public Holder<Fluid> typeHolder() {
        return variant.typeHolder();
    }

    @Override
    public boolean isEmpty() {
        return variant.isBlank();
    }

    public boolean is(Fluid fluid) {
        return variant.isOf(fluid);
    }

    public DataComponentMap getComponents() {
        return variant.getComponents();
    }

    public DataComponentPatch getComponentsPatch() {
        return variant.getComponentsPatch();
    }

    public boolean isComponentsPatchEmpty() {
        return !variant.hasComponents();
    }

    public <D> FluidResource with(DataComponentType<D> type, D data) {
        return of(variant.withComponents(DataComponentPatch.builder().set(type, data).build()));
    }

    public <D> FluidResource with(Supplier<? extends DataComponentType<D>> type, D data) {
        return with(type.get(), data);
    }

    public FluidResource without(DataComponentType<?> type) {
        return of(variant.withComponents(DataComponentPatch.builder().remove(type).build()));
    }

    public FluidStack toStack(int amount) {
        return isEmpty() || amount <= 0 ? FluidStack.EMPTY : FluidStack.fromVariant(variant, amount);
    }

    public boolean matches(FluidStack stack) {
        return stack.isEmpty() ? isEmpty() : variant.equals(stack.toVariant());
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof FluidResource other && variant.equals(other.variant));
    }

    @Override
    public int hashCode() {
        return variant.hashCode();
    }

    @Override
    public String toString() {
        return variant.toString();
    }
}
