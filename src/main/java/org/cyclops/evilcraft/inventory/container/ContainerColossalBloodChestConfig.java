package org.cyclops.evilcraft.inventory.container;

import net.minecraft.world.flag.FeatureFlags;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfigCommon;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfigScreenFactoryProvider;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.cyclopscore.inventory.container.ContainerTypeData;
import org.cyclops.evilcraft.EvilCraft;

/**
 * Config for {@link ContainerColossalBloodChest}.
 * @author rubensworks
 */
public class ContainerColossalBloodChestConfig extends GuiConfigCommon<ContainerColossalBloodChest, IModBase> {

    public ContainerColossalBloodChestConfig() {
        // ContainerTypeData: the block opens it with extra data (IBlockGui), so on Fabric it must be an
        // extended menu type (Cyclops Core makes one from an IContainerFactoryCommon)
        super(EvilCraft._instance,
                "colossal_blood_chest",
                eConfig -> new ContainerTypeData<>((id, inventory, data) -> new ContainerColossalBloodChest(id, inventory), FeatureFlags.VANILLA_SET));
    }

    @Override
    public GuiConfigScreenFactoryProvider<ContainerColossalBloodChest> getScreenFactoryProvider() {
        return new ContainerColossalBloodChestConfigScreenFactoryProvider();
    }
}
