package org.cyclops.evilcraft.inventory.container;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.cyclopscore.client.gui.ScreenFactorySafe;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.GuiConfig;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.client.gui.container.ContainerScreenColossalBloodChest;

/**
 * Config for {@link ContainerColossalBloodChest}.
 * @author rubensworks
 */
public class ContainerColossalBloodChestConfig extends GuiConfig<ContainerColossalBloodChest> {

    public ContainerColossalBloodChestConfig() {
        super(EvilCraft._instance,
                "colossal_blood_chest",
                eConfig -> new MenuType<>(ContainerColossalBloodChest::new, FeatureFlags.VANILLA_SET));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public <U extends Screen & MenuAccess<ContainerColossalBloodChest>> MenuScreens.ScreenConstructor<ContainerColossalBloodChest, U> getScreenFactory() {
        return new ScreenFactorySafe<>(ContainerScreenColossalBloodChest::new);
    }

}
