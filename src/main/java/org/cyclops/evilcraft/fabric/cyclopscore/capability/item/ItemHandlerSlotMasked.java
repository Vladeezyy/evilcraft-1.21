package org.cyclops.evilcraft.fabric.cyclopscore.capability.item;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.world.Container;

import java.util.ArrayList;
import java.util.List;

/**
 * An item storage of some slots of an inventory.
 * @author rubensworks
 */
public class ItemHandlerSlotMasked extends CombinedStorage<ItemVariant, SingleSlotStorage<ItemVariant>> {

    public ItemHandlerSlotMasked(Container inventory, int... slots) {
        super(slots(inventory, slots));
    }

    private static List<SingleSlotStorage<ItemVariant>> slots(Container inventory, int[] slots) {
        InventoryStorage storage = InventoryStorage.of(inventory, null);
        List<SingleSlotStorage<ItemVariant>> list = new ArrayList<>(slots.length);
        for (int slot : slots) {
            list.add(storage.getSlot(slot));
        }
        return list;
    }

}
