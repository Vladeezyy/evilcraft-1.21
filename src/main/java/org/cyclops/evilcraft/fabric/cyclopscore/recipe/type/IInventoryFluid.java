package org.cyclops.evilcraft.fabric.cyclopscore.recipe.type;



import net.minecraft.world.item.crafting.RecipeInput;
import org.cyclops.evilcraft.fabric.transfer.ResourceHandler;
import org.cyclops.evilcraft.fabric.transfer.FluidResource;

/**
 * An inventory that can also contain fluids.
 * @author rubensworks
 */
public interface IInventoryFluid extends RecipeInput {

    public ResourceHandler<FluidResource> getFluidHandler();

}
