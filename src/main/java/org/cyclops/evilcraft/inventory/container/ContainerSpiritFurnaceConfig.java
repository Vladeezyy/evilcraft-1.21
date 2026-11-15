package org.cyclops.evilcraft.inventory.container;

import net.minecraft.world.flag.FeatureFlags;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfigCommon;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfigScreenFactoryProvider;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.cyclopscore.inventory.container.ContainerTypeData;
import org.cyclops.evilcraft.EvilCraft;

/**
 * Config for {@link ContainerSpiritFurnace}.
 * @author rubensworks
 */
public class ContainerSpiritFurnaceConfig extends GuiConfigCommon<ContainerSpiritFurnace, IModBase> {

    public ContainerSpiritFurnaceConfig() {
        // ContainerTypeData: the block opens it with extra data (IBlockGui), so on Fabric it must be an
        // extended menu type (Cyclops Core makes one from an IContainerFactoryCommon)
        super(EvilCraft._instance,
                "spirit_furnace",
                eConfig -> new ContainerTypeData<>((id, inventory, data) -> new ContainerSpiritFurnace(id, inventory), FeatureFlags.VANILLA_SET));
    }

    @Override
    public GuiConfigScreenFactoryProvider<ContainerSpiritFurnace> getScreenFactoryProvider() {
        return new ContainerSpiritFurnaceConfigScreenFactoryProvider();
    }
}
