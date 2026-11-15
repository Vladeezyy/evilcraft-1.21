package org.cyclops.evilcraft.inventory.container;

import net.minecraft.world.flag.FeatureFlags;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfigCommon;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfigScreenFactoryProvider;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.cyclopscore.inventory.container.ContainerTypeData;
import org.cyclops.evilcraft.EvilCraft;

/**
 * Config for {@link ContainerSanguinaryEnvironmentalAccumulator}.
 * @author rubensworks
 */
public class ContainerSanguinaryEnvironmentalAccumulatorConfig extends GuiConfigCommon<ContainerSanguinaryEnvironmentalAccumulator, IModBase> {

    public ContainerSanguinaryEnvironmentalAccumulatorConfig() {
        // ContainerTypeData: the block opens it with extra data (IBlockGui), so on Fabric it must be an
        // extended menu type (Cyclops Core makes one from an IContainerFactoryCommon)
        super(EvilCraft._instance,
                "sanguinary_environmental_accumulator",
                eConfig -> new ContainerTypeData<>((id, inventory, data) -> new ContainerSanguinaryEnvironmentalAccumulator(id, inventory), FeatureFlags.VANILLA_SET));
    }

    @Override
    public GuiConfigScreenFactoryProvider<ContainerSanguinaryEnvironmentalAccumulator> getScreenFactoryProvider() {
        return new ContainerSanguinaryEnvironmentalAccumulatorConfigScreenFactoryProvider();
    }
}
