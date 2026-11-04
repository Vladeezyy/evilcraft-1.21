package org.cyclops.evilcraft.fabric.cyclopscore;



import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import org.cyclops.evilcraft.fabric.fluids.SimpleFluidContent;
import org.cyclops.evilcraft.fabric.registries.DeferredHolder;
import org.cyclops.cyclopscore.inventory.SimpleInventory;

/**
 * Referenced registry entries of Cyclops Core; the fluid content and inventory components, which Cyclops Core
 * only has on NeoForge, are registered by this mod.
 * @author rubensworks
 */
public class RegistryEntries {

    public static final DeferredHolder<ParticleType<?>, ParticleType<?>> PARTICLE_BLUR = DeferredHolder.create(Registries.PARTICLE_TYPE, Identifier.parse("cyclopscore:blur"));
    public static final DeferredHolder<ParticleType<?>, ParticleType<?>> PARTICLE_DROP_COLORED = DeferredHolder.create(Registries.PARTICLE_TYPE, Identifier.parse("cyclopscore:drop_colored"));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> COMPONENT_CAPACITY = DeferredHolder.create(Registries.DATA_COMPONENT_TYPE, Identifier.parse("cyclopscore:capacity"));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> COMPONENT_ENERGY_STORAGE = DeferredHolder.create(Registries.DATA_COMPONENT_TYPE, Identifier.parse("cyclopscore:energy_storage"));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> COMPONENT_FLUID_CONTENT = DeferredHolder.create(Registries.DATA_COMPONENT_TYPE, Identifier.parse("evilcraft:fluid_content"));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleInventory>> COMPONENT_INVENTORY = DeferredHolder.create(Registries.DATA_COMPONENT_TYPE, Identifier.parse("evilcraft:inventory"));

}
