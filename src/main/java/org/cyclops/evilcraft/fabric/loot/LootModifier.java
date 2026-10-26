package org.cyclops.evilcraft.fabric.loot;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.function.Predicate;

/**
 * A loot modifier that only applies when all of its loot conditions pass.
 */
public abstract class LootModifier implements IGlobalLootModifier {

    public static final Codec<LootItemCondition[]> LOOT_CONDITIONS_CODEC = LootItemCondition.DIRECT_CODEC.listOf()
            .xmap(list -> list.toArray(LootItemCondition[]::new), java.util.List::of);

    protected final LootItemCondition[] conditions;
    private final Predicate<LootContext> combinedConditions;

    protected LootModifier(LootItemCondition[] conditions) {
        this.conditions = conditions;
        this.combinedConditions = context -> {
            for (LootItemCondition condition : conditions) {
                if (!condition.test(context)) {
                    return false;
                }
            }
            return true;
        };
    }

    protected static <T extends LootModifier> Products.P1<RecordCodecBuilder.Mu<T>, LootItemCondition[]> codecStart(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(lm -> lm.conditions));
    }

    @Override
    public final ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        return combinedConditions.test(context) ? doApply(generatedLoot, context) : generatedLoot;
    }

    protected abstract ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context);
}
