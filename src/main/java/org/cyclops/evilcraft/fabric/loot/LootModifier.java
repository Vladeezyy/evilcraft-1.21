package org.cyclops.evilcraft.fabric.loot;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;

/**
 * A loot modifier that only applies when its loot condition passes.
 */
public abstract class LootModifier implements IGlobalLootModifier {

    protected final Optional<Holder<LootItemCondition>> condition;
    private final int priority;

    protected LootModifier(Optional<Holder<LootItemCondition>> condition, int priority) {
        this.condition = condition;
        this.priority = priority;
    }

    protected static <T extends LootModifier> Products.P2<RecordCodecBuilder.Mu<T>, Optional<Holder<LootItemCondition>>, Integer> codecStart(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(LootItemCondition.CODEC.optionalFieldOf("condition").forGetter(lm -> ((LootModifier) lm).condition),
                Codec.INT.optionalFieldOf("priority", 1000).forGetter(lm -> ((LootModifier) lm).priority));
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public final ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        return condition.isEmpty() || condition.get().value().test(context) ? doApply(generatedLoot, context) : generatedLoot;
    }

    protected abstract ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context);
}
