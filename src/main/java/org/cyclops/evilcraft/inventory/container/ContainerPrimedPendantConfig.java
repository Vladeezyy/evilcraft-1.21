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
import org.cyclops.evilcraft.client.gui.container.ContainerScreenPrimedPendant;

/**
 * Config for {@link ContainerPrimedPendant}.
 * @author rubensworks
 */
public class ContainerPrimedPendantConfig extends GuiConfig<ContainerPrimedPendant> {

    public ContainerPrimedPendantConfig() {
        super(EvilCraft._instance,
                "primed_pendant",
                eConfig -> new ContainerTypeData<>(ContainerPrimedPendant::new, FeatureFlags.VANILLA_SET));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public <U extends Screen & MenuAccess<ContainerPrimedPendant>> MenuScreens.ScreenConstructor<ContainerPrimedPendant, U> getScreenFactory() {
        return new ScreenFactorySafe<>(ContainerScreenPrimedPendant::new);
    }

}
