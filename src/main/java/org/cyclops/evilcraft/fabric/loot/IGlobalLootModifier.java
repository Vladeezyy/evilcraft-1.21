package org.cyclops.evilcraft.fabric.loot;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

/**
 * Changes the generated loot of loot tables (the global loot modifiers of NeoForge, loaded from
 * data/[namespace]/loot_modifiers).
 */
public interface IGlobalLootModifier {

    ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context);

    MapCodec<? extends IGlobalLootModifier> codec();
}
