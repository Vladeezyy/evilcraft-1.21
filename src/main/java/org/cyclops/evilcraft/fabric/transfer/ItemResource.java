package org.cyclops.evilcraft.fabric.transfer;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

/**
 * An item with components and without a count; a view on a Fabric {@link ItemVariant}, which are cached.
 */
public final class ItemResource implements Resource {

    public static final ItemResource EMPTY = new ItemResource(ItemVariant.blank());

    private final ItemVariant variant;

    private ItemResource(ItemVariant variant) {
        this.variant = variant;
    }

    public static ItemResource of(ItemVariant variant) {
        return variant.isBlank() ? EMPTY : new ItemResource(variant);
    }

    public static ItemResource of(ItemStack stack) {
        return stack.isEmpty() ? EMPTY : of(ItemVariant.of(stack));
    }

    public static ItemResource of(ItemLike item) {
        return of(ItemVariant.of(item));
    }

    public static ItemResource of(ItemLike item, DataComponentPatch patch) {
        return of(ItemVariant.of(item, patch));
    }

    public ItemVariant variant() {
        return variant;
    }

    public Item value() {
        return variant.getItem();
    }

    public Item getItem() {
        return variant.getItem();
    }

    public Holder<Item> typeHolder() {
        return variant.getRegistryEntry();
    }

    @Override
    public boolean isEmpty() {
        return variant.isBlank();
    }

    public boolean is(Item item) {
        return variant.isOf(item);
    }

    public boolean is(TagKey<Item> tag) {
        return variant.getRegistryEntry().is(tag);
    }

    public DataComponentMap getComponents() {
        return variant.getComponentMap();
    }

    public DataComponentPatch getComponentsPatch() {
        return variant.getComponents();
    }

    public <D> D get(DataComponentType<? extends D> type) {
        return variant.getComponentMap().get(type);
    }

    public <D> D getOrDefault(DataComponentType<? extends D> type, D defaultValue) {
        return variant.getComponentMap().getOrDefault(type, defaultValue);
    }

    public boolean has(DataComponentType<?> type) {
        return variant.getComponentMap().has(type);
    }

    public <D> ItemResource with(DataComponentType<D> type, D data) {
        return of(variant.withComponentChanges(DataComponentPatch.builder().set(type, data).build()));
    }

    public <D> ItemResource with(Supplier<? extends DataComponentType<D>> type, D data) {
        return with(type.get(), data);
    }

    public ItemResource without(DataComponentType<?> type) {
        return of(variant.withComponentChanges(DataComponentPatch.builder().remove(type).build()));
    }

    public ItemStack toStack(int count) {
        return isEmpty() ? ItemStack.EMPTY : variant.toStack(count);
    }

    public ItemStack toStack() {
        return toStack(1);
    }

    public int getMaxStackSize() {
        return variant.getComponentMap().getOrDefault(net.minecraft.core.component.DataComponents.MAX_STACK_SIZE, 1);
    }

    public boolean matches(ItemStack stack) {
        return stack.isEmpty() ? isEmpty() : variant.matches(stack);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof ItemResource other && variant.equals(other.variant));
    }

    @Override
    public int hashCode() {
        return variant.hashCode();
    }

    @Override
    public String toString() {
        return variant.toString();
    }
}
