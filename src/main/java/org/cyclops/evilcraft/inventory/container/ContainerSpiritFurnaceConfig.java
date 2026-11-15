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
import org.cyclops.evilcraft.client.gui.container.ContainerScreenSpiritFurnace;

/**
 * Config for {@link ContainerSpiritFurnace}.
 * @author rubensworks
 */
public class ContainerSpiritFurnaceConfig extends GuiConfig<ContainerSpiritFurnace> {

    public ContainerSpiritFurnaceConfig() {
        // ContainerTypeData: the block opens it with extra data (IBlockGui), so on Fabric it must be an
        // extended menu type (Cyclops Core makes one from an IContainerFactoryCommon)
        super(EvilCraft._instance,
                "spirit_furnace",
                eConfig -> new ContainerTypeData<>((id, inventory, data) -> new ContainerSpiritFurnace(id, inventory), FeatureFlags.VANILLA_SET));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public <U extends Screen & MenuAccess<ContainerSpiritFurnace>> MenuScreens.ScreenConstructor<ContainerSpiritFurnace, U> getScreenFactory() {
        return new ScreenFactorySafe<>(ContainerScreenSpiritFurnace::new);
    }

}
