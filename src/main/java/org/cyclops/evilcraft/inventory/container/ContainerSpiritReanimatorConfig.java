package org.cyclops.evilcraft.inventory.container;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.flag.FeatureFlags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.cyclopscore.client.gui.ScreenFactorySafe;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.GuiConfig;
import org.cyclops.evilcraft.fabric.cyclopscore.inventory.container.ContainerTypeData;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.client.gui.container.ContainerScreenSpiritReanimator;

/**
 * Config for {@link ContainerSpiritReanimator}.
 * @author rubensworks
 */
public class ContainerSpiritReanimatorConfig extends GuiConfig<ContainerSpiritReanimator> {

    public ContainerSpiritReanimatorConfig() {
        // ContainerTypeData: the block opens it with extra data (IBlockGui), so on Fabric it must be an
        // extended menu type (Cyclops Core makes one from an IContainerFactoryCommon)
        super(EvilCraft._instance,
                "spirit_reanimator",
                eConfig -> new ContainerTypeData<>((id, inventory, data) -> new ContainerSpiritReanimator(id, inventory), FeatureFlags.VANILLA_SET));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public <U extends Screen & MenuAccess<ContainerSpiritReanimator>> MenuScreens.ScreenConstructor<ContainerSpiritReanimator, U> getScreenFactory() {
        return new ScreenFactorySafe<>(ContainerScreenSpiritReanimator::new);
    }

}
