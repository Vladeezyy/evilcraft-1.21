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
import org.cyclops.evilcraft.client.gui.container.ContainerScreenOriginsOfDarkness;

/**
 * Config for {@link ContainerOriginsOfDarkness}.
 * @author rubensworks
 */
public class ContainerOriginsOfDarknessConfig extends GuiConfig<ContainerOriginsOfDarkness> {

    public ContainerOriginsOfDarknessConfig() {
        super(EvilCraft._instance,
                "origins_of_darkness",
                eConfig -> new ContainerTypeData<>(ContainerOriginsOfDarkness::new, FeatureFlags.VANILLA_SET));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public <U extends Screen & MenuAccess<ContainerOriginsOfDarkness>> MenuScreens.ScreenConstructor<ContainerOriginsOfDarkness, U> getScreenFactory() {
        return new ScreenFactorySafe<>(ContainerScreenOriginsOfDarkness::new);
    }

}
