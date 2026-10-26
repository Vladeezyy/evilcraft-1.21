package org.cyclops.evilcraft.fabric.cyclopscore.config.configurabletypeaction;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import org.cyclops.cyclopscore.config.configurabletypeaction.ConfigurableTypeActionCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.LootModifierConfig;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;
import org.cyclops.evilcraft.fabric.loot.GlobalLootModifiers;
import org.cyclops.evilcraft.fabric.loot.IGlobalLootModifier;

/**
 * Registers loot modifier types.
 * @author rubensworks
 */
public class LootModifierActionFabric<T extends IGlobalLootModifier> extends ConfigurableTypeActionCommon<LootModifierConfig<T>, MapCodec<T>, ModBase<?>> {
    @Override
    public void onRegisterForgeFilled(LootModifierConfig<T> eConfig) {
        GlobalLootModifiers.registerCodec(ResourceLocation.fromNamespaceAndPath(eConfig.getMod().getModId(), eConfig.getNamedId()), eConfig.getInstance());
        eConfig.onForgeRegistered();
    }
}
