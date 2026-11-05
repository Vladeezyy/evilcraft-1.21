package org.cyclops.evilcraft.fabric.transfer;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.cyclops.evilcraft.fabric.capabilities.ItemCapability;

import javax.annotation.Nullable;

/**
 * Where an item that is being used lives: changing it (a filled bucket, a changed component) goes through here.
 */
public interface ItemAccess {

    /**
     * The hand of a player; what does not fit goes to the inventory. Creative players keep their items.
     */
    static ItemAccess forPlayerInteraction(Player player, InteractionHand hand) {
        return new OfContext(ContainerItemContext.forPlayerInteraction(player, hand));
    }

    static ItemAccess forHandlerIndex(ResourceHandler<ItemResource> handler, int index) {
        return new OfHandler(handler, index);
    }

    /**
     * Changes the stack directly: its count and components, never its item.
     */
    static ItemAccess forStack(ItemStack stack) {
        return new OfStack(stack);
    }

    static ItemAccess of(ContainerItemContext context) {
        return new OfContext(context);
    }

    default ItemAccess oneByOne() {
        return new OneByOne(this);
    }

    @Nullable
    default <T> T getCapability(ItemCapability<T, ItemAccess> capability) {
        return capability.getCapability(getResource().toStack(), this);
    }

    ItemResource getResource();

    int getAmount();

    int insert(ItemResource resource, int amount, TransactionContext transaction);

    int extract(ItemResource resource, int amount, TransactionContext transaction);

    default int exchange(ItemResource newResource, int amount, @Nullable TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(newResource, amount);
        ItemResource current = getResource();
        TransferPreconditions.checkNonEmpty(current);
        try (Transaction nested = Transaction.open(transaction)) {
            int extracted = extract(current, amount, nested);
            if (extracted > 0 && insert(newResource, extracted, nested) == extracted) {
                nested.commit();
                return extracted;
            }
        }
        return 0;
    }

    /**
     * This access for Fabric's item lookups.
     */
    default ContainerItemContext toContext() {
        return ContainerItemContext.ofSingleSlot(new Slot(this));
    }

    final class OfContext implements ItemAccess {
        private final ContainerItemContext context;

        private OfContext(ContainerItemContext context) {
            this.context = context;
        }

        @Override
        public ItemResource getResource() {
            return ItemResource.of(context.getItemVariant());
        }

        @Override
        public int getAmount() {
            return (int) Math.min(Integer.MAX_VALUE, context.getAmount());
        }

        @Override
        public int insert(ItemResource resource, int amount, TransactionContext transaction) {
            return (int) context.insert(resource.variant(), amount, transaction.fabric());
        }

        @Override
        public int extract(ItemResource resource, int amount, TransactionContext transaction) {
            return (int) context.extract(resource.variant(), amount, transaction.fabric());
        }

        @Override
        public ContainerItemContext toContext() {
            return context;
        }
    }

    final class OfHandler implements ItemAccess {
        private final ResourceHandler<ItemResource> handler;
        private final int index;

        private OfHandler(ResourceHandler<ItemResource> handler, int index) {
            this.handler = handler;
            this.index = index;
        }

        @Override
        public ItemResource getResource() {
            return handler.getResource(index);
        }

        @Override
        public int getAmount() {
            return handler.getAmountAsInt(index);
        }

        @Override
        public int insert(ItemResource resource, int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            int inserted = handler.insert(index, resource, amount, transaction);
            return inserted < amount ? inserted + handler.insert(resource, amount - inserted, transaction) : inserted;
        }

        @Override
        public int extract(ItemResource resource, int amount, TransactionContext transaction) {
            return handler.extract(index, resource, amount, transaction);
        }
    }

    final class OfStack extends SnapshotJournal<OfStack.State> implements ItemAccess {
        record State(ItemResource resource, int amount) {
        }

        private final ItemStack stack;
        private final Item item;

        private OfStack(ItemStack stack) {
            this.stack = stack;
            this.item = stack.getItem();
        }

        @Override
        public ItemResource getResource() {
            return ItemResource.of(stack);
        }

        @Override
        public int getAmount() {
            return stack.getCount();
        }

        private void set(ItemResource resource, int amount) {
            stack.setCount(amount);
            if (amount > 0 && stack.getComponents() instanceof PatchedDataComponentMap components) {
                components.restorePatch(resource.getComponentsPatch());
            }
        }

        @Override
        public int insert(ItemResource resource, int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            if (amount == 0 || stack == ItemStack.EMPTY || !resource.is(item)
                    || (stack.getCount() > 0 && !resource.equals(getResource()))) {
                return 0;
            }
            updateSnapshots(transaction);
            set(resource, stack.getCount() + amount);
            return amount;
        }

        @Override
        public int extract(ItemResource resource, int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            if (amount == 0 || !resource.equals(getResource())) {
                return 0;
            }
            int extracted = Math.min(amount, stack.getCount());
            updateSnapshots(transaction);
            stack.setCount(stack.getCount() - extracted);
            return extracted;
        }

        @Override
        protected State createSnapshot() {
            return new State(getResource(), stack.getCount());
        }

        @Override
        protected void revertToSnapshot(State snapshot) {
            set(snapshot.resource(), snapshot.amount());
        }
    }

    final class OneByOne implements ItemAccess {
        private final ItemAccess delegate;

        private OneByOne(ItemAccess delegate) {
            this.delegate = delegate;
        }

        @Override
        public ItemResource getResource() {
            return delegate.getResource();
        }

        @Override
        public int getAmount() {
            return Math.min(1, delegate.getAmount());
        }

        @Override
        public int insert(ItemResource resource, int amount, TransactionContext transaction) {
            return delegate.insert(resource, amount, transaction);
        }

        @Override
        public int extract(ItemResource resource, int amount, TransactionContext transaction) {
            return delegate.extract(resource, Math.min(1, amount), transaction);
        }

        @Override
        public ItemAccess oneByOne() {
            return this;
        }
    }

    final class Slot implements SingleSlotStorage<ItemVariant> {
        private final ItemAccess access;

        private Slot(ItemAccess access) {
            this.access = access;
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount,
                           net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction) {
            return resource.isBlank() ? 0 : access.insert(ItemResource.of(resource),
                    (int) Math.min(Integer.MAX_VALUE, maxAmount), TransactionContext.wrap(transaction));
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount,
                            net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction) {
            return resource.isBlank() ? 0 : access.extract(ItemResource.of(resource),
                    (int) Math.min(Integer.MAX_VALUE, maxAmount), TransactionContext.wrap(transaction));
        }

        @Override
        public boolean isResourceBlank() {
            return access.getResource().isEmpty();
        }

        @Override
        public ItemVariant getResource() {
            return access.getResource().variant();
        }

        @Override
        public long getAmount() {
            return access.getAmount();
        }

        @Override
        public long getCapacity() {
            return Math.max(access.getAmount(), access.getResource().getMaxStackSize());
        }
    }
}
