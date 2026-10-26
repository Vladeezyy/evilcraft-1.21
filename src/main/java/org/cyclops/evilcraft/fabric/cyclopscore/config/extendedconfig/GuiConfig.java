package org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.cyclops.cyclopscore.config.ConfigurableTypeCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.config.ConfigurableTypes;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

import java.util.function.Function;

/**
 * Config for menu types and their screens.
 * @author rubensworks
 */
public abstract class GuiConfig<T extends AbstractContainerMenu> extends ExtendedConfigForge<GuiConfig<T>, MenuType<T>> {

    public GuiConfig(ModBase<?> mod, String namedId, Function<GuiConfig<T>, ? extends MenuType<T>> elementConstructor) {
        super(mod, namedId, elementConstructor);
    }

    @Override
    public String getTranslationKey() {
        return "gui." + getMod().getModId() + "." + getNamedId();
    }

    // Needed for config gui
    @Override
    public String getFullTranslationKey() {
        return getTranslationKey();
    }

    @Override
    public ConfigurableTypeCommon getConfigurableType() {
        return ConfigurableTypes.GUI;
    }

    @Override
    public Registry<? super MenuType<T>> getRegistry() {
        return BuiltInRegistries.MENU;
    }

    @Environment(EnvType.CLIENT)
    public abstract <U extends Screen & MenuAccess<T>> MenuScreens.ScreenConstructor<T, U> getScreenFactory();

    @Environment(EnvType.CLIENT)
    public void onRegisterMenuScreens() {
        MenuScreens.register(getInstance(), getScreenFactory());
    }
}
