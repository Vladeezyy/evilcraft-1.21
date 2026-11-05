package org.cyclops.evilcraft.item;

import org.cyclops.evilcraft.fabric.cyclopscore.helper.IModHelpersNeoForge;
import net.minecraft.world.item.Items;
import org.cyclops.evilcraft.fabric.capabilities.Capabilities;
import org.cyclops.evilcraft.fabric.capabilities.RegisterCapabilitiesEvent;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.cyclops.evilcraft.fabric.transfer.ItemResource;
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
        RegisterCapabilitiesEvent.addListener(this::registerCapability);
    }

    protected void registerCapability(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.Fluid.ITEM, (stack, context) -> new ResourceHandlerFluidSwapEmpty(
                context,
                new FluidStack(RegistryEntries.FLUID_POISON, IModHelpersNeoForge.get().getFluidHelpers().getBucketVolume()),
                ItemResource.of(getInstance()),
                ItemResource.of(Items.GLASS_BOTTLE)
        ), getInstance());
    }
}
