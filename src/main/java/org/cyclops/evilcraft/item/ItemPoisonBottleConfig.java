package org.cyclops.evilcraft.item;

import net.minecraft.world.item.Items;
import org.cyclops.evilcraft.fabric.capabilities.Capabilities;
import org.cyclops.evilcraft.fabric.capabilities.RegisterCapabilitiesEvent;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.cyclops.evilcraft.fabric.cyclopscore.capability.fluid.ResourceHandlerFluidSwapEmpty;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfigCommon;
import org.cyclops.cyclopscore.helper.IModHelpers;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.RegistryEntries;

/**
 * Config for the {@link ItemPoisonBottle}.
 * @author rubensworks
 *
 */
public class ItemPoisonBottleConfig extends ItemConfigCommon<IModBase> {

    public ItemPoisonBottleConfig() {
        super(
                EvilCraft._instance,
            "poison_bottle",
                (eConfig, properties) -> new ItemPoisonBottle(properties
                        .stacksTo(1))
        );
        EvilCraft._instance.getModEventBus().addListener(this::registerCapability);
    }

    protected void registerCapability(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.Fluid.ITEM, (stack, context) -> new ResourceHandlerFluidSwapEmpty(
                context,
                new FluidStack(RegistryEntries.FLUID_POISON, IModHelpers.get().getFluidHelpers().getBucketVolume()),
                ItemResource.of(getInstance()),
                ItemResource.of(Items.GLASS_BOTTLE)
        ), getInstance());
    }
}
