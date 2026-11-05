package org.cyclops.evilcraft.fabric.event;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

/**
 * Adds entries to a creative mode tab when it is built.
 */
public class BuildCreativeModeTabContentsEvent {

    private final CreativeModeTab tab;
    private final FabricCreativeModeTabOutput entries;

    public BuildCreativeModeTabContentsEvent(CreativeModeTab tab, FabricCreativeModeTabOutput entries) {
        this.tab = tab;
        this.entries = entries;
    }

    public static void addListener(Consumer<BuildCreativeModeTabContentsEvent> listener) {
        CreativeModeTabEvents.MODIFY_OUTPUT_ALL.register((tab, entries) -> listener.accept(new BuildCreativeModeTabContentsEvent(tab, entries)));
    }

    public CreativeModeTab getTab() {
        return tab;
    }

    public CreativeModeTab.ItemDisplayParameters getParameters() {
        return entries.getContext();
    }

    public void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
        entries.accept(stack, visibility);
    }

    public void accept(ItemStack stack) {
        entries.accept(stack);
    }

    public void accept(ItemLike item) {
        entries.accept(item);
    }

    public void acceptAll(java.util.Collection<ItemStack> stacks) {
        entries.acceptAll(stacks);
    }
}
