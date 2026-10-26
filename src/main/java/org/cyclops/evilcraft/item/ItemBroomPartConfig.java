package org.cyclops.evilcraft.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.evilcraft.fabric.event.RegisterColorHandlersEvent;
import org.cyclops.evilcraft.fabric.event.BuildCreativeModeTabContentsEvent;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.evilcraft.fabric.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.evilcraft.EvilCraft;

import java.util.Collection;
import java.util.Collections;

/**
 * Config for the {@link ItemBroomPart}.
 * @author rubensworks
 *
 */
public class ItemBroomPartConfig extends ItemConfig {

    public ItemBroomPartConfig() {
        super(
            EvilCraft._instance,
            "broom_part",
                eConfig -> new ItemBroomPart(new Item.Properties()
                        )
        );
        BuildCreativeModeTabContentsEvent.addListener(this::fillCreativeTab);
        if (MinecraftHelpers.isClientSide()) {
            RegisterColorHandlersEvent.Item.addListener(this::registerColors);
        }
    }

    @Environment(EnvType.CLIENT)
    public void registerColors(RegisterColorHandlersEvent.Item event) {
        event.register(new ItemBroomPart.ItemColor(), getInstance());
    }

    @Override
    protected Collection<ItemStack> getDefaultCreativeTabEntries() {
        // Register items dynamically into tab, because when this is called, capabilities are not initialized yet.
        return Collections.emptyList();
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
        ((ItemBroomPart) getInstance()).fillItemCategory(list);
        return list;
    }

}
