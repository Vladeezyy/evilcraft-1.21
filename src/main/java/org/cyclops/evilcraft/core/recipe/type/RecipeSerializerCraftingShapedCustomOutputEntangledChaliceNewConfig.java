package org.cyclops.evilcraft.core.recipe.type;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.cyclops.evilcraft.fabric.capabilities.Capabilities;
import org.cyclops.evilcraft.fabric.transfer.ItemAccess;
import org.cyclops.evilcraft.fabric.transfer.Transaction;
import org.cyclops.cyclopscore.config.extendedconfig.RecipeConfigCommon;
import org.cyclops.cyclopscore.helper.IModHelpers;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.evilcraft.fabric.cyclopscore.recipe.type.RecipeCraftingShapedCustomOutput;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.RegistryEntries;
import org.cyclops.evilcraft.item.ItemEntangledChalice;

/**
 * Config for vengeance pickaxe recipes.
 * @author rubensworks
 */
public class RecipeSerializerCraftingShapedCustomOutputEntangledChaliceNewConfig extends RecipeConfigCommon<RecipeCraftingShapedCustomOutput, IModBase> {

    public RecipeSerializerCraftingShapedCustomOutputEntangledChaliceNewConfig() {
        super(EvilCraft._instance,
                "crafting_shaped_custom_output_entangled_chalice_new",
                eConfig -> new RecipeCraftingShapedCustomOutput.Serializer(() -> new ItemStackTemplate(RegistryEntries.ITEM_ENTANGLED_CHALICE.get(), 2), (inventory, staticOutput) -> {
                    ItemAccess newStack = ItemAccess.forStack(new ItemStack(RegistryEntries.ITEM_ENTANGLED_CHALICE));
                    ItemEntangledChalice.FluidHandler fluidHandler = (ItemEntangledChalice.FluidHandler) newStack.getCapability(Capabilities.Fluid.ITEM);
                    if (!IModHelpers.get().getMinecraftHelpers().isClientSideThread()) {
                        try (var tx = Transaction.openRoot()) {
                            fluidHandler.setNextTankID(tx);
                            tx.commit();
                        }
                    }
                    return newStack.getResource().toStack(2);
                }).getRecipeSerializer());
    }

}
