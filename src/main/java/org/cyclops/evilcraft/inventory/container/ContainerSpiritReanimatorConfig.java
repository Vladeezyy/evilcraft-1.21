package org.cyclops.evilcraft.inventory.container;

import net.minecraft.world.flag.FeatureFlags;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfigCommon;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfigScreenFactoryProvider;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.cyclopscore.inventory.container.ContainerTypeData;
import org.cyclops.evilcraft.EvilCraft;

/**
 * Config for {@link ContainerSpiritReanimator}.
 * @author rubensworks
 */
public class ContainerSpiritReanimatorConfig extends GuiConfigCommon<ContainerSpiritReanimator, IModBase> {

    public ContainerSpiritReanimatorConfig() {
        // ContainerTypeData: the block opens it with extra data (IBlockGui), so on Fabric it must be an
        // extended menu type (Cyclops Core makes one from an IContainerFactoryCommon)
        super(EvilCraft._instance,
                "spirit_reanimator",
                eConfig -> new ContainerTypeData<>((id, inventory, data) -> new ContainerSpiritReanimator(id, inventory), FeatureFlags.VANILLA_SET));
    }

    @Override
    public GuiConfigScreenFactoryProvider<ContainerSpiritReanimator> getScreenFactoryProvider() {
        return new ContainerSpiritReanimatorConfigScreenFactoryProvider();
    }
}
