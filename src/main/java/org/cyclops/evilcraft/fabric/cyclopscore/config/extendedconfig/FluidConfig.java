package org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import org.cyclops.cyclopscore.config.ConfigurableTypeCommon;
import org.cyclops.cyclopscore.datastructure.Wrapper;
import org.cyclops.evilcraft.fabric.cyclopscore.config.ConfigurableTypes;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;
import org.cyclops.evilcraft.fabric.fluids.BaseFlowingFluid;
import org.cyclops.evilcraft.fabric.fluids.FluidType;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Config for fluids (a source and a flowing fluid).
 * @author rubensworks
 */
public abstract class FluidConfig extends ExtendedConfig<FluidConfig, BaseFlowingFluid.Properties> {

    private ResourceLocation stillTexture;
    private ResourceLocation flowTexture;

    public FluidConfig(ModBase<?> mod, String namedId, Function<FluidConfig, BaseFlowingFluid.Properties> elementConstructor) {
        super(mod, namedId, elementConstructor);
    }

    protected static BaseFlowingFluid.Properties getDefaultFluidProperties(ModBase<?> mod, String texturePrefixPath,
                                                                            Consumer<FluidType.Properties> fluidAttributesConsumer) {
        FluidType.Properties fluidAttributes = FluidType.Properties.create();
        fluidAttributesConsumer.accept(fluidAttributes);
        FluidType fluidType = new FluidType(fluidAttributes);

        Wrapper<BaseFlowingFluid.Properties> properties = new Wrapper<>();
        final Wrapper<Fluid> source = new Wrapper<>();
        final Wrapper<Fluid> flowing = new Wrapper<>();
        properties.set(new TexturedProperties(
                () -> fluidType,
                () -> {
                    if (source.get() == null) {
                        source.set(new BaseFlowingFluid.Source(properties.get()));
                    }
                    return source.get();
                },
                () -> {
                    if (flowing.get() == null) {
                        flowing.set(new BaseFlowingFluid.Flowing(properties.get()));
                    }
                    return flowing.get();
                },
                ResourceLocation.fromNamespaceAndPath(mod.getModId(), texturePrefixPath + "_still"),
                ResourceLocation.fromNamespaceAndPath(mod.getModId(), texturePrefixPath + "_flow")
        ));
        return properties.get();
    }

    @Override
    public String getTranslationKey() {
        return "block." + getMod().getModId() + ".block_" + getNamedId();
    }

    @Override
    public ConfigurableTypeCommon getConfigurableType() {
        return ConfigurableTypes.FLUID;
    }

    public ResourceLocation getIconLocationStill() {
        return ResourceLocation.fromNamespaceAndPath(getMod().getModId(), "blocks/" + getNamedId() + "_still");
    }

    public ResourceLocation getIconLocationFlow() {
        return ResourceLocation.fromNamespaceAndPath(getMod().getModId(), "blocks/" + getNamedId() + "_flow");
    }

    /**
     * Fluid properties with the textures for rendering.
     */
    public static class TexturedProperties extends BaseFlowingFluid.Properties {
        private final ResourceLocation stillTexture;
        private final ResourceLocation flowTexture;

        public TexturedProperties(java.util.function.Supplier<FluidType> fluidType, java.util.function.Supplier<? extends Fluid> still,
                                  java.util.function.Supplier<? extends Fluid> flowing, ResourceLocation stillTexture, ResourceLocation flowTexture) {
            super(fluidType, still, flowing);
            this.stillTexture = stillTexture;
            this.flowTexture = flowTexture;
        }

        public ResourceLocation getStillTexture() {
            return stillTexture;
        }

        public ResourceLocation getFlowTexture() {
            return flowTexture;
        }
    }

}
