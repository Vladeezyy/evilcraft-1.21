package org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import org.cyclops.cyclopscore.config.ConfigurableTypeCommon;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigCommon;
import org.cyclops.cyclopscore.datastructure.Wrapper;
import org.cyclops.evilcraft.fabric.cyclopscore.config.ConfigurableTypes;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBaseNeoForge;
import org.cyclops.evilcraft.fabric.fluids.BaseFlowingFluid;
import org.cyclops.evilcraft.fabric.fluids.FluidType;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Config for fluids (a source and a flowing fluid).
 * @author rubensworks
 */
public abstract class FluidConfigNeoForge extends ExtendedConfigCommon<FluidConfigNeoForge, BaseFlowingFluid.Properties, ModBaseNeoForge<?>> {

    private Fluid sourceFluid;
    private Fluid flowingFluid;

    public FluidConfigNeoForge(ModBaseNeoForge<?> mod, String namedId, Function<FluidConfigNeoForge, BaseFlowingFluid.Properties> elementConstructor) {
        super(mod, namedId, elementConstructor);
    }

    public Fluid getSourceFluid() {
        return sourceFluid;
    }

    public Fluid getFlowingFluid() {
        return flowingFluid;
    }

    protected static BaseFlowingFluid.Properties getDefaultFluidProperties(FluidConfigNeoForge config, Consumer<FluidType.Properties> fluidAttributesConsumer) {
        FluidType.Properties fluidAttributes = FluidType.Properties.create();
        fluidAttributesConsumer.accept(fluidAttributes);
        FluidType fluidType = new FluidType(fluidAttributes);

        Wrapper<BaseFlowingFluid.Properties> properties = new Wrapper<>();
        final Wrapper<Fluid> source = new Wrapper<>();
        final Wrapper<Fluid> flowing = new Wrapper<>();
        String modId = config.getMod().getModId();
        properties.set(new TexturedProperties(
                () -> fluidType,
                () -> {
                    if (source.get() == null) {
                        source.set(new BaseFlowingFluid.Source(properties.get()));
                        config.sourceFluid = source.get();
                    }
                    return source.get();
                },
                () -> {
                    if (flowing.get() == null) {
                        flowing.set(new BaseFlowingFluid.Flowing(properties.get()));
                        config.flowingFluid = flowing.get();
                    }
                    return flowing.get();
                },
                Identifier.fromNamespaceAndPath(modId, "block/" + config.getNamedId() + "_still"),
                Identifier.fromNamespaceAndPath(modId, "block/" + config.getNamedId() + "_flow")
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

    /**
     * Fluid properties with the textures for rendering, which NeoForge has in its client fluid extensions.
     */
    public static class TexturedProperties extends BaseFlowingFluid.Properties {
        private final Identifier stillTexture;
        private final Identifier flowTexture;

        public TexturedProperties(Supplier<FluidType> fluidType, Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing,
                                  Identifier stillTexture, Identifier flowTexture) {
            super(fluidType, still, flowing);
            this.stillTexture = stillTexture;
            this.flowTexture = flowTexture;
        }

        public Identifier getStillTexture() {
            return stillTexture;
        }

        public Identifier getFlowTexture() {
            return flowTexture;
        }
    }

}
