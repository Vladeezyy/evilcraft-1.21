package org.cyclops.evilcraft.fabric.cyclopscore.infobook.condition;



import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBaseNeoForge;

/**
 * Condition handler for checking if an item has been registered.
 * @author rubensworks
 *
 */
public class ItemSectionConditionHandler implements ISectionConditionHandler {

    @Override
    public boolean isSatisfied(ModBaseNeoForge<?> mod, String param) {
        return BuiltInRegistries.ITEM.containsKey(Identifier.parse(param));
    }

}
