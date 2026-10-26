package org.cyclops.evilcraft.block;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.evilcraft.fabric.capabilities.Capabilities;
import org.cyclops.evilcraft.fabric.capabilities.RegisterCapabilitiesEvent;
import org.cyclops.evilcraft.fabric.event.BuildCreativeModeTabContentsEvent;
import org.cyclops.cyclopscore.config.ConfigurablePropertyCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.BlockConfig;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.blockentity.BlockEntityEntangledChalice;
import org.cyclops.evilcraft.client.render.blockentity.RenderItemStackBlockEntityEntangledChalice;
import org.cyclops.evilcraft.item.ItemEntangledChalice;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * Config for the {@link BlockEntangledChalice}.
 * @author rubensworks
 *
 */
public class BlockEntangledChaliceConfig extends BlockConfig {

    @ConfigurablePropertyCommon(category = "machine", comment = "If the fluid should be rendered statically. Fluids won't be shown fluently, but more efficiently.", requiresMcRestart = true)
    public static boolean staticBlockRendering = false;

    public BlockEntangledChaliceConfig() {
        super(
                EvilCraft._instance,
                "entangled_chalice",
                eConfig -> new BlockEntangledChalice(Block.Properties.of()
                        .requiresCorrectToolForDrops()
                        .strength(2.5F)
                        .sound(SoundType.STONE)),
                (eConfig, block) -> new ItemEntangledChalice(block, (new Item.Properties())
                        )
        );
        RegisterCapabilitiesEvent.addListener(this::registerCapability);
        BuildCreativeModeTabContentsEvent.addListener(this::fillCreativeTab);
    }

    protected void registerCapability(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, context) -> new ItemEntangledChalice.FluidHandler(stack, BlockEntityEntangledChalice.BASE_CAPACITY), getInstance());
    }

    @Override
    protected Collection<ItemStack> defaultCreativeTabEntries() {
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
        ((BlockEntangledChalice) getInstance()).fillItemCategory(list);
        return list;
    }

    @Override
    public void onForgeRegistered() {
        super.onForgeRegistered();
        if (getMod().getModHelpers().getMinecraftHelpers().isClientSide()) {
            RenderItemStackBlockEntityEntangledChalice.ItemRenderProperties.register(getItemInstance());
        }
    }

}
