package org.cyclops.evilcraft.fabric.cyclopscore.config.configurabletypeaction;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.material.Fluid;
import org.cyclops.cyclopscore.config.configurabletypeaction.ConfigurableTypeActionCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.FluidConfig;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;
import org.cyclops.evilcraft.fabric.fluids.BaseFlowingFluid;
import org.cyclops.evilcraft.fabric.fluids.FluidType;
import org.cyclops.evilcraft.fabric.fluids.SoundActions;

import java.util.Optional;

/**
 * Registers the source and flowing fluid of a fluid config, their attributes for Fabric's transfer API and, on the
 * client, their rendering.
 * @author rubensworks
 */
public class FluidActionFabric extends ConfigurableTypeActionCommon<FluidConfig, BaseFlowingFluid.Properties, ModBase<?>> {

    @Override
    public void onRegisterForgeFilled(FluidConfig config) {
        BaseFlowingFluid.Properties properties = config.getInstance();
        Fluid still = Registry.register(BuiltInRegistries.FLUID,
                ResourceLocation.fromNamespaceAndPath(config.getMod().getModId(), config.getNamedId()), properties.getStill().get());
        Fluid flowing = Registry.register(BuiltInRegistries.FLUID,
                ResourceLocation.fromNamespaceAndPath(config.getMod().getModId(), config.getNamedId() + "_flowing"), properties.getFlowing().get());
        FluidType fluidType = properties.getFluidType().get();
        FluidVariantAttributeHandler handler = new FluidVariantAttributeHandler() {
            @Override
            public Component getName(FluidVariant fluidVariant) {
                return Component.translatable(fluidType.getDescriptionId());
            }

            @Override
            public Optional<SoundEvent> getFillSound(FluidVariant variant) {
                return Optional.ofNullable(fluidType.getSound(SoundActions.BUCKET_FILL));
            }

            @Override
            public Optional<SoundEvent> getEmptySound(FluidVariant variant) {
                return Optional.ofNullable(fluidType.getSound(SoundActions.BUCKET_EMPTY));
            }

            @Override
            public int getLuminance(FluidVariant variant) {
                return fluidType.getLightLevel();
            }

            @Override
            public int getTemperature(FluidVariant variant) {
                return fluidType.getTemperature();
            }

            @Override
            public int getViscosity(FluidVariant variant, @org.jetbrains.annotations.Nullable net.minecraft.world.level.Level world) {
                return fluidType.getViscosity();
            }
        };
        FluidVariantAttributes.register(still, handler);
        FluidVariantAttributes.register(flowing, handler);
        if (config.getMod().getModHelpers().getMinecraftHelpers().isClientSide() && properties instanceof FluidConfig.TexturedProperties textured) {
            registerClient(still, flowing, textured);
        }
        config.onForgeRegistered();
    }

    @Environment(EnvType.CLIENT)
    protected static void registerClient(Fluid still, Fluid flowing, FluidConfig.TexturedProperties properties) {
        FluidRenderHandlerRegistry.INSTANCE.register(still, flowing,
                new SimpleFluidRenderHandler(properties.getStillTexture(), properties.getFlowTexture()));
    }
}
