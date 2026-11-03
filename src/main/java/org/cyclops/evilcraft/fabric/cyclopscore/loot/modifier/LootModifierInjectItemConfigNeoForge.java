package org.cyclops.evilcraft.fabric.cyclopscore.loot.modifier;



import org.cyclops.evilcraft.fabric.cyclopscore.CyclopsCoreNeoForge;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.LootModifierConfigNeoForge;

/**
 * @author rubensworks
 */
public class LootModifierInjectItemConfigNeoForge extends LootModifierConfigNeoForge<LootModifierInjectItem> {
    public LootModifierInjectItemConfigNeoForge() {
        super(CyclopsCoreNeoForge._instance, "inject_item", (eConfig) -> LootModifierInjectItem.CODEC.get());
    }
}
