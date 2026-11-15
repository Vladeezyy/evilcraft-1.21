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
import org.cyclops.evilcraft.client.gui.container.ContainerScreenBloodInfuser;

/**
 * Config for {@link ContainerBloodInfuser}.
 * @author rubensworks
 */
public class ContainerBloodInfuserConfig extends GuiConfig<ContainerBloodInfuser> {

    public ContainerBloodInfuserConfig() {
        // ContainerTypeData: the block opens it with extra data (IBlockGui), so on Fabric it must be an
        // extended menu type (Cyclops Core makes one from an IContainerFactoryCommon)
        super(EvilCraft._instance,
                "blood_infuser",
                eConfig -> new ContainerTypeData<>((id, inventory, data) -> new ContainerBloodInfuser(id, inventory), FeatureFlags.VANILLA_SET));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public <U extends Screen & MenuAccess<ContainerBloodInfuser>> MenuScreens.ScreenConstructor<ContainerBloodInfuser, U> getScreenFactory() {
        return new ScreenFactorySafe<>(ContainerScreenBloodInfuser::new);
    }

}
