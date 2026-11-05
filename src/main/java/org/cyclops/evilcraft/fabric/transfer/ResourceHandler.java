package org.cyclops.evilcraft.fabric.transfer;

/**
 * An indexed container of resources, with the names of NeoForge's transfer API.
 * Fluid amounts are millibuckets; {@link ResourceHandlerStorage} converts them to Fabric's droplets.
 */
public interface ResourceHandler<T extends Resource> {

    int size();

    T getResource(int index);

    long getAmountAsLong(int index);

    default int getAmountAsInt(int index) {
        return (int) Math.min(Integer.MAX_VALUE, getAmountAsLong(index));
    }

    long getCapacityAsLong(int index, T resource);

    default int getCapacityAsInt(int index, T resource) {
        return (int) Math.min(Integer.MAX_VALUE, getCapacityAsLong(index, resource));
    }

    boolean isValid(int index, T resource);

    int insert(int index, T resource, int amount, TransactionContext transaction);

    default int insert(T resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        int inserted = 0;
        int size = size();
        for (int index = 0; index < size && inserted < amount; index++) {
            inserted += insert(index, resource, amount - inserted, transaction);
        }
        return inserted;
    }

    int extract(int index, T resource, int amount, TransactionContext transaction);

    default int extract(T resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        int extracted = 0;
        int size = size();
        for (int index = 0; index < size && extracted < amount; index++) {
            extracted += extract(index, resource, amount - extracted, transaction);
        }
        return extracted;
    }

    @SuppressWarnings("unchecked")
    static <T extends Resource> Class<ResourceHandler<T>> asClass() {
        return (Class<ResourceHandler<T>>) (Class<?>) ResourceHandler.class;
    }
}
