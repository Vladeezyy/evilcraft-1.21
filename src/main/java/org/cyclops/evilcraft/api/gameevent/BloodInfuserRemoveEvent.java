package org.cyclops.evilcraft.api.gameevent;

import org.cyclops.evilcraft.fabric.event.ListenerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Event when a player removes a crafted item from a slot.
 * @author rubensworks
 */
public class BloodInfuserRemoveEvent {

    public static final ListenerList<BloodInfuserRemoveEvent> LISTENERS = new ListenerList<>();

    public final Player player;
    public final ItemStack output;

    /**
     * Make a new instance.
     * @param player The player removing the item.
     * @param output The item that was picked up from the output slot.
     */
    public BloodInfuserRemoveEvent(Player player, ItemStack output) {
        this.player = player;
        this.output = output;
    }

}
