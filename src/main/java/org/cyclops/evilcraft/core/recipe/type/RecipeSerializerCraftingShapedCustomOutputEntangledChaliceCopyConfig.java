package org.cyclops.evilcraft.core.recipe.type;

import net.minecraft.world.item.ItemStackTemplate;
import org.cyclops.evilcraft.fabric.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.cyclops.cyclopscore.config.extendedconfig.RecipeConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.evilcraft.fabric.cyclopscore.recipe.type.RecipeCraftingShapedCustomOutput;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.RegistryEntries;
import org.cyclops.evilcraft.item.ItemEntangledChalice;

/**
 * Config for vengeance pickaxe recipes.
 * @author rubensworks
 */
public class RecipeSerializerCraftingShapedCustomOutputEntangledChaliceCopyConfig extends RecipeConfigCommon<RecipeCraftingShapedCustomOutput, IModBase> {

    public RecipeSerializerCraftingShapedCustomOutputEntangledChaliceCopyConfig() {
        super(EvilCraft._instance,
                "crafting_shaped_custom_output_entangled_chalice_copy",
                eConfig -> new RecipeCraftingShapedCustomOutput.Serializer(() -> new ItemStackTemplate(RegistryEntries.ITEM_ENTANGLED_CHALICE.get(), 2), (inventory, staticOutput) -> {
                    ItemAccess newStack = ItemAccess.forStack(staticOutput.copy());
                    String tankID = ((ItemEntangledChalice.FluidHandler) Capabilities.Fluid.ITEM.getCapability(inventory.getItem(4), ItemAccess.forStack(inventory.getItem(4)))).getTankID();
                    try (var tx = Transaction.openRoot()) {
                        ((ItemEntangledChalice.FluidHandler) Capabilities.Fluid.ITEM.getCapability(newStack, null)).setTankID(tankID, tx);
                        tx.commit();
                    }
                    return newStack.getResource().toStack(2);
                }).getRecipeSerializer());
    }

}
