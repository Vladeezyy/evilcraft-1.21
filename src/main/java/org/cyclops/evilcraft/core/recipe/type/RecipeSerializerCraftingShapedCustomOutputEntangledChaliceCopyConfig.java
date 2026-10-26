package org.cyclops.evilcraft.core.recipe.type;

import net.minecraft.world.item.ItemStack;
import org.cyclops.evilcraft.fabric.fluids.FluidUtil;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.RecipeConfig;
import org.cyclops.evilcraft.fabric.cyclopscore.recipe.type.RecipeCraftingShapedCustomOutput;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.RegistryEntries;
import org.cyclops.evilcraft.item.ItemEntangledChalice;

/**
 * Config for vengeance pickaxe recipes.
 * @author rubensworks
 */
public class RecipeSerializerCraftingShapedCustomOutputEntangledChaliceCopyConfig extends RecipeConfig<RecipeCraftingShapedCustomOutput> {

    public RecipeSerializerCraftingShapedCustomOutputEntangledChaliceCopyConfig() {
        super(EvilCraft._instance,
                "crafting_shaped_custom_output_entangled_chalice_copy",
                eConfig -> new RecipeCraftingShapedCustomOutput.Serializer(() -> new ItemStack(RegistryEntries.ITEM_ENTANGLED_CHALICE, 2), (inventory, staticOutput) -> {
                    ItemStack newStack = staticOutput.copy();
                    String tankID = ((ItemEntangledChalice.FluidHandler) FluidUtil.getFluidHandler(inventory.getItem(4)).orElse(null)).getTankID();
                    ((ItemEntangledChalice.FluidHandler) FluidUtil.getFluidHandler(newStack).orElse(null)).setTankID(tankID);
                    newStack.setCount(2);
                    return newStack;
                }));
    }

}
