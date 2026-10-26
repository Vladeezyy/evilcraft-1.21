package org.cyclops.evilcraft.fabric.cyclopscore.config.configurabletypeaction;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.cyclops.cyclopscore.config.StreamCodecConsumerHack;
import org.cyclops.cyclopscore.config.configurabletypeaction.ConfigurableTypeActionRegistry;
import org.cyclops.cyclopscore.inventory.container.IContainerFactoryCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.GuiConfig;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

/**
 * Registers menu types (menus that receive extra data become Fabric extended menu types) and, on the client, their
 * screens.
 * @author rubensworks
 */
public class GuiActionFabric<T extends AbstractContainerMenu> extends ConfigurableTypeActionRegistry<GuiConfig<T>, MenuType<T>, ModBase<?>> {

    @Override
    public void onRegisterModInit(GuiConfig<T> eConfig) {
        super.onRegisterModInit(eConfig);
        eConfig.addInstanceTransformer(this::transformMenuType);
    }

    @Override
    public void onRegisterForgeFilled(GuiConfig<T> eConfig) {
        register(eConfig, () -> {
            eConfig.onForgeRegistered();
            if (eConfig.getMod().getModHelpers().getMinecraftHelpers().isClientSide()) {
                eConfig.onRegisterMenuScreens();
            }
            return null;
        });
    }

    protected MenuType<T> transformMenuType(MenuType<T> menuType) {
        if (menuType.constructor instanceof IContainerFactoryCommon<T> containerFactoryCommon) {
            // Menus that are opened with a FriendlyByteBuf of extra data (see MinecraftHelpersFabric#openMenu)
            StreamCodecConsumerHack<? super RegistryFriendlyByteBuf> packetCodec = new StreamCodecConsumerHack<>();
            return new ExtendedScreenHandlerType<>((syncId, inventory, voidData) -> {
                FriendlyByteBuf buffer = packetCodec.getAndResetBuffer();
                T screen = containerFactoryCommon.create(syncId, inventory, buffer);
                buffer.release();
                return screen;
            }, packetCodec);
        }
        return menuType;
    }
}
