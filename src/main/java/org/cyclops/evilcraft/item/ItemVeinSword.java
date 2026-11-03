package org.cyclops.evilcraft.item;

import net.minecraft.world.item.Tier;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/**
 * A strong pickaxe that may call up spirits.
 * @author rubensworks
 *
 */
public class ItemVeinSword extends Item {

    /**
     * The looting level of this sword.
     */
    public static final int LOOTING_LEVEL = 2;

    public ItemVeinSword(Properties properties) {
        super(properties.sword(ToolMaterial.GOLD, 3.0F, -2.4F));
    }

    /**
     * The gold tier with the durability of the config.
     */
    private record ConfiguredDurabilityTier(Tier tier) implements Tier {
        @Override
        public int getUses() {
            return ItemVeinSwordConfig.durability;
        }

        @Override
        public float getSpeed() {
            return tier.getSpeed();
        }

        @Override
        public float getAttackDamageBonus() {
            return tier.getAttackDamageBonus();
        }

        @Override
        public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() {
            return tier.getIncorrectBlocksForDrops();
        }

        @Override
        public int getEnchantmentValue() {
            return tier.getEnchantmentValue();
        }

        @Override
        public net.minecraft.world.item.crafting.Ingredient getRepairIngredient() {
            return tier.getRepairIngredient();
        }
    }

    public ItemStack getEnchantedItemStack(HolderLookup.Provider holders) {
        ItemStack sword = new ItemStack(this);
        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchantments.set(holders.holderOrThrow(ResourceKey.create(Registries.ENCHANTMENT, Identifier.parse("minecraft:looting"))), LOOTING_LEVEL);
        EnchantmentHelper.setEnchantments(sword, enchantments.toImmutable());
        return sword;
    }

}
