package org.cyclops.evilcraft.item;

import net.minecraft.world.item.CreativeModeTab;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.cyclops.evilcraft.fabric.event.BuildCreativeModeTabContentsEvent;
import org.cyclops.cyclopscore.config.ConfigurablePropertyCommon;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.evilcraft.EvilCraft;

import java.util.Collection;

/**
 * Config for the {@link ItemWeatherContainer}.
 * @author rubensworks
 *
 */
public class ItemWeatherContainerConfig extends ItemConfigCommon<IModBase> {

    @ConfigurablePropertyCommon(category = "general", comment = "If shapeless crafting of the higher tiers of weather containers should be enabled.", requiresMcRestart = true)
    public static boolean shapelessRecipes = true;

    public ItemWeatherContainerConfig() {
        super(
                EvilCraft._instance,
            "weather_container",
                (eConfig, properties) -> new ItemWeatherContainer(properties)
        );
        BuildCreativeModeTabContentsEvent.addListener(this::fillCreativeTab);
    }

    @Override
    public Collection<ItemStack> getDefaultCreativeTabEntries() {
        NonNullList<ItemStack> list = NonNullList.create();
        ((ItemWeatherContainer) getInstance()).fillItemCategory(list);
        return list;
    }

    protected void fillCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == EvilCraft._instance.getDefaultCreativeTab()) {
            for (ItemStack itemStack : dynamicCreativeTabEntries()) {
                event.accept(itemStack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
    }

    protected Collection<ItemStack> dynamicCreativeTabEntries() {
        NonNullList<ItemStack> list = NonNullList.create();
        ((ItemWeatherContainer) getInstance()).fillItemCategory(list);
        return list;
    }
}
