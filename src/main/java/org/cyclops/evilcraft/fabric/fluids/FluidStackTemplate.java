package org.cyclops.evilcraft.fabric.fluids;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;

/**
 * An immutable, non-empty fluid with an amount, as written in recipes: {"id": ..., "amount": ..., "components": ...}.
 */
public record FluidStackTemplate(Holder<Fluid> fluid, int amount, DataComponentPatch components) {

    public static final MapCodec<FluidStackTemplate> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            FluidStack.FLUID_NON_EMPTY_CODEC.fieldOf("id").forGetter(FluidStackTemplate::fluid),
            ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter(FluidStackTemplate::amount),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(FluidStackTemplate::components)
    ).apply(i, FluidStackTemplate::new));

    public static final Codec<FluidStackTemplate> CODEC = MAP_CODEC.codec();

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidStackTemplate> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.FLUID), FluidStackTemplate::fluid,
            ByteBufCodecs.VAR_INT, FluidStackTemplate::amount,
            DataComponentPatch.STREAM_CODEC, FluidStackTemplate::components,
            FluidStackTemplate::new);

    public FluidStack create() {
        return new FluidStack(fluid, amount, components);
    }
}
