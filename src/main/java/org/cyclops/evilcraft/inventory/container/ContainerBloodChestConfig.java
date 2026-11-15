package org.cyclops.evilcraft.inventory.container;

import net.minecraft.world.flag.FeatureFlags;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfigCommon;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfigScreenFactoryProvider;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.cyclopscore.inventory.container.ContainerTypeData;
import org.cyclops.evilcraft.EvilCraft;

/**
 * Config for {@link ContainerBloodChest}.
 * @author rubensworks
 */
public class ContainerBloodChestConfig extends GuiConfigCommon<ContainerBloodChest, IModBase> {

    public ContainerBloodChestConfig() {
        // ContainerTypeData: the block opens it with extra data (IBlockGui), so on Fabric it must be an
        // extended menu type (Cyclops Core makes one from an IContainerFactoryCommon)
        super(EvilCraft._instance,
                "blood_chest",
                eConfig -> new ContainerTypeData<>((id, inventory, data) -> new ContainerBloodChest(id, inventory), FeatureFlags.VANILLA_SET));
    }

    @Override
    public GuiConfigScreenFactoryProvider<ContainerBloodChest> getScreenFactoryProvider() {
        return new ContainerBloodChestConfigScreenFactoryProvider();
    }
}
