package org.cyclops.evilcraft.fabric.cyclopscore.capability.item;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.Container;
import java.util.ArrayList;
import java.util.List;

/**
 * An item storage of some slots of an inventory.
 * @author rubensworks
 */
public class ItemHandlerSlotMasked extends CombinedStorage<ItemVariant, SingleSlotStorage<ItemVariant>> {

    public ItemHandlerSlotMasked(Container inventory, int... slots) {
        super(slots(inventory, null, slots));
    }

    /**
     * @param onExtracted Called after each committed extraction from one of the slots, also through the slot views,
     *                    which is how hoppers and {@code StorageUtil.move} extract (an override of
     *                    {@link #extract} alone misses those).
     */
    public ItemHandlerSlotMasked(Container inventory, Runnable onExtracted, int... slots) {
        super(slots(inventory, onExtracted, slots));
    }

    private static List<SingleSlotStorage<ItemVariant>> slots(Container inventory, Runnable onExtracted, int[] slots) {
        InventoryStorage storage = InventoryStorage.of(inventory, null);
        List<SingleSlotStorage<ItemVariant>> list = new ArrayList<>(slots.length);
        for (int slot : slots) {
            list.add(onExtracted == null ? storage.getSlot(slot) : new NotifyingSlot(storage.getSlot(slot), onExtracted));
        }
        return list;
    }

    private record NotifyingSlot(SingleSlotStorage<ItemVariant> slot, Runnable onExtracted) implements SingleSlotStorage<ItemVariant> {

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            return slot.insert(resource, maxAmount, transaction);
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            long extracted = slot.extract(resource, maxAmount, transaction);
            if (extracted > 0) {
                transaction.addOuterCloseCallback(result -> {
                    if (result.wasCommitted()) {
                        onExtracted.run();
                    }
                });
            }
            return extracted;
        }

        @Override
        public boolean isResourceBlank() {
            return slot.isResourceBlank();
        }

        @Override
        public ItemVariant getResource() {
            return slot.getResource();
        }

        @Override
        public long getAmount() {
            return slot.getAmount();
        }

        @Override
        public long getCapacity() {
            return slot.getCapacity();
        }

        @Override
        public long getVersion() {
            return slot.getVersion();
        }
    }

}
