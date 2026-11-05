package org.cyclops.evilcraft.fabric.cyclopscore.inventory.slot;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.cyclops.cyclopscore.inventory.slot.SlotExtended;

/**
 * Slot that is used for only accepting an item matching the given ingredient. Cyclops Core has it since 26.1.
 * @author rubensworks
 */
public class SlotSingleIngredient extends SlotExtended {

    private final Ingredient ingredient;

    public SlotSingleIngredient(Container inventory, int index, int x, int y, Ingredient ingredient) {
        super(inventory, index, x, y);
        this.ingredient = ingredient;
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return super.mayPlace(itemStack) && ingredient.test(itemStack);
    }

    public Ingredient getIngredientAllowed() {
        return ingredient;
    }
}
