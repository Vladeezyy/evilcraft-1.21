package org.cyclops.evilcraft.fabric.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.function.Consumer;

/**
 * When the tooltip of an item stack is built (client only: only call {@link #addListener} on the client).
 */
@Environment(EnvType.CLIENT)
public class ItemTooltipEvent {

    private final ItemStack itemStack;
    private final Item.TooltipContext context;
    private final TooltipFlag flags;
    private final List<Component> toolTip;

    public ItemTooltipEvent(ItemStack itemStack, Item.TooltipContext context, TooltipFlag flags, List<Component> toolTip) {
        this.itemStack = itemStack;
        this.context = context;
        this.flags = flags;
        this.toolTip = toolTip;
    }

    public static void addListener(Consumer<ItemTooltipEvent> listener) {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> listener.accept(new ItemTooltipEvent(stack, context, type, lines)));
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Item.TooltipContext getContext() {
        return context;
    }

    public TooltipFlag getFlags() {
        return flags;
    }

    public List<Component> getToolTip() {
        return toolTip;
    }
}
