package org.cyclops.evilcraft.fabric.cyclopscore.inventory;

import org.cyclops.cyclopscore.inventory.ItemLocation;


import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.cyclops.evilcraft.fabric.transfer.ItemAccess;
import org.cyclops.evilcraft.fabric.transfer.ItemResource;
import org.cyclops.evilcraft.fabric.transfer.TransactionContext;

/**
 * An item access backed by an item location.
 * @author rubensworks
 */
public class ItemAccessItemLocation implements ItemAccess {

    private final Player player;
    private final ItemLocation itemLocation;

    public ItemAccessItemLocation(Player player, ItemLocation itemLocation) {
        this.player = player;
        this.itemLocation = itemLocation;
    }

    @Override
    public ItemResource getResource() {
        return ItemResource.of(itemLocation.getItemStack(player));
    }

    @Override
    public int getAmount() {
        return itemLocation.getItemStack(player).getCount();
    }

    @Override
    public int insert(ItemResource itemResource, int amount, TransactionContext transactionContext) {
        itemLocation.setItemStack(player, itemResource.toStack(amount));
        return amount;
    }

    @Override
    public int extract(ItemResource itemResource, int amount, TransactionContext transactionContext) {
        itemLocation.setItemStack(player, ItemStack.EMPTY);
        return amount;
    }
}
