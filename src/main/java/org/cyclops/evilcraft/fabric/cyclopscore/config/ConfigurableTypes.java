package org.cyclops.evilcraft.fabric.cyclopscore.config;

import org.cyclops.cyclopscore.config.ConfigurableTypeCommon;
import org.cyclops.cyclopscore.config.configurabletypeaction.ConfigurableTypeActionRegistry;
import org.cyclops.cyclopscore.config.configurabletypeaction.DummyActionCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.config.configurabletypeaction.BlockActionFabric;
import org.cyclops.evilcraft.fabric.cyclopscore.config.configurabletypeaction.EntityActionFabric;
import org.cyclops.evilcraft.fabric.cyclopscore.config.configurabletypeaction.FluidActionFabric;
import org.cyclops.evilcraft.fabric.cyclopscore.config.configurabletypeaction.GuiActionFabric;
import org.cyclops.evilcraft.fabric.cyclopscore.config.configurabletypeaction.ItemActionFabric;
import org.cyclops.evilcraft.fabric.cyclopscore.config.configurabletypeaction.LootModifierActionFabric;
import org.cyclops.evilcraft.fabric.cyclopscore.config.configurabletypeaction.ParticleActionFabric;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.*;

/**
 * The configurable types of the configs of this mod, with the categories of Cyclops Core's config files.
 * @author rubensworks
 */
public class ConfigurableTypes {

    public static final ConfigurableTypeCommon ITEM = new ConfigurableTypeCommon(true, ItemConfig.class, new ItemActionFabric(), "item");
    public static final ConfigurableTypeCommon BLOCK = new ConfigurableTypeCommon(true, BlockConfig.class, new BlockActionFabric(), "block");
    public static final ConfigurableTypeCommon ENTITY = new ConfigurableTypeCommon(true, EntityConfig.class, new EntityActionFabric<>(), "entity");
    public static final ConfigurableTypeCommon PARTICLE = new ConfigurableTypeCommon(true, ParticleConfig.class, new ParticleActionFabric<>(), "particle");
    public static final ConfigurableTypeCommon GUI = new ConfigurableTypeCommon(true, GuiConfig.class, new GuiActionFabric<>(), "gui");
    public static final ConfigurableTypeCommon FLUID = new ConfigurableTypeCommon(true, FluidConfig.class, new FluidActionFabric(), "fluid");
    public static final ConfigurableTypeCommon LOOT_MODIFIER = new ConfigurableTypeCommon(true, LootModifierConfig.class, new LootModifierActionFabric<>(), "loot_modifier");

    public static final ConfigurableTypeCommon EFFECT = registry(EffectConfig.class, "potion");
    public static final ConfigurableTypeCommon ARMOR_MATERIAL = registry(ArmorMaterialConfig.class, "armor_material");
    public static final ConfigurableTypeCommon BLOCK_ENTITY = registry(BlockEntityConfig.class, "block_entity");
    public static final ConfigurableTypeCommon CRITERION_TRIGGER = registry(CriterionTriggerConfig.class, "criterion_trigger");
    public static final ConfigurableTypeCommon DATA_COMPONENT = registry(DataComponentConfig.class, "data_component");
    public static final ConfigurableTypeCommon ENCHANTMENT_EFFECT_COMPONENT = registry(EnchantmentEffectComponentConfig.class, "enchantment_entity_effect");
    public static final ConfigurableTypeCommon ENCHANTMENT_ENTITY_EFFECT = registry(EnchantmentEntityEffectConfig.class, "enchantment_entity_effect");
    public static final ConfigurableTypeCommon LOOT_FUNCTION = registry(LootFunctionConfig.class, "loot_function");
    public static final ConfigurableTypeCommon RECIPE = registry(RecipeConfig.class, "recipe");
    public static final ConfigurableTypeCommon RECIPE_TYPE = registry(RecipeTypeConfig.class, "recipe_type");
    public static final ConfigurableTypeCommon SOUND_EVENT = registry(SoundEventConfig.class, "sound_event");
    public static final ConfigurableTypeCommon VILLAGER = registry(VillagerConfig.class, "mob");
    public static final ConfigurableTypeCommon WORLD_FEATURE = registry(WorldFeatureConfig.class, "world_feature");
    public static final ConfigurableTypeCommon WORLD_STRUCTURE = registry(WorldStructureConfig.class, "world_structure");
    public static final ConfigurableTypeCommon WORLD_STRUCTURE_PIECE = registry(WorldStructurePieceConfig.class, "world_structure_piece");
    public static final ConfigurableTypeCommon DUMMY = new ConfigurableTypeCommon(false, DummyConfig.class, new DummyActionCommon<>(), "general");

    @SuppressWarnings("rawtypes")
    private static ConfigurableTypeCommon registry(Class<? extends org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigCommon> configClass, String category) {
        return new ConfigurableTypeCommon(true, configClass, new ConfigurableTypeActionRegistry<>(), category);
    }
}
