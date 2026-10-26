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
import org.cyclops.evilcraft.client.gui.container.ContainerScreenExaltedCrafter;

/**
 * Config for {@link ContainerExaltedCrafter}.
 * @author rubensworks
 */
public class ContainerExaltedCrafterConfig extends GuiConfig<ContainerExaltedCrafter> {

    public ContainerExaltedCrafterConfig() {
        super(EvilCraft._instance,
                "exalted_crafter",
                eConfig -> new ContainerTypeData<>(ContainerExaltedCrafter::new, FeatureFlags.VANILLA_SET));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public <U extends Screen & MenuAccess<ContainerExaltedCrafter>> MenuScreens.ScreenConstructor<ContainerExaltedCrafter, U> getScreenFactory() {
        return new ScreenFactorySafe<>(ContainerScreenExaltedCrafter::new);
    }

}
