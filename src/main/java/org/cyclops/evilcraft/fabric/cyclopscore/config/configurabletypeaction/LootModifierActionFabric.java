package org.cyclops.evilcraft.fabric.cyclopscore.config.configurabletypeaction;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import org.cyclops.cyclopscore.config.configurabletypeaction.ConfigurableTypeActionCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.LootModifierConfigNeoForge;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBaseNeoForge;
import org.cyclops.evilcraft.fabric.loot.GlobalLootModifiers;
import org.cyclops.evilcraft.fabric.loot.IGlobalLootModifier;

/**
 * Registers loot modifier types.
 * @author rubensworks
 */
public class LootModifierActionFabric<T extends IGlobalLootModifier> extends ConfigurableTypeActionCommon<LootModifierConfigNeoForge<T>, MapCodec<T>, ModBaseNeoForge<?>> {
    @Override
    public void onRegistriesFilled(LootModifierConfigNeoForge<T> eConfig) {
        GlobalLootModifiers.registerCodec(Identifier.fromNamespaceAndPath(eConfig.getMod().getModId(), eConfig.getNamedId()), eConfig.getInstance());
        eConfig.onRegistryRegistered();
    }
}
