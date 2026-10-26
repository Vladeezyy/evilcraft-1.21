package org.cyclops.evilcraft.fabric.cyclopscore.config.configurabletypeaction;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.cyclops.cyclopscore.config.configurabletypeaction.ConfigurableTypeActionRegistry;
import org.cyclops.cyclopscore.item.ItemInformationProviderCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

/**
 * Registers items and adds them to the creative tab.
 * @author rubensworks
 */
public class ItemActionFabric extends ConfigurableTypeActionRegistry<ItemConfig, Item, ModBase<?>> {

    @Override
    public void onRegisterForgeFilled(ItemConfig eConfig) {
        register(eConfig, () -> {
            this.polish(eConfig);
            eConfig.onForgeRegistered();
            return null;
        });
    }

    protected void polish(ItemConfig config) {
        for (ItemStack itemStack : config.getDefaultCreativeTabEntriesPublic()) {
            config.getMod().registerDefaultCreativeTabEntry(itemStack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        ItemInformationProviderCommon.registerItem(config.getInstance());
    }
}
